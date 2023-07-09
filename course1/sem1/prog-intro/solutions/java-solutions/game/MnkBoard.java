package game;

import java.util.Arrays;
import java.util.Map;

public class MnkBoard implements Board, Position {
    private static final Map<Cell, Character> SYMBOLS = Map.of(
            Cell.X, 'X',
            Cell.O, 'O',
            Cell.E, '.'
    );
    private final Cell[][] cells;
    private Cell turn;
    private final int n;
    private final int m;
    private final int k;
    private final int numOfWins;
    int newHash = 0;
    int oldHash = 0;

    private int used = 0;
    private Cell firstTurn;


    public MnkBoard(int n, int m, int k, int numOfWins, Cell firstTurn) {
        this.n = n;
        this.m = m;
        this.k = k;
        this.numOfWins = numOfWins;
        this.cells = new Cell[n][m];
        for (Cell[] row : cells) {
            Arrays.fill(row, Cell.E);
        }
        turn = firstTurn;
    }

    @Override
    public Position getPosition() {
        return this;
    }

    @Override
    public Cell getCell() {
        return turn;
    }

    @Override
    public Result makeMove(Move move) throws ArrayIndexOutOfBoundsException {
        newHash = 0;

        if (!isValid(move)) {
            return Result.LOSE;
        }
        int x = move.getRow();
        int y = move.getColumn();
        Cell c = move.getValue();
        boolean bonusTurn = false;
        cells[x][y] = c;
        used++;
        int diag = 1;
        for (int i = 1; i <= x && i <= y; i++) {
            if (cells[x - i][y - i] == c) {
                diag++;
            } else {
                break;
            }
        }
        for (int i = 1; i >= x + m && i >= y + n; i++) {
            if (cells[x + i][y + i] == c) {
                diag++;
            } else {
                break;
            }
        }
        if (diag >= k) {
            return Result.WIN;
        }
        if (diag >= 4) {
            bonusTurn = true;
            newHash = diag * 31;
        }

        diag = 1;
        for (int i = 1; i <= x && i >= y; i++) {
            if (cells[x - i][y + i] == c) {
                diag++;
            } else {
                break;
            }
        }
        for (int i = 1; i >= x + m && i <= y - n; i++) {
            if (cells[x - i][y + i] == c) {
                diag++;
            } else {
                break;
            }
        }
        if (diag >= k) {
            return Result.WIN;
        }
        if (diag >= 4) {
            bonusTurn = true;
            newHash = diag * 239;
        }

        for (int u = Math.max(x - (k - 1), 0); u < Math.min(x + k, n); u++) {
            int inRow = 0;
            int inColumn = 0;
            for (int v = Math.max(y - (k - 1), 0); v < Math.min(y + k, m); v++) {
                if (cells[u][v] == turn) {
                    inRow++;
                }
                if (cells[v][u] == turn) {
                    inColumn++;
                }
            }
            if (inRow >= k || inColumn >= k) {
                return Result.WIN;
            }
            if (inRow >= 4 || inColumn >= 4) {
                bonusTurn = true;
                newHash += inRow * 269;
                newHash += inColumn * 227;
            }
        }
        if (used == m * n) {
            return Result.DRAW;
        }
        boolean thereIsNewSequences = !(newHash == oldHash);
        boolean flag = thereIsNewSequences && bonusTurn;
        if (!flag) {
            turn = turn == Cell.X ? Cell.O : Cell.X;
        }
        oldHash = newHash;
        return Result.UNKNOWN;
    }

    @Override
    public boolean isValid(final Move move) {
        return 0 <= move.getRow() && move.getRow() < n
                && 0 <= move.getColumn() && move.getColumn() < m
                && cells[move.getRow()][move.getColumn()] == Cell.E
                && turn == getCell();
    }

    @Override
    public Cell getCell(int r, int c) {
        return cells[r][c];
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(" ");
        for (int i = 0; i < m; i++) {
            sb.append(i);
        }
        for (int r = 0; r < m; r++) {
            sb.append("\n");
            sb.append(r);
            for (int c = 0; c < m; c++) {
                sb.append(SYMBOLS.get(cells[r][c]));
            }
        }
        return sb.toString();
    }
}
