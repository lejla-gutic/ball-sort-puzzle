package si.um.feri.BallSortPuzzle.lwjgl3;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class AssetPacker {
    private static final String RAW_ASSETS = "lwjgl3/assets-raw";
    private static final String PACKED_ASSETS = "core/assets";

    public static void main(String[] args) {
        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.maxWidth = 8192;
        settings.maxHeight = 8192;
        settings.combineSubdirectories = true;

        TexturePacker.process(
            settings,
            RAW_ASSETS,
            PACKED_ASSETS,
            "game"
        );
    }
}
