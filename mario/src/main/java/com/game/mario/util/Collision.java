package com.game.mario.util;

import java.util.ArrayList;

// import com.mathmaurer.audio.Audio;
import com.game.mario.game.GameManager;
import com.game.mario.item.GameItem;
import com.game.mario.item.Coin;
import com.game.mario.character.GameCharacter;
import com.game.mario.character.Antagonist;
import com.game.mario.character.Champignon;
import com.game.mario.character.Turtle;
import com.game.mario.character.Mario;

public class Collision {
	public enum Position {
		BEHIND, FRONT
	}

	/*
	 * this function manage the collision with each champignon , tortue which are in
	 * an array
	 */
	public static void Champignon(ArrayList<Champignon> champTab, ArrayList<Turtle> tortueTab, Mario mario) {
		for (int i = 0; i < champTab.size(); i++) {
			if (champTab.get(i).isLiving() == true) {// on verifie que le champignon n'est pas mort
				// on verifie si mario tue le champignon ou le contraire
				champTab.get(i).kill(mario);
				// collision du champignon avec la tortue
				for (int j = 0; j < tortueTab.size(); j++) {
					if (tortueTab.get(j).isLiving() == true) {// on verifie que la tortue est vivante
						if (champTab.get(i).near(tortueTab.get(j)) == true) {
							System.out.println("near");
							champTab.get(i).contact(tortueTab.get(j));
						}
					}
				}

				// collision du champignon avec un autre champignon
				for (int j = 0; j < champTab.size(); j++) {
					if (champTab.get(j).isLiving() == true) {
						if (i != j) {
							if (champTab.get(i).near(champTab.get(j)) == true) {
								champTab.get(i).contact(champTab.get(j));
							}
						}
					}
				}
			}

		}

	}

	public static void tortue(ArrayList<Turtle> tortueTab, ArrayList<? extends GameCharacter> champTab, Mario mario) {
		for (int i = 0; i < tortueTab.size(); i++) {
			tortueTab.get(i).kill(mario);
			if (tortueTab.get(i).isLiving() == true || tortueTab.get(i).isZombie() == true) {
				// collision de la tortue avec une autre tortue
				for (int j = 0; j < tortueTab.size(); j++) {
					if (i != j && tortueTab.get(j).isLiving() == true) {// on verifie que la tortue n'est pas morte
						if (tortueTab.get(i).near(tortueTab.get(j)) == true) {
							if (tortueTab.get(i).isZombie() == true) {
								tortueTab.get(j).setLiving(false);
							} else {

								tortueTab.get(i).contact(champTab.get(j));
							}
						}
					}
				}

				// collision de la tortue avec un champignon
				for (int j = 0; j < champTab.size(); j++) {
					if (champTab.get(j).isLiving() == true) {
						if (tortueTab.get(i).near(champTab.get(j))) {
							if (tortueTab.get(i).isZombie() == true) {
								champTab.get(j).setLiving(false);
							} else {

								tortueTab.get(i).contact(champTab.get(j));
							}
						}
					}
				}

			}
		}
	}

	public static void piece(ArrayList<Coin> pieceTab, Mario mario) {
		int minus = 0;
		if (pieceTab.size() > 1) {
			int tab[] = around(pieceTab, 0, pieceTab.size() - 1, 0, 30, mario);
			if (tab != null) {
				for (int i = 0; i <= tab[1] - tab[0] - minus; i++) {
					if (mario.contactCoin(pieceTab.get(tab[0] + i)) == true) {
						pieceTab.remove(tab[0] + i);
						minus++;
						mario.setScore(mario.getScore() + 1);
						// Audio.playSong("/audio/piece.wav");
					}

				}
			}
		} else if (pieceTab.size() == 1) {
			if (mario.contactCoin(pieceTab.get(0)) == true) {
				pieceTab.remove(0);
				mario.setScore(mario.getScore() + 1);
			}

		}

	}

