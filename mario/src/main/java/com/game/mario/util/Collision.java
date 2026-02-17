package com.game.mario.util;

import java.util.ArrayList;

// import com.mathmaurer.audio.Audio;
import com.game.mario.game.GameManager;
import com.game.mario.item.GameItem;
import com.game.mario.item.Coin;
import com.game.mario.character.GameCharacter;
import com.game.mario.character.Antagonist;

import com.game.mario.character.Mario;

public class Collision {
	public enum Position {
		BEHIND, FRONT
	}

	/**
	 * Manages collision detection and collection of coins by Mario.
	 * Removes collected coins from the list and updates Mario's score.
	 *
	 * @param pieceTab the list of coins available in the game world
	 * @param mario    the Mario character instance
	 */
	public static void piece(ArrayList<Coin> pieceTab, Mario mario) {
		int minus = 0;
		if (pieceTab.size() > 1) {
			int tab[] = marioBetweenObject(pieceTab, 0, pieceTab.size() - 1, 0, 30, mario);
			if (tab != null) {
				int begin = tab[0];
				int end = tab[1];

				if (begin == -1) {
					begin = 0;
				}

				if (end == -2) {
					end = 0;
				}

				for (int i = 0; i <= end - begin - minus; i++) {
					if (mario.contactCoin(pieceTab.get(begin + i)) == true) {
						pieceTab.remove(begin + i);
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

	/**
	 * Manages collision detection between Mario and game objects.
	 * Determines if Mario is in contact with objects and handles merged object
	 * collisions.
	 *
	 * @param objectTab the list of game objects to check for collision with Mario
	 * @param precision the collision detection precision range in pixels
	 * @param mario     the Mario character instance
	 * @return true if Mario is in contact with an object, false otherwise
	 */
	public static boolean mario(ArrayList<? extends GameItem> objectTab, int precision, Mario mario) {
		int indexMemory = 0, cmptMerge = 0, cmpt = 0;
		boolean enter = false;
		// on determines les objects qui sont autour de mario
		int tab[] = marioBetweenObject(objectTab, 0, objectTab.size() - 1, 0, precision, mario);
		if (tab != null) {
			int begin = tab[0];
			int end = tab[1];

			if (begin == -1) {
				begin = 0;
			}

			if (end == -2) {
				end = 0;
			}

			for (int i = 0; i < end - begin + 1; i++) {
				if (mario.near(objectTab.get(begin + i)) == true) {
					cmptMerge++;
					if (enter == false) {
						indexMemory = begin + i;
						enter = true;
					}

				} else {
					cmpt++;
				}

			}
			// dans le cas ou mario n'est proche d'aucun object
			if (cmpt == end - begin + 1) {
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

	/**
	 * Manages collision detection for antagonists with game objects and other
	 * antagonists.
	 * Sets the blocking objects and nearby antagonists for each antagonist in the
	 * list.
	 *
	 * @param antagonistsTab the list of antagonists to process
	 * @param objectTab      the list of game objects that may block antagonist
	 *                       movement
	 * @param xMax           the maximum x-axis coordinate boundary
	 */
	public static void antagonist(ArrayList<Antagonist> antagonistsTab, ArrayList<? extends GameItem> objectTab,
			int xMax) {
		Antagonist tabC[];
		int tabO[];

		for (int j = 0; j < antagonistsTab.size(); j++) {
			tabO = antagonistBetweenObject(objectTab, 0, objectTab.size() - 1, 0, xMax, antagonistsTab.get(j));
			antagonistsTab.get(j).setBehindObject(tabO[0]);
			antagonistsTab.get(j).setFrontObject(tabO[1]);

			/*
			 * on retire le personnage du tableau, une même personne est toujours proche de
			 * d'elle meme
			 */
			Antagonist tempAntagonist = antagonistsTab.remove(j);
			if (antagonistsTab.size() >= 1) {
				GameManager.getAllAntagonistPositionWriterLocker().lock();
				if (!GameManager.getAllAntagonistPositionReaderLocker().isLocked()) {
					tabC = aroundCharacter(antagonistsTab, 0, antagonistsTab.size() - 1, 0, tempAntagonist);
					setNear(tempAntagonist, tabC[0], Position.BEHIND);
					setNear(tempAntagonist, tabC[1], Position.FRONT);
				}
				GameManager.getAllAntagonistPositionWriterLocker().unlock();
			}

			antagonistsTab.add(j, tempAntagonist);
		}

	}

	/**
	 * Updates the nearest character reference for a target antagonist if the
	 * provided
	 * character is closer than the previously stored one.
	 *
	 * @param targerCharacter the target antagonist whose nearest character will be
	 *                        updated
	 * @param character       the antagonist character to potentially set as nearest
	 * @param position        the direction position (BEHIND or FRONT) relative to
	 *                        the target
	 */
	public static void setNear(Antagonist targerCharacter, Antagonist character, Position position) {
		if (position == Position.BEHIND) {
			if (targerCharacter.getBehindCharacter() == null)
				targerCharacter.setBehindCharacter(character);
			else if (character != null) {
				if (character.getX() + character.getWidth() >= targerCharacter.getBehindCharacter().getX()
						+ targerCharacter.getBehindCharacter().getWidth()) {
					targerCharacter.setBehindCharacter(character);
				}
			}
		} else {
			if (targerCharacter.getFrontCharacter() == null) {
				targerCharacter.setFrontCharacter(character);
			} else if (character != null) {
				if (character.getX() <= targerCharacter.getFrontCharacter().getX()) {
					targerCharacter.setFrontCharacter(character);
				}
			}
		}
	}

	/**
	 * Uses binary search to find game objects closest to Mario's x-position.
	 * Returns indices of objects on either side of or containing Mario.
	 *
	 * @param tab       the sorted list of game items to search through
	 * @param begin     the starting index for the binary search
	 * @param end       the ending index for the binary search
	 * @param middle    the middle index for the current binary search iteration
	 * @param precision the collision detection precision range in pixels
	 * @param character the game character (Mario) to check proximity to
	 * @return an array containing two indices: the index of the nearest object to
	 *         the left and right of Mario
	 */
	public static int[] marioBetweenObject(ArrayList<? extends GameItem> tab, int begin, int end, int middle,
			int precision,
			GameCharacter character) {
		middle = (begin + end) / 2;
		if (character.getX() + character.getWidth() < tab.get(middle).getX() - precision) {
			end = middle;
			if (begin < end)
				return marioBetweenObject(tab, begin, end, middle, precision, character);
			else {
				int array[] = new int[2];
				array[0] = -1;
				array[1] = begin;
				return array;
			}
		} else if (character.getX() > tab.get(middle).getX() + tab.get(middle).getWidth() + precision) {
			begin = middle;
			if (begin < end - 1)
				return marioBetweenObject(tab, begin, end, middle, precision, character);
			else if (begin == end - 1) {
				int array[] = new int[2];
				array[0] = begin;
				array[1] = end;
				return array;
			} else {
				int array[] = new int[2];
				array[0] = end;
				array[1] = -2;
				return array;
			}
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

	/**
	 * Checks if a character and game item have overlapping y-axis positions.
	 *
	 * @param character the game character to check
	 * @param gameItem  the game item to check for y-axis collision
	 * @return true if the character and item overlap on the y-axis, false otherwise
	 */
	public static boolean yCollision(GameCharacter character, GameItem gameItem) {
		return character.getY() + character.getHeight() > gameItem.getY()
				&& character.getY() < gameItem.getY() + gameItem.getHeight();
	}

	/**
	 * Uses binary search to find game objects that enclose or are adjacent to an
	 * antagonist.
	 * Returns the x-axis positions of blocking objects on either side of the
	 * antagonist.
	 *
	 * @param tab       the sorted list of game items to search through
	 * @param begin     the starting index for the binary search
	 * @param end       the ending index for the binary search
	 * @param middle    the middle index for the current binary search iteration
	 * @param xMax      the maximum x-axis coordinate boundary
	 * @param character the antagonist character to find enclosing objects for
	 * @return an array containing two x-axis positions: the right boundary of the
	 *         left blocking object and the left boundary of the right blocking
	 *         object
	 */
	public static int[] antagonistBetweenObject(ArrayList<? extends GameItem> tab, int begin, int end, int middle,
			int xMax,
			GameCharacter character) {
		middle = (begin + end) / 2;
		if (character.getX() + character.getWidth() <= tab.get(middle).getX()) {
			end = middle;
			if (begin < end) {
				if (middle > 0) {
					if (character.getX() >= tab.get(middle - 1).getX() + tab.get(middle - 1).getWidth()) {
						int array[] = new int[2];
						if (yCollision(character, tab.get(middle - 1))) {
							array[0] = tab.get(middle - 1).getX() + tab.get(middle - 1).getWidth();
						} else {
							array[0] = 0;
						}

						if (yCollision(character, tab.get(middle))) {
							array[1] = tab.get(middle).getX();
						} else {
							array[1] = xMax;
						}
						return array;
					}
				}

				return antagonistBetweenObject(tab, begin, end, middle, xMax, character);
			} else {
				int array[] = new int[2];
				array[0] = 0;
				if (yCollision(character, tab.get(begin))) {
					array[1] = tab.get(begin).getX();
				} else {
					array[1] = xMax;
				}
				return array;
			}
		} else if (character.getX() >= tab.get(middle).getX() + tab.get(middle).getWidth()) {
			begin = middle;
			if (begin < end - 1) {
				if (character.getX() + character.getWidth() <= tab.get(middle + 1).getX()) {
					int array[] = new int[2];

					if (yCollision(character, tab.get(middle))) {
						array[0] = tab.get(middle).getX() + tab.get(middle).getWidth();
					} else {
						array[0] = 0;
					}

					if (yCollision(character, tab.get(middle + 1))) {
						array[1] = tab.get(middle + 1).getX();
					} else {
						array[1] = xMax;
					}
					return array;
				}
				return antagonistBetweenObject(tab, begin, end, middle, xMax, character);
			} else if (begin == end - 1) {
				if (tab.get(begin).getX() + tab.get(begin).getWidth() <= character.getX()
						&& character.getX() + character.getWidth() <= tab.get(end).getX()) {
					int array[] = new int[2];
					if (yCollision(character, tab.get(begin))) {
						array[0] = tab.get(begin).getX() + tab.get(begin).getWidth();
					} else {
						array[0] = 0;
					}

					if (yCollision(character, tab.get(end))) {
						array[1] = tab.get(end).getX();
					} else {
						array[1] = xMax;
					}
					return array;
				} else {
					int array[] = new int[2];
					if (yCollision(character, tab.get(end))) {
						array[0] = tab.get(end).getX() + tab.get(end).getWidth();
					} else {
						array[0] = 0;
					}

					array[1] = xMax;
					return array;
				}
			} else {
				int array[] = new int[2];
				if (yCollision(character, tab.get(end))) {
					array[0] = tab.get(end).getX() + tab.get(end).getWidth();
				} else {
					array[0] = 0;
				}
				array[1] = xMax;
				return array;
			}
		} else {
			int array[] = new int[2];
			if (yCollision(character, tab.get(begin))) {
				array[0] = tab.get(begin).getX() + tab.get(begin).getWidth();
			} else {
				array[0] = 0;
			}

			if (yCollision(character, tab.get(end))) {
				array[1] = tab.get(end).getX();
			} else {
				array[1] = xMax;
			}

			return array;
		}
	}

	/**
	 * Removes antagonists marked for removal from the provided list.
	 *
	 * @param tab the list of antagonists to update by removing marked ones
	 */
	public static void updateTab(ArrayList<? extends Antagonist> tab) {
		for (int i = 0; i < tab.size(); i++) {
			if (tab.get(i).isRemove() == true)
				tab.remove(i);
		}
	}

	/**
	 * Uses binary search to find antagonists positioned on either side of a given
	 * character.
	 * Returns the nearest antagonist to the left and right of the target character.
	 *
	 * @param tab        the sorted list of antagonists to search through
	 * @param begin      the starting index for the binary search
	 * @param end        the ending index for the binary search
	 * @param middle     the middle index for the current binary search iteration
	 * @param personnage the target character to find surrounding antagonists for
	 * @return an array containing two antagonists: the nearest one behind (left)
	 *         and the nearest one in front (right)
	 */
	public static Antagonist[] aroundCharacter(ArrayList<? extends Antagonist> tab, int begin, int end, int middle,
			GameCharacter character) {
		middle = (begin + end) / 2;
		if (character.getX() + character.getWidth() < tab.get(middle).getX()) {
			end = middle;
			if (begin < end) {
				if (middle > 0) {
					if (character.getX() >= tab.get(middle - 1).getX() + tab.get(middle - 1).getWidth()) {
						Antagonist array[] = new Antagonist[2];
						array[0] = tab.get(middle - 1);
						array[1] = tab.get(middle);
						return array;
					}
				}

				return aroundCharacter(tab, begin, end, middle, character);
			} else {
				Antagonist array[] = new Antagonist[2];
				array[0] = null;
				array[1] = tab.get(begin);
				return array;
			}
		} else if (character.getX() > tab.get(middle).getX() + tab.get(middle).getWidth()) {
			begin = middle;
			if (begin < end - 1) {
				if (character.getX() + character.getWidth() < tab.get(middle + 1).getX()) {
					Antagonist array[] = new Antagonist[2];
					array[0] = tab.get(middle);
					array[1] = tab.get(middle + 1);
					return array;
				}
				return aroundCharacter(tab, begin, end, middle, character);
			} else if (begin == end - 1) {
				if (tab.get(begin).getX() + tab.get(begin).getWidth() < character.getX()
						&& character.getX() + character.getWidth() < tab.get(end).getX()) {
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
