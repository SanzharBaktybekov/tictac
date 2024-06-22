package com.epam.rd.autocode.concurrenttictactoe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
     //   TicTacToe ticTacToe = new TicTacToeImpl();
     //   Player p1 = Player.createPlayer(ticTacToe, 'X', new PlayerStrategyImpl());
     //   Player p2 = Player.createPlayer(ticTacToe, 'X', new PlayerStrategyImpl());

        testCase("" +
                        "XOX\n" +
                        "OXO\n" +
                        "X  ",
                new PlayerStrategyImpl(), new PlayerStrategyImpl());
    }

    private static void testCase(String expected, PlayerStrategy... strategies) {
        final TicTacToe ticTacToe = TicTacToe.buildGame();
        final List<Thread> playerThreads = new ArrayList<>();
        final List<Character> marks = Arrays.asList('X', 'O');
        for (int i = 0; i < marks.size(); i++) {
            Player player = Player.createPlayer(ticTacToe, marks.get(i), strategies[i]);
            Thread thread = new Thread(player);
            playerThreads.add(thread);
        }
        playerThreads.forEach(Thread::start);
        playerThreads.forEach(ThrowingConsumer.silentConsumer(Thread::join));
        System.out.println(expected);
        System.out.println(tableString(ticTacToe.table()));
    }

    public static String tableString(char[][] table){
        return Arrays.stream(table)
                .map(String::new)
                .collect(Collectors.joining("\n"));
    }

    @FunctionalInterface
    public interface ThrowingConsumer<T, E extends Throwable> {
        void apply(T o) throws E;
        static <T, E extends Throwable> Consumer<T> silentConsumer(ThrowingConsumer<T, E> throwingConsumer) {
            return (param) -> {
                try {
                    throwingConsumer.apply(param);
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            };
        }
    }
}
