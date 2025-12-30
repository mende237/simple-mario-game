package com.game.mario.game;

import javafx.scene.paint.Color;
import javafx.scene.canvas.GraphicsContext;

import com.game.mario.util.TransitionState;
import com.game.mario.App;

public class Transition {
	// private Font font;

	// private TransitionState state;
	/*********************************
	 * constructor
	 **********************************/
	// public Transition() {
	// try {
	// this.font = Font.createFont(Font.TRUETYPE_FONT,
	// new File("src/police/SuperMario256.ttf"));
	// this.font = this.font.deriveFont(20.f);
	// } catch (FontFormatException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// this.setBackground(Color.black);
	// }
	/**************************************
	 * getter
	 *************************************/
	// public TransitionState getState() {
	// return state;
	// }
	/**************************************
	 * setter
	 *************************************/
	// public void setState(TransitionState state) {
	// this.state = state;
	// }

	/*************************************
	 * methods
	 *************************************/
	public static GraphicsContext transition(Scene scene, GraphicsContext gc, TransitionState state) {
		// scene.setBackground(Color.BLACK);
		gc.setFill(Color.BLACK);
		if (state == TransitionState.DIE) {
			// System.out.println("transition");
			/* we verifie if the is not game over */
			if (App.scene.mario.isLiving() == false && App.scene.mario.getNumberOfLive() > 0) {
				// System.out.println("enter");
				App.scene.restart(App.scene.getxPos());
				App.scene.mario.setIsOnObject(false);
				App.scene.mario.setWalke(false);

			}
			gc.fillText("live ".toUpperCase() + scene.mario.getNumberOfLive(), 350, 175);
		}

		return gc;
	}

}
