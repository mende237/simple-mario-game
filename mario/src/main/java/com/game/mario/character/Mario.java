package com.game.mario.character;

import com.game.mario.App;
import com.game.mario.game.GameManager;
import com.game.mario.item.Coin;
import com.game.mario.item.GameItem;
import com.game.mario.util.Config;
import com.game.mario.util.MarioState;
import com.game.mario.util.TransitionState;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Mario extends GameCharacter {
	// ****************************************property**********************************//
	private ImageView icoMario;
	private Image imgMario;

	private boolean jump;// is true if mario is jumping
	private boolean isOnObject;
	private int yObjectCollision;
	private int counter;// determine the duration and height of jump
	private boolean fall; // is true when mario is jumping
	private static int numberOfLive;
	private static int score;
	private static boolean isLiving = true;
	private MarioState state = MarioState.STANDING;
	public static final int HEIGHT = 50;

	// ********************************constructor******************************************//
	public Mario(int x, int y) {
		super(x, y, 28, HEIGHT);
		this.imgMario = new Image(getClass().getResource("images/marioMarcheDroite.png").toExternalForm());
		this.icoMario = new ImageView(this.imgMario);
		super.setLiving(true);
		this.jump = false;
		this.counter = 0;
		this.fall = false;
		Mario.numberOfLive = Config.MARIO_NUMBER_OF_LIVES;
		Mario.score = 0;
	}

	// *********************************************getter*******************************//
	public boolean isJump() {
		return jump;
	}

	public Image getImageMario() {
		return this.imgMario;
	}

	public boolean isOnObject() {
		return this.isOnObject;
	}

	public int getYObjectCollision() {
		return this.yObjectCollision;
	}

	public boolean isFall() {
		return this.fall;
	}

	public int getScore() {
		return score;
	}

	public int getNumberOfLive() {
		return numberOfLive;
	}

	@Override
	public boolean isLiving() {
		return isLiving;
	}

	public MarioState getState() {
		return this.state;
	}

	// *******************************************setter************************************//
	public void setJump(boolean jump) {
		this.jump = jump;
	}

	public void setIsOnObject(boolean onObject) {
		this.isOnObject = onObject;
	}

	public void setNumberOfLive(int numberOfLive) {
		Mario.numberOfLive = numberOfLive;
	}

	public void setScore(int score) {
		Mario.score = score;
	}

	public void setFall(boolean fall) {
		this.fall = fall;
	}

	@Override
	public void setLiving(boolean isLiving) {
		Mario.isLiving = isLiving;
	}

	public void setState(MarioState state) {
		this.state = state;
	}

	// ********************************************method************************************//
	public Image jump(int begin) {
		// ImageIcon ico;
		Image img;
		String str;
		// System.out.println(begin);
		if (super.isLiving() == true) {
			// monter du saut
			if (this.counter <= 10) {
				state = MarioState.JUMPING;
				if (super.getY() > App.scene.getHeightRoof() && begin - super.getHeight() - super.getY() <= 130) {
					super.setY(this.getY() - 4);
				} else {
					// System.out.println("la hauteur max est " + this.getY());
					this.counter = 11;
				}

				if (super.isToRight() == true)
					str = "images/marioSautDroite.png";
				else
					str = "images/marioSautGauche.png";
				// descente du saut

			} else if (super.getY() + super.getHeight() < App.scene.getYFloor()) {
				state = MarioState.FALLING;
				super.setY(super.getY() + 1);

				if (super.isToRight() == true)
					str = "images/marioSautDroite.png";
				else
					str = "images/marioSautGauche.png";
				// end of jump
			} else {
				state = MarioState.STANDING;
				if (super.isToRight() == true)
					str = "images/marioArretDroite.png";
				else
					str = "images/marioArretGauche.png";
				this.jump = false;
				this.counter = 0;
			}
		} else {
			str = "images/marioSautDroite.png";
		}
		// ico = new ImageIcon(getClass().getResource(str));
		// img = ico.getImage();
		img = new Image(getClass().getResource(str).toExternalForm());
		return img;
	}

	public Image fall(int begin) {
		// ImageIcon ico;
		Image img;
		String str;
		if (super.isLiving() == true) {
			// descente
			if (super.getY() + super.getHeight() < App.scene.getYFloor()) {
				state = MarioState.FALLING;
				super.setY(super.getY() + 1);

				if (super.isToRight() == true)
					str = "images/marioSautDroite.png";
				else
					str = "images/marioSautGauche.png";
				// fin de la descente
			} else {
				state = MarioState.STANDING;
				if (super.isToRight() == true)
					str = "images/marioArretDroite.png";
				else
					str = "images/marioArretGauche.png";
				this.fall = false;
			}
		} else {
			state = MarioState.HIT_BY_ANTAGONIST;
			str = "images/marioSautDroite.png";
		}
		// ico = new ImageIcon(getClass().getResource(str));
		// img = ico.getImage();
		img = new Image(getClass().getResource(str).toExternalForm());
		return img;

	}

	public void contact(GameItem gameItem, boolean merge) {
		// horizontal contact
		if ((super.frontCollision(
				gameItem) == true && this.isToRight() == true)
				|| (super.backCollision(gameItem) == true && this.isToRight() == false) && merge == false) {
			state = MarioState.BLOCKING_BY_OBJECT_HORIZONTAL;
			this.setWalke(false);
			App.scene.setDx(0);
		}

		// bottom contact
		if (super.bottomCollision(gameItem) == true) {// mario jump on an object
			state = MarioState.ON_OBJECT;
			App.scene.setYFloor(gameItem.getY());
			this.isOnObject = true;
			this.yObjectCollision = gameItem.getY();
		} else if (super.bottomCollision(gameItem) == false && merge == false) {// mario fall on the initial floor
			if (state != MarioState.BLOCKING_BY_OBJECT_HORIZONTAL) {
				state = MarioState.STANDING;
			}
			App.scene.setYFloor(293);// altitude initiale
			if (this.jump == false && this.getY() + this.getHeight() < App.scene.getYFloor())
				this.fall = true;

			this.isOnObject = false;
		}

		// top contact
		if (super.topCollision(gameItem) == true) {
			state = MarioState.BLOCKING_BY_OBJECT_VERTICAL;
			App.scene.setHeightRoof(gameItem.getHeight() + gameItem.getY());// roof become bottom of object
		} else if (super.topCollision(gameItem) == false && this.jump == false) {
			App.scene.setHeightRoof(0);// altitude initiale du plafond (ciel)
		}
	}

	@Override
	public Image walk(String name, int frequency) {
		String str;
		// ImageIcon ico;
		Image img;

		if (super.isWalke() == false || App.scene.getxPos() <= 0 || App.scene.getxPos() >= 4100) {
			if (App.scene.getxPos() <= 0) {
				state = MarioState.BLOCKING_BY_HORIZONTAL_BEGINNING_MAP;
			} else if (App.scene.getxPos() >= 4100) {
				state = MarioState.BLOCKING_BY_HORIZONTAL_END_MAP;
			} else if (state != MarioState.BLOCKING_BY_OBJECT_HORIZONTAL) {
				state = MarioState.STANDING;
			}
			if (super.isToRight() == true)
				str = "images/" + name + "ArretDroite.png";
			else
				str = "images/" + name + "ArretGauche.png";
		} else {
			state = MarioState.WALKING;
			super.setCounter(super.getCounter() + 1);
			if (super.getCounter() / frequency == 0) {
				if (super.isToRight() == true)
					str = "images/" + name + "ArretDroite.png";
				else
					str = "images/" + name + "ArretGauche.png";
			} else {
				if (super.isToRight() == true)
					str = "images/" + name + "MarcheDroite.png";
				else
					str = "images/" + name + "MarcheGauche.png";
			}
			if (super.getCounter() == 2 * frequency)
				super.setCounter(0);
		}
		// ico = new ImageView(getClass().getResource(str));
		// img = ico.getImage();
		img = new Image(getClass().getResource(str).toExternalForm());
		return img;
	}

	public boolean contactCoin(Coin coin) {
		if (this.near(coin) == true) {
			state = MarioState.HIT_COIN;
			return true;
		} else
			return false;
	}

	public void init(int x, int y, int nbrLive) {
		setX(x);
		setY(y);
		setWalke(false);
		isLiving = true;
		numberOfLive = nbrLive;
	}

	@Override
	public Image die() {
		if (this.getY() > -this.getHeight()) {
			super.setY(super.getY() - 1);
			GameManager.setState(TransitionState.DYING);
		} else {
			GameManager.setInterupt(true);
			GameManager.setState(TransitionState.GAMEOVER);
		}

		Image imageMarioDie = new Image(getClass().getResource("images/marioMeurt.png").toExternalForm());
		return imageMarioDie;
	}
}
