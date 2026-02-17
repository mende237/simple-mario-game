package com.game.mario.item;

import javafx.scene.image.Image;

public class Pipe extends GameItem {

	public Pipe(int x, int y) {
		super(x, y, 65, 43, "pipe", new Image(Pipe.class.getResource("images/tuyauRouge.png").toExternalForm()));
	}

}
