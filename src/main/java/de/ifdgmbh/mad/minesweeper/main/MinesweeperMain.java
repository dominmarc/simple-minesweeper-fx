/*
 * Copyright (c) 2021 iFD GmbH Chemnitz http://www.ifd-gmbh.com
 */
package de.ifdgmbh.mad.minesweeper.main;

import java.net.URISyntaxException;

import de.ifdgmbh.mad.minesweeper.helper.BasicGameFunctionsHelper;
import de.ifdgmbh.mad.minesweeper.logger.MinesweeperLogger;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 *
 * Main class for our minesweeper project
 *
 * @author MAD
 * @author iFD
 */
public class MinesweeperMain extends Application {

	static final MinesweeperLogger LOGGER = MinesweeperLogger.getLogger(MinesweeperMain.class);

	/**
	 * Application start point
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MinesweeperLogger.load();
		} catch (FileLoadingException e) {
			return;
		}

		for (int i = 1; i < 5; i++)
			LOGGER.info(BasicGameFunctionsHelper.getPrintBar());

		try {
			FileProvider.loadFiles();
			ImageProvider.loadFiles();
		} catch (FileLoadingException | URISyntaxException e) {
			LOGGER.error("", e);
			return;
		}
		launch(args);
	}

	/**
	 * Loads the in .fxml file defined scene and applies it to the stage that is
	 * about to open
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		FxmlOpener newFXML = new FxmlOpener(FileProvider.getStartFileURL(), 0, null,
				FileProvider.getStartStyleURL().toString());

		// open
		if (!newFXML.open())
			LOGGER.error("Error on opening file!");
		else
			LOGGER.info("Success...");
	}
}
