package si.um.feri.BallSortPuzzle.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
public class GameWorld {

    private final List<Tube> tubes = new ArrayList<>();
    private int selectedTube = -1;
    private int moves = 0;

    public GameWorld(LevelConfig config) {
        initWorld(config);
    }

    private void initWorld(LevelConfig config) {
        List<Ball> balls = new ArrayList<>();

        for (int i = 0; i < config.colorCount; i++) {
            for (int j = 0; j < config.capacity; j++) {
                balls.add(new Ball(BallColor.values()[i]));
            }
        }

        java.util.Collections.shuffle(balls, new Random());

        for (int i = 0; i < config.tubeCount; i++) {
            tubes.add(new Tube(config.capacity));
        }

        int index = 0;
        for (Ball ball : balls) {
            tubes.get(index % config.colorCount).addBall(ball);
            index++;
        }
    }

    public boolean move(int from, int to) {
        Tube source = tubes.get(from);
        Tube dest = tubes.get(to);

        if (source.isEmpty()) return false;

        Ball ball = source.peekTop();
        if (!dest.canAdd(ball)) return false;

        dest.addBall(source.removeTop());
        moves++;
        return true;
    }

    public boolean isSolved(List<Tube> tubes1) {
        return tubes1.stream().allMatch(Tube::isSolved);
    }

    public List<Tube> getTubes() {
        return tubes;
    }

    public int getMoves() {
        return moves;
    }
}
