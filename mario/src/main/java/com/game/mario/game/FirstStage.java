package com.game.mario.game;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import com.game.mario.App;

import com.game.mario.item.Block;
import com.game.mario.item.Coin;
import com.game.mario.item.GameItem;
import com.game.mario.item.Pipe;
import com.game.mario.character.Turtle;
import com.game.mario.game.AI.GamerAI;
import com.game.mario.game.AI.WindowFilter;
import com.game.mario.character.Antagonist;
import com.game.mario.character.Mushroom;
import com.game.mario.character.Mario;
import com.game.mario.sound.Audio;
import com.game.mario.util.Collision;
import com.game.mario.util.Config;
import com.game.mario.util.MarioState;
import com.game.mario.util.TransitionState;

public class FirstStage extends Stage {
	private Thread gamerAI_Thread;

	private Image imgFond1;
	private Image imgFond2;
	private Image imgDepart;
	private Image imgChateau1;

	private Image imgChateauFin;
	private Image imgDrapeau;
	private Image coinImage;

	private ImageView icoFond;
	private ImageView icoDepart;
	private ImageView icoChateau1;
	private ImageView icoChateauFin;
	private ImageView icoDrapeau;

	private static ArrayList<Coin> coinTab = null;
	private static ArrayList<Antagonist> antagonistTab = null;
	private static ArrayList<GameItem> gameItems = null;

	private Font font;
	private static Mushroom champignon1;
	private static Mushroom champignon2;
	private static Mushroom champignon3;
	private static Mushroom champignon4;
	private static Mushroom champignon5;
	private static Mushroom champignon6;

	private static Turtle tortue1;
	private static Turtle tortue2;
	private static Turtle tortue3;
	private static Turtle tortue4;
	private static Turtle tortue5;
	private static Turtle tortue6;

	private static int xFond1;
	private static int xFond2;

	static {
		init(0);
	}

	private static void init(int position) {
		if (antagonistTab != null) {
			for (Antagonist antagonist : antagonistTab) {
				antagonist.getThread().interrupt();
			}
		}

		coinTab = new ArrayList<Coin>();
		antagonistTab = new ArrayList<Antagonist>();
		gameItems = new ArrayList<GameItem>();

		int pas = 0;
		for (int i = 0; i < 7; i++) {
			gameItems.add(new Pipe(600 + pas + position, 228));
			pas = pas + 500;
		}
		pas = 0;
		for (int i = 0; i < 4; i++) {
			gameItems.add(new Block(800 + pas + position, 180));
			pas = pas + 30;
		}

		pas = 0;
		for (int i = 4; i < 8; i++) {
			gameItems.add(new Block(1700 + pas + position, 180));
			pas = pas + 30;
		}

		for (int i = 8; i < 11; i++) {
			gameItems.add(new Block(1800 + pas + position, 140));
			pas = pas + 30;
		}

		gameItems.add(new Block(2200, 180));
		gameItems.add(new Block(2300, 140));
		gameItems.add(new Block(2400, 180));

		gameItems.add(new Block(3200, 180));
		gameItems.add(new Block(3300, 140));
		gameItems.add(new Block(3400, 180));

		pas = 0;
		for (int i = 0; i < 4; i++) {
			coinTab.add(new Coin(800 + pas + position, 145));
			pas += 30;
		}

		pas = 0;
		for (int i = 0; i < 4; i++) {
			coinTab.add(new Coin(1700 + pas + position, 145));
			pas += 30;
		}

		pas = 0;
		for (int i = 0; i < 3; i++) {
			coinTab.add(new Coin(1910 + pas + position, 100));
			pas += 40;
		}

		coinTab.add(new Coin(2200, 150));
		coinTab.add(new Coin(2300, 110));
		coinTab.add(new Coin(2400, 150));

		coinTab.add(new Coin(3200, 150));
		coinTab.add(new Coin(3300, 110));
		coinTab.add(new Coin(3400, 150));

		champignon1 = new Mushroom(800, 263);
		champignon2 = new Mushroom(850, 263);
		champignon3 = new Mushroom(1500, 263);
		champignon4 = new Mushroom(3000, 263);
		champignon5 = new Mushroom(3200, 263);
		champignon6 = new Mushroom(3500, 263);

		tortue1 = new Turtle(700, 243);
		tortue2 = new Turtle(2000, 243);
		tortue3 = new Turtle(1900, 243);
		tortue4 = new Turtle(2500, 243);
		tortue5 = new Turtle(2900, 243);
		tortue6 = new Turtle(3300, 243);

		antagonistTab.add(tortue1);
		antagonistTab.add(tortue2);
		antagonistTab.add(tortue3);
		antagonistTab.add(tortue4);
		antagonistTab.add(tortue5);
		antagonistTab.add(tortue6);

		antagonistTab.add(champignon1);
		antagonistTab.add(champignon2);
		antagonistTab.add(champignon3);
		antagonistTab.add(champignon4);
		antagonistTab.add(champignon5);
		antagonistTab.add(champignon6);

		coinTab.sort((a, b) -> Integer.compare(a.getX(), b.getY()));
		antagonistTab.sort((a, b) -> Integer.compare(a.getX(), b.getX()));
		gameItems.sort((a, b) -> Integer.compare(a.getX(), b.getX()));

		initBackground();
	}

