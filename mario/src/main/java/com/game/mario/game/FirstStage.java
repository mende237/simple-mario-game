package com.game.mario.game;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import com.game.mario.App;

import com.game.mario.item.Bloc;
import com.game.mario.item.Coin;
import com.game.mario.item.GameItem;
import com.game.mario.item.Tuyau;
import com.game.mario.character.Turtle;
import com.game.mario.character.Antagonist;
import com.game.mario.character.Champignon;
import com.game.mario.character.Mario;
import com.game.mario.util.Collision;

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

	private static ArrayList<Coin> coinTab;
	private static ArrayList<Antagonist> antagonistTab;
	private static ArrayList<GameItem> gameItems;

	private Font font;
	private static Champignon champignon1;
	private static Champignon champignon2;
	private static Champignon champignon3;
	private static Champignon champignon4;
	private static Champignon champignon5;
	private static Champignon champignon6;

	private static Turtle tortue1;
	private static Turtle tortue2;
	private static Turtle tortue3;
	private static Turtle tortue4;
	private static Turtle tortue5;
	private static Turtle tortue6;

	private int xFond1;
	private int xFond2;

	static {
		coinTab = new ArrayList<Coin>();
		antagonistTab = new ArrayList<Antagonist>();
		gameItems = new ArrayList<GameItem>();

		int pas = 0;
		for (int i = 0; i < 7; i++) {
			gameItems.add(new Tuyau(600 + pas, 228));
			pas = pas + 500;
		}
		pas = 0;
		for (int i = 0; i < 4; i++) {
			gameItems.add(new Bloc(800 + pas, 180));
			pas = pas + 30;
		}

		pas = 0;
		for (int i = 4; i < 8; i++) {
			gameItems.add(new Bloc(1700 + pas, 180));
			pas = pas + 30;
		}

		for (int i = 8; i < 11; i++) {
			gameItems.add(new Bloc(1800 + pas, 140));
			pas = pas + 30;
		}

		gameItems.add(new Bloc(2200, 180));
		gameItems.add(new Bloc(2300, 140));
		gameItems.add(new Bloc(2400, 180));

		gameItems.add(new Bloc(3200, 180));
		gameItems.add(new Bloc(3300, 140));
		gameItems.add(new Bloc(3400, 180));

		pas = 0;
		for (int i = 0; i < 4; i++) {
			coinTab.add(new Coin(800 + pas, 145));
			pas += 30;
		}

		pas = 0;
		for (int i = 0; i < 4; i++) {
			coinTab.add(new Coin(1700 + pas, 145));
			pas += 30;
		}

		pas = 0;
		for (int i = 0; i < 3; i++) {
			coinTab.add(new Coin(1910 + pas, 100));
			pas += 40;
		}

		coinTab.add(new Coin(2200, 150));
		coinTab.add(new Coin(2300, 110));
		coinTab.add(new Coin(2400, 150));

		coinTab.add(new Coin(3200, 150));
		coinTab.add(new Coin(3300, 110));
		coinTab.add(new Coin(3400, 150));

		champignon1 = new Champignon(800, 263);
		champignon2 = new Champignon(850, 263);
		champignon3 = new Champignon(1500, 263);
		champignon4 = new Champignon(3000, 263);
		champignon5 = new Champignon(3200, 263);
		champignon6 = new Champignon(3500, 263);

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

		antagonistTab.sort((a, b) -> Integer.compare(a.getX(), b.getX()));
		gameItems.sort((a, b) -> Integer.compare(a.getX(), b.getX()));

	}

	// *************************************constructor***************************************//
	public FirstStage(boolean aiMode) {

		// super(293, 0, 0);
		super(293, 0, 0, 5000, gameItems, antagonistTab, coinTab);

		this.xFond1 = -50;
		this.xFond2 = 750;
		mario = new Mario(300, super.getYFloor() - 50);

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

		if (aiMode) {
			GamerAI gamerAI = new GamerAI(this, mario, xMax);
			this.gamerAI_Thread = new Thread(gamerAI);
			this.gamerAI_Thread.start();
		}
	}

	// ****************************************setter*******************************//
	public void setxFond1(int xFond1) {
		this.xFond1 = xFond1;
	}

	public void setxFond2(int xFond2) {
		this.xFond2 = xFond2;
	}

	// ****************************************methods********************************//
	public void backgroundDisplacement() {
		if (super.getxPos() >= 0 && super.getxPos() <= 4100) {
			super.setxPos(super.getxPos() + super.getDx());
			this.xFond1 = this.xFond1 - super.getDx();
			this.xFond2 = this.xFond2 - super.getDx();
		} else if (this.getxPos() >= 4100 && this.mario.isToRight() == false) {
			super.setxPos(super.getxPos() + super.getDx());
			this.xFond1 = this.xFond1 - super.getDx();
			this.xFond2 = this.xFond2 - super.getDx();
		} else if (super.getxPos() <= 0 && super.mario.isToRight() == true) {
			super.setxPos(super.getxPos() + super.getDx());
			this.xFond1 = this.xFond1 - super.getDx();
			this.xFond2 = this.xFond2 - super.getDx();
		}

		if (this.xFond1 == -800)
			this.xFond1 = 800;
		else if (this.xFond2 == -800)
			this.xFond2 = 800;
		else if (this.xFond1 == 800)
			this.xFond1 = -800;
		else if (this.xFond2 == 800)
			this.xFond2 = -800;
	}

	@Override
	public void paint(GraphicsContext gc) {// repaint the background each 3ms

		gc.setFont(this.font);
		gc.setFill(Color.WHITE);
		/*
		 * ici on gere la collision de chaque champignon du tableau de champignon avec
		 * chaque tortue du tableau de tortue . et meme avec mario
		 */
		if (GameManager.isInterupt() == true) {

			gc = Transition.transition(this, gc, GameManager.getState());
			GameManager.setInterupt(false);
			System.out.println("affichage de la transition");

		} else {
			if (App.scene.mario.isLiving() == false && App.scene.mario.getNumberOfLive() > 0) {
				// System.out.println("enter");
				App.scene.restart(App.scene.getxPos());
				App.scene.mario.setIsOnObject(false);
				App.scene.mario.setWalke(false);

			}

			Collision.updateTab(antagonistTab);

			Collision.piece(coinTab, this.mario);

			if (Collision.mario(gameItems, 0, this.mario) == false) {
				App.scene.setHeightRoof(0);
				App.scene.setYFloor(293);
			}

			this.backgroundDisplacement();
			if (super.getxPos() >= 0 && super.getxPos() <= 4650) {

				for (int i = 0; i < gameItems.size(); i++) {
					gameItems.get(i).displacement();
				}

				for (int i = 0; i < coinTab.size(); i++) {
					coinTab.get(i).displacement();
				}

				for (int i = 0; i < antagonistTab.size(); i++) {
					antagonistTab.get(i).displacement();
				}
			}

			Collision.antagonist(antagonistTab, gameItems, this.xMax);
			GameManager.setBegin(true);

			// ***********************paint of backgrounds' game***********************//
			gc.drawImage(this.imgFond1, this.xFond1, 0);
			gc.drawImage(this.imgFond2, this.xFond2, 0);

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
			gc.drawImage(imgDrapeau, 4000 - super.getxPos(), 115);
			// -----paint of chateau end
			gc.drawImage(imgChateauFin, 4100 - super.getxPos(), 145);

			// print of all champignons of the game
			for (int i = 0; i < antagonistTab.size(); i++) {
				if (antagonistTab.get(i).isLiving() == true) {// on verifie si le champignon est encore envie
					// avant d'afficher
					gc.drawImage(antagonistTab.get(i).walk(antagonistTab.get(i).getName(),
							antagonistTab.get(i).getWalkFrequency()), antagonistTab.get(i).getX(),
							antagonistTab.get(i).getY());
				} else {
					if (antagonistTab.get(i).getName().equalsIgnoreCase("tortue")) {
						gc.drawImage(antagonistTab.get(i).die(), antagonistTab.get(i).getX(),
								(293 - antagonistTab.get(i).getHeight()));
					} else {
						gc.drawImage(antagonistTab.get(i).die(), antagonistTab.get(i).getX(), 282);
					}
				}
			}

			// **************************add of mario in scene's
			// game*************************//
			if (super.mario.getNumberOfLive() > 0) {
				if (super.mario.isJump() == true && super.mario.isFall() == false) {
					if (super.mario.isOnObject() == false) {
						// --------------when mario jumps to suffer from an object
						gc.drawImage(super.mario.jump(293), super.mario.getX(), super.mario.getY());

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
					gc.drawImage(super.mario.walk("mario", 50), super.mario.getX(), super.mario.getY());
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

	@Override
	// this function puts all the object of the scene in their initial position
	public void restart(int position) {
		this.xFond1 = -50;
		this.xFond2 = 750;
		super.mario.setX(300);
		super.mario.setWalke(false);
		super.mario.setLiving(true);

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
