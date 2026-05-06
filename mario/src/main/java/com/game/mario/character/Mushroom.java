package com.game.mario.character;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import com.game.mario.game.GameManager;

import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

import com.game.mario.App;
import com.game.mario.sound.Audio;
import com.game.mario.util.Config;
import com.game.mario.util.MarioState;
import com.game.mario.util.TransitionState;

public class Mushroom extends Antagonist implements Runnable {
	/***************************************
	 * property
	 *******************************************/
	private ImageView icoMushroom;
	private Image imageMushroom;

	private int dxMushroom;

	/**************************************
	 * constructor
	 *****************************************/
	public Mushroom(int x, int y) {
		super(x, y, 27, 30, "champ", Config.MUSHROOM_THREAD_PAUSE, Config.MUSHROOM_WALK_FREQUENCY);
		super.nbreOfLive = 1;
		super.setToRight(true);
		super.setWalk(true);

		this.imageMushroom = new Image(getClass().getResource("images/champArretDroite.png").toExternalForm());
		this.icoMushroom = new ImageView(imageMushroom);
		super.setLiving(true);
		super.setThread(new Thread(this));
		super.getThread().start();
	}

	/*********************************
	 * getter
	 *****************************************/
	public Image getImageMushroom() {
		return imageMushroom;
	}

	/****************************************
	 * setter
	 **********************************/

	/***************************************
	 * methods
	 **************************************/
	@Override
	public void run() {
		// on attend 20ms que tous les objects de la scene s'affiche
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		while (!super.getThread().isInterrupted()) {
			if (super.isLiving() == true)
				this.move();
			else {
				super.getThread().interrupt();
				break;
			}

			try {
				Thread.sleep(super.breakDuration);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

	}

	public void setImageMushroom(Image imageMushroom) {
		this.imageMushroom = imageMushroom;
	}

	public void move() {
		if (canMove()) {

			boolean lockRead = GameManager.getAllAntagonistPositionReaderLocker().tryLock();

			if (!GameManager.getAllAntagonistPositionWriterLocker().isLocked()) {
				// on verifie si mario tue le champignon ou c'est le champignon qui tu mario

				this.positionLocker.lock();
				Optional<Antagonist> behindCharacter = Optional.ofNullable(this.behindCharacter);
				Optional<Antagonist> frontCharacter = Optional.ofNullable(this.frontCharacter);

				boolean lockBehind = behindCharacter.map(Antagonist::getPositionLocker).map(ReentrantLock::isLocked)
						.orElse(false);
				boolean lockFront = frontCharacter.map(Antagonist::getPositionLocker).map(ReentrantLock::isLocked)
						.orElse(false);

				if (!lockBehind && !lockFront) {

					int zomeMin = super.getZoneMin(super.behindCharacter, super.behindObject);
					int zoneMax = super.getZoneMax(super.frontCharacter, super.frontObject);

					if (super.isWalk() == true && super.isLiving() == true) {
						if (super.getX() + super.getWidth() < zoneMax && super.getX() > zomeMin) {
							if (super.isToRight() == true) {
								this.dxMushroom = 1;
								super.setX(super.getX() + this.dxMushroom);
							} else {
								this.dxMushroom = -1;
								super.setX(super.getX() + this.dxMushroom);
							}
						}

						if (super.getX() <= zomeMin) {
							if (this.characterDirectlyBehind == true && this.behindCharacter.zombie == true) {
								this.setLiving(false);
								super.behindCharacter.setLiving(false);
								super.remove = true;
								super.behindCharacter.remove = true;
								if (super.frontCharacter != null && super.behindCharacter.behindCharacter != null) {
									super.frontCharacter.behindCharacter = super.behindCharacter.behindCharacter;
									super.behindCharacter.behindCharacter.frontCharacter = super.frontCharacter;
								}

								App.scene.getMario().updateState(MarioState.KILLING_ANTAGONIST, true);
								Audio.playSong("smb-stomp.wav");
							}

							super.setToRight(true);
							this.dxMushroom = 1;
							super.setX(super.getX() + this.dxMushroom);
							// super.setWalke(false);
						}

						if (super.getX() + super.getWidth() >= zoneMax) {
							if (this.characterDirectlyFront == true && this.frontCharacter.zombie == true) {
								this.setLiving(false);
								super.frontCharacter.setLiving(false);
								super.remove = true;
								super.frontCharacter.remove = true;
								if (super.behindCharacter != null && super.frontCharacter.frontCharacter != null) {
									super.behindCharacter.frontCharacter = super.frontCharacter.frontCharacter;
									super.frontCharacter.frontCharacter.behindCharacter = super.behindCharacter;
								}
								App.scene.getMario().updateState(MarioState.KILLING_ANTAGONIST, true);
								Audio.playSong("smb-stomp.wav");

							}

							super.setToRight(false);
							this.dxMushroom = -1;
							super.setX(super.getX() + this.dxMushroom);

						}
					}

					this.kill(App.scene.getMario());
					// System.out.println("live");
					if (super.isLiving() == false) {
						System.out.println("champignon tuer !!!");
						super.remove = true;
					}
				}

				this.positionLocker.unlock();
			}

			if (lockRead) {
				GameManager.getAllAntagonistPositionReaderLocker().unlock();
			}
		}

	}

	@Override
	public Image walk(String name, int frequency) {
		String str;
		// ImageIcon ico;
		Image img;

		if (super.isWalk() == false) {
			if (super.isToRight() == true)
				str = "images/" + name + "ArretDroite.png";
			else
				str = "images/" + name + "ArretGauche.png";
		} else {
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
		// ico = new ImageIcon(getClass().getResource(str));
		// img = ico.getImage();

		img = new Image(getClass().getResource(str).toExternalForm());
		return img;
	}

	public void kill(Mario mario) {
		if (mario.near(this) == true && super.isLiving() == true && mario.isLiving() == true) {
			// dans ce cas c'est mario qui tu le champignon
			if (mario.bottomCollision(this) == true && (mario.isJump() == true || mario.isFall() == true)) {
				mario.updateState(MarioState.KILLING_ANTAGONIST, true);
				Audio.playSong("smb-stomp.wav");
				// System.out.println("tuer champ");
				super.setLiving(false);
				super.remove = true;
				// System.out.println("mario mort");
			} else {
				mario.updateState(MarioState.HIT_BY_ANTAGONIST, true);
				// System.out.println("tuer mario");
				mario.setNumberOfLive(mario.getNumberOfLive() - 1);
				mario.setLiving(false);
				Audio.playSong("game-over.wav");
				GameManager.setState(TransitionState.REDUCING_LIVE);
				if (mario.getNumberOfLive() <= 0) {
					System.out.println("champ position x " + this.getX());
					mario.updateState(MarioState.DEAD, true);
				}
			}
		}
	}

	@Override
	public Image die() {
		String str;
		if (super.isToRight() == true) {
			str = "champEcraseDroite.png";
		} else {
			str = "champEcraseGauche.png";
		}

		Image imgMushroom = new Image(getClass().getResource("images/" + str).toExternalForm());

		return imgMushroom;
	}

}
