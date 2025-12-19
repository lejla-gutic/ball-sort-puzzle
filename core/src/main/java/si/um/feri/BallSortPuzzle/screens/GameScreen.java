package si.um.feri.BallSortPuzzle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;


import si.um.feri.BallSortPuzzle.BallSortPuzzle;
import si.um.feri.BallSortPuzzle.GameManager;

import com.badlogic.gdx.Preferences;

import java.util.List;

import si.um.feri.BallSortPuzzle.assets.AssetDescriptors;
import si.um.feri.BallSortPuzzle.game.Ball;
import si.um.feri.BallSortPuzzle.game.BallColor;
import si.um.feri.BallSortPuzzle.game.GameWorld;
import si.um.feri.BallSortPuzzle.game.LevelConfig;
import si.um.feri.BallSortPuzzle.game.LevelType;
import si.um.feri.BallSortPuzzle.game.Tube;

public class GameScreen extends ScreenAdapter {
    private Stage stage;
    private TextureAtlas atlas;
    private Preferences prefs;
    private Skin skin;

    private TextureRegion backgroundRegion;

    private final BallSortPuzzle game;
    private GameWorld world;

    private int selectedTube = -1;
    private boolean animating = false;

    private Music gameMusic;
    private Sound pickSound;
    private Sound popSound;
    private ParticleEffect confettiLeft;
    private ParticleEffect confettiRight;

    private boolean levelCompleted = false;
    private Group winPopup;

    private Array<Actor> tubeActors = new Array<>();
    private Group tubesGroup;

    private boolean scoreSaved = false;
    private boolean initialized = false;


    public GameScreen(BallSortPuzzle game) {
        this.game = game;
    }

    @Override
    public void show() {

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        prefs = Gdx.app.getPreferences("settings");

        AssetManager assetManager = game.getAssetManager();
        atlas = assetManager.get(AssetDescriptors.UI_ATLAS);
        skin = assetManager.get(AssetDescriptors.ORANGE_SKIN);

        backgroundRegion = atlas.findRegion("main_background");

        pickSound = assetManager.get(AssetDescriptors.PICK_SOUND);
        popSound  = assetManager.get(AssetDescriptors.POP_SOUND);

        gameMusic = assetManager.get(AssetDescriptors.GAME_MUSIC);
        gameMusic.setLooping(true);

        confettiLeft  = new ParticleEffect(
            assetManager.get(AssetDescriptors.CONFETTI_LEFT)
        );
        confettiRight = new ParticleEffect(
            assetManager.get(AssetDescriptors.CONFETTI_RIGHT)
        );

        if (!initialized) {
            String levelStr = prefs.getString("start_level", "Easy");
            LevelType levelType = LevelType.valueOf(levelStr.toUpperCase());
            LevelConfig config = LevelConfig.forLevel(levelType);

            world = new GameWorld(config);

            tubesGroup = new Group();
            stage.addActor(tubesGroup);

            drawTubes();
            createTopBar();

            initialized = true;
        }
        else {
            stage.addActor(tubesGroup);
            redraw();
            createTopBar();
        }

        if (prefs.getBoolean("music_on", true)) {
            gameMusic.play();
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        game.getBatch().begin();
        game.getBatch().draw(
            backgroundRegion,
            0, 0,
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight()
        );
        game.getBatch().end();

        stage.act(delta);
        stage.draw();

        if (levelCompleted) {
            game.getBatch().begin();

            confettiLeft.update(delta);
            confettiLeft.draw(game.getBatch());

            confettiRight.update(delta);
            confettiRight.draw(game.getBatch());

            game.getBatch().end();
        }

    }

    private void drawTubes() {
        tubesGroup.clear();
        tubeActors.clear();

        float spacing = 5f;
        float ballSpacing = 40f;
        float tubeBottomPadding = 50f;

        int tubeCount = world.getTubes().size();
        int tubesPerRow = tubeCount / 2;

        Image tempTube = createTubeImage();
        float tubeWidth = tempTube.getWidth();

        float rowSpacing = 50f;
        float baseY = Gdx.graphics.getHeight() * 0.50f;

        float totalWidth = tubesPerRow * tubeWidth + (tubesPerRow - 1) * spacing;
        float startX = (Gdx.graphics.getWidth() - totalWidth) / 2f;

        for (int i = 0; i < tubeCount; i++) {
            int index = i;
            Tube tube = world.getTubes().get(i);

            Group tubeGroup = new Group();

            Image tubeImage = createTubeImage();
            tubeImage.setPosition(0, 0);
            tubeGroup.addActor(tubeImage);

            tubeGroup.setSize(tubeImage.getWidth(), tubeImage.getHeight());

            // balls
            List<Ball> balls = tube.getBalls();
            for (int j = 0; j < balls.size(); j++) {

                Ball ball = balls.get(j);
                Image ballImage = new Image(
                    atlas.findRegion(getBallRegionName(ball.getColor()))
                );

                ballImage.setSize(40, 40);

                float bx = tubeImage.getX() + (tubeImage.getWidth() - ballImage.getWidth()) / 2f;
                float by = tubeImage.getY() + tubeBottomPadding + j * ballSpacing;

                ballImage.setPosition(bx, by);
                tubeGroup.addActor(ballImage);
            }

            int row = i / tubesPerRow;   // 0 ili 1
            int col = i % tubesPerRow;   // pozicija u redu

            float x = startX + col * (tubeWidth + spacing);
            float y = baseY - row * (tubeImage.getHeight() + rowSpacing);

            tubeGroup.setPosition(x, y);

            if (index == selectedTube) {
                tubeGroup.setColor(1f, 1f, 0.7f, 1f); // žućkasti highlight

                tubeGroup.addAction(
                    Actions.sequence(
                        Actions.moveBy(0, 20, 0.12f),
                        Actions.moveBy(0, -10, 0.10f)
                    )
                );
            }


            tubeGroup.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    onTubeClicked(index);
                }
            });

