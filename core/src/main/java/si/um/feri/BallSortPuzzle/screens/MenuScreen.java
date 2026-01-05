package si.um.feri.BallSortPuzzle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import si.um.feri.BallSortPuzzle.BallSortPuzzle;
import si.um.feri.BallSortPuzzle.assets.AssetDescriptors;

public class MenuScreen extends ScreenAdapter {

    private final BallSortPuzzle game;
    private Stage stage;
    private Viewport viewport;

    private Skin uiSkin;
    private TextureAtlas atlas;
    private Image backgroundImage;
    private Music menuMusic;
    private Preferences prefs;

    private static final float WORLD_WIDTH = 950f;
    private static final float WORLD_HEIGHT = 800f;


    public MenuScreen(BallSortPuzzle game) {
        this.game = game;
    }

    @Override
    public void show() {
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT);
        stage = new Stage(viewport, game.getBatch());
        Gdx.input.setInputProcessor(stage);

        AssetManager assetManager = game.getAssetManager();
        uiSkin = assetManager.get(AssetDescriptors.ORANGE_SKIN);
        atlas = assetManager.get(AssetDescriptors.UI_ATLAS);

        TextureRegion backgroundRegion = atlas.findRegion("menu_background");
        backgroundImage = new Image(backgroundRegion);
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);

        prefs = Gdx.app.getPreferences("settings");

        menuMusic = assetManager.get(AssetDescriptors.MENU_MUSIC);
        menuMusic.setLooping(true);

        boolean musicOn = prefs.getBoolean("music_on", true);

        if (musicOn) {
            menuMusic.setLooping(true);
            menuMusic.setVolume(0.25f);
            menuMusic.play();
        }
        else {
            menuMusic.pause();
        }

        stage.addActor(buildUI());

        stage.getRoot().getColor().a = 0;
        stage.getRoot().addAction(Actions.fadeIn(0.7f));
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

    private Table buildUI() {

        Table root = new Table();
        root.setFillParent(true);

        TextButton playBtn = new TextButton("PLAY", uiSkin, "menu-blue");
        TextButton leaderboardBtn = new TextButton("LEADERBOARD", uiSkin, "menu-blue");
        TextButton settingsBtn = new TextButton("SETTINGS", uiSkin, "menu-blue");
        TextButton quitBtn = new TextButton("QUIT", uiSkin, "menu-blue");

        playBtn.getLabel().setFontScale(1.4f);
        leaderboardBtn.getLabel().setFontScale(1.4f);
        settingsBtn.getLabel().setFontScale(1.4f);
        quitBtn.getLabel().setFontScale(1.4f);

        playBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game));
            }
        });

        leaderboardBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new LeaderboardScreen(game));
            }
        });

        settingsBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SettingsScreen(game));
            }
        });

        quitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        Table buttonTable = new Table();
        buttonTable.defaults().pad(20).fillX().width(500).height(50); ;

        buttonTable.add(playBtn).row();
        buttonTable.add(leaderboardBtn).row();
        buttonTable.add(settingsBtn).row();
        buttonTable.add(quitBtn).row();

        root.add(buttonTable).center();

        return root;
    }

    @Override
    public void hide() {
        if (menuMusic != null) menuMusic.stop();
    }

    @Override
    public void dispose() {
        if (menuMusic != null) {
            menuMusic.stop();
        }
        stage.dispose();
    }

}
