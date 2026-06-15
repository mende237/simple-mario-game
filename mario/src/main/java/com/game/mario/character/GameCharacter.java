package com.game.mario.character;

import com.game.mario.item.GameItem;
import com.game.mario.util.Axe;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public abstract class GameCharacter {

	private int height, width;// the height and the width of a character
	private int x, y; // the position of character
	private boolean walk; // verify if the character is immobile or not
	private boolean toRight; // is true when the character is turned in right position
	private int counter; // the displacement frequency
	private boolean living;
	protected ImageView icoCharacter;
	protected Image imgCharacter;
	protected int walkFrequency;

	public GameCharacter(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.height = height;
		this.width = width;
		this.toRight = false;
		this.walk = false;
	}

	// **************************************getter******************************************//
	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public boolean isWalk() {
		return walk;
	}

	public boolean isToRight() {
		return toRight;
	}

	public int getCounter() {
		return counter;
	}

	public void setLiving(boolean living) {
		this.living = living;
	}

	public Image getImgCharacter() {
		return imgCharacter;
	}

	public int getWalkFrequency() {
		return this.walkFrequency;
	}

	// **************************************setter****************************************//
	public boolean isLiving() {
		return living;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public void setToRight(boolean toRight) {
		this.toRight = toRight;
	}

	public void setWalk(boolean walk) {
		this.walk = walk;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setImgCharacter(Image imgCharacter) {
		this.imgCharacter = imgCharacter;
	}

	// *************************************methods****************************************//
	public abstract Image walk(String name, int frequency);

	// protected abstract void kill(Mario mario);

	public abstract Image die();

	public boolean near(GameItem gameItem) {
		if (this.getX() > gameItem.getX() + gameItem.getWidth()
				|| this.getX() + this.getWidth() < gameItem.getX()
				|| this.getY() > gameItem.getY() + gameItem.getHeight()
				|| this.getY() + this.getHeight() < gameItem.getY()) {
			return false;
		}
		return true;
	}

	public boolean near(GameCharacter character) {
		if (character.getX() > this.getX() + this.getWidth()
				|| character.getX() + character.getWidth() < this.getX()
				|| character.getY() > this.getY() + this.getHeight()
				|| character.getY() + character.getHeight() < this.getY()) {
			return false;
		}
		return true;
	}

	public boolean near(GameCharacter character, Axe axe, int doubt) {
		switch (axe) {
			case HORIZONTAL:
				if (character.getX() > this.getX() + this.getWidth() + doubt
						|| character.getX() + character.getWidth() < this.getX() - doubt
						|| character.getY() > this.getY() + this.getHeight()
						|| character.getY() + character.getHeight() < this.getY()) {
					return false;
				}
				return true;
			case VERTICAL:
				if (character.getX() > this.getX() + this.getWidth()
						|| character.getX() + character.getWidth() < this.getX()
						|| character.getY() > this.getY() + this.getHeight() + doubt
						|| character.getY() + character.getHeight() < this.getY() - doubt) {
					return false;
				}
				return true;
			default:
				if (character.getX() > this.getX() + this.getWidth() + doubt
						|| character.getX() + character.getWidth() < this.getX() - doubt
						|| character.getY() > this.getY() + this.getHeight() + doubt
						|| character.getY() + character.getHeight() < this.getY() - doubt) {
					return false;
				}
				return true;
		}

	}

	public boolean frontCollision(GameItem gameItem) {
		if (this.x + this.width < gameItem.getX() || this.x + this.width > gameItem.getX()
				|| this.y + this.height <= gameItem.getY() || this.y >= gameItem.getY() + gameItem.getHeight()) {
			return false;
		} else {
			// System.out.println("front collision " + gameItem.getX());
			return true;
		}
	}

	public boolean backCollision(GameItem gameItem) {
		if (this.x > gameItem.getX() + gameItem.getWidth() || this.x < gameItem.getX() + gameItem.getWidth()
				|| this.y + this.height <= gameItem.getY() || this.y >= gameItem.getY() + gameItem.getHeight()) {
			return false;
		} else {
			return true;
		}

	}

	public boolean bottomCollision(GameItem gameItem) {
		if (this.getX() >= gameItem.getX() + gameItem.getWidth() || this.getX() + this.getWidth() <= gameItem.getX()
				|| this.getY() + this.getHeight() >= gameItem.getY() + gameItem.getHeight()
				|| this.getY() + this.getHeight() <= gameItem.getY()) {
			return false;
		}
		// System.out.println("bottom collision");
		return true;
	}

	public boolean topCollision(GameItem gameItem) {
		if (this.getX() >= gameItem.getX() + gameItem.getWidth() || this.getX() + this.getWidth() <= gameItem.getX()
				|| this.getY() >= gameItem.getY() + gameItem.getHeight() || this.getY() <= gameItem.getY()) {
			return false;
		}
		return true;

	}

	// collision avec un personnage
	public boolean frontCollision(GameCharacter personnage) {
		if (this.x + this.width < personnage.getX() - 1
				|| this.x + this.width > personnage.getX() + 1
				|| this.y + this.height <= personnage.getY()
				|| this.y >= personnage.getY() + personnage.getHeight()) {
			return false;
		} else {
			return true;
		}
	}

	public boolean backCollision(GameCharacter personnage) {
		if (this.x > personnage.getX() + personnage.getWidth() + 1
				|| this.x < personnage.getX() + personnage.getWidth() - 1
				|| this.y + this.height <= personnage.getY()
				|| this.y >= personnage.getY() + personnage.getHeight()) {
			return false;
		} else {
			return true;
		}

	}

	public boolean bottomCollision(GameCharacter personnage) {
		if (this.getX() > personnage.getX() + personnage.getWidth()
				|| this.getX() + this.getWidth() < personnage.getX()
				|| this.getY() + this.getHeight() > personnage.getY() + personnage.getHeight()
				|| this.getY() + this.getHeight() < personnage.getY()) {
			return false;
		}
		return true;
	}

	public boolean topCollision(GameCharacter personnage) {
		if (this.getX() > personnage.getX() + personnage.getWidth()
				|| this.getX() + this.getWidth() < personnage.getX()
				|| this.getY() > personnage.getY() + personnage.getHeight()
				|| this.getY() < personnage.getY()) {
			return false;
		}
		return true;

	}

}
