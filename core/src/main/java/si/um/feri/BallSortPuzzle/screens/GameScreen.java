package si.um.feri.BallSortPuzzle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;

import si.um.feri.BallSortPuzzle.BallSortPuzzle;
import si.um.feri.BallSortPuzzle.GameManager;

public class GameScreen extends ScreenAdapter {
    private final BallSortPuzzle game;
    private Stage stage;

    public GameScreen(BallSortPuzzle game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

       // new GameManager().resetScores();


        // za test
        /*GameManager gm = new GameManager();
        gm.addScore("Lejla", 5000);
        gm.addScore("Hana", 1500);
        gm.addScore("Nejra", 3400);*/
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        stage.act(delta);
        stage.draw();
    }
}
