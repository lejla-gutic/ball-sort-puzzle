package si.um.feri.BallSortPuzzle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import si.um.feri.BallSortPuzzle.BallSortPuzzle;
import si.um.feri.BallSortPuzzle.assets.AssetDescriptors;

public class IntroScreen extends ScreenAdapter {

    private final BallSortPuzzle game;
    private Stage stage;
    private Viewport viewport;

    private Image backgroundImage;
    private Image bearImage;
    private Image logoImage;

    private static final float WORLD_WIDTH = 950f;
    private static final float WORLD_HEIGHT = 800f;

    public IntroScreen(BallSortPuzzle game) {
        this.game = game;
    }

    @Override
    public void show() {
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT);
        stage = new Stage(viewport, game.getBatch());
        Gdx.input.setInputProcessor(stage);

        AssetManager assetManager = game.getAssetManager();
        TextureAtlas atlas = assetManager.get(AssetDescriptors.UI_ATLAS);

        TextureRegion background = atlas.findRegion("intro_background");
        backgroundImage = new Image(background);
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);

        bearImage = new Image(atlas.findRegion("bear"));
        logoImage = new Image(atlas.findRegion("logo"));

        float centerX = (WORLD_WIDTH - bearImage.getWidth()) / 2f;
        float pileY = WORLD_HEIGHT * 0.15f;

        bearImage.setPosition(centerX, pileY - bearImage.getHeight());
        bearImage.setOrigin(
            bearImage.getWidth() / 2f,
            bearImage.getHeight() / 2f
        );
        bearImage.getColor().a = 0f;

        float logoStartX = (WORLD_WIDTH - logoImage.getWidth()) / 2f;
        float logoStartY = WORLD_HEIGHT;

        logoImage.setPosition(logoStartX, logoStartY);
        logoImage.getColor().a = 0f;

        stage.addActor(bearImage);
        stage.addActor(logoImage);

        startAnimation(centerX, pileY);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    private void startAnimation(float centerX, float pileY) {

        bearImage.addAction(Actions.sequence(

            Actions.moveTo(centerX, pileY + 20, 0.8f, Interpolation.sineOut),

            Actions.parallel(
                Actions.fadeIn(0.3f),
                Actions.moveBy(0, 40, 0.6f, Interpolation.sineOut),
                Actions.rotateBy(15f, 0.4f)
            ),

            Actions.rotateBy(-30f, 0.5f),
            Actions.rotateBy(15f, 0.4f),

            Actions.delay(1f),

            Actions.parallel(
                Actions.fadeOut(0.4f),
                Actions.moveTo(centerX, pileY - bearImage.getHeight(), 0.7f, Interpolation.sineIn)
            ),

            Actions.run(() -> logoImage.addAction(
                Actions.sequence(
                    Actions.delay(0.2f),

                    Actions.parallel(
                        Actions.fadeIn(0.8f),
                        Actions.moveTo(
                            (WORLD_WIDTH - logoImage.getWidth()) / 2f,
                            WORLD_HEIGHT * 0.4f,
                            1.0f,
                            Interpolation.sineOut
                        )
                    ),

                    // bounce
                    Actions.moveBy(0, -12, 0.5f, Interpolation.sineOut),
                    Actions.moveBy(0, 12, 0.5f, Interpolation.sineIn),

                    Actions.run(() -> game.setScreen(new MenuScreen(game)))
                )
            ))
        ));

    }
}
