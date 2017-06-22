package com.example.lluismontabes.gameofboatsandcards.Model;

/**
 * Created by Lous on 22/06/2017.
 */

public class ServerMessage {

    public static final int MESSAGE_ERROR = -1;
    public static final int MESSAGE_NONE = 0;

    public static final int MESSAGE_SENDING_PLAYER1READY = 1;
    public static final int MESSAGE_SENDING_PLAYER2READY = 2;

    public static final int MESSAGE_SENDING_SCORE = 3;

    public static final int MESSAGE_SENDING_PLAYER1POS = 4;
    public static final int MESSAGE_SENDING_PLAYER2POS = 5;

    public static final int MESSAGE_SENDING_PLAYER1ANGLE = 6;
    public static final int MESSAGE_SENDING_PLAYER2ANGLE = 7;

    public static final int MESSAGE_SENDING_PLAYER1EVENT = 8;
    public static final int MESSAGE_SENDING_PLAYER2EVENT = 9;

    public static final int MESSAGE_REQUESTING_PLAYER1READY = 10;
    public static final int MESSAGE_REQUESTING_PLAYER2READY = 11;

    public static final int MESSAGE_REQUESTING_SCORE = 12;

    public static final int MESSAGE_REQUESTING_PLAYER1POS = 13;
    public static final int MESSAGE_REQUESTING_PLAYER2POS = 14;

    public static final int MESSAGE_REQUESTING_PLAYER1ANGLE = 15;
    public static final int MESSAGE_REQUESTING_PLAYER2ANGLE = 16;

    public static final int MESSAGE_REQUESTING_PLAYER1EVENT = 17;
    public static final int MESSAGE_REQUESTING_PLAYER2EVENT = 18;

    public static final int MESSAGE_CREATED_MATCH = 100;
    public static final int MESSAGE_RESTORE_MATCH = 101;

}
