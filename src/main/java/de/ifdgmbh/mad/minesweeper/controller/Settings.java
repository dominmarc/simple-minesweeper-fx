/* 
 * Copyright (c) 2021 iFD GmbH Chemnitz http://www.ifd-gmbh.com
 */
package de.ifdgmbh.mad.minesweeper.controller;

import de.ifdgmbh.mad.minesweeper.level.Level;
import de.ifdgmbh.mad.minesweeper.level.Level.LevelType;

/**
 * Settings class for simple minesweeper
 * 
 * @author MAD
 */
public class Settings {

	private Level level;
	private LevelType difficulty;
	private int maxTimer = 0;
	private int bombs;
	private int fields;
	private int fieldLength;

	/**
	 * Constructor for level based settings.
	 * 
	 * @param level    type of level
	 * @param maxTimer game time counter (set to 0 to disable)
	 */
	public Settings(LevelType level, int maxTimer) {
		this.level = Level.getLevel(level);
		this.difficulty = level;
		this.maxTimer = maxTimer;
		initialize();
	}

	/**
	 * Constructor for custom settings.
	 * 
	 * @param fields   number of fields
	 * @param bombs    number of bombs
	 * @param maxTimer game time counter (set to 0 to disable)
	 */
	public Settings(int fields, int bombs, int maxTimer) {
		this.level = Level.getCustomLevel(bombs, fields);
		this.difficulty = LevelType.CUSTOM;
		this.maxTimer = maxTimer;
		initialize();
	}

	/**
	 * Returns easy settings with no timer restrictions.
	 */
	public static Settings getEasySettings() {
		return new Settings(LevelType.EASY, 0);
	}

	private void initialize() {
		this.bombs = this.level.getBombCount();
		this.fields = this.level.getFieldCount();
		this.fieldLength = this.level.getFieldLength();
	}

	// Getter and Setter

	public int getMaxTimer() {
		return maxTimer;
	}

	public void setMaxTimer(int maxTimer) {
		this.maxTimer = maxTimer;
	}

	public LevelType getDifficulty() {
		return difficulty;
	}

	public int getBombs() {
		return bombs;
	}

	public int getFields() {
		return fields;
	}

	public int getFieldLength() {
		return fieldLength;
	}
}
