package com.game.mario.game;

import com.game.mario.character.Antagonist;
import com.game.mario.character.Mario;
import com.game.mario.item.GameItem;
import com.game.mario.util.Collision;
import com.game.mario.util.Config;
import com.game.mario.util.GameStateLogger;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import proto.Data;
import proto.GameServiceGrpc;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;

public class GamerAI implements Runnable {
    // The AI will react to the new game state after rendering of 4 frames
    // (4*4=16ms)
    private final int REACTION_TIME = (int) (Config.AI_REACTION_FREQUENCY * ((1 / (double) Config.FPS) * 1000)); // milliseconds.

    // the new game state
    private Stage stage;
    private int contextItemWidth;
    private int contextAntogonistWidth;
    private int contextCoinWidth;
    private WindowFilter windowFilter;

    private final ManagedChannel channel;
    private final GameServiceGrpc.GameServiceBlockingStub blockingStub;

    public GamerAI(Stage stage, int contextItemWidth, int contextCoinWidth, int contextAntogonistWidth,
            WindowFilter windowFilter) {
        this.stage = stage;
        this.contextItemWidth = contextItemWidth;
        this.contextCoinWidth = contextCoinWidth;
        this.contextAntogonistWidth = contextAntogonistWidth;
        this.windowFilter = windowFilter;

        // Initialize gRPC client
        this.channel = ManagedChannelBuilder.forAddress(Config.host, Config.port)
                .usePlaintext() // plaintext for local testing
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

    private GameItem[] getItemContext(ArrayList<? extends GameItem> items, int contextWidth) {
        if (contextWidth <= 0) {
            return null;
        }

        int nbrNearestItems;
        int cmpt = 0;
        Mario mario = stage.getMario();

        GameItem[] gameItems = new GameItem[contextWidth];
        int tabO[] = Collision.marioBetweenObject(items, 0, items.size() - 1, 0,
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

        if (contextWidth <= nbrNearestItems) {
            for (int i = 0; i < contextWidth; i++) {
                gameItems[i] = items.get(begin + i);
            }
        } else {
            if (contextWidth < items.size()) {
                int rest = contextWidth - nbrNearestItems;
                int nbrLeft = rest / 2;
                int nbrRight = rest - nbrLeft;

                if (nbrLeft > begin) {
                    nbrRight += (nbrLeft - begin);
                    nbrLeft = begin;
                }

                int leftBound = Math.max(0, begin - nbrLeft);
                for (int i = leftBound; i < begin; i++) {
                    gameItems[cmpt] = items.get(i);
                    cmpt++;
                }

                for (int i = 0; i < nbrNearestItems; i++) {
                    gameItems[cmpt] = items.get(begin + i);
                    cmpt++;
                }

                int rightBound = Math.min(items.size(), end + nbrRight + 1);
                for (int i = end + 1; i < rightBound; i++) {
                    gameItems[cmpt] = items.get(i);
                    cmpt++;
                }

            } else {
                for (int i = 0; i < items.size(); i++) {
                    gameItems[i] = items.get(i);
                }
            }
        }

        return gameItems;
    }

    private void filterAntogonist(Antagonist antagonists[]) {
        if (antagonists == null)
            return;

        for (int i = 0; i < antagonists.length; i++) {
            if (!(antagonists[i].getX() >= this.windowFilter.nim()
                    && antagonists[i].getX() <= this.windowFilter.max())) {
                antagonists[i] = null;
            }
        }
    }

    private void filterItem(GameItem gameItems[]) {
        if (gameItems == null)
            return;

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
            if (!stage.mario.isLiving() || GameManager.isInterupt()) {
                continue;
            }

            Antagonist[] antagonists = getAntagonistContext();
            GameItem[] gameItems = getItemContext(this.stage.getGameItems(), this.contextItemWidth);
            GameItem[] coins = getItemContext(this.stage.getCoins(), this.contextCoinWidth);

            filterAntogonist(antagonists);
            filterItem(gameItems);
            filterItem(coins);

            this.stage.mario.lockAllState();

            // Log Mario state to file
            GameStateLogger.logFullStateSnapshot(stage.getMario().getState());

            // Build GameData
            Mario mario = stage.getMario();
            Data.Mario marioObject = Data.Mario.newBuilder()
                    .setPosition(Data.Position
                            .newBuilder()
                            .setX(mario.getX())
                            .setY(mario.getY()))
                    .setDimensions(Data.Dimensions.newBuilder()
                            .setHeight(mario.getHeight())
                            .setWidth(mario.getWidth()))
                    .setNumberOfLive(mario.getNumberOfLive())
                    .putAllState(mario.getState().keySet().stream()
                            .collect(Collectors.toMap(state -> state.name(), state -> mario.getState().get(state)
                                    .getValue())))
                    .build();
            this.stage.mario.unlockAllState();

            this.stage.mario.marKAllStateAsRead();

            List<Data.Antagonist> protoAntagonists = new ArrayList<>();
            if (antagonists != null) {
                for (Antagonist ant : antagonists) {
                    if (ant != null) {
                        protoAntagonists.add(Data.Antagonist.newBuilder()
                                .setPosition(Data.Position.newBuilder()
                                        .setX(ant.getX())
                                        .setY(ant.getY()))
                                .setDimensions(Data.Dimensions.newBuilder()
                                        .setHeight(ant.getHeight())
                                        .setWidth(ant.getWidth()))
                                .setSpeed(ant.getBreakDuration())
                                .setName(ant.getName() != null ? ant.getName() : "unknown")
                                .setIsdead(!ant.isLiving())
                                .setIsZombie(ant.isZombie())
                                .build());
                    }
                }
            }

            List<Data.Item> protoItems = new ArrayList<>();
            if (gameItems != null) {
                for (GameItem item : gameItems) {
                    if (item != null) {
                        protoItems.add(Data.Item.newBuilder()
                                .setPosition(Data.Position.newBuilder()
                                        .setX(item.getX())
                                        .setY(item.getY()))
                                .setDimensions(Data.Dimensions.newBuilder()
                                        .setHeight(item.getHeight())
                                        .setWidth(item.getWidth()))
                                .setName(item.getName() != null ? item.getName() : "unknown")
                                .build());
                    }
                }
            }

            List<Data.Coin> protoCoins = new ArrayList<>();
            if (coins != null) {
                for (GameItem coin : coins) {
                    if (coin != null) {
                        protoCoins.add(Data.Coin.newBuilder()
                                .setPosition(Data.Position.newBuilder()
                                        .setX(coin.getX())
                                        .setY(coin.getY()))
                                .setDimensions(Data.Dimensions.newBuilder()
                                        .setHeight(coin.getHeight())
                                        .setWidth(coin.getWidth()))
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
                    .addAllCoins(protoCoins)
                    .build();

            // Send GameData and get Action
            long startTime = System.currentTimeMillis();
            try {
                Data.Action action = blockingStub.getAction(gameData);
                long endTime = System.currentTimeMillis();
                System.out.println("Received action from Python server: " +
                        action.getAction() + " in "
                        + (endTime - startTime) + "ms");
                stage.setAiAction(action.getAction()); // Set the AI's action in the Stage
            } catch (Exception e) {
                System.err.println("gRPC call failed: " + e.getMessage());
            }

            try {
                Thread.sleep(REACTION_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
