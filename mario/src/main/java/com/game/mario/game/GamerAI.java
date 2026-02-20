package com.game.mario.game;

import com.game.mario.character.Antagonist;
import com.game.mario.character.Mario;
import com.game.mario.item.GameItem;
import com.game.mario.util.Collision;

public class GamerAI implements Runnable {
    private final int REACTION_TIME = 500; // milliseconds
    private Stage stage;
    private int contextItemWidth;
    private int contextAntogonistWidth;
    private WindowFilter windowFilter;

    public GamerAI(Stage stage, int contextItemWidth, int contextAntogonistWidth, WindowFilter windowFilter) {
        this.stage = stage;
        this.contextItemWidth = contextItemWidth;
        this.contextAntogonistWidth = contextAntogonistWidth;
        this.windowFilter = windowFilter;
    }

    private Antagonist[] getAntagonistContext() {
        if (this.contextAntogonistWidth <= 0)
            return null;

        Antagonist[] antagonists = new Antagonist[this.contextAntogonistWidth];

        Antagonist[] nearestAntagonist = Collision.aroundCharacter(stage.getAntagonists(), 0,
                stage.getAntagonists().size(), 0, stage.getMario());

        if (nearestAntagonist[0] == null || nearestAntagonist[1] == null) {
            // System.out.println("--------------------------------------------");
            if (this.contextAntogonistWidth <= this.stage.getAntagonists().size()) {
                if (nearestAntagonist[0] == null) {
                    for (int i = 0; i < this.contextAntogonistWidth; i++) {
                        antagonists[i] = this.stage.getAntagonists().get(i);
                    }
                } else {
                    int j = this.stage.getAntagonists().size() - 1;
                    for (int i = 0; i < this.contextAntogonistWidth; i++) {
                        antagonists[i] = this.stage.getAntagonists().get(j);
                        j--;
                    }
                }
            } else {
                for (int i = 0; i < this.stage.getAntagonists().size(); i++) {
                    antagonists[i] = this.stage.getAntagonists().get(i);
                }
            }
        } else {
            if (this.contextAntogonistWidth <= 2) {
                if (this.contextAntogonistWidth == 1) {
                    antagonists[0] = nearestAntagonist[1];
                } else {
                    antagonists[0] = nearestAntagonist[0];
                    antagonists[1] = nearestAntagonist[1];
                }
            } else {
                int nbrLeft = (this.contextAntogonistWidth - 2) / 2;
                int nbrRight = (this.contextAntogonistWidth - 2) - nbrLeft;

                // System.out.println("******* left " + nbrLeft + " *********** right " +
                // nbrRight);
                Antagonist current = (Antagonist) nearestAntagonist[0].getBehindCharacter();
                int i = 0;
                while (current != null && i < nbrLeft) {
                    antagonists[i] = current;
                    current = (Antagonist) current.getBehindCharacter();
                    i++;
                }

                int nbrFoundAntagonistLeft = i;

                antagonists[i] = nearestAntagonist[0];
                i++;
                nbrRight += (nbrLeft - nbrFoundAntagonistLeft);
                antagonists[i] = nearestAntagonist[1];
                i++;
                current = (Antagonist) nearestAntagonist[1].getFrontCharacter();
                int j = 0;
                while (current != null && j < nbrRight) {
                    antagonists[i] = current;
                    current = (Antagonist) current.getFrontCharacter();
                    j++;
                    i++;
                }
            }
        }

        return antagonists;
    }

    private GameItem[] getItemContext() {
        if (this.contextItemWidth <= 0) {
            return null;
        }

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

                int leftBound = Math.max(0, begin - nbrLeft);
                for (int i = leftBound; i < begin; i++) {
                    gameItems[cmpt] = stage.getGameItems().get(i);
                    cmpt++;
                }

                for (int i = 0; i < nbrNearestItems; i++) {
                    gameItems[cmpt] = stage.getGameItems().get(begin + i);
                    cmpt++;
                }

                int rightBound = Math.min(stage.getGameItems().size(), end + nbrRight + 1);
                for (int i = end + 1; i < rightBound; i++) {
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

    private int[] filter(int xTab[]) {
        int[] acceptedRange = new int[2];
        acceptedRange[0] = -1;
        acceptedRange[1] = -1;

        for (int i = 0; i < xTab.length; i++) {
            if (xTab[i] >= this.windowFilter.nim() && xTab[i] <= this.windowFilter.max()) {
                if (acceptedRange[0] == -1) {
                    acceptedRange[0] = i;
                }
                acceptedRange[1] = i;
            }
        }

        return acceptedRange;
    }

    @Override
    public void run() {

        while (true) {
            // GameItem[] gameItems = getItemContext();

            // System.out.print("Mario " + this.stage.getMario().getX());
            // for (GameItem gameItem : gameItems) {
            // if (gameItem != null) {
            // System.out.print(" Name " + gameItem.getName() + " x : " + gameItem.getX() +
            // " ");
            // }
            // }
            // System.out.println();

            Antagonist[] antagonists = getAntagonistContext();

            for (Antagonist antagonist : antagonists) {
                if (antagonist != null) {
                    System.out.print(antagonist.getName() + " x : " + antagonist.getX() + " ");
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
