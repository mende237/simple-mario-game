package com.game.mario.game;

import com.game.mario.character.Antagonist;
import com.game.mario.character.Mario;
import com.game.mario.item.GameItem;
import com.game.mario.util.Collision;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import proto.Data;
import proto.GameServiceGrpc;

import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;

public class GamerAI implements Runnable {
    private final int REACTION_TIME = 16; // milliseconds
    private Stage stage;
    private int contextItemWidth;
    private int contextAntogonistWidth;
    private WindowFilter windowFilter;

    private final ManagedChannel channel;
    private final GameServiceGrpc.GameServiceBlockingStub blockingStub;

    public GamerAI(Stage stage, int contextItemWidth, int contextAntogonistWidth, WindowFilter windowFilter) {
        this.stage = stage;
        this.contextItemWidth = contextItemWidth;
        this.contextAntogonistWidth = contextAntogonistWidth;
        this.windowFilter = windowFilter;

        // Initialize gRPC client
        this.channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext() // Use plaintext for local testing
                .build();
        this.blockingStub = GameServiceGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
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

    private void filterAntogonist(Antagonist antagonists[]) {

        for (int i = 0; i < antagonists.length; i++) {
            if (!(antagonists[i].getX() >= this.windowFilter.nim()
                    && antagonists[i].getX() <= this.windowFilter.max())) {
                antagonists[i] = null;
            }
        }
    }

    private void filterItem(GameItem gameItems[]) {

        for (int i = 0; i < gameItems.length; i++) {
            if (!(gameItems[i].getX() >= this.windowFilter.nim()
                    && gameItems[i].getX() <= this.windowFilter.max())) {
                gameItems[i] = null;
            }
        }
    }

    @Override
    public void run() {

        while (true) {
            Antagonist[] antagonists = getAntagonistContext();
            GameItem[] gameItems = getItemContext();

            filterAntogonist(antagonists);
            filterItem(gameItems);

            // Build GameData
            Mario mario = stage.getMario();
            Data.Mario marioObject = Data.Mario.newBuilder()
                    .setX(mario.getX())
                    .setY(mario.getY())
                    .setHeight(mario.getHeight())
                    .setWidth(mario.getWidth())
                    .setState(mario.getState().name())
                    .build();

            List<Data.Antagonist> protoAntagonists = new ArrayList<>();
            if (antagonists != null) {
                for (Antagonist ant : antagonists) {
                    if (ant != null) {
                        protoAntagonists.add(Data.Antagonist.newBuilder()
                                .setX(ant.getX())
                                .setY(ant.getY())
                                .setHeight(ant.getHeight())
                                .setWidth(ant.getWidth())
                                .setSpeed(0) // Assuming speed is not directly available or 0 for now
                                .setName(ant.getName() != null ? ant.getName() : "unknown")
                                .setIsdead(!ant.isLiving()) // Assuming isLiving means not dead
                                .build());
                    }
                }
            }

            List<Data.Item> protoItems = new ArrayList<>();
            if (gameItems != null) {
                for (GameItem item : gameItems) {
                    if (item != null) {
                        protoItems.add(Data.Item.newBuilder()
                                .setX(item.getX())
                                .setY(item.getY())
                                .setHeight(item.getHeight())
                                .setWidth(item.getWidth())
                                .setName(item.getName() != null ? item.getName() : "unknown")
                                .build());
                    }
                }
            }

            Data.GameData gameData = Data.GameData.newBuilder()
                    .setMario(marioObject)
                    .setFloorLevel(stage.getYFloor())
                    .setAntagonistContextWidth(contextAntogonistWidth)
                    .setItemContextWidth(contextItemWidth)
                    .addAllAntagonists(protoAntagonists)
                    .addAllItems(protoItems)
                    .build();

            // Send GameData and get Action
            long startTime = System.currentTimeMillis();
            try {
                Data.Action action = blockingStub.getAction(gameData);
                long endTime = System.currentTimeMillis();
                System.out.println("Received action from Python server: " + action.getAction() + " in "
                        + (endTime - startTime) + "ms");
                stage.setAiAction(action.getAction()); // Set the AI's action in the Stage
            } catch (Exception e) {
                System.err.println("gRPC call failed: " + e.getMessage());
            }

            // for (Antagonist antagonist : antagonists) {
            // if (antagonist != null) {
            // System.out.print(antagonist.getName() + " x : " + antagonist.getX() + " ");
            // }
            // }

            // System.out.println();

            try {
                Thread.sleep(REACTION_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
