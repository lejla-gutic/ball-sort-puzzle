package si.um.feri.BallSortPuzzle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

import java.util.ArrayList;
import java.util.List;
public class GameManager {
    private static final String SCORE_FILE = "scores.json";
    private final Json json;
    private List<ScoreEntry> scores;

    public GameManager() {
        json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);

        loadScores();
    }

    public static class ScoreEntry {
        public String name;
        public int score;

        public ScoreEntry() {}

        public ScoreEntry(String name, int score) {
            this.name = name;
            this.score = score;
        }
    }

    private void loadScores() {
        FileHandle file = Gdx.files.local(SCORE_FILE);

        if (!file.exists()) {
            scores = new ArrayList<>();
            saveScores();
        }
        else {
            scores = json.fromJson(ArrayList.class, ScoreEntry.class, file);
        }
    }

    private void saveScores() {
        FileHandle file = Gdx.files.local(SCORE_FILE);
        file.writeString(json.prettyPrint(scores), false);
    }

    public void addScore(String name, int score) {
        scores.add(new ScoreEntry(name, score));
        saveScores();
    }

    public void resetScores() {
        scores.clear();
        saveScores();
    }


    public List<ScoreEntry> getScores() {
        return scores;
    }

}
