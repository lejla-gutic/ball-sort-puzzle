package si.um.feri.BallSortPuzzle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;

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

    private final BallSortPuzzle game;
    private GameWorld world;

    private int selectedTube = -1;
    private boolean animating = false;

    private Music gameMusic;
    private Sound pickSound;
    private Sound popSound;

    private Preferences prefs;


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

        pickSound = assetManager.get(AssetDescriptors.PICK_SOUND);
        popSound  = assetManager.get(AssetDescriptors.POP_SOUND);

        gameMusic = assetManager.get(AssetDescriptors.GAME_MUSIC);
        gameMusic.setLooping(true);

        if (prefs.getBoolean("music_on", true)) {
            gameMusic.play();
        }

        // new GameManager().resetScores();

        // za test
        /*GameManager gm = new GameManager();
        gm.addScore("Lejla", 5000);
        gm.addScore("Hana", 1500);
        gm.addScore("Nejra", 3400);*/


        String levelStr = prefs.getString("start_level", "Easy");

        LevelType levelType = LevelType.valueOf(levelStr.toUpperCase());
        LevelConfig config = LevelConfig.forLevel(levelType);

        world = new GameWorld(config);

        drawTubes();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        stage.act(delta);
        stage.draw();
    }

    private void drawTubes() {
        float spacing = 50f;
        float ballSpacing = 40f;
        float tubeBottomPadding = 50f;

        int tubeCount = world.getTubes().size();

        Image tempTube = createTubeImage();
        float tubeWidth = tempTube.getWidth();

        float totalWidth = tubeCount * tubeWidth + (tubeCount - 1) * spacing;
        float startX = (Gdx.graphics.getWidth() - totalWidth) / 2f;
        float baseY = Gdx.graphics.getHeight() * 0.25f;

        for (int i = 0; i < tubeCount; i++) {
            int index = i;
            Tube tube = world.getTubes().get(i);

            Group tubeGroup = new Group();

            Image tubeImage = createTubeImage();
            tubeImage.setPosition(0, 0);
            tubeGroup.addActor(tubeImage);

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

            tubeGroup.setPosition(startX + i * (tubeWidth + spacing), baseY);

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

            stage.addActor(tubeGroup);
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
        if (animating) return;

        if (selectedTube == -1) {
            selectedTube = index;

            if (prefs.getBoolean("sound_on", true)) {
                pickSound.play();
            }

            highlightTube(index);
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

        if (source.isEmpty()){
            redraw();
            return;
        }

        Ball ball = source.peekTop();
        if(!dest.canAdd(ball)) {
            redraw();   // myb shake
            return;
        }

        animating = true;

        float spacing = 50f;
        float ballSpacing = 40f;
        float tubeBottomPadding = 50f;

        float tubeWidth = 160;
        float startX = (Gdx.graphics.getWidth() - world.getTubes().size() * tubeWidth - (world.getTubes().size() - 1) * spacing) / 2f;
        float baseY = Gdx.graphics.getHeight() * 0.25f;

        float fromX = startX + from * (tubeWidth + spacing) + tubeWidth / 2f - 20;
        float fromY = baseY + tubeBottomPadding + (source.getBalls().size() - 1) * ballSpacing;

        float toX = startX + to * (tubeWidth + spacing) + tubeWidth / 2f - 20;
        float toY = baseY + tubeBottomPadding + dest.getBalls().size() * ballSpacing;

        // ball iamge
        Image movingBall = new Image(
            atlas.findRegion(getBallRegionName(ball.getColor()))
        );
        movingBall.setSize(40, 40);
        movingBall.setPosition(fromX, fromY);

        stage.addActor(movingBall);

        // animation
        movingBall.addAction(
            Actions.sequence(
                Actions.moveTo(movingBall.getX(), baseY + 300, 0.15f),
                Actions.moveTo(toX, baseY + 300, 0.15f),
                Actions.moveTo(toX, toY, 0.15f),
                Actions.run(() -> {
                    world.move(from, to);

                    if (prefs.getBoolean("sound_on", true)) {
                        popSound.play();
                    }

                    animating = false;
                    redraw();
                    checkWin();
                })
            )
        );

    }

    private void redraw() {
        stage.clear();
        drawTubes();
    }
    private void highlightTube(int index) {
        redraw();
    }
    private void checkWin() {
        if (world.isSolved()) {
            System.out.println("LEVEL SOLVED!");
        }
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


    @Override
    public void hide() {
        if (gameMusic != null) {
            gameMusic.stop();
        }
    }




}
