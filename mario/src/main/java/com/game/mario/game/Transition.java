package com.game.mario.game;

import javafx.scene.paint.Color;
import javafx.scene.canvas.GraphicsContext;

import com.game.mario.util.Config;
import com.game.mario.util.TransitionState;
import com.game.mario.App;

public class Transition {
	private static int transitionCount = 100;
	private static int cmpt = 0;

	/*************************************
	 * methods
	 *************************************/
	public static GraphicsContext transition(Scene scene, GraphicsContext gc, TransitionState state) {

		if (cmpt >= transitionCount) {
			cmpt = 0;
			GameManager.setInterupt(false);
			GameManager.setState(TransitionState.PLAYING);
		} else {
			cmpt++;
		}

		if (state == TransitionState.GAMEOVER) {
			gc.setFill(Color.BLACK);
			gc.fillText("game over".toUpperCase(), 350, 175);
		}

		return gc;
	}

}
