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
import com.game.mario.item.Tuyau;
import com.game.mario.character.Turtle;
import com.game.mario.character.Antagonist;
import com.game.mario.character.Champignon;
import com.game.mario.character.Mario;
import com.game.mario.util.Collision;

public class FirstStage extends Scene {
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

	private ArrayList<Bloc> blocTab;
	private ArrayList<Tuyau> tuyauTab;
	private ArrayList<Coin> pieceTab;
	private ArrayList<Champignon> champTab;
	private ArrayList<Turtle> tortueTab;
	private ArrayList<Antagonist> antagonistTab;

	private Font font;
	private Champignon champignon1;
	private Champignon champignon2;
	private Champignon champignon3;
	private Champignon champignon4;
	private Champignon champignon5;
	private Champignon champignon6;

	private Turtle tortue1;
	private Turtle tortue2;
	private Turtle tortue3;
	private Turtle tortue4;
	private Turtle tortue5;
	private Turtle tortue6;

	private int xFond1;
	private int xFond2;

	// *************************************constructor***************************************//
	public FirstStage() {

		super(293, 0, 0);
		this.xFond1 = -50;
		this.xFond2 = 750;
		mario = new Mario(300, super.getYFloor() - 50);

		// try {
		// this.font = Font.createFont(Font.TRUETYPE_FONT, new
		// File("src/police/SuperMario256.ttf"));
		// this.font = this.font.deriveFont(20.f);
		// } catch (FontFormatException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		this.font = Font.loadFont(getClass().getResourceAsStream("/com/game/mario/police/SuperMario256.ttf"), 20);

		// this.pieceImage = (new
		// ImageIcon(getClass().getResource("images/piece1.png"))).getImage();

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
		//
		blocTab = new ArrayList<Bloc>();
		tuyauTab = new ArrayList<>();
		pieceTab = new ArrayList<Coin>();
		champTab = new ArrayList<Champignon>();
		tortueTab = new ArrayList<Turtle>();
		antagonistTab = new ArrayList<Antagonist>();

		int pas = 0;
		for (int i = 0; i < 7; i++) {
			this.tuyauTab.add(new Tuyau(600 + pas, 228));
			pas = pas + 500;
		}
		pas = 0;
		for (int i = 0; i < 4; i++) {
			this.blocTab.add(new Bloc(800 + pas, 180));
			pas = pas + 30;
		}

		pas = 0;
		for (int i = 4; i < 8; i++) {
			this.blocTab.add(new Bloc(1700 + pas, 180));
			pas = pas + 30;
		}

		for (int i = 8; i < 11; i++) {
			this.blocTab.add(new Bloc(1800 + pas, 140));
			pas = pas + 30;
		}

		this.blocTab.add(new Bloc(2200, 180));
		this.blocTab.add(new Bloc(2300, 140));
		this.blocTab.add(new Bloc(2400, 180));

		this.blocTab.add(new Bloc(3200, 180));
		this.blocTab.add(new Bloc(3300, 140));
		this.blocTab.add(new Bloc(3400, 180));

		pas = 0;
		for (int i = 0; i < 4; i++) {
			this.pieceTab.add(new Coin(800 + pas, 145));
			pas += 30;
		}

		pas = 0;
		for (int i = 0; i < 4; i++) {
			this.pieceTab.add(new Coin(1700 + pas, 145));
			pas += 30;
		}

		pas = 0;
		for (int i = 0; i < 3; i++) {
			this.pieceTab.add(new Coin(1910 + pas, 100));
			pas += 40;
		}

		this.pieceTab.add(new Coin(2200, 150));
		this.pieceTab.add(new Coin(2300, 110));
		this.pieceTab.add(new Coin(2400, 150));

		this.pieceTab.add(new Coin(3200, 150));
		this.pieceTab.add(new Coin(3300, 110));
		this.pieceTab.add(new Coin(3400, 150));

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

		this.tortueTab.add(tortue1);
		this.tortueTab.add(tortue2);
		this.tortueTab.add(tortue3);
		this.tortueTab.add(tortue4);
		this.tortueTab.add(tortue5);
		this.tortueTab.add(tortue6);

		this.champTab.add(champignon1);
		this.champTab.add(champignon2);
		this.champTab.add(champignon3);
		this.champTab.add(champignon4);
		this.champTab.add(champignon5);
		this.champTab.add(champignon6);

		// //int pas = 0;
		// for (int i = 0; i < 500; i++) {
		// this.champTab.add(new Champignon(700 + pas, 260));
		// pas += 40;
		// }

		for (int i = 0; i < champTab.size(); i++) {
			antagonistTab.add(champTab.get(i));
		}

		for (int i = 0; i < tortueTab.size(); i++) {
			antagonistTab.add(tortueTab.get(i));
		}

		antagonistTab.sort((a, b) -> Integer.compare(a.getX(), b.getX()));

		// Thread screenChrono = new Thread(new DisplayFrequence());
		Thread chrono = new Thread(new Chrono());
		chrono.start();
		SceneUpdater.update(getGraphicsContext2D());
		// screenChrono.start();
	}

	// ****************************************getter*****************************//
	public ArrayList<Bloc> getBlocTab() {
		return blocTab;
	}

	public ArrayList<Champignon> getChampTab() {
		return champTab;
	}

