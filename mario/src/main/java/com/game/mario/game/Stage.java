package com.game.mario.game;

import java.util.ArrayList;

import com.game.mario.character.Antagonist;
import com.game.mario.item.Coin;
import com.game.mario.item.GameItem;

public abstract class Stage extends Scene {
    protected ArrayList<GameItem> gameItems;
    protected ArrayList<Antagonist> antagonists;
    protected ArrayList<Coin> coins;
    protected int xMax;
    private int aiAction = -1; // Default to no action

    public Stage(int yFloor, int heightRoof, int xPos, int xMax, ArrayList<GameItem> gameItems,
            ArrayList<Antagonist> antagonists,
            ArrayList<Coin> coins) {
        super(yFloor, heightRoof, xPos);
        this.gameItems = gameItems;
        this.antagonists = antagonists;
        this.coins = coins;
        this.xMax = xMax;
    }

    public int getAiAction() {
        return aiAction;
    }

    public void setAiAction(int aiAction) {
        this.aiAction = aiAction;
    }

    public ArrayList<GameItem> getGameItems() {
        return gameItems;
    }

    public void setGameItems(ArrayList<GameItem> gameItems) {
        this.gameItems = gameItems;
    }

    public ArrayList<Antagonist> getAntagonists() {
        return antagonists;
    }

    public void setAntagonists(ArrayList<Antagonist> antagonists) {
        this.antagonists = antagonists;
    }

    public ArrayList<Coin> getCoins() {
        return coins;
    }

    public void setCoins(ArrayList<Coin> coins) {
        this.coins = coins;
    }
}
