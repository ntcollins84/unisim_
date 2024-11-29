package io.github.uoyteamsix;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * A class which holds the {@link Camera} object and implements the camera movement logic.
 */
public class CameraController extends InputAdapter {
    private final OrthographicCamera camera;
    private final Vector2 lastDragPosition;

    private boolean isCurrentlyDragging = false;
    private float desiredZoomLevel = 0.5f;

    // Map boundaries for constraining the camera.
    private float mapWidth;
    private float mapHeight;

    // Maximum zoom level to keep the whole map visible
    private float maxZoomLevel;

    public CameraController() {
        camera = new OrthographicCamera();
        camera.zoom = desiredZoomLevel;
        lastDragPosition = new Vector2();
    }

    /**
     * Set the map dimensions to constrain the camera.
     *
     * @param mapWidth  the width of the map in pixels
     * @param mapHeight the height of the map in pixels
     */
    public void setMapDimensions(float mapWidth, float mapHeight) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        updateMaxZoomLevel();
    }

    /**
     * Updates the camera's viewport size.
     *
     * @param width  the new viewport width
     * @param height the new viewport height
     */
    public void setViewportDimensions(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        updateMaxZoomLevel();
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        desiredZoomLevel += amountY * 0.03f;
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.RIGHT) {
            isCurrentlyDragging = true;
            lastDragPosition.set(screenX, screenY);
            return true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (isCurrentlyDragging) {
            isCurrentlyDragging = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (!isCurrentlyDragging) {
            return false;
        }

        // Convert mouse movement into world space which allows movement of the camera whilst keeping the mouse cursor
        // fixed relative to the same point in world space.
        var mouseNow = camera.unproject(new Vector3(screenX, screenY, 0));
        camera.translate(camera.unproject(new Vector3(lastDragPosition, 0)).sub(mouseNow));
        lastDragPosition.set(screenX, screenY);
        return true;
    }

    /**
     * Handles any input events and updates the camera accordingly. Should be called once per frame.
     *
     * @param deltaTime the delta time between the last call of update
     */
    public void update(float deltaTime) {
        // Allow use of WSAD keys to move camera if not currently dragging the camera. Decide speed factor based on
        // whether shift is currently held.
        if (!isCurrentlyDragging) {
            final var cameraMovementSpeed = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ? 250.0f : 100.0f;
            final var cameraMovementDelta = deltaTime * cameraMovementSpeed;
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                camera.translate(0.0f, cameraMovementDelta);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                camera.translate(0.0f, -cameraMovementDelta);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                camera.translate(-cameraMovementDelta, 0.0f);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                camera.translate(cameraMovementDelta, 0.0f);
            }
        }
        camera.update();

        // Handle camera zooming. The code below gives the effect of zooming into a point by keeping the mouse position
        // constant relative to world space.
        var mouseStart = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

        // Clamp desired zoom level. Do this every frame in case the maximum zoom level gets updated.
        desiredZoomLevel = MathUtils.clamp(desiredZoomLevel, 0.2f, maxZoomLevel);

        // Update zoom level and call update so that the next call to unproject has updated values.
        camera.zoom = Interpolation.linear.apply(camera.zoom, desiredZoomLevel, deltaTime * 12.0f);
        camera.update();

        // The difference in mouse position in world space before and after zooming is the amount we need to
        // translate by to keep the mouse position constant.
        var mouseEnd = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        camera.translate(mouseStart.sub(mouseEnd));

        // Clamp camera position to the map boundaries.
        var halfViewport = new Vector2(camera.viewportWidth, camera.viewportHeight).scl(camera.zoom / 2.0f);
        camera.position.x = MathUtils.clamp(camera.position.x, halfViewport.x, mapWidth - halfViewport.x);
        camera.position.y = MathUtils.clamp(camera.position.y, halfViewport.y, mapHeight - halfViewport.y);

        // Final camera update with the new position.
        camera.update();
    }

    /**
     * Updates the maximum zoom level to ensure the whole map fits within the viewport.
     */
    private void updateMaxZoomLevel() {
        // Calculate the maximum zoom level based on the aspect ratio and map size.
        float viewportAspectRatio = camera.viewportWidth / camera.viewportHeight;
        float mapAspectRatio = mapWidth / mapHeight;

        // Set maxZoomLevel based on the smallest fitting value for width or height.
        if (viewportAspectRatio < mapAspectRatio) {
            // Taller aspect ratio: limit based on map height.
            maxZoomLevel = mapHeight / camera.viewportHeight;
        } else {
            // Wider aspect ratio: limit based on map width.
            maxZoomLevel = mapWidth / camera.viewportWidth;
        }
    }

    /**
     * @return the orthographic camera controlled by this class
     */
    public OrthographicCamera getCamera() {
        return camera;
    }

    /**
     * @return whether the user is currently panning the camera with the mouse
     */
    public boolean isPanning() {
        return isCurrentlyDragging;
    }

    /**
     * @return whether the camera is currently zooming in
     */
    public boolean isZoomingIn() {
        return (desiredZoomLevel - camera.zoom) < -0.01f;
    }

    /**
     * @return whether the camera is currently zooming out
     */
    public boolean isZoomingOut() {
        return (desiredZoomLevel - camera.zoom) > 0.01f;
    }
}