	/* this function manage the collision with mario and objects */
	public static boolean mario(ArrayList<? extends GameItem> objectTab, int precision, Mario mario) {
		int indexMemory = 0, cmptMerge = 0, cmpt = 0;
		boolean enter = false;
		// on determines les objects qui sont autour de mario
		int tab[] = around(objectTab, 0, objectTab.size() - 1, 0, precision, mario);
		if (tab != null) {
			for (int i = 0; i < tab[1] - tab[0] + 1; i++) {
				if (mario.near(objectTab.get(tab[0] + i)) == true) {
					cmptMerge++;
					if (enter == false) {
						indexMemory = tab[0] + i;
						enter = true;
					}

				} else {
					cmpt++;
				}

			}
			// dans le cas ou mario n'est proche d'aucun object
			if (cmpt == tab[1] - tab[0] + 1) {
				if (mario.isJump() == false) {
					mario.setIsOnObject(false);
				}
				return false;
			} else {
				// in case of mario is near two objects at the same time
				// the two objects are merged together
				if (cmptMerge >= 2) {
					mario.contact(objectTab.get(indexMemory), true);
				} else {
					mario.contact(objectTab.get(indexMemory), false);
				}

				return true;
			}

		} else {
			if (mario.isJump() == false) {
				mario.setIsOnObject(false);
			}
			return false;
		}

	}

	public static void tortue(ArrayList<Turtle> tortueTab, ArrayList<Champignon> champTab,
			ArrayList<? extends GameItem> objectTab, int xMax) {
		Antagonist tabC[];
		int tabO[];
		if (GameManager.DOWN() >= 0) {
			for (int j = 0; j < tortueTab.size(); j++) {
				tabO = aroundObject(objectTab, 0, objectTab.size() - 1, 0, xMax, tortueTab.get(j));
				tortueTab.get(j).setBehindObject(tabO[0]);
				tortueTab.get(j).setFrontObject(tabO[1]);

				if (champTab.size() >= 1) {
					tabC = aroundCharacter(champTab, 0, champTab.size() - 1, 0, tortueTab.get(j));
					tortueTab.get(j).setBehindCharacter(tabC[0]);
					tortueTab.get(j).setFrontCharacter(tabC[1]);
				}
				/*
				 * on retire le personnage du tableau une même personne est toujours proche de
				 * d'elle meme
				 */
				Turtle temp = tortueTab.remove(j);
				if (tortueTab.size() >= 1) {
					tabC = aroundCharacter(tortueTab, 0, tortueTab.size() - 1, 0, temp);
					setNear(temp, tabC[0], Position.BEHIND);
					setNear(temp, tabC[1], Position.FRONT);
				}
				tortueTab.add(j, temp);
			}
			GameManager.UP();
		}
	}

	public static void antagonist(ArrayList<Antagonist> antagonistsTab, ArrayList<? extends GameItem> objectTab,
			int xMax) {
		Antagonist tabC[];
		int tabO[];
		if (GameManager.DOWN() >= 0) {
			for (int j = 0; j < antagonistsTab.size(); j++) {
				tabO = aroundObject(objectTab, 0, objectTab.size() - 1, 0, xMax, antagonistsTab.get(j));
				antagonistsTab.get(j).setBehindObject(tabO[0]);
				antagonistsTab.get(j).setFrontObject(tabO[1]);

				/*
				 * on retire le personnage du tableau, une même personne est toujours proche de
				 * d'elle meme
				 */
				Antagonist tempAntagonist = antagonistsTab.remove(j);
				if (antagonistsTab.size() >= 1) {
					tabC = aroundCharacter(antagonistsTab, 0, antagonistsTab.size() - 1, 0, tempAntagonist);

					setNear(tempAntagonist, tabC[0], Position.BEHIND);
					setNear(tempAntagonist, tabC[1], Position.FRONT);
				}

				antagonistsTab.add(j, tempAntagonist);
			}
			GameManager.UP();
		}
	}

	/* collision de chaque champignon S */
	public static void chamignon(ArrayList<Champignon> champTab, ArrayList<Turtle> tortueTab,
			ArrayList<? extends GameItem> objectTab, int xMax) {
		Antagonist tabC[];
		int tabO[];
		if (GameManager.DOWN() >= 0) {
			for (int j = 0; j < champTab.size(); j++) {
				tabO = aroundObject(objectTab, 0, objectTab.size() - 1, 0, xMax, champTab.get(j));
				champTab.get(j).setBehindObject(tabO[0]);
				champTab.get(j).setFrontObject(tabO[1]);
				if (tortueTab.size() >= 1) {
					tabC = aroundCharacter(tortueTab, 0, tortueTab.size() - 1, 0, champTab.get(j));
					champTab.get(j).setBehindCharacter(tabC[0]);
					champTab.get(j).setFrontCharacter(tabC[1]);
				}

				Champignon temp = champTab.remove(j);
				if (champTab.size() >= 1) {
					tabC = aroundCharacter(champTab, 0, champTab.size() - 1, 0, temp);
					setNear(temp, tabC[0], Position.BEHIND);
					setNear(temp, tabC[1], Position.FRONT);
				}
				champTab.add(j, temp);
			}
			GameManager.UP();
		}

	}

