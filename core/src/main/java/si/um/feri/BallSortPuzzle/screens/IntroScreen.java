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

import si.um.feri.BallSortPuzzle.BallSortPuzzle;
import si.um.feri.BallSortPuzzle.assets.AssetDescriptors;

public class IntroScreen extends ScreenAdapter {

    private final BallSortPuzzle game;
    private Stage stage;

    private TextureRegion background;
    private Image bearImage;
    private Image logoImage;

    public IntroScreen(BallSortPuzzle game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        AssetManager assetManager = game.getAssetManager();
        TextureAtlas atlas = assetManager.get(AssetDescriptors.UI_ATLAS);

        background = atlas.findRegion("intro_background");
        bearImage = new Image(atlas.findRegion("bear"));
        logoImage = new Image(atlas.findRegion("logo"));

        float centerX = (Gdx.graphics.getWidth() - bearImage.getWidth()) / 2f;
        float pileY = Gdx.graphics.getHeight() * 0.15f;

        // Bear starts hidden in ball pile
        bearImage.setPosition(centerX, pileY - bearImage.getHeight());
        bearImage.setOrigin(
            bearImage.getWidth() / 2f,
            bearImage.getHeight() / 2f
        );
        bearImage.getColor().a = 0f; // invisible initially

        // Logo starts above screen
        float logoStartX = (Gdx.graphics.getWidth() - logoImage.getWidth()) / 2f;
        float logoStartY = Gdx.graphics.getHeight();

        logoImage.setPosition(logoStartX, logoStartY);
        logoImage.getColor().a = 0f;

        // Add actors
        stage.addActor(bearImage);
        stage.addActor(logoImage);

        startAnimation(centerX, pileY);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        game.getBatch().begin();
        game.getBatch().draw(
            background,
            0, 0,
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight()
        );
        game.getBatch().end();

        stage.act(delta);
        stage.draw();
    }

    private void startAnimation(float centerX, float pileY) {

        bearImage.addAction(Actions.sequence(

            // 1) Bear climbs invisibly into the ball pile
            Actions.moveTo(centerX, pileY + 20, 0.8f, Interpolation.sineOut),

            // 2) Bear pops out (fade + rotate + jump)
            Actions.parallel(
                Actions.fadeIn(0.3f),
                Actions.moveBy(0, 40, 0.6f, Interpolation.sineOut),
                Actions.rotateBy(15f, 0.4f)
            ),

            // 3) Cute wobble
            Actions.rotateBy(-30f, 0.5f),
            Actions.rotateBy(15f, 0.4f),

            // 4) Pause
            Actions.delay(1f),

            // 5) Bear falls back into the ball pile
            Actions.parallel(
                Actions.fadeOut(0.4f),
                Actions.moveTo(centerX, pileY - bearImage.getHeight(), 0.7f, Interpolation.sineIn)
            ),

            // 6) After bear disappears → logo slides from top
            Actions.run(() -> logoImage.addAction(
                Actions.sequence(
                    Actions.delay(0.2f),

                    // slide-in + fade
                    Actions.parallel(
                        Actions.fadeIn(0.8f),
                        Actions.moveTo(
                            (Gdx.graphics.getWidth() - logoImage.getWidth()) / 2f,
                            Gdx.graphics.getHeight() * 0.4f,
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
