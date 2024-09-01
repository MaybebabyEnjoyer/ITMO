package ru.itmo.wp.model;

public class State {
    private final int size;
    private final Cell[][] cells;
    private GameResult phase;
    private boolean crossesMove;
    private int moveCount = 0;

    public State(int size) {
        this.size = size;
        this.cells = new Cell[size][size];
        this.phase = GameResult.RUNNING;
        this.crossesMove = true;
    }

    public int getSize() {
        return size;
    }

    public Cell[][] getCells() {
        return cells;
    }

    public GameResult getPhase() {
        return phase;
    }

    public boolean isCrossesMove() {
        return crossesMove;
    }

    public void makeMove(int row, int col) {
        if (!isValid(row, col)) {
            throw new IllegalArgumentException();
        }

        moveCount++;
        cells[row][col] = crossesMove ? Cell.X : Cell.O;
        if (checkWin(row, col)) {
            phase = crossesMove ? GameResult.WON_X : GameResult.WON_O;
            return;
        }

        if (checkDraw()) {
            phase = GameResult.DRAW;
            return;
        }

        crossesMove = !crossesMove;
        phase = GameResult.RUNNING;
    }

    public boolean isEnd() {
        return phase != GameResult.RUNNING;
    }

    private boolean isValid(int row, int col) {
        return 0 <= row && row < size
                && 0 <= col && col < size
                && cells[row][col] == null;
    }

    private boolean checkDraw() {
        return size * size == moveCount;
    }

    private boolean checkWin(int row, int col) {
        return check(row, col, 0, 1) || check(row, col, 1, 0)
                || check(row, col, 1, 1) || check(row, col, -1, 1);
    }

    private boolean check(int row, int col, int dx, int dy) {
        int count = 1;
        Cell currTurn = crossesMove ? Cell.X : Cell.O;
        for (int i = 1; i < size; i++) {
            if (row + dx * i < size && col + dy * i < size
                    && row + dx * i >= 0 && col + dy * i >= 0) {
                if (cells[row + dx * i][col + dy * i] != currTurn || count == size) {
                    break;
                }
                count++;
            }
        }
        for (int j = 1; j < size; j++) {
            if (row - dx * j >= 0 && col - dy * j >= 0
                    && row - dx * j < size && col - dy * j < size) {
                if (cells[row - dx * j][col - dy * j] != currTurn || count == size) {
                    break;
                }
                count++;
            }
        }
        return count == size;
    }
}
