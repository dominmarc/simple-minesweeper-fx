package de.ifdgmbh.mad.minesweeper.helper;

import java.security.SecureRandom;

import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

/**
 * Helper Class for minesweeper game by MAD
 * 
 * @author MAD
 * @author iFD
 */
public class BasicGameFunctionsHelper {

	private static final String BAR = "==============================================";

	/** Background to visually show a user marked field */
	private static final Background CHECKED = new Background(
			new BackgroundFill(Color.web("#FFFFFF"), null, new Insets(0)));;

	/** Background to visually show a bomb */
	private static final Background BOMB = new Background(
			new BackgroundFill(Color.web("#FA7D7D"), null, new Insets(0)));

	/** Background to visually show a raw field */
	private static final Background UNCHECKED = new Background(
			new BackgroundFill(Color.web("#DEDEDE"), null, new Insets(0)));

	/**
	 * Constructor
	 */
	private BasicGameFunctionsHelper() {
		// we don't need to construct objects of this class
	}

	/**
	 * Returns a fresh generated gamefield.
	 * 
	 * @param SIZE represents the number of fields in the game
	 */
	public static int[][] buildGamefield(final int SIZE) {
		// gamefield = (length + 2) X (length + 2)
		// +2 because initialized with (-1) frame
		final int length = (int) Math.sqrt(SIZE);

		int[][] gamefield = new int[length + 2][length + 2];

		// create 10 bombs
		SecureRandom rand = new SecureRandom();
		int bombs = 10;
		for (int k = 1; k <= bombs; k++) {
			int var1;
			int var2;
			do {
				var1 = rand.nextInt(length) + 1;
				var2 = rand.nextInt(length) + 1;
			} while (gamefield[var1][var2] == 9);
			gamefield[var1][var2] = 9;
		}

		int bombCounter = 0;

		// loop through all fields
		for (int y = 1; y < 10; y++) {
			for (int x = 1; x < 10; x++) {
				// if field is a bomb (9) we can skip the field
				if (gamefield[x][y] != 9) {
					// loop through [][] around the current field (around x and around y)
					for (int around_y = y - 1; around_y <= y + 1; around_y++) {
						for (int around_x = x - 1; around_x <= x + 1; around_x++) {
							// check if there is a bomb
							if (gamefield[around_x][around_y] == 9) {
								bombCounter++;
							}
						}
					}
					// value of the field represents the number of bombs around the field
					gamefield[x][y] = bombCounter;
				}
				bombCounter = 0;
			}
		}

		// print field && initialize the frame with -1 (0, 10)
		for (int g = 0; g < 11; g++) {
			for (int h = 0; h < 11; h++) {
				if (g == 0 || g == 10) {
					gamefield[h][g] = -1;
				}
				if (h == 0 || h == 10) {
					gamefield[h][g] = -1;
				}
			}
		}

		return gamefield;
	}

	public static String getPrintBar() {
		return BAR;
	}

	/**
	 * Background to visually show a user marked field
	 */
	public static Background getChecked() {
		return CHECKED;
	}

	/**
	 * Background to visually show a raw field
	 */
	public static Background getUnchecked() {
		return UNCHECKED;
	}

	/**
	 * Background to visually show a bomb
	 */
	public static Background getBomb() {
		return BOMB;
	}

	/**
	 * Tests for a user marked field
	 */
	public static boolean isChecked(Background input) {
		return (input.equals(CHECKED));
	}

	/**
	 * Tests for a bomb marked field
	 */
	public static boolean isBomb(Background input) {
		return (input.equals(BOMB));
	}

	/**
	 * Tests for a raw field
	 */
	public static boolean isUnchecked(Background input) {
		return (input.equals(UNCHECKED));
	}
}