	// *************************************constructor***************************************//
	public FirstStage(boolean aiMode) {

		// super(293, 0, 0);
		super(Config.Y_FLOOR, 0, 0, Config.X_MAX, gameItems, antagonistTab, coinTab);
		initBackground();
		mario = new Mario(Config.MARIO_X, Config.Y_FLOOR - Mario.HEIGHT);

		this.font = Font.loadFont(getClass().getResourceAsStream("/com/game/mario/police/SuperMario256.ttf"), 20);

		this.coinImage = new Image(getClass().getResource("/com/game/mario/item/images/piece1.png").toExternalForm());

		icoFond = new ImageView(new Image(getClass().getResource("images/fondEcran.png").toExternalForm()));
		this.imgFond1 = this.icoFond.getImage();
		this.imgFond2 = this.icoFond.getImage();

		this.icoChateau1 = new ImageView(new Image(getClass().getResource("images/chateau1.png").toExternalForm()));
		this.imgChateau1 = this.icoChateau1.getImage();

		this.icoDepart = new ImageView(new Image(getClass().getResource("images/depart.png").toExternalForm()));
		this.imgDepart = this.icoDepart.getImage();

		this.icoChateauFin = new ImageView(
				new Image(getClass().getResource("images/chateauFin.png").toExternalForm()));
		this.imgChateauFin = this.icoChateauFin.getImage();

		this.icoDrapeau = new ImageView(new Image(getClass().getResource("images/drapeau.png").toExternalForm()));
		this.imgDrapeau = this.icoDrapeau.getImage();

		Thread chrono = new Thread(new Chrono());
		chrono.start();
		SceneUpdater.update(getGraphicsContext2D());

		Audio.playBackgroundMusic("Theme.wav");

		if (aiMode) {
			GamerAI gamerAI = new GamerAI(this, Config.CONTEXT_ITEM_WIDTH, Config.CONTEXT_COIN_WIDTH,
					Config.CONTEXT_ANTAGONIST_WIDTH,
					new WindowFilter(Config.WINDOW_FILTER_MIN, Config.WINDOW_FILTER_MAX));
			this.gamerAI_Thread = new Thread(gamerAI);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// Ensure the game is initialized before the AI starts
			this.gamerAI_Thread.start();
		}
	}

	// ****************************************methods********************************//
	public void backgroundDisplacement() {
		if (super.getxPos() >= 0 && super.getxPos() <= Config.X_MAX) {
			super.setxPos(super.getxPos() + super.getDx());
			xFond1 = xFond1 - super.getDx();
			xFond2 = xFond2 - super.getDx();
		} else if (this.getxPos() >= Config.X_MAX && this.mario.isToRight() == false) {
			super.setxPos(super.getxPos() + super.getDx());
			xFond1 = xFond1 - super.getDx();
			xFond2 = xFond2 - super.getDx();
		} else if (super.getxPos() <= 0 && super.mario.isToRight() == true) {
			super.setxPos(super.getxPos() + super.getDx());
			xFond1 = xFond1 - super.getDx();
			xFond2 = xFond2 - super.getDx();
		}

		if (xFond1 == -800)
			xFond1 = 800;
		else if (xFond2 == -800)
			xFond2 = 800;
		else if (xFond1 == 800)
			xFond1 = -800;
		else if (xFond2 == 800)
			xFond2 = -800;
	}

