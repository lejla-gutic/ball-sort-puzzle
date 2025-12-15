package si.um.feri.BallSortPuzzle.game;

import java.util.Stack;
public class Tube {
    public final Stack<Ball> balls = new Stack<>();
    private final int capacity;

    public Tube(int capacity) {
        this.capacity = capacity;
    }
    public boolean isEmpty() {
        return balls.isEmpty();
    }
    public boolean isFull() {
        return balls.size() == capacity;
    }
    public Ball peekTop() {
        return balls.isEmpty() ? null : balls.peek();
    }
    public boolean canAdd(Ball ball) {
        if (isFull()) return false;
        if (isEmpty()) return true;
        return peekTop().getColor() == ball.getColor();
    }
    public void addBall(Ball ball) {
        if(!canAdd(ball)) {
            throw new IllegalStateException("Invalid move");
        }
        balls.push(ball);
    }
    public Ball removeTop() {
        if (balls.isEmpty()) {
            throw new IllegalStateException("Tube is empty");
        }
        return balls.pop();
    }
    public boolean isSolved() {
        if (balls.isEmpty()) return true;
        BallColor color = balls.peek().getColor();
        return balls.stream().allMatch(b -> b.getColor() == color) && balls.size() == capacity;
    }

    public Stack<Ball> getBalls() {
        return balls;
    }

}
