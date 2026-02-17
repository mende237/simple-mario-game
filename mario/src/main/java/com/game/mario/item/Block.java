package com.game.mario.item;

import javafx.scene.image.Image;

public class Block extends GameItem {

	public Block(int x, int y) {
		super(x, y, 30, 30, "block", new Image(Block.class.getResource("images/bloc.png").toExternalForm()));
	}

}
