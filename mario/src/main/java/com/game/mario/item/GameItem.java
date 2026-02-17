package com.game.mario.item;

import com.game.mario.App;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class GameItem {
	/****************************************
	 * property
	 *********************************************/
	private int height, width;
	private int x, y;
	protected Image imgObject;
	protected ImageView icoObject;
	protected String name;

	/***************************************
	 * constructor
	 ********************************************/
	public GameItem(int x, int y, int height, int width, String name) {
		this.height = height;
		this.width = width;
		this.x = x;
		this.y = y;
		this.name = name;
	}

	public GameItem(int x, int y, int height, int width, String name, Image imgObject) {
		this(x, y, height, width, name);
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

	public String getName() {
		return this.name;
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

	public void displacement() {
		if (App.scene.getxPos() != -1) {
			this.x = this.x - ((int) App.scene.getDx());
		}
	}

	/*******************************************
	 * methods
	 ********************************************/

}
