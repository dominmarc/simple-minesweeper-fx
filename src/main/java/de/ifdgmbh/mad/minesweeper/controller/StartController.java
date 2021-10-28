/* 
 * Copyright (c) 2021 iFD GmbH Chemnitz http://www.ifd-gmbh.com
 */
package de.ifdgmbh.mad.minesweeper.controller;

import de.ifdgmbh.mad.minesweeper.helper.BasicGameFunctionsHelper;
import de.ifdgmbh.mad.minesweeper.interfaces.IController;
import de.ifdgmbh.mad.minesweeper.level.Level;
import de.ifdgmbh.mad.minesweeper.level.Level.LevelType;
import de.ifdgmbh.mad.minesweeper.logger.MinesweeperLogger;
import de.ifdgmbh.mad.minesweeper.main.FileProvider;
import de.ifdgmbh.mad.minesweeper.main.ImageProvider;
import de.ifdgmbh.mad.minesweeper.main.PopUp;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Screen;
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
	/** Container for a little game preview */
	@FXML
	private AnchorPane buttonPane;
	/** button to start the game/ open the game */
	@FXML
	private Button startButton;
	/** resets the game preview */
	@FXML
	private Button resetButton;
	/** button to close the game, end the application */
	@FXML
	private Button closeButton;
	/** button to minimize the game */
	@FXML
	private Button minButton;
	/** contains all the possible levels */
	@FXML
	private ComboBox<LevelType> levelSelection;
	/** contains all the possible methods to enter gamefield size */
	@FXML
	private ComboBox<String> fieldSelection;
	/** optional input for max number of time */
	@FXML
	private TextField txtTime;
	/** number of fields for custom level */
	@FXML
	private TextField txtFields;
	/** number of bombs for custom level */
	@FXML
	private TextField txtBombs;

	/** number of buttons in game preview */
	static int NUM_OF_BUTTONS;

	Button[] buttons;
	int[][] previewField;
	static final MinesweeperLogger LOGGER = MinesweeperLogger.getLogger(StartController.class);

	@Override
	public void initVariable(String value) {
		// nothing
	}

	@Override
	public void initialize() {
		NUM_OF_BUTTONS = (int) ((buttonPane.getMinWidth() / Game.BUTTON_SIZE)
				* (buttonPane.getMinWidth() / Game.BUTTON_SIZE));

		buttons = new Button[NUM_OF_BUTTONS + 1];
		previewField = BasicGameFunctionsHelper.buildGamefield(NUM_OF_BUTTONS, 5);

		final int len = Level.getFieldLength(NUM_OF_BUTTONS);

		/* x position of the button */
		int posX = 0;
		/* y position of the button */
		int posY = 0;
		/* Button index */
		int index = 1;
		for (int i = 1; i <= len; i++) {
			for (int t = 1; t <= len; t++) {
				buildButtons(t, i, posX, posY, index);
				index++;
				posX += Game.BUTTON_SIZE;
			}
			posX = 0;
			posY += Game.BUTTON_SIZE;
		}

		// draw game field lines
		for (int l = 0; l <= (len * Game.BUTTON_SIZE); l += Game.BUTTON_SIZE) {
			Line line = new Line(0, 0, 0, (len * Game.BUTTON_SIZE));
			line.setLayoutX(l);
			buttonPane.getChildren().add(line);
			Line line2 = new Line(0, 0, (len * Game.BUTTON_SIZE), 0);
			line2.setLayoutY(l);
			buttonPane.getChildren().add(line2);
		}

		// level selection
		levelSelection.getItems().addAll(LevelType.EASY, LevelType.INTERMEDIATE, LevelType.HARD, LevelType.CUSTOM);
		levelSelection.setOnAction(e -> {
			LevelType lvl;
			if ((lvl = levelSelection.getSelectionModel().getSelectedItem()) != null && lvl.equals(LevelType.CUSTOM)) {
				txtBombs.setEditable(true);
				txtFields.setEditable(true);
			} else {

			}
		});
		// field method selection
		fieldSelection.getItems().addAll("OFFSET", "NUMBER");

		// apply combo box style
		fieldSelection.getStylesheets().add(FileProvider.getcomboBoxStyleURL().toString());
		levelSelection.getStylesheets().add(FileProvider.getcomboBoxStyleURL().toString());
	}

	/**
	 * Click on start button/ starts the game/ checks for correct start conditions
	 */
	public void startButtonClicked() {
		Settings setting = createSettings();
		if (setting == null)
			return;

		Game game = new Game(setting);
		if (!game.start()) {
			infoUser("A problem occured!\nCannot start game.");
			return;
		}
		LOGGER.info("Success... closing start window...");
		Stage me = (Stage) mainPane.getScene().getWindow();
		me.close();
	}

	/**
	 * Resets the game preview
	 */
	public void resetButtonClicked() {
		previewField = BasicGameFunctionsHelper.buildGamefield(NUM_OF_BUTTONS, 6);
		for (int idx = 1; idx <= NUM_OF_BUTTONS; idx++) {
			buttons[idx].setBackground(BasicGameFunctionsHelper.getUnchecked());
			buttons[idx].setGraphic(null);
			buttons[idx].setText("");
		}
	}

	/**
	 * Creates the settings to pass to the game instance.</br>
	 * Note: this returns null on wrong selections!</br>
	 * </br>
	 * 
	 * @return setting for the game
	 */
	private Settings createSettings() {
		// check timer
		int maxTimer = 0;

		if (!txtTime.getText().isBlank())
			try {
				maxTimer = Integer.parseInt(txtTime.getText());
			} catch (NumberFormatException e) {
				infoUser(
						"Cannot identify your settings for maximum time!\nRemove your input or make sure to enter a value between 2 and 9999!");
				return null;
			}
		// check level
		LevelType level = levelSelection.getSelectionModel().getSelectedItem();

		if (level == null) {
			infoUser("Please choose a difficulty!");
			return null;

		} else if (level.equals(LevelType.CUSTOM)) {
			if (txtBombs.getText().isBlank() || txtFields.getText().isBlank()) {
				infoUser("Please enter a number of bombs and fields!");
				return null;
			}
			try {
				int bombs = Integer.parseInt(txtBombs.getText());
				int fields = Integer.parseInt(txtFields.getText());

				int fieldMethod = fieldSelection.getSelectionModel().getSelectedIndex();
				if (fieldMethod == -1) {
					infoUser(
							"Choose a method!\nOffset will create (x*x) fields.\nNumber will create x fields, where x is the square of a natural number.");
					return null;

					// get fields according to selected method
				} else if (fieldMethod == 0)
					fields = fields * fields;
				else
					fields = Level.getFieldLength(fields) * Level.getFieldLength(fields);

				// there cannot be more bombs than fields
				if (bombs >= fields) {
					infoUser("There should always be more fields than bombs!");
					return null;
				}

				// number of fields
				int maxLen = (int) (Screen.getPrimary().getBounds().getHeight() - 120) / Game.BUTTON_SIZE;

				if (fields > (maxLen * maxLen)) {
					infoUser("This will not fit your screen!");
					return null;
				}
				if (fields < 4) {
					infoUser("Please add at least 4 fields!");
					return null;
				}

				// number of bombs
				if (bombs <= 0) {
					infoUser("Don't you want some bombs?\n\nGo ahead and add some!");
					return null;
				}

				return new Settings(fields, bombs, maxTimer);
			} catch (NumberFormatException e) {
				infoUser(
						"Cannot identify your settings for field and bomb count!\nPlease stay within natural numbers ().");
				return null;
			}
		} else
			return new Settings(level, maxTimer);
	}

	/**
	 * Builds and adds buttons to the button container in order to preview the game.
	 */
	private void buildButtons(final int x, final int y, int posX, int posY, final int index) {
		buttons[index] = new Button();
		buttonPane.getChildren().add(buttons[index]);
		buttons[index].setLayoutX(posX);
		buttons[index].setLayoutY(posY);
		buttons[index].setPrefWidth(Game.BUTTON_SIZE);
		buttons[index].setPrefHeight(Game.BUTTON_SIZE);
		buttons[index].setPadding(Game.fieldButtonInsets);
		buttons[index].setStyle("-fx-font-size: 17px; -fx-font-weight: bold;");
		buttons[index].setBackground(BasicGameFunctionsHelper.getUnchecked());

		// click events
		buttons[index].setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				primaryButtonAction(index, x, y);
			} else if (event.getButton() == MouseButton.SECONDARY) {
				secondaryButtonAction(index);
			}
		});
	}

	private void primaryButtonAction(int idx, int x, int y) {
		// value of the game field
		int fieldValue = previewField[x][y];

		if (fieldValue > 9 || fieldValue < 0) {
			infoUser("A problem occured within the preview, you may restart the application!");
			LOGGER.error("Value of field [x:{},y:{}] (idx:{}) was [{}] - resetting preview!", String.valueOf(x),
					String.valueOf(y), String.valueOf(idx), String.valueOf(fieldValue));
			resetButtonClicked();
			return;
		}

		switch (fieldValue) {
		case 9:
			// bomb hit
			buttons[idx].setBackground(BasicGameFunctionsHelper.getBomb());
			return;
		case 0:
			// zero field/ no bomb nearby
			// open all surrounding zero fields
			buttons[idx].setBackground(BasicGameFunctionsHelper.getChecked());
			return;

		// relevant number fields
		case 1:
			buttons[idx].setTextFill(Color.rgb(56, 0, 254));
			break;
		case 2:
			buttons[idx].setTextFill(Color.rgb(0, 107, 4));
			break;
		default:
			buttons[idx].setTextFill(Color.rgb(142, 11, 0));
			break;
		}

		buttons[idx].setBackground(BasicGameFunctionsHelper.getChecked());
		buttons[idx].setText(String.valueOf(fieldValue));
	}

	private void secondaryButtonAction(int idx) {
		if (BasicGameFunctionsHelper.isBomb(buttons[idx].getBackground())) {
			buttons[idx].setBackground(BasicGameFunctionsHelper.getUnchecked());
			buttons[idx].setGraphic(null);
		} else {
			buttons[idx].setBackground(BasicGameFunctionsHelper.getBomb());
			buttons[idx].setGraphic(new ImageView(ImageProvider.getFlagIMG()));
		}
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

	/**
	 * Click on minimize button
	 */
	public void minButtonClick() {
		Stage tempStage = (Stage) mainPane.getScene().getWindow();
		tempStage.setIconified(true);
	}

	/**
	 * Button to close the application
	 */
	public void closeButtonClick() {
		Stage temp = (Stage) mainPane.getScene().getWindow();

		if (temp != null) {
			LOGGER.warn("Someone closed the application on exit button.");
			LOGGER.info("==============================================");
			LOGGER.info("=====================END======================");
			LOGGER.info("==============================================");

			temp.close();
			System.exit(0);
		} else
			LOGGER.error("Could not close application on by exit button!");
	}
}
