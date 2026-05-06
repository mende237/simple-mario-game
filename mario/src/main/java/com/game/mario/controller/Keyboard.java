package com.game.mario.controller;

import com.game.mario.App;
import com.game.mario.game.Scene;
import com.game.mario.game.Stage;

import com.game.mario.sound.Audio;

import javafx.scene.input.KeyEvent;

public class Keyboard {
	private final Scene scene;

	public Keyboard(Scene scene) {
		this.scene = scene;
	}

	public void handleKeyPressed(KeyEvent event) {
		// Only process keyboard input if AI is not providing an action
		if (App.scene instanceof Stage && ((Stage) App.scene).getAiAction() != -1) {
			return;
		}

		switch (event.getCode()) {
			case NUMPAD6:
				scene.getMario().setWalk(true);
				scene.getMario().setToRight(true);
				scene.setDx(1); // displacement to right of background
				break;
			case NUMPAD4:
				scene.getMario().setWalk(true);
				scene.getMario().setToRight(false);
				scene.setDx(-1); // displacement to left of background
				break;
			case SPACE:
				scene.getMario().setJump(true);
				Audio.playSong("saut.wav");
				break;
			default:
				// Handle other keys if needed
				break;
		}
	}

	public void handleKeyReleased(KeyEvent event) {
		// Only process keyboard input if AI is not providing an action
		if (App.scene instanceof Stage && ((Stage) App.scene).getAiAction() != -1) {
			return;
		}
		scene.getMario().setWalk(false);
		scene.setDx(0); // immobilization of background
	}
}
