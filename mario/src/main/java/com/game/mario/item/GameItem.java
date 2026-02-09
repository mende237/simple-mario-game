package com.game.mario.item;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import com.game.mario.App;
import com.game.mario.game.GameManager;

public class GameItem {
	/****************************************
	 * property
	 *********************************************/
	private int height, width;
	private int x, y;
	protected Image imgObject;
	protected ImageView icoObject;
	protected int sem;
	protected static int globalSem = 1;

	/***************************************
	 * constructor
	 ********************************************/
	public GameItem(int x, int y, int height, int width) {
		this.height = height;
		this.width = width;
		this.x = x;
		this.y = y;
	}

	public GameItem(int x, int y, int height, int width, Image imgObject) {
		this(x, y, height, width);
		icoObject = new ImageView(imgObject);
		this.imgObject = imgObject;
		icoObject.setX(x);
		icoObject.setY(y);
		icoObject.setFitWidth(width);
		icoObject.setFitHeight(height);
	}

	/*************************************
	 * getter
	 ****************************************************/
	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public int getY() {
		return y;
	}

	public int getX() {
		return x;
	}

	public Image getImgObject() {
		return imgObject;
	}

	public void setImageObject(Image img) {
		imgObject = img;
	}

	/***********************************
	 * setter
	 ******************************************************/
	public void setWidth(int width) {
		this.width = width;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	// public void displacement() {
	// if (App.scene.getxPos() != -1)
	// this.x = this.x - App.scene.getDx();
	// }

	public void displacement() {
		if (App.scene.getxPos() != -1) {
			// GameManager.DOWN();
			this.x = this.x - ((int) App.scene.getDx());
			// GameManager.UP();
		}
	}

	/*******************************************
	 * methods
	 ********************************************/
	public void DOWN() {
		while (this.sem - 1 < 0) {

		}
		this.sem = this.sem - 1;
	}

	public static int DOWNALL() {
		if (GameItem.globalSem > 0) {
			GameItem.globalSem = GameItem.globalSem - 1;
			return GameItem.globalSem;
		} else
			return -1;
	}

	public void UP() {
		this.sem = 1;
	}

	public static void UPALL() {
		GameItem.globalSem = 1;
	}
}
