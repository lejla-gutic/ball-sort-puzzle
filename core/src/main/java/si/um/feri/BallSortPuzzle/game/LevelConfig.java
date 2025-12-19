package si.um.feri.BallSortPuzzle.game;

public class LevelConfig {
    public final int tubeCount;
    public final int colorCount;
    public final int capacity;

    public LevelConfig(int tubeCount, int colorCount, int capacity) {
        this.tubeCount = tubeCount;
        this.colorCount = colorCount;
        this.capacity = capacity;
    }

    public static LevelConfig forLevel(LevelType level) {
        switch(level) {
            case EASY:
                return new LevelConfig(6, 4, 4);
            case MEDIUM:
                return new LevelConfig(8, 6, 4);
            case HARD:
                return new LevelConfig(10, 8, 4);
            default:
                throw new IllegalArgumentException("Unknown level");
        }
    }
}
