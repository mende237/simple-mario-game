package com.game.mario.game;

import com.game.mario.character.Mario;
import com.game.mario.util.Collision;

public class GamerAI implements Runnable {
    private final int REACTION_TIME = 500; // milliseconds
    private Stage stage;
    private Mario mario;
    private int xMax;

    public GamerAI(Stage stage, Mario mario, int xMax) {
        this.stage = stage;
        this.mario = mario;
        this.xMax = xMax;
    }

    @Override
    public void run() {

        while (true) {
            int tabO[] = Collision.marioBetweenObject(stage.getGameItems(), 0, stage.getGameItems().size() - 1, 0,
                    0,
                    mario);

            System.out.println(
                    "x mario " + mario.getX() + " width " + mario.getWidth() + "  " + tabO[0] + " ----- "
                            + tabO[1]);

            try {
                Thread.sleep(REACTION_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
