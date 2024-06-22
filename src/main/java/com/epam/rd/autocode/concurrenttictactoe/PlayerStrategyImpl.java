package com.epam.rd.autocode.concurrenttictactoe;


import java.util.stream.Stream;

public class PlayerStrategyImpl implements PlayerStrategy {
    @Override
    public Move computeMove(final char mark, final TicTacToe ticTacToe) {
        final char[][] table = ticTacToe.table();
        return Stream.of(
                        new Move(0, 0),
                        new Move(0, 1),
                        new Move(0, 2),
                        new Move(1, 0),
                        new Move(1, 1),
                        new Move(1, 2),
                        new Move(2, 0),
                        new Move(2, 1),
                        new Move(2, 2)
                )
                .filter(move -> table[move.row][move.column] == ' ')
                .findFirst().orElse(null);
    }
}
