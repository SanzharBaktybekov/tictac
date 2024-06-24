package com.epam.rd.autocode.concurrenttictactoe;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Started!");
        TicTacToe ticTacToe = TicTacToe.buildGame();

        Player player1 = new PlayerImpl(ticTacToe, 'X', new PlayerStrategyImpl());
        Player player2 = new PlayerImpl(ticTacToe, 'O', new PlayerStrategyImpl());

        Thread thread = new Thread(player1);
        Thread thread2 = new Thread(player2);

        thread.start();
        thread2.start();

        thread.join();
        thread2.join();

        System.out.println(tableString(ticTacToe.table()));
    }
    public static String tableString(char[][] table){
        return Arrays.stream(table)
                .map(String::new)
                .collect(Collectors.joining("\n"));
    }
}