	/*
	 * this fonction modifies the character closest to a character if the one that
	 * is passed
	 * through parameter is closer than the one which was there before
	 */
	public static void setNear(Antagonist personnageCible, Antagonist personnage, Position position) {
		if (position == Position.BEHIND) {
			if (personnageCible.getBehindCharacter() == null)
				personnageCible.setBehindCharacter(personnage);
			else if (personnage != null) {
				if (personnage.getX() + personnage.getWidth() >= personnageCible.getBehindCharacter().getX()
						+ personnageCible.getBehindCharacter().getWidth()) {
					personnageCible.setBehindCharacter(personnage);
				}
			}
		} else {
			if (personnageCible.getFrontCharacter() == null) {
				personnageCible.setFrontCharacter(personnage);
			} else if (personnage != null) {
				if (personnage.getX() <= personnageCible.getFrontCharacter().getX()) {
					personnageCible.setFrontCharacter(personnage);
				}
			}
		}
	}

	/*
	 * this function return the index of the two object which are extremely near of
	 * abscissa which is passed in parameter of the function
	 * to achieve that it uses the principle of dichotomous search
	 */
	public static int[] around(ArrayList<? extends GameItem> tab, int begin, int end, int middle, int precision,
			GameCharacter personnage) {
		middle = (begin + end) / 2;
		if (personnage.getX() + personnage.getWidth() < tab.get(middle).getX() - precision) {
			end = middle;
			if (begin < end)
				return around(tab, begin, end, middle, precision, personnage);
			else
				return null;
		} else if (personnage.getX() > tab.get(middle).getX() + tab.get(middle).getWidth() + precision) {
			begin = middle;
			if (begin < end - 1)
				return around(tab, begin, end, middle, precision, personnage);
			else if (begin == end - 1) {
				int array[] = new int[2];
				array[0] = begin;
				array[1] = end;
				return array;
			} else
				return null;
		} else {
			int array[] = new int[2];
			if (middle >= 2 && middle <= end - 2) {
				array[0] = middle - 2;
				array[1] = middle + 2;
			} else {
				array[0] = begin;
				array[1] = end;
			}
			return array;
		}
	}

	public static boolean yCollision(GameCharacter character, GameItem gameItem) {
		return character.getY() + character.getHeight() > gameItem.getY()
				&& character.getY() < gameItem.getY() + gameItem.getHeight();
	}

