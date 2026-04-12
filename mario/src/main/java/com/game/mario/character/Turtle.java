package com.game.mario.character;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import com.game.mario.game.GameManager;

import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

import com.game.mario.App;
import com.game.mario.util.Axe;
import com.game.mario.util.Config;
import com.game.mario.util.MarioState;
import com.game.mario.util.TransitionState;

public class Turtle extends Antagonist implements Runnable {

	/***************************************
	 * property
	 *******************************************/
	private ImageView icoTurtle;
	private Image imageTurtle;
	private int dxTurtle;
	private boolean justDie = false;

	/**************************************
	 * constructor
	 *****************************************/
	public Turtle(int x, int y) {
		super(x, y, 43, 50, "tortue", Config.TURTLE_THREAD_PAUSE, Config.TURTLE_WALK_FREQUENCY);
		super.nbreOfLive = 2;
		super.setToRight(true);
		super.setWalke(true);
		this.zombie = false;
		this.imageTurtle = new Image(getClass().getResource("images/tortueMarcheDroite.png").toExternalForm());
		this.icoTurtle = new ImageView(this.imageTurtle);
		super.setLiving(true);
		super.setThread(new Thread(this));
		super.getThread().start();
	}

	/****************************************
	 * getter
	 ***********************************************/
	public Image getImageTurtle() {
		return this.imageTurtle;
	}

	public boolean isZombie() {
		return this.zombie;
	}

	/****************************************
	 * setter
	 ***********************************************/
	public void setZombie(boolean zombie) {
		this.zombie = zombie;
	}

