package com.game.mario.game;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import com.game.mario.character.Mario;

public abstract class Scene extends Canvas {
	// *************************** property *********************************** //
	protected Mario mario;

	private int yFloor;
	private int heightRoof;
	private int dx = 0;
	private int xPos;

	// ************************************* constructor
	// *************************************** //
	public Scene(int yFloor, int heightRoof, int xPos) {
		super(800, 600); // Set the Canvas size (width x height)
		this.xPos = xPos;
		this.yFloor = yFloor;
		this.heightRoof = heightRoof;
	}

	// **************************************** getter *****************************
	// //
	public int getxPos() {
		return xPos;
	}

	public int getDx() {
		return this.dx;
	}

	public int getYFloor() {
		return this.yFloor;
	}

	public int getHeightRoof() {
		return this.heightRoof;
	}

	public Mario getMario() {
		return this.mario;
	}

	// **************************************** setter
	// ******************************* //
	public void setxPos(int xPos) {
		this.xPos = xPos;
	}

	public void setDx(int x) {
		this.dx = x;
	}

	public void setYFloor(int yFloor) {
		this.yFloor = yFloor;
	}

	public void setHeightRoof(int heightRoof) {
		this.heightRoof = heightRoof;
	}

	// **************************************** methods
	// ******************************** //
	public abstract void backgroundDisplacement();

	public abstract void restart(int position);

	// Paint method for rendering graphics
	public abstract void paint(GraphicsContext gc);
}