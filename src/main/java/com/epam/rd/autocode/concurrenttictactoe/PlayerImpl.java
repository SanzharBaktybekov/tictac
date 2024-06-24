package com.epam.rd.autocode.concurrenttictactoe;

import java.util.Arrays;
import java.util.stream.IntStream;

public class PlayerImpl implements Player {
    private final TicTacToe ticTacToe;
    private final char mark;
    private final PlayerStrategy playerStrategy;

    public PlayerImpl(TicTacToe ticTacToe, char mark, PlayerStrategy playerStrategy) {
        this.ticTacToe = ticTacToe;
        this.mark = mark;
        this.playerStrategy = playerStrategy;
    }

    private boolean gameIsOver(TicTacToe ticTacToe) {
        char[][] table = ticTacToe.table();
        boolean rowWin = Arrays.stream(table)
                .anyMatch(row -> row[0] == row[1] && row[1] == row[2] && row[0] != ' ');

        boolean colWin = IntStream.range(0, 3)
                .anyMatch(col -> table[0][col] == table[1][col] && table[1][col] == table[2][col] && table[0][col] != ' ');

        boolean diagWin = (table[0][0] == table[1][1] && table[1][1] == table[2][2] && table[0][0] != ' ') ||
                (table[0][2] == table[1][1] && table[1][1] == table[2][0] && table[0][2] != ' ');

        boolean noEmptyCells = Arrays.stream(table)
                .flatMapToInt(row -> new String(row).chars())
                .noneMatch(cell -> cell == ' ');

        return rowWin || colWin || diagWin || noEmptyCells;
    }

    private boolean IWillWait(char mark, TicTacToe ticTacToe) {
        return ((ticTacToe.lastMark() == ' ' && mark == 'O') || (mark == ticTacToe.lastMark()));
    }

    @Override
    public void run() {
        while (!gameIsOver(ticTacToe)) {
            synchronized (Player.class) {
                try {
                    while (IWillWait(mark, ticTacToe)) {
                        Player.class.wait();
                    }
                    if (gameIsOver(ticTacToe)) {
                        return;
                    }
                    Move move = playerStrategy.computeMove(mark, ticTacToe);
                    if (move != null) {
                        ticTacToe.setMark(move.row, move.column, mark);
                        Player.class.notify();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
            }
        }
    }
}