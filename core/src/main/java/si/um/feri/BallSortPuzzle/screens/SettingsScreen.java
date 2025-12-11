package si.um.feri.BallSortPuzzle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import si.um.feri.BallSortPuzzle.BallSortPuzzle;
import si.um.feri.BallSortPuzzle.assets.AssetDescriptors;

public class SettingsScreen extends ScreenAdapter {

    private final BallSortPuzzle game;

    private Stage stage;
    private Viewport viewport;

    private Skin skin;
    private TextureAtlas atlas;
    private BitmapFont titleFont;
    private TextureRegion backgroundRegion;

    private Preferences prefs;

    public SettingsScreen(BallSortPuzzle game) {
        this.game = game;
    }

    @Override
    public void show() {

        viewport = new FitViewport(950, 800);
        stage = new Stage(viewport, game.getBatch());
        Gdx.input.setInputProcessor(stage);

        AssetManager am = game.getAssetManager();

        skin = am.get(AssetDescriptors.ORANGE_SKIN);
        atlas = am.get(AssetDescriptors.UI_ATLAS);
        titleFont = am.get(AssetDescriptors.FONT);

        prefs = Gdx.app.getPreferences("settings");

        backgroundRegion = atlas.findRegion("menu_background");

        Table root = buildUI();
        stage.addActor(root);

        // fade-in animacija
        stage.getRoot().getColor().a = 0f;
        stage.getRoot().addAction(Actions.fadeIn(0.6f));
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0,0,0,1);

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
    }

    private Table buildUI() {

        Table root = new Table();
        root.setFillParent(true);
        root.top().padTop(70);

        //// BACK BUTTON
        TextButton backBtn = new TextButton(" BACK", skin);
        backBtn.addListener(e -> {
            game.setScreen(new MenuScreen(game));
            return true;
        });

        root.add(backBtn).left().padLeft(40);
        root.row().padTop(40);

        //// TITLE
        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.WHITE);
        Label title = new Label("SETTINGS", titleStyle);
        title.setFontScale(2f);
        root.add(title).center();
        root.row().padTop(50);

        //// MAIN CONTENT TABLE
        Table settingsTable = new Table();
        settingsTable.defaults().pad(20);

        // MUSIC toggle
        CheckBox musicToggle = new CheckBox(" Music", skin);
        musicToggle.setChecked(prefs.getBoolean("music_on", true));
        settingsTable.add(musicToggle).left().row();

        // SOUND toggle
        CheckBox soundToggle = new CheckBox(" Sound", skin);
        soundToggle.setChecked(prefs.getBoolean("sound_on", true));
        settingsTable.add(soundToggle).left().row();

        // UNDO toggle
        CheckBox undoToggle = new CheckBox(" Unlimited Undo", skin);
        undoToggle.setChecked(prefs.getBoolean("undo_unlimited", true));
        settingsTable.add(undoToggle).left().row();

        // LEVEL ROW
        Label levelLabel = new Label("Start Level:", skin.get("white", Label.LabelStyle.class));
        levelLabel.setFontScale(1.2f);

        Table levelRow = new Table();
        TextButton easyBtn = new TextButton("EASY", skin);
        TextButton mediumBtn = new TextButton("MEDIUM", skin);
        TextButton hardBtn = new TextButton("HARD", skin);

        // highlight selected
        highlightLevelButton(prefs.getString("start_level", "Easy"), easyBtn, mediumBtn, hardBtn);

        easyBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                prefs.putString("start_level", "Easy");
                prefs.flush();
                highlightLevelButton("Easy", easyBtn, mediumBtn, hardBtn);
            }
        });

        mediumBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                prefs.putString("start_level", "Medium");
                prefs.flush();
                highlightLevelButton("Medium", easyBtn, mediumBtn, hardBtn);
            }
        });

        hardBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                prefs.putString("start_level", "Hard");
                prefs.flush();
                highlightLevelButton("Hard", easyBtn, mediumBtn, hardBtn);
            }
        });

        levelRow.add(easyBtn).padRight(10);
        levelRow.add(mediumBtn).padRight(10);
        levelRow.add(hardBtn);

        settingsTable.add(levelLabel).left().row();
        settingsTable.add(levelRow).left().row();

        // ===== SAVE PREFERENCES FOR TOGGLES =====
        musicToggle.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                prefs.putBoolean("music_on", musicToggle.isChecked());
                prefs.flush();
            }
        });

        soundToggle.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                prefs.putBoolean("sound_on", soundToggle.isChecked());
                prefs.flush();
            }
        });

        undoToggle.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                prefs.putBoolean("undo_unlimited", undoToggle.isChecked());
                prefs.flush();
            }
        });

        root.add(settingsTable).center();

        return root;
    }

    private void highlightLevelButton(String selected, TextButton easy, TextButton medium, TextButton hard) {
        // reset boje
        easy.getLabel().setColor(Color.WHITE);
        medium.getLabel().setColor(Color.WHITE);
        hard.getLabel().setColor(Color.WHITE);

        if (selected.equals("Easy")) easy.getLabel().setColor(Color.YELLOW);
        if (selected.equals("Medium")) medium.getLabel().setColor(Color.YELLOW);
        if (selected.equals("Hard")) hard.getLabel().setColor(Color.YELLOW);
    }
}
