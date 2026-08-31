package com.example.tictactoe.models;

/**
 * Stores the small amount of data published for one RTDB Tic-Tac-Toe room.
 */
public class GameRoom {

    /** The room name entered by its creator. */
    private String name = "";

    /** The Firebase user ID of the player using X. */
    private String playerX = "";

    /** The Firebase user ID of the player using O, or an empty string while waiting. */
    private String playerO = "";

    /** The complete semicolon-separated sequence of moves played in this room. */
    private String moves = "";

    /**
     * Creates an empty room object for Firebase snapshot conversion.
     */
    public GameRoom() {
    }

    /**
     * Creates a room with all values that will be written to Firebase.
     *
     * @param name room name entered by the creator
     * @param playerX Firebase user ID of player X
     * @param playerO Firebase user ID of player O, or an empty string
     * @param moves complete move sequence, or an empty string
     */
    public GameRoom(String name, String playerX, String playerO, String moves) {
        this.name = name;
        this.playerX = playerX;
        this.playerO = playerO;
        this.moves = moves;
    }

    /**
     * Returns the displayed room name.
     *
     * @return room name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the displayed room name when Firebase creates this object.
     *
     * @param name room name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the Firebase user ID assigned to X.
     *
     * @return player X user ID
     */
    public String getPlayerX() {
        return playerX;
    }

    /**
     * Sets the Firebase user ID assigned to X.
     *
     * @param playerX player X user ID
     */
    public void setPlayerX(String playerX) {
        this.playerX = playerX;
    }

    /**
     * Returns the Firebase user ID assigned to O.
     *
     * @return player O user ID, or an empty string while waiting
     */
    public String getPlayerO() {
        return playerO;
    }

    /**
     * Sets the Firebase user ID assigned to O.
     *
     * @param playerO player O user ID
     */
    public void setPlayerO(String playerO) {
        this.playerO = playerO;
    }

    /**
     * Returns the complete move sequence.
     *
     * @return semicolon-separated move sequence
     */
    public String getMoves() {
        return moves;
    }

    /**
     * Sets the complete move sequence.
     *
     * @param moves semicolon-separated move sequence
     */
    public void setMoves(String moves) {
        this.moves = moves;
    }
}
