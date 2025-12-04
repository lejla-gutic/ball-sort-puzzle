package si.um.feri.BallSortPuzzle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import si.um.feri.BallSortPuzzle.BallSortPuzzle;
import si.um.feri.BallSortPuzzle.assets.AssetDescriptors;

public class LeaderboardScreen extends ScreenAdapter {

    private final BallSortPuzzle game;

    private Stage stage;
    private Viewport viewport;

    private Skin skin;
    private TextureAtlas atlas;
    private TextureRegion background;
    private BitmapFont titleFont;

    public LeaderboardScreen(BallSortPuzzle game) {
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

        background = atlas.findRegion("background");

        stage.addActor(buildUI());

        stage.getRoot().getColor().a = 0f;
        stage.getRoot().addAction(Actions.fadeIn(0.7f));
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

    private Table buildUI() {

        Table root = new Table();
        root.setFillParent(true);

        TextButton backBtn = new TextButton(" BACK", skin);
        backBtn.addListener(e -> {
            game.setScreen(new MenuScreen(game));
            return true;
        });

        // Put icon + text together
        Table backTable = new Table();
        backTable.add(backBtn);

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.WHITE);
        Label title = new Label("Leaderboard", titleStyle);
        title.setFontScale(2f);

        Table scoreTable = new Table(skin);
        scoreTable.defaults().pad(10).padLeft(30).padRight(30);

        // Example hard-coded results
        addRow(scoreTable, "Lejla", "1200");
        addRow(scoreTable, "Nejra", "850");
        addRow(scoreTable, "Emina", "780");
        addRow(scoreTable, "Ajla", "600");

        root.top().padTop(100);

        root.add(backTable).left().padLeft(40);
        root.row().padTop(80);

        root.add(title).center();
        root.row().padTop(60);

        root.add(scoreTable).center();
        root.row();

        return root;
    }

    private void addRow(Table table, String name, String score) {
        Label nameLabel = new Label(name, skin);
        Label scoreLabel = new Label(score, skin);

        nameLabel.setFontScale(1.4f);
        scoreLabel.setFontScale(1.4f);

        table.add(nameLabel).expandX().left();
        table.add(scoreLabel).right();
        table.row();
    }
}
