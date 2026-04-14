package com.game.mario.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONObject;

public class Config {
    public static final int FPS = 250;
    public static final int AI_REACTION_FREQUENCY = 4; // the AI will react to the new game state after rendering of 4
                                                       // frames
    public static final int FRAME_STATE_VALIDITY = AI_REACTION_FREQUENCY + 1; // the state of
                                                                              // Mario validity
    // the AI has enough time to react to it
    public static final int MARIO_NUMBER_OF_LIVES = 3;
    public static final int X_MAX = 4100;
    public static final int Y_MAX = 293;

    public static final int MARIO_WALK_FREQUENCY = 50;
    public static final int MUSHROOM_WALK_FREQUENCY = 50;
    public static final int TURTLE_WALK_FREQUENCY = 100;

    public static final int MUSHROOM_THREAD_PAUSE = 20; // Duration in milli second
    public static final int TURTLE_THREAD_PAUSE = 50; // Duration in milli second

    public static int CONTEXT_ITEM_WIDTH;
    public static int CONTEXT_ANTAGONIST_WIDTH;
    public static int CONTEXT_COIN_WIDTH;
    public static int WINDOW_FILTER_MIN;
    public static int WINDOW_FILTER_MAX;

    public static String host;
    public static int port;

    static {
        try {
            String content = new String(Files.readAllBytes(Paths.get("../config/context.json")));
            JSONObject json = new JSONObject(content);
            CONTEXT_ITEM_WIDTH = json.getInt("contextItemWidth");
            CONTEXT_ANTAGONIST_WIDTH = json.getInt("contextAntogonistWidth");
            CONTEXT_COIN_WIDTH = json.getInt("contextCoinWidth");
            JSONObject windowFilter = json.getJSONObject("windowFilter");
            WINDOW_FILTER_MIN = windowFilter.getInt("min");
            WINDOW_FILTER_MAX = windowFilter.getInt("max");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            String content = new String(Files.readAllBytes(Paths.get("../config/server.json")));
            JSONObject json = new JSONObject(content);
            host = json.getString("host");
            port = json.getInt("port");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
