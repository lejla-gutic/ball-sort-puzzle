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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
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

    private static final Color MAROON = new Color(0.55f, 0.1f, 0.25f, 1f);

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

        TextButton backBtn = new TextButton(" Back", skin);
        backBtn.getLabel().setFontScale(1.1f);

        Image backIcon = new Image(skin.getDrawable("image-left"));

        Table backRow = new Table();
        backRow.add(backIcon).size(24).padRight(8);
        backRow.add(backBtn);

        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });

        root.add(backRow).left().padLeft(1);
        root.row().padTop(30);


        Table card = new Table();
        card.setBackground(skin.getDrawable("panel-peach"));
        card.pad(40);
        card.defaults().growX().pad(18);

        Label title = new Label("SETTINGS", skin);
        title.setFontScale(1.6f);
        title.setAlignment(Align.center);
        card.add(title).center().row();


        CheckBox musicToggle = createSwitch("music_on", true);
        CheckBox soundToggle = createSwitch("sound_on", true);
        CheckBox undoToggle  = createSwitch("undo_unlimited", true);

        Table settingsPanel = new Table();
        settingsPanel.setBackground(skin.getDrawable("panel-maroon"));
        settingsPanel.pad(25);
        settingsPanel.defaults().growX().pad(12);

        settingsPanel.add(createSettingRow("image-music-down", "Music", musicToggle)).row();
        settingsPanel.add(createSettingRow("image-sound-down", "Sound", soundToggle)).row();
        settingsPanel.add(createSettingRow("image-settings-down", "Unlimited Undo", undoToggle)).row();

        card.add(settingsPanel).growX().row();


        Label levelLabel = new Label("CHOOSE LEVEL", skin);
        levelLabel.setFontScale(1.2f);
        levelLabel.setColor(MAROON);
        card.add(levelLabel).padTop(25).left().row();

        Table levelRow = new Table();

        TextButton easyBtn = new TextButton("EASY", skin);
        TextButton mediumBtn = new TextButton("MEDIUM", skin);
        TextButton hardBtn = new TextButton("HARD", skin);

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

        levelRow.add(easyBtn).pad(8);
        levelRow.add(mediumBtn).pad(8);
        levelRow.add(hardBtn).pad(8);

        card.add(levelRow).left().row();

        root.add(card).center().width(560);

        card.setTransform(true);
        card.setScale(0.95f);
        card.addAction(Actions.scaleTo(1f, 1f, 0.25f));

        return root;
    }

    private void highlightLevelButton(String selected, TextButton easy, TextButton medium, TextButton hard) {
        easy.getLabel().setColor(Color.WHITE);
        medium.getLabel().setColor(Color.WHITE);
        hard.getLabel().setColor(Color.WHITE);

        if (selected.equals("Easy")) easy.getLabel().getColor().set(MAROON);
        if (selected.equals("Medium")) medium.getLabel().getColor().set(MAROON);
        if (selected.equals("Hard")) hard.getLabel().getColor().set(MAROON);
    }

    private Table createSettingRow(String iconName, String text, CheckBox toggle) {
        Table row = new Table();

        Image icon = new Image(skin.getDrawable(iconName));
        icon.setSize(28, 28);

        Label label = new Label(text, skin);
        label.setFontScale(1.1f);

        row.add(icon).size(28).padRight(20);
        row.add(label).expandX().left();
        row.add(toggle).right();

        return row;
    }


    private CheckBox createSwitch(String key, boolean def) {
        CheckBox.CheckBoxStyle style = new CheckBox.CheckBoxStyle();
        style.checkboxOn = skin.getDrawable("switch");
        style.checkboxOff = skin.getDrawable("switch-off");
        style.font = titleFont;
        CheckBox toggle = new CheckBox("", style);
        toggle.setChecked(prefs.getBoolean(key, def));

        toggle.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                prefs.putBoolean(key, toggle.isChecked());
                prefs.flush();
            }
        });

        return toggle;
    }



}
