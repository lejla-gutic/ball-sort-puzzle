package si.um.feri.BallSortPuzzle.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import si.um.feri.BallSortPuzzle.BallSortPuzzle;
import si.um.feri.BallSortPuzzle.GameManager;
import si.um.feri.BallSortPuzzle.assets.AssetDescriptors;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;


public class LeaderboardScreen extends ScreenAdapter {

    private final BallSortPuzzle game;

    private Stage stage;
    private Viewport viewport;

    private Skin skin;
    private TextureAtlas atlas;
    private BitmapFont titleFont;
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
        titleFont = am.get(AssetDescriptors.FONT);

        backgroundRegion = atlas.findRegion("menu_background");

        Table root = buildUI();
        stage.addActor(root);

        // Fade-in animation
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

        Table titleRow = new Table();

        Image medalLeft = new Image(atlas.findRegion("medal"));
        Image medalRight = new Image(atlas.findRegion("medal"));

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.WHITE);
        Label title = new Label("LEADERBOARD", titleStyle);
        title.setFontScale(2f);

        titleRow.add(medalLeft).size(60,60).padRight(20);
        titleRow.add(title);
        titleRow.add(medalRight).size(60,60).padLeft(20);

        root.add(titleRow).center();
        root.row().padTop(40);


        // score tables
        Table scoreTable = new Table();
        scoreTable.defaults().pad(8);

        GameManager gm = new GameManager();

        List<GameManager.ScoreEntry> entries = gm.getScores();

        entries.sort((a, b) ->Integer.compare(b.score, a.score));

        int rank = 1;
        for (GameManager.ScoreEntry e : entries) {
            addRow(scoreTable, rank, e.name, e.score);
            rank++;
        }

        root.add(scoreTable).width(750);

        return root;
    }

    private void addRow(Table table, int rank, String name, int score) {

        Table container = new Table();

        Image bottomBorder = new Image(skin.newDrawable("blue-card-dark"));
        bottomBorder.setHeight(4);

        Table row = new Table();
        row.setBackground(skin.newDrawable("blue-card"));  // main purple card

        Label rankLabel = new Label(String.valueOf(rank), skin.get("white", Label.LabelStyle.class));
        rankLabel.setFontScale(1.3f);

        Image userIcon = new Image(atlas.findRegion("user"));

        Label nameLabel = new Label(name, skin.get("white", Label.LabelStyle.class));
        nameLabel.setFontScale(1.3f);

        Image trophy = new Image(atlas.findRegion("trophy"));

        Label scoreLabel = new Label(String.format("%,d", score), skin.get("white", Label.LabelStyle.class));
        scoreLabel.setFontScale(1.3f);

        row.add(rankLabel).padLeft(20).width(60).left();
        row.add(userIcon).size(45,45).padRight(10);
        row.add(nameLabel).expandX().left();
        row.add(trophy).size(45,45).padRight(10);
        row.add(scoreLabel).right().padRight(20);

        row.addAction(Actions.sequence(
            Actions.alpha(0),
            Actions.fadeIn(0.4f)
        ));

        container.add(row).growX().height(70);
        container.row();
        container.add(bottomBorder).growX().height(4);
        container.row();

        table.add(container).width(750);
        table.row().padBottom(8);
    }

}