	public ArrayList<Turtle> getTortueTab() {
		return tortueTab;
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
		// super.paintComponent(gc);
		// Graphics gc = (Graphics2D) g;
		gc.setFont(this.font);
		gc.setFill(Color.WHITE);
		// System.out.println(pieceTab.get(0).getX());
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
			// on supprime tous les caractères qui sont deja mort
			// Collision.updateTab(champTab);
			// Collision.updateTab(tortueTab);

			Collision.updateTab(antagonistTab);

			Collision.piece(this.pieceTab, this.mario);
			// Contact.mario(this.tuyauTab, 43, this.mario);
			// Contact.mario(this.blocTab, 30, this.mario);

			if (Collision.mario(this.tuyauTab, 43, this.mario) == false
					&& Collision.mario(this.blocTab, 30, this.mario) == false) {
				App.scene.setHeightRoof(0);
				App.scene.setYFloor(293);
			}

			//
			// Contact.tortue(this.tortueTab, this.tuyauTab);
			// // collision detection from mario with other objects of ours game
			// GameManagement.setBegin(true);

			// displacement of fixed objects of ours games

			// if(GameManagement.DOWN() >= 0) {
			this.backgroundDisplacement();
			if (super.getxPos() >= 0 && super.getxPos() <= 4650) {

				for (int i = 0; i < this.blocTab.size(); i++) {
					this.blocTab.get(i).displacement();
				}

				for (int i = 0; i < this.tuyauTab.size(); i++) {
					this.tuyauTab.get(i).displacement();
				}

				for (int i = 0; i < this.pieceTab.size(); i++) {
					this.pieceTab.get(i).displacement();
				}

				for (int i = 0; i < this.champTab.size(); i++) {
					this.champTab.get(i).displacement();
				}

				for (int i = 0; i < this.tortueTab.size(); i++) {
					this.tortueTab.get(i).displacement();
				}
			}

			// GameManagement.UP();
			// }else {
			// //System.out.println("enter displement");
			// }
			// Collision.chamignon(this.champTab, this.tortueTab, this.tuyauTab);
			// Collision.tortue(this.tortueTab, this.champTab, this.tuyauTab);
			Collision.antagonist(this.antagonistTab, this.tuyauTab);
			GameManager.setBegin(true);

			// ***********************paint of backgrounds' game***********************//
			gc.drawImage(this.imgFond1, this.xFond1, 0);
			gc.drawImage(this.imgFond2, this.xFond2, 0);

			// ------paint of chateau
			gc.drawImage(this.imgChateau1, 10 - super.getxPos(), 95);

			// ------paint of start arrow
			gc.drawImage(this.imgDepart, 220 - super.getxPos(), 234);

			// -----paint object
			for (int i = 0; i < blocTab.size(); i++) {
				gc.drawImage(this.blocTab.get(i).getImgObject(), this.blocTab.get(i).getX(),
						this.blocTab.get(i).getY());
			}

			for (int i = 0; i < tuyauTab.size(); i++) {
				gc.drawImage(this.tuyauTab.get(i).getImgObject(), this.tuyauTab.get(i).getX(),
						this.tuyauTab.get(i).getY());
			}

			//
			Coin.startAlternation();
			for (int i = 0; i < pieceTab.size(); i++) {
				gc.drawImage(this.pieceTab.get(i).getImagePiece(), this.pieceTab.get(i).getX(),
						this.pieceTab.get(i).getY());
			}

			// -----paint flag
			gc.drawImage(imgDrapeau, 4000 - super.getxPos(), 115);
			// -----paint of chateau end
			gc.drawImage(imgChateauFin, 4100 - super.getxPos(), 145);

			// print of all champignons of the game
			for (int i = 0; i < this.champTab.size(); i++) {
				if (this.champTab.get(i).isLiving() == true) {// on verifie si le champignon est encore envie
					// avant d'afficher
					gc.drawImage(this.champTab.get(i).walk("champ", 50), this.champTab.get(i).getX(),
							this.champTab.get(i).getY());
				} else {

					gc.drawImage(champTab.get(i).die(), this.champTab.get(i).getX(), 282);
				}
			}

			// print of all turtles of the scene
			for (int i = 0; i < tortueTab.size(); i++) {
				if (tortueTab.get(i).isLiving() == true) {
					gc.drawImage(this.tortueTab.get(i).walk("tortue", 100), this.tortueTab.get(i).getX(),
							this.tortueTab.get(i).getY());
				} else {
					gc.drawImage(tortueTab.get(i).die(), this.tortueTab.get(i).getX(),
							(293 - this.tortueTab.get(i).getHeight()));
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

		for (int i = 0; i < this.blocTab.size(); i++) {
			this.blocTab.get(i).setX(this.blocTab.get(i).getX() + position);
		}

		for (int i = 0; i < this.tuyauTab.size(); i++) {
			this.tuyauTab.get(i).setX(this.tuyauTab.get(i).getX() + position);
		}

		for (int i = 0; i < this.pieceTab.size(); i++) {
			this.pieceTab.get(i).setX(this.pieceTab.get(i).getX() + position);
		}

		for (int i = 0; i < this.champTab.size(); i++) {
			this.champTab.get(i).setX(this.champTab.get(i).getX() + position);
		}

		for (int i = 0; i < this.tortueTab.size(); i++) {
			this.tortueTab.get(i).setX(this.tortueTab.get(i).getX() + position);
		}

		super.setxPos(0);
	}
}
