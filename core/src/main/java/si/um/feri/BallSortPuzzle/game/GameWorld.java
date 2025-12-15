package si.um.feri.BallSortPuzzle.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
public class GameWorld {

    private final List<Tube> tubes = new ArrayList<>();
    private int moves = 0;

    public GameWorld(LevelConfig config) {
        initWorld(config);
    }

    private void initWorld(LevelConfig config) {
        List<Ball> balls = new ArrayList<>();

        for (int i = 0; i < config.colorCount; i++) {
            BallColor color = BallColor.values()[i];
            for (int j = 0; j < config.capacity; j++) {
                balls.add(new Ball(color));
            }
        }

        Collections.shuffle(balls);

        for (int i = 0; i < config.tubeCount; i++) {
            tubes.add(new Tube(config.capacity));
        }

        int tubeIndex = 0;
        for (Ball ball : balls) {
            while (tubeIndex < tubes.size() && tubes.get(tubeIndex).isFull()) {
                tubeIndex++;
            }
            tubes.get(tubeIndex).addBallInitial(ball);
        }
    }

    public boolean move(int from, int to) {
        if (from == to) return false;

        Tube source = tubes.get(from);
        Tube dest = tubes.get(to);

        if (source.isEmpty()) return false;

        Ball ball = source.peekTop();
        if (!dest.canAdd(ball)) return false;

        dest.addBall(source.removeTop());
        moves++;
        return true;
    }

    public boolean isSolved() {
        return tubes.stream().allMatch(Tube::isSolved);
    }


    public List<Tube> getTubes() {
        return tubes;
    }

    public int getMoves() {
        return moves;
    }
}
