package si.um.feri.BallSortPuzzle.assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class AssetDescriptors {

    public static final AssetDescriptor<TextureAtlas> UI_ATLAS =
        new AssetDescriptor<>(AssetPaths.UI_ATLAS, TextureAtlas.class);

    public static final AssetDescriptor<Skin> ORANGE_SKIN =
        new AssetDescriptor<>("skin/uiskin.json", Skin.class);

    public static final AssetDescriptor<BitmapFont> FONT =
        new AssetDescriptor<>(AssetPaths.FONT, BitmapFont.class);

    public static final AssetDescriptor<ParticleEffect> CONFETTI_LEFT =
        new AssetDescriptor<>(AssetPaths.CONFETTI_LEFT, ParticleEffect.class);

    public static final AssetDescriptor<ParticleEffect> CONFETTI_RIGHT =
        new AssetDescriptor<>(AssetPaths.CONFETTI_RIGHT, ParticleEffect.class);

    public static final AssetDescriptor<Sound> POP_SOUND =
        new AssetDescriptor<>(AssetPaths.POP_SOUND, Sound.class);

    public static final AssetDescriptor<Sound> PICK_SOUND =
        new AssetDescriptor<>(AssetPaths.PICK_SOUND, Sound.class);

    public static final AssetDescriptor<Music> GAME_MUSIC =
        new AssetDescriptor<>(AssetPaths.GAME_MUSIC, Music.class);

    public static final AssetDescriptor<Music> MENU_MUSIC =
        new AssetDescriptor<>(AssetPaths.MENU_MUSIC, Music.class);


    private AssetDescriptors() {
    }
}
