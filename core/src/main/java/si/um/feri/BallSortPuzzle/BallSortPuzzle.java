package si.um.feri.BallSortPuzzle;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;

import si.um.feri.BallSortPuzzle.assets.AssetDescriptors;
import si.um.feri.BallSortPuzzle.screens.IntroScreen;

public class BallSortPuzzle extends Game {
    private SpriteBatch batch;
    private AssetManager assetManager;
    private TextureRegion ballRegion;

    @Override
    public void create() {
        batch = new SpriteBatch();

        assetManager = new AssetManager();

        assetManager.load(AssetDescriptors.UI_ATLAS);
        assetManager.load(AssetDescriptors.FONT);
        assetManager.load(AssetDescriptors.CONFETTI_LEFT);
        assetManager.load(AssetDescriptors.CONFETTI_RIGHT);
        assetManager.load(AssetDescriptors.POP_SOUND);
        assetManager.load(AssetDescriptors.PICK_SOUND);
        assetManager.load(AssetDescriptors.MUSIC);
        assetManager.load(AssetDescriptors.ORANGE_SKIN);

        assetManager.finishLoading();
        setScreen(new IntroScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        assetManager.dispose();
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public SpriteBatch getBatch() {
        return batch;
    }

}
