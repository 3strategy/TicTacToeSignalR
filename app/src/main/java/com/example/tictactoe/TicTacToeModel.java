package com.example.tictactoe;

public class TicTacToeModel {
    private String[][] board;
    private String currentPlayer;

    public TicTacToeModel() {
        board = new String[3][3];
        currentPlayer = "X";
        resetGame();
    }

    public void changePlayer() {
        if (currentPlayer.equals("X")) {
            currentPlayer = "O";
        } else {
            currentPlayer = "X";
        }
    }

    public boolean isLegal(int row, int col) {
        if (row < 0 || row > 2 || col < 0 || col > 2) {
            return false;
        }
        return board[row][col].isEmpty();
    }

    public void makeMove(int row, int col) {
        if (isLegal(row, col)) {
            board[row][col] = currentPlayer;
        }
    }

    // Explicitly set a move for a given player (used for remote moves)
    public boolean setMove(int row, int col, String player) {
        if (isLegal(row, col)) {
            board[row][col] = player;
            return true;
        }
        return false;
    }

    public boolean checkWin() {
        // TODO: Implement win checking logic
        return false;
    }

    public boolean isTie() {
        // TODO: Implement tie checking logic
        return false;
    }

    public void resetGame() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = "";
            }
        }
        currentPlayer = "X";
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }
}
