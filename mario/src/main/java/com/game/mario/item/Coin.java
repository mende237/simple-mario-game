package com.game.mario.item;

import javafx.scene.image.Image;

public class Coin extends GameItem {
	/****************************************
	 * property
	 *****************************************/
	private static int cmpt = 0;
	// private final int PAUSE = 15;
	private static Image imgPiece;

	/****************************************
	 * constructor
	 **************************************/
	public Coin(int x, int y) {
		super(x, y, 30, 30, "coin", new Image(Coin.class.getResource("images/piece1.png").toExternalForm()));
	}

	/******************************************
	 * getters
	 *****************************************/
	public Image getImagePiece() {
		return Coin.imgPiece;
	}

	/******************************************
	 * methods
	 *****************************************/
	// public static void startAlternation() {
	// ImageIcon ico;
	// String str;
	// Coin.cmpt++;
	// if (Coin.cmpt / 100 == 0)
	// str = "src/images/piece1.png";
	// else
	// str = "src/images/piece2.png";
	// if (Coin.cmpt == 200)
	// Coin.cmpt = 0;

	// ico = new ImageIcon(str);
	// Coin.imgPiece = ico.getImage();
	// }

	public static void startAlternation() {
		String str;
		Coin.cmpt++;
		if (Coin.cmpt / 100 == 0)
			str = "images/piece1.png";
		else
			str = "images/piece2.png";
		if (Coin.cmpt == 200)
			Coin.cmpt = 0;

		Coin.imgPiece = new Image(Coin.class.getResource(str).toExternalForm());
	}

	// @Override
	// public void run() {
	// try {
	// Thread.sleep(20);
	// System.out.println(true);
	// } catch (InterruptedException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// while(true) {
	// this.pieceAlternation();
	// try {
	// Thread.sleep(PAUSE);
	// } catch (InterruptedException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	// }

}
