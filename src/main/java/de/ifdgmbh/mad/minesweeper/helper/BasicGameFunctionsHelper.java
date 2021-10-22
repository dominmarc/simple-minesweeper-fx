/* 
 * Copyright (c) 2021 iFD GmbH Chemnitz http://www.ifd-gmbh.com
 */
package de.ifdgmbh.mad.minesweeper.helper;

import java.security.SecureRandom;

import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Region;
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
	 * @param SIZE  represents the number of fields in the game
	 * @param BOMBS represents the number of bombs in the game
	 */
	public static int[][] buildGamefield(final int SIZE, final int BOMBS) {
		// gamefield = (length + 2) X (length + 2)
		// +2 because initialized with (-1) frame
		final int length = (int) Math.sqrt(SIZE);

		int[][] gamefield = new int[length + 2][length + 2];

		// create bombs
		SecureRandom rand = new SecureRandom();
		for (int k = 1; k <= BOMBS; k++) {
			int var1;
			int var2;
			do {
				var1 = rand.nextInt(length) + 1;
				var2 = rand.nextInt(length) + 1;
			} while (gamefield[var1][var2] == 9);
			gamefield[var1][var2] = 9;
		}

		/** the number of bombs around the field */
		int bombCounter = 0;

		// loop through all fields
		for (int y = 1; y <= length; y++) {
			for (int x = 1; x <= length; x++) {
				// if field is a bomb (9) we can skip the field
				if (gamefield[x][y] != 9) {
					// loop through [][] around the current field (around x and around y)
					for (int aroundY = y - 1; aroundY <= y + 1; aroundY++) {
						for (int aroundX = x - 1; aroundX <= x + 1; aroundX++) {
							// check if there is a bomb
							if (gamefield[aroundX][aroundY] == 9) {
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
		for (int g = 0; g <= (length + 1); g++) {
			for (int h = 0; h <= (length + 1); h++) {
				if (g == 0 || g == (length + 1)) {
					gamefield[h][g] = -1;
				}
				if (h == 0 || h == (length + 1)) {
					gamefield[h][g] = -1;
				}
			}
		}

		return gamefield;
	}

	/**
	 * Function to set an absolute size setting to a node.
	 * 
	 * @param node   the element to set the size
	 * @param width
	 * @param height
	 * 
	 * @return the size edited not
	 */
	public static Region fixSize(Region node, double width, double height) {
		node.setMinSize(width, height);
		node.setMaxSize(width, height);
		node.setPrefSize(width, height);
		return node;
	}

	/**
	 * Function to set an absolute location to a node.</br>
	 * Along with that sets id to identify in style-file.
	 * 
	 * @param node the element to set the location
	 * @param x    coordinate of location
	 * @param y    coordinate of location
	 * 
	 * @return the location edited node
	 */
	public static Region fixLoc(Region node, String id, double x, double y) {
		node.setLayoutX(x);
		node.setLayoutY(y);
		// set id
		node.setId(id);
		return node;
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
	 * Background to visually show a bomb (red)
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
