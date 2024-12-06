package io.github.uoyteamsix;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Objects;

/**
 * NEW CLASS
 * A class representing a high score screen
 * Displays a text input for the player's name
 */
public class HighScoreScreen extends ScreenAdapter {
    private final UniSimGame game;
    private final int score;
    SpriteBatch batch;
    Viewport viewport;
    OrthographicCamera camera;
    BitmapFont fontBig;
    BitmapFont fontSmall;
    Texture endScreen;
    Preferences highScores;
    Stage stage;
    boolean highScoreAchieved = false;

    public HighScoreScreen(UniSimGame game, float satisfaction) {
        this.game = game;
        this.score = (int) (satisfaction * 100);

        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        viewport = new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewport.setCamera(camera);
        batch = new SpriteBatch();
        stage = new Stage(viewport);

        // Create fonts
        var generator = new FreeTypeFontGenerator(Gdx.files.internal("ui/font.ttf"));
        var parametersBig = new FreeTypeFontGenerator.FreeTypeFontParameter();
        var parametersSmall = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parametersBig.size = 150;
        fontBig = generator.generateFont(parametersBig);
        fontBig.setColor(Color.BLACK);
        parametersSmall.size = 50;
        fontSmall = generator.generateFont(parametersSmall);
        fontSmall.setColor(Color.BLACK);

        // Set background
        endScreen = new Texture("screens/map-blurred.jpg");

        // Get highscores
        highScores = Gdx.app.getPreferences("High Scores");

        // Check high score achieved
        int pos = -1;
        for (int i = 0; i < 5; i++) {
            if (highScores.getInteger(i + "score", 0) < score) {
                highScoreAchieved = true;
                pos = i;
                break;
            }
        }

        // Create display
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        //table.setDebug(true);

        table.add(createHighScoreMessage()).top().padBottom(200);
        table.row();
        table.add(createNameInput(pos)).bottom();
        table.row();

        Gdx.input.setInputProcessor(this.stage);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        stage.act(delta);

        // Draw assets to screen
        batch.begin();
        batch.draw(endScreen, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        batch.end();

        stage.draw();

        // If high score not achieved go straight to end screen
        if (!highScoreAchieved) {
            game.setScreen(new EndScreen(game, score));
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        batch.dispose();
        stage.dispose();
    }

    /**
     * Inserts a player's score into the leaderboard
     * Only checks through top 5
     *
     * @param score the player's score
     * @param pos the place the score is being inserted to
     */
    public void insertScore(int score, String name, int pos) {
        // Store replaced data
        int prevScore;
        String prevName;
        for (int i = pos; i < 5; i++) {
            String key = String.valueOf(i);
            // Get previous placement data
            prevScore = highScores.getInteger(key + "score", 0);
            prevName = highScores.getString(key + "name", "");
            // Insert new data
            highScores.putInteger(key + "score", score);
            highScores.putString(key + "name", name);
            highScores.flush();
            // Set previous data as current data
            score = prevScore;
            name = prevName;
            // If previous data was empty then no need to continue
            if (score == 0) { break; }
        }
    }

    /**
     * @return a display with a "high score" message and the player's score
     */
    public Table createHighScoreMessage() {
        Table highScore = new Table();

        var labelStyleBig = new Label.LabelStyle(fontBig, Color.BLACK);
        var labelStyleSmall = new Label.LabelStyle(fontSmall, Color.BLACK);

        highScore.add(new Label("New high score!", labelStyleBig));
        highScore.row();
        highScore.add(new Label("Score: " + score + "%", labelStyleSmall));
        highScore.row();

        return highScore;
    }

    /**
     * @param pos the leaderboard passed to insertScore
     * @return an display with an area to input a name to go with a score
     */
    public Table createNameInput(int pos) {
        Table nameInput = new Table();

        // Label for text input
        var labelStyle = new Label.LabelStyle(fontSmall, Color.BLACK);
        Label enterNameLabel = new Label("Enter your name: ", labelStyle);


        var style = new TextField.TextFieldStyle();
        style.font = fontSmall;
        style.fontColor = Color.GRAY;
        style.focusedFontColor = Color.BLACK;

        // Create text input
        TextField inputBox = new TextField("", style);
        inputBox.setText("Name...");
        inputBox.setWidth(500);
        inputBox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                // Clear default text when focused into
                if (Objects.equals(inputBox.getText(), "Name...")) {
                    inputBox.setText("");
                }
            }
        });
        stage.addActor(inputBox);

        var buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = fontSmall;
        buttonStyle.fontColor = Color.BLACK;

        // Create submit button
        TextButton button = new TextButton("Submit", buttonStyle);
        button.setSize(32, 32);
        button.addListener(new ClickListener() {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                // Get entered name, add to leaderboard, go to end screen
                String name = inputBox.getText();
                insertScore(score, name, pos);
                game.setScreen(new EndScreen(game, score));
                return true;
            }
        });

        nameInput.add(enterNameLabel);
        nameInput.row();
        nameInput.add(inputBox);
        nameInput.add(button);

        return nameInput;
    }
}
/*
TODO: Make it look halfway decent i.e. textures
See if you can get UiAssets to load stuff without crashing the game
 */
