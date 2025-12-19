package si.um.feri.BallSortPuzzle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import si.um.feri.BallSortPuzzle.BallSortPuzzle;
import si.um.feri.BallSortPuzzle.GameManager;
import si.um.feri.BallSortPuzzle.assets.AssetDescriptors;

import java.util.List;

public class LeaderboardScreen extends ScreenAdapter {

    private final BallSortPuzzle game;
    private Stage stage;
    private Viewport viewport;

    private Skin skin;
    private TextureAtlas atlas;
    private BitmapFont font;
    private TextureRegion backgroundRegion;

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
        font = am.get(AssetDescriptors.FONT);
        backgroundRegion = atlas.findRegion("menu_background");

        stage.addActor(buildUI());
        Table backTable = new Table();
        backTable.setFillParent(true);
        backTable.bottom().right().pad(30);

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

        backTable.add(backRow);

        stage.addActor(backTable);


        stage.getRoot().getColor().a = 0f;
        stage.getRoot().addAction(Actions.fadeIn(0.6f));
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

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
        root.top().padTop(115);

        Image medalLeft = new Image(atlas.findRegion("medal"));
        Image medalRight = new Image(atlas.findRegion("medal"));

        Label title = new Label("LEADERBOARD", new Label.LabelStyle(font, Color.WHITE));
        title.setFontScale(2f);

        Table titleRow = new Table();
        titleRow.add(medalLeft).size(60);
        titleRow.add(title).padLeft(20).padRight(20);
        titleRow.add(medalRight).size(60);

        root.add(titleRow);
        root.row().padTop(40);

        Table scoreTable = new Table();
        scoreTable.top();
        scoreTable.defaults().pad(8).expandX();

        GameManager gm = new GameManager();
        List<GameManager.ScoreEntry> entries = gm.getScores();
        entries.sort((a, b) -> Integer.compare(b.score, a.score));

        int rank = 1;
        for (GameManager.ScoreEntry e : entries) {
            addRow(scoreTable, rank++, e.name, e.score);
        }

        scoreTable.invalidateHierarchy();

        ScrollPane.ScrollPaneStyle scrollStyle = new ScrollPane.ScrollPaneStyle();

        scrollStyle.vScroll = makeColoredDrawable(new Color(0f, 0f, 0f, 0.04f), 6, 6);

        scrollStyle.vScrollKnob = makeColoredDrawable(new Color(0.2f, 0.4f, 0.9f, 0.45f), 6, 40);

        ScrollPane scrollPane = new ScrollPane(scoreTable, scrollStyle);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setOverscroll(false, true);
        scrollPane.setScrollbarsOnTop(true);
        scrollPane.setForceScroll(false, true);

        root.add(scrollPane)
            .width(750)
            .height(420);

        return root;
    }

    private void addRow(Table table, int rank, String name, int score) {

        Table row = new Table();
        row.setBackground(skin.newDrawable("blue-card"));

        Label rankLabel = new Label(String.valueOf(rank), skin.get("white", Label.LabelStyle.class));
        rankLabel.setFontScale(1.3f);

        Image userIcon = new Image(atlas.findRegion("user"));
        Image trophy = new Image(atlas.findRegion("trophy"));

        Label nameLabel = new Label(name, skin.get("white", Label.LabelStyle.class));
        nameLabel.setFontScale(1.3f);

        Label scoreLabel = new Label(String.format("%,d", score), skin.get("white", Label.LabelStyle.class));
        scoreLabel.setFontScale(1.3f);

        row.add(rankLabel).width(60).left().padLeft(20);
        row.add(userIcon).size(45).padRight(10);
        row.add(nameLabel).expandX().left();
        row.add(trophy).size(45).padRight(10);
        row.add(scoreLabel).right().padRight(20);

        row.addAction(Actions.fadeIn(0.4f));

        table.add(row).width(750).height(70);
        table.row().padBottom(8);
    }

    private TextureRegionDrawable makeColoredDrawable(Color color, int width, int height) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();

        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(texture));
        drawable.setMinWidth(width);
        drawable.setMinHeight(height);
        return drawable;
    }
}
