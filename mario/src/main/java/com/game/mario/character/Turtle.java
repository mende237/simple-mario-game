package com.game.mario.character;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import com.game.mario.game.GameManager;
import com.game.mario.App;
import com.game.mario.util.Axe;
import com.game.mario.util.TransitionState;

public class Turtle extends Antagonist implements Runnable {

	/***************************************
	 * property
	 *******************************************/
	private ImageView icoTurtle;
	private Image imageTurtle;
	private int dxTortue;
	private int PAUSE = 50;// the duration of break
	private boolean justDie = false;

	/**************************************
	 * constructor
	 *****************************************/
	public Turtle(int x, int y) {
		super(x, y, 43, 50);
		super.nbreOfLive = 2;
		super.setToRight(true);
		super.setWalke(true);
		this.zombie = false;
		// this.icoTortue = new
		// ImageIcon(getClass().getResource("/images/champArretDroite.png"));
		// this.imageTortue = icoTortue.getImage();
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

		while (true) {
			if (this.zombie == true)
				PAUSE = 5;
			else
				PAUSE = 50;
			// when the character is dead we stop its thread
			if (super.remove == true) {
				super.getThread().interrupt();
				break;
			}

			this.move();
			try {
				Thread.sleep(PAUSE);
			} catch (InterruptedException e) {
				e.printStackTrace();
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
		if (GameManager.isBegin() == true) {
			// we verify if another character is not running its own critical region
			if (GameManager.DOWN() >= 0) {
				// we determine the area in which the character can move without any problems
				int zoneMin = super.getZoneMin(super.behindCharacter, super.behindObjet);
				int zoneMax = super.getZoneMax(super.frontCharacter, super.frontObjet);
				// we can move if only if the character is alive or in zombi state
				if (super.isWalke() == true && (super.isLiving() == true || this.zombie == true)) {
					if (super.getX() + super.getWidth() < zoneMax && super.getX() > zoneMin) {
						if (super.isToRight() == true) {
							this.dxTortue = 1;
							super.setX(super.getX() + this.dxTortue);
						} else {
							this.dxTortue = -1;
							super.setX(super.getX() + this.dxTortue);
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
						}

						if (collision == true) {
							this.setLiving(false);
							super.remove = true;
							if (super.frontCharacter != null && super.behindCharacter.behindCharacter != null) {
								super.frontCharacter.behindCharacter = super.behindCharacter.behindCharacter;
								super.behindCharacter.behindCharacter.frontCharacter = super.frontCharacter;
							}
						}
						super.setToRight(true);
						this.dxTortue = 1;
						super.setX(super.getX() + this.dxTortue);
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
						}

						super.setToRight(false);
						this.dxTortue = -1;
						super.setX(super.getX() + this.dxTortue);

					}
				}

				this.kill(App.scene.mario);
				if (super.nbreOfLive <= 0) {
					// super.getThread().stop();
					super.remove = true;
				}

				GameManager.UP();
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
		// ico = new ImageIcon(getClass().getResource(str));
		// img = ico.getImage();
		img = new Image(getClass().getResource(str).toExternalForm());
		return img;
	}

	@Override
	public void kill(Mario mario) {
		// super.pause = true;
		int precision = 0;
		if (super.isLiving() == false)
			precision = 27;

		if (mario.near(this, Axe.VERTICAL, precision) == true && mario.isLiving() == true) {
			if (mario.bottomCollision(this) == true) {
				if (mario.isJump() == true || mario.isFall() == true) {
					if (super.isLiving() == true) {
						super.nbreOfLive -= 1;
						super.setLiving(false);
						this.justDie = true;
						super.setHeight(23);
						super.setWidth(25);
						super.setY(293 - super.getHeight());
						// dans le cas ou la tortue ne vient pas de mourir elle passe a l'etat zombie
						// in the case that turtle is dead since a long time it become zombi
					} else if (this.justDie == false) {

						this.zombie = true;
						super.setToRight(mario.isToRight());
						int zoneMin, zoneMax;
						zoneMin = super.getZoneMin(super.behindCharacter, super.behindObjet);
						zoneMax = super.getZoneMax(super.frontCharacter, super.frontObjet);
						if (mario.isToRight() == true) {
							if (super.getX() + 29 > zoneMax) {
								super.setX(super.getX() + zoneMax - (super.getX() + super.getWidth()) - 1);
							} else
								super.setX(super.getX() + 29);

						} else {
							if (super.getX() - 29 < zoneMin) {
								super.setX(zoneMin + 1);
							} else {
								super.setX(super.getX() - 29);
							}
						}
					}
				}
			}

			if (mario.isJump() == false && mario.isFall() == false && (this.zombie == true || super.isLiving() == true)
					&& justDie == false) {
				if (mario.frontCollision(this) == true || mario.backCollision(this) == true) {
					// System.out.println(this.zombie);
					mario.setNumberOfLive(mario.getNumberOfLive() - 1);
					// System.out.println("nombre de vie: "+mario.getNumberOfLive());
					mario.setLiving(false);
					// Audio.playSong("/audio/game-over.wav");
					GameManager.setInterupt(true);
					GameManager.setState(TransitionState.DIE);
					if (mario.getNumberOfLive() <= 0) {
						GameManager.setState(TransitionState.GAMEOVER);
					}

				}
			}
		} else
			this.justDie = false;

		// super.pause = false;
	}

	@Override
	public Image die() {
		super.setHeight(23);
		super.setWidth(25);
		// ImageIcon icoTortueMort = new
		// ImageIcon(getClass().getResource("/images/tortueFermee.png"));
		// Image imgTortue = icoTortueMort.getImage();
		Image imgTortue = new Image(getClass().getResource("images/tortueFermee.png").toExternalForm());
		return imgTortue;
	}

}
