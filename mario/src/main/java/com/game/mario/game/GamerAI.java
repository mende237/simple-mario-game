package com.game.mario.game;

import com.game.mario.character.Mario;
import com.game.mario.item.GameItem;
import com.game.mario.util.Collision;

public class GamerAI implements Runnable {
    private final int REACTION_TIME = 500; // milliseconds
    private Stage stage;
    private int contextItemWidth;

    public GamerAI(Stage stage, int contextItemWidth) {
        this.stage = stage;
        this.contextItemWidth = contextItemWidth;
    }

    private GameItem[] getItemContext() {
        int nbrNearestItems;
        int cmpt = 0;
        Mario mario = stage.getMario();

        GameItem[] gameItems = new GameItem[this.contextItemWidth];
        int tabO[] = Collision.marioBetweenObject(stage.getGameItems(), 0, stage.getGameItems().size() - 1, 0,
                0,
                mario);

        int begin = tabO[0];
        int end = tabO[1];

        if (begin < 0 || end < 0) {
            nbrNearestItems = 1;

            if (begin < 0) {
                begin = 0;
            }

            if (end < 0) {
                end = 0;
            }
        } else {
            nbrNearestItems = end - begin + 1;
        }

        if (this.contextItemWidth <= nbrNearestItems) {
            for (int i = 0; i < this.contextItemWidth; i++) {
                gameItems[i] = stage.getGameItems().get(begin + i);
            }
        } else {
            if (this.contextItemWidth < stage.getGameItems().size()) {
                int rest = this.contextItemWidth - nbrNearestItems;
                int nbrLeft = rest / 2;
                int nbrRight = rest - nbrLeft;

                if (nbrLeft > begin) {
                    nbrRight += (nbrLeft - begin);
                    nbrLeft = begin;
                }

                for (int i = 0; i < nbrLeft; i++) {
                    gameItems[cmpt] = stage.getGameItems().get(i);
                    cmpt++;
                }

                for (int i = 0; i < nbrNearestItems; i++) {
                    gameItems[cmpt] = stage.getGameItems().get(begin + i);
                    cmpt++;
                }

                for (int i = end + 1; i < end + nbrRight + 1; i++) {
                    gameItems[cmpt] = stage.getGameItems().get(i);
                    cmpt++;
                }

            } else {
                for (int i = 0; i < this.stage.getGameItems().size(); i++) {
                    gameItems[i] = this.stage.getGameItems().get(i);
                }
            }
        }

        return gameItems;
    }

    @Override
    public void run() {

        while (true) {
            GameItem[] gameItems = getItemContext();

            System.out.print("Mario " + this.stage.getMario().getX());
            for (GameItem gameItem : gameItems) {
                if (gameItem != null) {
                    System.out.print(" Name " + gameItem.getName() + " x : " + gameItem.getX() + "  ");
                }
            }
            System.out.println();

            try {
                Thread.sleep(REACTION_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
