/* 
 * Copyright (c) 2021 iFD GmbH Chemnitz http://www.ifd-gmbh.com
 */
package de.ifdgmbh.mad.minesweeper.controller;

import de.ifdgmbh.mad.minesweeper.interfaces.IController;
import de.ifdgmbh.mad.minesweeper.logger.MinesweeperLogger;
import de.ifdgmbh.mad.minesweeper.main.FileProvider;
import de.ifdgmbh.mad.minesweeper.main.FxmlOpener;
import de.ifdgmbh.mad.minesweeper.main.PopUp;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Controller for start window of minesweeper
 * 
 * @author MAD
 * @author iFD
 */
public class StartController implements IController {
	/** Container for all elements */
	@FXML
	private AnchorPane mainPane;
	/** button to start the game/ open the game */
	@FXML
	private Button startButton;

	static final MinesweeperLogger LOGGER = MinesweeperLogger.getLogger(StartController.class);

	@Override
	public void initVariable(String value) {
		// nothing
	}

	@Override
	public void initialize() {
		// nothing
	}

	/**
	 * Click on start button
	 */
	public void startButtonClicked() {

		Game game = new Game("0300000000");
		if (!game.start()) {
			infoUser("A problem occured!\nCannot start game.");
			return;
		}
		LOGGER.info("Success... closing start window...");
		Stage me = (Stage) mainPane.getScene().getWindow();
		me.close();
	}

	/**
	 * Shows an information pop up
	 * 
	 * @param msg the Message to be shown
	 */
	private void infoUser(String msg) {
		PopUp info = new PopUp();
		info.createInfoPopUp(msg);
		info.showPopUp();
	}

}
