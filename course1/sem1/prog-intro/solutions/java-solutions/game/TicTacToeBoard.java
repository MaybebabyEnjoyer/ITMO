package game;

import java.util.Arrays;
import java.util.Map;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class TicTacToeBoard implements Board, Position {
    private static final Map<Cell, Character> SYMBOLS = Map.of(
            Cell.X, 'X',
            Cell.O, 'O',
            Cell.E, '.'
    );

    private final Cell[][] cells;
    private Cell turn;
    private final int m = settings.m;
    private final int n = settings.n;
    private final int k = settings.k;

    public TicTacToeBoard() {
        this.cells = new Cell[m][n];
        for (Cell[] row : cells) {
            Arrays.fill(row, Cell.E);
        }
        turn = Cell.X;
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
    public Result makeMove(final Move move) {
        if (!isValid(move)) {
            return Result.LOSE;
        }

        cells[move.getRow()][move.getColumn()] = move.getValue();
        try {
            int inDiag1 = 0;
            int inDiag2 = 0;
            int empty = 0;
            int flag = 0;
            for (int u = 0; u < m; u++) {
                int inRow = 0;
                for (int v = 0; v < n; v++) {
                    if (cells[u][v] == turn) {
                        inRow++;
                        if (inRow >= k) return Result.WIN;
                        if (inRow >= 4 + flag) flag += 1;
                        else flag = 0;
                    } else inRow = 0;
                }
            }

            for (int u = 0; u < n; u++) {
                int inColumn = 0;
                for (int v = 0; v < m; v++) {
                    if (cells[v][u] == turn) {
                        inColumn++;
                        if (inColumn >= k) return Result.WIN;
                        if (inColumn >= 4 + flag) flag += 1;
                        else flag = 0;
                    } else inColumn = 0;
                    if (cells[v][u] == Cell.E) {
                        empty++;
                    }
                }
            }

            for (int u = 0; u < n; u++) {
                for (int v = 0; v < m; v++) {
                    inDiag1 = 0;
                    inDiag2 = 0;
                    for (int i = 0; i < k; i++) {
                        if (u + i < n && v + i < m) {
                            if (cells[v + i][u + i] == turn) inDiag1++;
                            else inDiag1 = 0;
                            if (inDiag1 >= k) return Result.WIN;
                            if (inDiag1 >= 4 + flag) flag += 1;
                            else flag = 0;
                        } if (u - i >= 0 && v + i < m) {
                            if (cells[v+i][u-i] == turn) inDiag2++;
                            else inDiag2 = 0;
                            if (inDiag2 >= k) return Result.WIN;
                            if (inDiag2 >= 4 + flag) flag += 1;
                            else flag = 0;
                        }
                    }
                }
            }
            if (flag < 1) turn = turn == Cell.X ? Cell.O : Cell.X;
            return Result.UNKNOWN;
        } catch (Throwable e) {
            System.out.println("Exception, you lose." + e);
            return Result.LOSE;
        }
    }

    @Override
    public boolean isValid(final Move move) {
        return 0 <= move.getRow() && move.getRow() < m
                && 0 <= move.getColumn() && move.getColumn() < n
                && cells[move.getRow()][move.getColumn()] == Cell.E
                && turn == getCell();
    }

    @Override
    public Cell getCell(final int r, final int c) {
        return cells[r][c];
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                sb.append(SYMBOLS.get(cells[r][c])).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
