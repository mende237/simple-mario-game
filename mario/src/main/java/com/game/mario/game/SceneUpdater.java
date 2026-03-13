package com.game.mario.game;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import com.game.mario.App;
import javafx.util.Duration;

public class SceneUpdater {
	private final static double pause = 4; // waiting time between two loops turn

	public static void update(GraphicsContext gc) {
		Timeline timeline = new Timeline(new KeyFrame(Duration.millis(pause), event -> {
			// Process AI action
			if (App.scene instanceof Stage) {
				Stage currentStage = (Stage) App.scene;
				int aiAction = currentStage.getAiAction();

				// Reset Mario's movement state before applying AI action
				App.scene.getMario().setWalke(false);
				App.scene.setDx(0);

				switch (aiAction) {
					case 0: // Do nothing
						// Mario's movement is already reset above
						break;
					case 1: // Move forward (right)
						App.scene.getMario().setWalke(true);
						App.scene.getMario().setToRight(true);
						App.scene.setDx(1);
						break;
					case 2: // Move back (left)
						App.scene.getMario().setWalke(true);
						App.scene.getMario().setToRight(false);
						App.scene.setDx(-1);
						break;
					case 3: // Jump
						App.scene.getMario().setJump(true);
						break;
					default:
						// No AI action or invalid action, let keyboard control if any
						break;
				}
				// Reset AI action after processing
				currentStage.setAiAction(-1);
			}

			App.scene.paint(gc);
		}));

		timeline.setCycleCount(Timeline.INDEFINITE); // Repeat indefinitely
		timeline.play(); // Start the timeline
	}

}
