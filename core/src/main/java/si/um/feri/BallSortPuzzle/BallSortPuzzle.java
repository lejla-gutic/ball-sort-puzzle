package si.um.feri.BallSortPuzzle;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;

import si.um.feri.BallSortPuzzle.assets.AssetDescriptors;
import si.um.feri.BallSortPuzzle.assets.RegionNames;

public class BallSortPuzzle extends ApplicationAdapter {
    private SpriteBatch batch;
    private AssetManager assetManager;
    private TextureRegion ballRegion;

    @Override
    public void create() {
        assetManager = new AssetManager();

        assetManager.load(AssetDescriptors.UI_ATLAS);
        assetManager.load(AssetDescriptors.FONT);
        assetManager.load(AssetDescriptors.CONFETTI_LEFT);
        assetManager.load(AssetDescriptors.CONFETTI_RIGHT);
        assetManager.load(AssetDescriptors.POP_SOUND);
        assetManager.load(AssetDescriptors.PICK_SOUND);
        assetManager.load(AssetDescriptors.MUSIC);

        assetManager.finishLoading();
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
    }

    @Override
    public void dispose() {
        batch.dispose();
        assetManager.dispose();
    }
}