            tubesGroup.addActor(tubeGroup);
            tubeActors.add(tubeGroup);
        }
    }

    private Image createTubeImage() {
        TextureRegion tubeRegion = atlas.findRegion("tube");
        Image tube = new Image(tubeRegion);

        tube.setSize(160, 280); // prilagodi po potrebi
        tube.setOrigin(tube.getWidth() / 2f, 0);

        return tube;
    }

    private void onTubeClicked(int index) {
        if (levelCompleted) return;
        if (animating) return;

        if (selectedTube == -1) {
            selectedTube = index;

            if (prefs.getBoolean("sound_on", true)) {
                popSound.play();
            }

            highlightTube(index);
            return;
        }

        if (selectedTube == index) {
            selectedTube = -1;
            redraw();
            return;
        }

        int from = selectedTube;
        int to = index;
        selectedTube = -1;

        animateMove(from, to);
    }

    private void animateMove(int from, int to) {
        Tube source = world.getTubes().get(from);
        Tube dest = world.getTubes().get(to);

        if (source.isEmpty()) {
            shakeActor(tubeActors.get(from));
            selectedTube = -1;
            return;
        }

        Ball ball = source.peekTop();
        if (!dest.canAdd(ball)) {
            shakeActor(tubeActors.get(to));
            selectedTube = -1;
            return;
        }

        animating = true;

        Group fromGroup = (Group) tubeActors.get(from);
        Group toGroup   = (Group) tubeActors.get(to);

        float ballSize = 40f;
        float ballSpacing = 40f;
        float tubeBottomPadding = 50f;

        // START – TAČNO IZ CENTRA SOURCE TUBE
        float fromX = fromGroup.getX()
            + (fromGroup.getWidth() - ballSize) / 2f;

        float fromY = fromGroup.getY()
            + tubeBottomPadding
            + (source.getBalls().size() - 1) * ballSpacing;

        // END – TAČNO U CENTAR TARGET TUBE
        float toX = toGroup.getX()
            + (toGroup.getWidth() - ballSize) / 2f;

        float toY = toGroup.getY()
            + tubeBottomPadding
            + dest.getBalls().size() * ballSpacing;

        float liftY = Math.max(fromGroup.getY(), toGroup.getY()) + 300;

        Image movingBall = new Image(
            atlas.findRegion(getBallRegionName(ball.getColor()))
        );
        movingBall.setSize(ballSize, ballSize);
        movingBall.setPosition(fromX, fromY);

        stage.addActor(movingBall);

        movingBall.addAction(
            Actions.sequence(
                Actions.moveTo(fromX, liftY, 0.15f),
                Actions.moveTo(toX,   liftY, 0.15f),
                Actions.moveTo(toX,   toY,   0.15f),
                Actions.run(() -> {
                    movingBall.remove();
                    boolean unlimitedUndo = prefs.getBoolean("undo_unlimited", true);
                    world.move(from, to, unlimitedUndo);

                    if (prefs.getBoolean("sound_on", true)) {
                        pickSound.play();
                    }

                    animating = false;
                    redraw();
                    checkWin();
                })
            )
        );
    }

    private void shakeActor(Actor actor) {
        actor.addAction(
            Actions.sequence(
                Actions.moveBy(-10, 0, 0.05f),
                Actions.moveBy(20, 0, 0.1f),
                Actions.moveBy(-10, 0, 0.05f)
            )
        );
    }

    private void redraw() {
        drawTubes();
    }
    private void highlightTube(int index) {
        redraw();
    }

    private void checkWin() {
        if (world.isSolved() && !levelCompleted) {
            startConfetti();
            showWinPopup();
        }
    }

    private void startConfetti() {
        levelCompleted = true;

        float midY = Gdx.graphics.getHeight() / 2.5f;

        confettiLeft.setPosition(40, midY);
        confettiRight.setPosition(Gdx.graphics.getWidth() - 40, midY);

        confettiLeft.start();
        confettiRight.start();
    }

    private void showWinPopup() {
        winPopup = new Group();

        int finalScore = calculateScore();

        float popupWidth = 520;
        float popupHeight = 360;

        float x = (Gdx.graphics.getWidth() - popupWidth) / 2f;
        float y = (Gdx.graphics.getHeight() - popupHeight) / 2f;

        Image bg = new Image(skin.getDrawable("panel-peach"));
        bg.setSize(popupWidth, popupHeight);
        bg.setColor(0.898f, 0.918f, 1f, 1f);
        winPopup.addActor(bg);

        Table content = new Table();
        content.setSize(popupWidth, popupHeight);
        content.pad(30);
        winPopup.addActor(content);

        BitmapFont font = game.getAssetManager().get(AssetDescriptors.FONT);

        Label.LabelStyle titleStyle =
            new Label.LabelStyle(font, Color.valueOf("233589"));
        Label title = new Label("LEVEL COMPLETED!", titleStyle);
        title.setFontScale(1.3f);
        title.setAlignment(Align.center);

        content.add(title).padBottom(15).row();

        Label.LabelStyle infoStyle =
            new Label.LabelStyle(font, Color.DARK_GRAY);

        Label movesLabel =
            new Label("Moves: " + world.getMoves(), infoStyle);
        movesLabel.setFontScale(0.9f);

        Label scoreLabel =
            new Label("Score: " + finalScore, infoStyle);
        scoreLabel.setFontScale(0.9f);

        Table statsRow = new Table();
        statsRow.add(movesLabel).padRight(30);
        statsRow.add(scoreLabel);

        content.add(statsRow).padBottom(20).row();

        TextField.TextFieldStyle tfStyle = new TextField.TextFieldStyle();
        tfStyle.font = font;
        tfStyle.fontColor = Color.DARK_GRAY;
        tfStyle.background = skin.getDrawable("panel-peach");

        TextField nameField = new TextField("", tfStyle);
        nameField.setMessageText("Enter your name");

        content.add(nameField)
            .width(320)
            .height(45)
            .padBottom(25)
            .row();

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.up = skin.getDrawable("button-orange");
        btnStyle.down = skin.getDrawable("button-orange-down");
        btnStyle.font = font;

        TextButton saveBtn = new TextButton("SAVE", btnStyle);
        TextButton nextBtn = new TextButton("NEXT", btnStyle);
        TextButton menuBtn = new TextButton("MENU", btnStyle);

        saveBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                if (scoreSaved) return;

                String name = nameField.getText().trim();
                if (name.isEmpty()) name = "Player";

                GameManager gm = new GameManager();
                gm.addScore(name, finalScore);

                scoreSaved = true;

                saveBtn.setText("SAVED");
                saveBtn.setDisabled(true);
                nameField.setDisabled(true);
            }
        });


        nextBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                goToNextLevel();
            }
        });

        menuBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });

        Table buttonRow = new Table();
        buttonRow.add(nextBtn).width(120).height(50).padRight(15);
        buttonRow.add(menuBtn).width(120).height(50).padRight(15);
        buttonRow.add(saveBtn).width(120).height(50);

        content.add(buttonRow);

        winPopup.setPosition(x, y);
        winPopup.setScale(0.85f);
        winPopup.getColor().a = 0f;

        winPopup.addAction(
            Actions.parallel(
                Actions.fadeIn(0.3f),
                Actions.scaleTo(1f, 1f, 0.3f)
            )
        );

        stage.addActor(winPopup);
    }


    private String getBallRegionName(BallColor color) {
        switch (color) {
            case RED: return "red_ball";
            case BLUE: return "blue_ball";
            case GREEN: return "green_ball";
            case YELLOW: return "yellow_ball";
            case PURPLE: return "purple_ball";
            case ORANGE: return "orange_ball";
            case PINK: return "pink_ball";
            case TURQUOISE: return "turquoise_ball";
            default: throw new IllegalArgumentException("Unknown color");
        }
    }

    private void goToNextLevel() {
        String current = prefs.getString("start_level", "Easy");

        LevelType nextLevel;

        switch (LevelType.valueOf(current.toUpperCase())) {
            case EASY:
                nextLevel = LevelType.MEDIUM;
                break;
            case MEDIUM:
                nextLevel = LevelType.HARD;
                break;
            case HARD:
            default:
                game.setScreen(new MenuScreen(game));
                return;
        }

        prefs.putString("start_level", nextLevel.name());
        prefs.flush();

        game.setScreen(new GameScreen(game));
    }

    private void createTopBar() {
        Table topBar = new Table();
        topBar.setFillParent(true);
        topBar.top().padTop(15).padLeft(200).padRight(200);

        ImageButton homeBtn = new ImageButton(new TextureRegionDrawable(atlas.findRegion("home_btn")));
        ImageButton settingsBtn = new ImageButton(new TextureRegionDrawable(atlas.findRegion("settings_btn")));
        ImageButton undoBtn = new ImageButton(new TextureRegionDrawable(atlas.findRegion("undo_btn")));
        ImageButton resetBtn = new ImageButton(new TextureRegionDrawable(atlas.findRegion("reset_btn")));

        float btnSize = 55f;
        homeBtn.getImage().setSize(btnSize, btnSize);
        settingsBtn.getImage().setSize(btnSize, btnSize);
        undoBtn.getImage().setSize(btnSize, btnSize);
        resetBtn.getImage().setSize(btnSize, btnSize);

        Label.LabelStyle levelStyle = new Label.LabelStyle(
            game.getAssetManager().get(AssetDescriptors.FONT),
            Color.WHITE
        );

        String levelText = "LEVEL: " + prefs.getString("start_level", "Easy").toUpperCase();
        Label levelLabel = new Label(levelText, levelStyle);
        levelLabel.setFontScale(0.6f);

        Table levelBox = new Table();
        levelBox.setBackground(new TextureRegionDrawable(atlas.findRegion("level_button")));
        levelBox.getBackground().setMinHeight(80);
        levelBox.getBackground().setMinWidth(200);
        levelBox.setColor(Color.DARK_GRAY);
        levelBox.add(levelLabel).pad(10);

        topBar.add(homeBtn).size(btnSize).padRight(10);
        topBar.add(settingsBtn).size(btnSize).padRight(20);

        topBar.add(levelBox).expandX().center();

        topBar.add(undoBtn).size(btnSize).padLeft(20);
        topBar.add(resetBtn).size(btnSize).padLeft(10);

        settingsBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SettingsScreen(game, GameScreen.this));
            }
        });

        homeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });

        undoBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (animating || levelCompleted) return;

                boolean undone = world.undo();
                if (undone) {
                    redraw();
                }
            }
        });

        resetBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game));
            }
        });

        stage.addActor(topBar);

    }

    private int calculateScore() {
        int moves = world.getMoves();
        String level = prefs.getString("start_level", "Easy");

        int base;
        switch (LevelType.valueOf(level.toUpperCase())) {
            case EASY: base = 1000; break;
            case MEDIUM: base = 2000; break;
            case HARD: base = 3000; break;
            default: base = 1000;
        }

        int score = base - moves * 10;
        return Math.max(score, 0);
    }



    @Override
    public void hide() {
        if (gameMusic != null) {
            gameMusic.stop();
        }
    }
}
