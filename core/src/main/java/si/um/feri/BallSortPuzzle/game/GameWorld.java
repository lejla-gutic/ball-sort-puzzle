package si.um.feri.BallSortPuzzle.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class GameWorld {

    private final List<Tube> tubes = new ArrayList<>();
    private int moves = 0;
    private Stack<Move> undoStack = new Stack<>();
    private static final int MAX_UNDO = 3;



    public GameWorld(LevelConfig config) {
        initWorld(config);
    }

    private void initWorld(LevelConfig config) {
        List<Ball> balls = new ArrayList<>();

        // 1️⃣ Kreiraj lopte
        for (int i = 0; i < config.colorCount; i++) {
            BallColor color = BallColor.values()[i];
            for (int j = 0; j < config.capacity; j++) {
                balls.add(new Ball(color));
            }
        }

        Collections.shuffle(balls);

        // 2️⃣ Kreiraj SVE tube-ove
        for (int i = 0; i < config.tubeCount; i++) {
            tubes.add(new Tube(config.capacity));
        }

        // 3️⃣ Popuni SAMO prvih colorCount tube-ova
        int ballIndex = 0;

        for (int t = 0; t < config.colorCount; t++) {
            Tube tube = tubes.get(t);

            for (int c = 0; c < config.capacity; c++) {
                tube.addBallInitial(balls.get(ballIndex));
                ballIndex++;
            }
        }
    }


    public boolean move(int from, int to, boolean unlimitedUndo) {
        if (from == to) return false;

        Tube source = tubes.get(from);
        Tube dest = tubes.get(to);

        if (source.isEmpty()) return false;

        Ball ball = source.peekTop();
        if (!dest.canAdd(ball)) return false;

        dest.addBall(source.removeTop());
        moves++;

        // 🔹 SPREMI POTEZ
        undoStack.push(new Move(from, to));

        // 🔒 LIMIT UNDO AKO NIJE UNLIMITED
        if (!unlimitedUndo && undoStack.size() > MAX_UNDO) {
            undoStack.remove(0); // izbaci najstariji potez
        }

        return true;
    }

    public boolean undo() {
        if (undoStack.isEmpty()) return false;

        Move last = undoStack.pop();

        Tube source = tubes.get(last.to);   // gdje je lopta sada
        Tube target = tubes.get(last.from); // gdje se vraća

        if (source.isEmpty()) return false;

        Ball ball = source.removeTop();
        target.addBallForce(ball);

        moves = Math.max(0, moves - 1);
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
