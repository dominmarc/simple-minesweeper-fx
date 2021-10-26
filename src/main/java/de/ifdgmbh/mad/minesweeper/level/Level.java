/* 
 * Copyright (c) 2021 iFD GmbH Chemnitz http://www.ifd-gmbh.com
 */
package de.ifdgmbh.mad.minesweeper.level;

/**
 * Level class for simple minesweeper
 * 
 * @author mad
 */
public class Level {
	/** number of bombs */
	private static int bombs;
	/** number of fields */
	private static int fields;
	/** level difficulty */
	private static LevelType difficulty;

	/** static level instance */
	private static final Level level = new Level();

	/**
	 * Enumeration of all game levels
	 * 
	 * @author mad
	 */
	public enum LevelType {

		EASY {
			@Override
			public String toString() {
				return "EASY";
			}
		},
		INTERMEDIATE {
			@Override
			public String toString() {
				return "INTERMEDIATE";
			}
		},
		HARD {
			@Override
			public String toString() {
				return "HARD";
			}
		},
		CUSTOM {
			@Override
			public String toString() {
				return "CUSTOM";
			}
		}
	}

	/**
	 * Creates an instance of an easy level.
	 * 
	 * @return Level Object
	 */
	public static Level getEasyLevel() {
		bombs = 10;
		fields = (9 * 9);
		difficulty = LevelType.EASY;
		return level;
	}

	/**
	 * Creates an instance of an intermediate level.
	 * 
	 * @return Level Object
	 */
	public static Level getIntermediateLevel() {
		bombs = 40;
		fields = (16 * 16);
		difficulty = LevelType.INTERMEDIATE;
		return level;
	}

	/**
	 * Creates an instance of a hard level.
	 * 
	 * @return Level Object
	 */
	public static Level getHardLevel() {
		bombs = 99;
		fields = (22 * 22);
		difficulty = LevelType.HARD;
		return level;
	}

	/**
	 * Creates an instance of a custom level.
	 * 
	 * @return Level Object
	 */
	public static Level getCustomLevel(final int bombCount, final int fieldCount) {
		bombs = bombCount;
		fields = fieldCount;
		difficulty = LevelType.CUSTOM;
		return level;
	}

	/**
	 * Returns the level object based on the specified type.</br>
	 * Note: type "Custom" will return null! </br>
	 * To get a custom level call {@link getCustomLevel} !</br>
	 * 
	 * @param type the type of level
	 * 
	 * @return Level object according to type
	 */
	public static Level getLevel(LevelType type) {
		switch (type) {
		case EASY:
			return getEasyLevel();
		case INTERMEDIATE:
			return getIntermediateLevel();
		case HARD:
			return getHardLevel();
		default:
			return null;
		}
	}

	/**
	 * Returns the number of bombs.
	 */
	public int getBombCount() {
		return bombs;
	}

	/**
	 * Returns the number of fields.
	 */
	public int getFieldCount() {
		return fields;
	}

	/**
	 * Returns the number of fields per column/ row
	 */
	public int getFieldLength() {
		return (int) Math.sqrt(fields);
	}

	/**
	 * Returns the number of fields per column/ row
	 */
	public static int getFieldLength(int num) {
		return (int) Math.sqrt(num);
	}

	/**
	 * Returns the kind of level.
	 */
	public LevelType getLevelType() {
		return difficulty;
	}
}
