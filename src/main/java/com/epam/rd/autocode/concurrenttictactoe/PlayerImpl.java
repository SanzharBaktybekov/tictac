package com.epam.rd.autocode.concurrenttictactoe;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.epam.rd.autocode.concurrenttictactoe.Main.tableString;

public class PlayerImpl implements Player {
    private static final Lock lock = new ReentrantLock();
    private static final Condition condition = lock.newCondition();
    private static int currentPlayerIndex = 0;
    private static int playerCounter = 0;
    private final int playerIndex;
    private final TicTacToe ticTacToe;
    private final char mark;
    private final PlayerStrategy playerStrategy;
    private static final AtomicBoolean gameOver = new AtomicBoolean(false);

    public PlayerImpl(TicTacToe ticTacToe, char mark, PlayerStrategy playerStrategy) {
        this.ticTacToe = ticTacToe;
        this.mark = mark;
        this.playerStrategy = playerStrategy;
        synchronized (PlayerImpl.class) {
            this.playerIndex = playerCounter++;
        }
    }

    private boolean gameIsOver(TicTacToe ticTacToe) {
        char[][] table = ticTacToe.table();
        for (int row = 0; row < 3; row++) {
            if (table[row][0] == table[row][1] && table[row][1] == table[row][2] && table[row][0] != ' ') {
                return true;
            }
        }
        for (int col = 0; col < 3; col++) {
            if (table[0][col] == table[1][col] && table[1][col] == table[2][col] && table[0][col] != ' ') {
                return true;
            }
        }
        if (table[0][0] == table[1][1] && table[1][1] == table[2][2] && table[0][0] != ' ') {
            return true;
        }
        if (table[0][2] == table[1][1] && table[1][1] == table[2][0] && table[0][2] != ' ') {
            return true;
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (table[row][col] == ' ') {
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    public void run() {
        while (!gameIsOver(ticTacToe)) {
            lock.lock();
            try {
                while (mark == ticTacToe.lastMark() && !gameOver.get()) {
                    condition.await();
                }
                if (gameOver.get()) {
                    return;
                }
                Move move = playerStrategy.computeMove(mark, ticTacToe);
                if (move == null) {
                    gameOver.set(true);
                    condition.signalAll();
                } else {
                    ticTacToe.setMark(move.row, move.column, mark);
                    if (gameIsOver(ticTacToe)) {
                        gameOver.set(true);
                        System.out.println("Game over! Player " + mark + " wins!");
                    }
                    condition.signalAll();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }
    }
}