	/*
	 * this function gives two the objects which enclose an character
	 * to achieve that it uses the principle of dichotomous search
	 */
	public static int[] aroundObject(ArrayList<? extends GameItem> tab, int begin, int end, int middle, int xMax,
			GameCharacter personnage) {
		middle = (begin + end) / 2;
		if (personnage.getX() + personnage.getWidth() < tab.get(middle).getX()) {
			end = middle;
			if (begin < end) {
				if (middle > 0) {
					if (personnage.getX() >= tab.get(middle - 1).getX() + tab.get(middle - 1).getWidth()) {
						int array[] = new int[2];
						if (yCollision(personnage, tab.get(middle - 1))) {
							array[0] = tab.get(middle - 1).getX() + tab.get(middle - 1).getWidth();
						} else {
							array[0] = 0;
						}

						if (yCollision(personnage, tab.get(middle))) {
							array[1] = tab.get(middle).getX();
						} else {
							array[1] = xMax;
						}
						return array;
					}
				}

				return aroundObject(tab, begin, end, middle, xMax, personnage);
			} else {
				int array[] = new int[2];
				array[0] = 0;
				if (yCollision(personnage, tab.get(begin))) {
					array[1] = tab.get(begin).getX();
				} else {
					array[1] = xMax;
				}
				return array;
			}
		} else if (personnage.getX() > tab.get(middle).getX() + tab.get(middle).getWidth()) {
			begin = middle;
			if (begin < end - 1) {
				if (personnage.getX() + personnage.getWidth() < tab.get(middle + 1).getX()) {
					int array[] = new int[2];

					if (yCollision(personnage, tab.get(middle))) {
						array[0] = tab.get(middle).getX() + tab.get(middle).getWidth();
					} else {
						array[0] = 0;
					}

					if (yCollision(personnage, tab.get(middle + 1))) {
						array[1] = tab.get(middle + 1).getX();
					} else {
						array[1] = xMax;
					}
					return array;
				}
				return aroundObject(tab, begin, end, middle, xMax, personnage);
			} else if (begin == end - 1) {
				if (tab.get(begin).getX() + tab.get(begin).getWidth() < personnage.getX()
						&& personnage.getX() + personnage.getWidth() < tab.get(end).getX()) {
					int array[] = new int[2];
					if (yCollision(personnage, tab.get(begin))) {
						array[0] = tab.get(begin).getX() + tab.get(begin).getWidth();
					} else {
						array[0] = 0;
					}

					if (yCollision(personnage, tab.get(end))) {
						array[1] = tab.get(end).getX();
					} else {
						array[1] = xMax;
					}
					return array;
				} else {
					int array[] = new int[2];
					if (yCollision(personnage, tab.get(end))) {
						array[0] = tab.get(end).getX() + tab.get(end).getWidth();
					} else {
						array[0] = 0;
					}

					array[1] = xMax;
					return array;
				}
			} else {
				int array[] = new int[2];
				if (yCollision(personnage, tab.get(end))) {
					array[0] = tab.get(end).getX() + tab.get(end).getWidth();
				} else {
					array[0] = 0;
				}
				array[1] = xMax;
				return array;
			}
		} else {
			int array[] = new int[2];
			if (yCollision(personnage, tab.get(begin))) {
				array[0] = tab.get(begin).getX() + tab.get(begin).getWidth();
			} else {
				array[0] = 0;
			}

			if (yCollision(personnage, tab.get(end))) {
				array[1] = tab.get(end).getX();
			} else {
				array[1] = xMax;
			}

			return array;
		}
	}

	public static void updateTab(ArrayList<? extends Antagonist> tab) {
		for (int i = 0; i < tab.size(); i++) {
			if (tab.get(i).isRemove() == true)
				tab.remove(i);
		}
	}

	/*
	 * this function give two the characters which enclose an character
	 * to achieve that it uses the principle of dichotomous search
	 */
	public static Antagonist[] aroundCharacter(ArrayList<? extends Antagonist> tab, int begin, int end, int middle,
			GameCharacter personnage) {
		middle = (begin + end) / 2;
		if (personnage.getX() + personnage.getWidth() < tab.get(middle).getX()) {
			end = middle;
			if (begin < end) {
				if (middle > 0) {
					if (personnage.getX() >= tab.get(middle - 1).getX() + tab.get(middle - 1).getWidth()) {
						Antagonist array[] = new Antagonist[2];
						array[0] = tab.get(middle - 1);
						array[1] = tab.get(middle);
						return array;
					}
				}

				return aroundCharacter(tab, begin, end, middle, personnage);
			} else {
				Antagonist array[] = new Antagonist[2];
				array[0] = null;
				array[1] = tab.get(begin);
				return array;
			}
		} else if (personnage.getX() > tab.get(middle).getX() + tab.get(middle).getWidth()) {
			begin = middle;
			if (begin < end - 1) {
				if (personnage.getX() + personnage.getWidth() < tab.get(middle + 1).getX()) {
					Antagonist array[] = new Antagonist[2];
					array[0] = tab.get(middle);
					array[1] = tab.get(middle + 1);
					return array;
				}
				return aroundCharacter(tab, begin, end, middle, personnage);
			} else if (begin == end - 1) {
				if (tab.get(begin).getX() + tab.get(begin).getWidth() < personnage.getX()
						&& personnage.getX() + personnage.getWidth() < tab.get(end).getX()) {
					Antagonist array[] = new Antagonist[2];
					array[0] = tab.get(begin);
					array[1] = tab.get(end);
					return array;
				} else {
					Antagonist array[] = new Antagonist[2];
					array[0] = tab.get(end);
					array[1] = null;
					return array;
				}
			} else {
				Antagonist array[] = new Antagonist[2];
				array[0] = tab.get(end);
				array[1] = null;
				return array;
			}
		} else {
			Antagonist array[] = new Antagonist[2];
			array[0] = tab.get(begin);
			array[1] = tab.get(end);
			return array;
		}
	}

}
