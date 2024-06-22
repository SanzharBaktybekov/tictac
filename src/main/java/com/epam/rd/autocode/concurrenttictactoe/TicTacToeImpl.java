package com.epam.rd.autocode.concurrenttictactoe;

public class TicTacToeImpl implements TicTacToe {
    private final char[][] table = {
            {' ',' ',' '},
            {' ',' ',' '},
            {' ',' ',' '},
    };
    private char lastMark;
    @Override
    public void setMark(int x, int y, char mark) {
        if(table[x][y] == ' ') {
            table[x][y] = mark;
        } else {
            throw new IllegalArgumentException();
        }
        lastMark = mark;
    }
    @Override
    public char[][] table() {
        return clone(table);
    }
    @Override
    public char lastMark() {
        return lastMark;
    }

    private char[][] clone(char[][] original) {
        char[][] clone = new char[original.length][original[0].length];
        for(int i = 0; i < original.length; i++) {
            System.arraycopy(original[i], 0, clone[i], 0, original[0].length);
        }
        return clone;
    }
}