	@Override
	public void paint(GraphicsContext gc) {// repaint the background each 3ms
		gc.setFont(this.font);
		gc.setFill(Color.WHITE);

		if (GameManager.isInterupt() == true) {
			gc = Transition.transition(this, gc, GameManager.getState());

			if (GameManager.getState() == TransitionState.PLAYING) {
				init(0);
				setxPos(0);
				super.mario.init(Config.MARIO_X, Config.Y_FLOOR - Mario.HEIGHT, Config.MARIO_NUMBER_OF_LIVES, 0);
			}
			System.out.println(GameManager.getState());
		} else {
			mario.updateStateValidityFrames();

			if (super.mario.isLiving() == false && super.mario.getNumberOfLive() > 0) {
				restart(getxPos());
				super.mario.setIsOnObject(false);
				super.mario.setWalk(false);
			}

			Collision.updateTab(antagonistTab);

			Collision.piece(coinTab, super.mario);

			if (Collision.mario(gameItems, 0, super.mario) == false) {
				App.scene.setHeightRoof(0);
				App.scene.setYFloor(Config.Y_FLOOR);
			}

			this.backgroundDisplacement();
			if (super.getxPos() >= 0 && super.getxPos() <= Config.X_MAX) {

				for (int i = 0; i < gameItems.size(); i++) {
					gameItems.get(i).displacement();
				}

				for (int i = 0; i < coinTab.size(); i++) {
					coinTab.get(i).displacement();
				}

				for (int i = 0; i < antagonistTab.size(); i++) {
					antagonistTab.get(i).displacement();
				}
			} else if (super.getxPos() >= Config.X_MAX) {
				super.mario.updateState(MarioState.WIN, true);
				init(0);
				setxPos(0);
				super.mario.init(Config.MARIO_X, Config.Y_FLOOR - Mario.HEIGHT, Config.MARIO_NUMBER_OF_LIVES, 0);
			}

			Collision.antagonist(antagonistTab, gameItems, this.xMax);
			GameManager.setBegin(true);

			// ***********************paint of backgrounds' game***********************//
			gc.drawImage(this.imgFond1, xFond1, 0);
			gc.drawImage(this.imgFond2, xFond2, 0);

			// ------paint of chateau
			gc.drawImage(this.imgChateau1, 10 - super.getxPos(), 95);

			// ------paint of start arrow
			gc.drawImage(this.imgDepart, 220 - super.getxPos(), 234);

			// -----paint object
			for (int i = 0; i < gameItems.size(); i++) {
				gc.drawImage(gameItems.get(i).getImgObject(), gameItems.get(i).getX(),
						gameItems.get(i).getY());
			}

			//
			Coin.startAlternation();
			for (int i = 0; i < coinTab.size(); i++) {
				gc.drawImage(coinTab.get(i).getImagePiece(), coinTab.get(i).getX(),
						coinTab.get(i).getY());
			}

			// -----paint flag
			gc.drawImage(imgDrapeau, Config.X_MAX - super.getxPos() + mario.getX() - 100, 115);
			// -----paint of chateau end
			gc.drawImage(imgChateauFin, Config.X_MAX - super.getxPos() + mario.getX(), 145);

			// print of all champignons of the game
			for (int i = 0; i < antagonistTab.size(); i++) {
				if (antagonistTab.get(i).isLiving() == true) {// on verifie si le champignon est encore envie
					// avant d'afficher
					gc.drawImage(antagonistTab.get(i).walk(antagonistTab.get(i).getName(),
							antagonistTab.get(i).getWalkFrequency()), antagonistTab.get(i).getX(),
							antagonistTab.get(i).getY());
				} else {
					if (antagonistTab.get(i) instanceof Turtle) {
						gc.drawImage(antagonistTab.get(i).die(), antagonistTab.get(i).getX(),
								(Config.Y_FLOOR - antagonistTab.get(i).getHeight()));
					} else {
						gc.drawImage(antagonistTab.get(i).die(), antagonistTab.get(i).getX(), Config.Y_FLOOR - 11);
					}
				}
			}

			// **************************add of mario in scene's
			// game*************************//
			if (super.mario.getNumberOfLive() > 0) {
				if (super.mario.isJump() == true && super.mario.isFall() == false) {
					if (super.mario.isOnObject() == false) {
						// --------------when mario jumps to suffer from an object
						gc.drawImage(super.mario.jump(Config.Y_FLOOR), super.mario.getX(), super.mario.getY());

					} else {
						// --------------when mario jumps to suffer from the floor
						gc.drawImage(super.mario.jump(super.mario.getYObjectCollision()), super.mario.getX(),
								super.mario.getY());
					}

				} else if (super.mario.isFall() == true) {
					gc.drawImage(super.mario.fall(super.mario.getYObjectCollision()), super.mario.getX(),
							super.mario.getY());

				} else {
					// ---------------when he walk or immobilize
					gc.drawImage(super.mario.walk("mario", super.mario.getWalkFrequency()), super.mario.getX(),
							super.mario.getY());
				}

			} else {
				// when mario is dead
				gc.drawImage(super.mario.die(), super.mario.getX(), super.mario.getY());
			}

			/* printing of score */
			gc.setFont(this.font);
			gc.setFill(Color.WHITE);
			gc.fillText("score " + this.mario.getScore(), 650, 33);
			gc.drawImage(this.coinImage, 750, 10);

			/* printing of time */
			gc.setFont(this.font);
			gc.setFill(Color.WHITE);
			gc.fillText("time Elapsed " + Chrono.getTimeElapse(), 0, 33);
		}

	}

	private static void initBackground() {
		xFond1 = -50;
		xFond2 = 750;
	}

	@Override
	// this function puts all the object of the scene in their initial position
	public void restart(int position) {
		initBackground();

		super.mario.init(Config.MARIO_X, Config.Y_FLOOR - Mario.HEIGHT, super.mario.getNumberOfLive(),
				super.mario.getScore());

		for (int i = 0; i < gameItems.size(); i++) {
			gameItems.get(i).setX(gameItems.get(i).getX() + position);
		}

		for (int i = 0; i < coinTab.size(); i++) {
			coinTab.get(i).setX(coinTab.get(i).getX() + position);
		}

		for (int i = 0; i < antagonistTab.size(); i++) {
			antagonistTab.get(i).setX(antagonistTab.get(i).getX() + position);
		}

		super.setxPos(0);
	}
}