	/***************************************
	 * methods
	 ***********************************************/
	@Override
	public void run() {
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		while (!super.getThread().isInterrupted()) {
			if (this.zombie == true)
				super.breakDuration = 5;
			else
				super.breakDuration = 50;
			// when the character is dead we stop its thread
			if (super.remove == true) {
				super.getThread().interrupt();
				break;
			}

			this.move();
			try {
				Thread.sleep(super.breakDuration);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	public void setIcoTurtle(ImageView icoTurtle) {
		this.icoTurtle = icoTurtle;
	}

	public void setImageTurtle(Image imageTurtle) {
		this.imageTurtle = imageTurtle;
	}

	public void move() {
		if (canMove()) {
			boolean lockRead = GameManager.getAllAntagonistPositionReaderLocker().tryLock();

			if (!GameManager.getAllAntagonistPositionWriterLocker().isLocked()) {
				this.positionLocker.lock();

				Optional<Antagonist> behindCharacter = Optional.ofNullable(this.behindCharacter);
				Optional<Antagonist> frontCharacter = Optional.ofNullable(this.frontCharacter);

				boolean lockBehind = behindCharacter.map(Antagonist::getPositionLocker).map(ReentrantLock::isLocked)
						.orElse(false);
				boolean lockFront = frontCharacter.map(Antagonist::getPositionLocker).map(ReentrantLock::isLocked)
						.orElse(false);

				if (!lockBehind && !lockFront) {
					// we determine the area in which the character can move without any problems
					int zoneMin = super.getZoneMin(super.behindCharacter, super.behindObject);
					int zoneMax = super.getZoneMax(super.frontCharacter, super.frontObject);
					// we can move if only if the character is alive or in zombi state
					if (super.isWalke() == true && (super.isLiving() == true || this.zombie == true)) {
						if (super.getX() + super.getWidth() < zoneMax && super.getX() > zoneMin) {
							if (super.isToRight() == true) {
								this.dxTurtle = 1;
								super.setX(super.getX() + this.dxTurtle);
							} else {
								this.dxTurtle = -1;
								super.setX(super.getX() + this.dxTurtle);
							}
						}

						// we verify if the character have crossed its lower limit
						if (super.getX() <= zoneMin) {
							boolean collision = false;
							/*
							 * we verify if there is one character directly behind this character there must
							 * have nothing between the two characters, and the if the behind character is
							 * in the zombi state
							 */
							if (this.characterDirectlyBehind == true && this.behindCharacter.zombie == true) {
								App.scene.getMario().updateState(MarioState.KILLING_ANTAGONIST, true);
								super.behindCharacter.setLiving(false);
								super.behindCharacter.remove = true;
								this.remove = true;
								collision = true;
							}

							/*
							 * we verify if there is one character directly behind the current character
							 * there must have nothing between the two characters, and the if the the
							 * current character is in the zombi state
							 */

							if (this.characterDirectlyBehind == true && this.zombie == true) {
								super.remove = true;
								super.behindCharacter.setLiving(false);
								super.behindCharacter.remove = true;
								collision = true;
								App.scene.getMario().updateState(MarioState.KILLING_ANTAGONIST, true);
							}

							if (collision == true) {
								this.setLiving(false);
								super.remove = true;
								if (super.frontCharacter != null && super.behindCharacter.behindCharacter != null) {
									super.frontCharacter.behindCharacter = super.behindCharacter.behindCharacter;
									super.behindCharacter.behindCharacter.frontCharacter = super.frontCharacter;
								}
								App.scene.getMario().updateState(MarioState.KILLING_ANTAGONIST, true);
							}
							super.setToRight(true);
							this.dxTurtle = 1;
							super.setX(super.getX() + this.dxTurtle);
						}

						if (super.getX() + super.getWidth() >= zoneMax) {
							/*
							 * we verify if there is one character directly in front of this character there
							 * must have nothing between the two characters , and the if the front character
							 * is in the zombi state
							 */
							boolean collision = false;
							if (this.characterDirectlyFront == true && this.frontCharacter.zombie == true) {
								super.frontCharacter.setLiving(false);
								super.frontCharacter.remove = true;
								super.remove = true;
								collision = true;
								App.scene.getMario().updateState(MarioState.KILLING_ANTAGONIST, true);
							}

							/*
							 * we verify if there is one character directly in front of the current
							 * character there must have nothing between the two characters, and the if the
							 * the current character is in the zombi state
							 */
							if (this.characterDirectlyFront == true && this.zombie == true) {
								super.remove = true;
								this.frontCharacter.setLiving(false);
								super.frontCharacter.remove = true;
								collision = true;
							}

							if (collision == true) {
								super.setLiving(false);
								super.remove = true;
								if (super.behindCharacter != null && super.frontCharacter.frontCharacter != null) {
									super.behindCharacter.frontCharacter = super.frontCharacter.frontCharacter;
									super.frontCharacter.frontCharacter.behindCharacter = super.behindCharacter;
								}
								App.scene.getMario().updateState(MarioState.KILLING_ANTAGONIST, true);
							}

							super.setToRight(false);
							this.dxTurtle = -1;
							super.setX(super.getX() + this.dxTurtle);

						}
					}

					this.kill(App.scene.getMario());
					if (super.nbreOfLive <= 0) {
						// super.getThread().stop();
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

		if (super.isWalke() == false) {
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

		img = new Image(getClass().getResource(str).toExternalForm());
		return img;
	}

	@Override
	public void kill(Mario mario) {
		int precision = 0;
		if (super.isLiving() == false)
			precision = 27;

		if (mario.near(this, Axe.VERTICAL, precision) == true && mario.isLiving() == true) {
			if (mario.bottomCollision(this) == true) {
				if (mario.isJump() == true || mario.isFall() == true) {
					if (super.isLiving() == true) {
						mario.updateState(MarioState.KILLING_ANTAGONIST, true);
						super.nbreOfLive -= 1;
						super.setLiving(false);
						this.justDie = true;
						super.setHeight(23);
						super.setWidth(25);
						super.setY(Config.Y_MAX - super.getHeight());
						// in the case that turtle is dead since a long time it become zombi
					} else if (this.justDie == false) {
						mario.updateState(MarioState.ZOMBIFIYING_ANTAGONIST, true);
						this.zombie = true;
						super.setToRight(mario.isToRight());
						int zoneMin, zoneMax;
						zoneMin = super.getZoneMin(super.behindCharacter, super.behindObject);
						zoneMax = super.getZoneMax(super.frontCharacter, super.frontObject);
						if (mario.isToRight() == true) {
							if (super.getX() + mario.getWidth() > zoneMax) {
								super.setX(super.getX() + zoneMax - (super.getX() + super.getWidth()) - 1);
							} else
								super.setX(super.getX() + mario.getWidth());

						} else {
							if (super.getX() - mario.getWidth() < zoneMin) {
								super.setX(zoneMin + 1);
							} else {
								super.setX(super.getX() - mario.getWidth());
							}
						}
					}
				}
			}

			if (mario.isJump() == false && mario.isFall() == false && (this.zombie == true || super.isLiving() == true)
					&& justDie == false) {
				if (mario.frontCollision(this) == true || mario.backCollision(this) == true) {
					mario.updateState(MarioState.HIT_BY_ANTAGONIST, true);
					// System.out.println(this.zombie);
					mario.setNumberOfLive(mario.getNumberOfLive() - 1);
					// System.out.println("nombre de vie: "+mario.getNumberOfLive());
					mario.setLiving(false);
					// Audio.playSong("/audio/game-over.wav");
					GameManager.setState(TransitionState.REDUCING_LIVE);
					if (mario.getNumberOfLive() <= 0) {
						System.out.println("tutle position x " + this.getX());
						mario.updateState(MarioState.DEAD, true);
					}

				}
			}
		} else
			this.justDie = false;
	}

	@Override
	public Image die() {
		super.setHeight(23);
		super.setWidth(25);
		Image imgTurtle = new Image(getClass().getResource("images/tortueFermee.png").toExternalForm());
		return imgTurtle;
	}

}
