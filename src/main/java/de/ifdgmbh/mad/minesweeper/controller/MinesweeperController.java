/* 
 * Copyright (c) 2021 iFD GmbH Chemnitz http://www.ifd-gmbh.com
 */
package de.ifdgmbh.mad.minesweeper.controller;

import java.util.Timer;
import java.util.TimerTask;

import de.ifdgmbh.mad.minesweeper.helper.BasicGameFunctionsHelper;
import de.ifdgmbh.mad.minesweeper.interfaces.IController;
import de.ifdgmbh.mad.minesweeper.logger.MinesweeperLogger;
import de.ifdgmbh.mad.minesweeper.main.ImageProvider;
import de.ifdgmbh.mad.minesweeper.main.PopUp;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

/**
 * Controller for minesweeper
 * 
 * @author MAD
 * @author iFD
 */
public class MinesweeperController implements IController {
	/** Main container for all other elements */
	@FXML
	private AnchorPane backgroundPane;
	/** Container for all game field buttons */
	@FXML
	private AnchorPane buttonPane;
	/** Array of buttons used within the buttonPane */
	@FXML
	private Button[] buttons;
	/** Button to minimize the stage */
	@FXML
	private Button minButton;
	/** Button to close the whole application */
	@FXML
	private Button closeButton;
	/** Button to start or reset the game */
	@FXML
	private Button resetButton;
	/** Label presenting the elapsed time after the game started */
	@FXML
	private Label timeLabel;
	/** Label showing the bombs left */
	@FXML
	private Label bombsLabel;
	/**
	 * Label representing the topBar where the user has the ability to drag the
	 * window
	 */
	@FXML
	private Label topBar;

	/**
	 * int[11][11] array; zeroes and tens not used (they frame the gamefield array
	 * with -1)</br>
	 * no bomb = 0</br>
	 * x bombs = x </br>
	 * bomb = 9
	 */
	private int[][] gamefield;

	/** Boolean representing whether the game is running or not */
	private boolean running = false;

	/** Value representing the number of bombs left */
	private int bombs = 10;

	/** Value representing the number of fields/ buttons */
	private final int SIZE = 82;

	private Timer timer;

	/** insets for all field buttons */
	static final Insets fieldButtonInsets = new Insets(0);

	// constant text
	private static final String BUTTON_START_TEXT = "START GAME";

	static final MinesweeperLogger LOGGER = MinesweeperLogger.getLogger(MinesweeperController.class);

//====================================================================================================
//==																								==	
//==Initialization:														                 	        ==
//==																								==	
//====================================================================================================		

	@Override
	public void initVariable(String value) {
		// nothing
	}

	@Override
	public void initialize() {
		LOGGER.info(BasicGameFunctionsHelper.getPrintBar());
		LOGGER.info("==============Local Multiplayer===============");
		LOGGER.info(BasicGameFunctionsHelper.getPrintBar());
		LOGGER.info("Starting initialization...");

		// set start button text
		resetButton.setText(BUTTON_START_TEXT);

		// set all Buttons
		buttons = new Button[SIZE];

		// build all buttons (sqrt(SIZE) gamefield)
		LOGGER.info("Building the gamefield... with {} fields...", String.valueOf(SIZE));
		/* x position of the button */
		int posX = 0;
		/* y position of the button */
		int posY = 0;
		/* Button index */
		int index = 1;
		for (int i = 1; i < 10; i++) {
			for (int t = 1; t < 10; t++) {
				buildButtons(t, i, posX, posY, index);
				index++;
				posX += 30;
			}
			posX = 0;
			posY += 30;
		}
		LOGGER.info("Successfully built the field!");
		LOGGER.info("Drawing lines...");
		// draw game field lines
		for (int l = 0; l <= 270; l += 30) {
			Line line = new Line(0, 0, 0, 270);
			line.setLayoutX(l);
			buttonPane.getChildren().add(line);
			Line line2 = new Line(0, 0, 270, 0);
			buttonPane.getChildren().add(line2);
			line2.setLayoutY(l);
		}
		// resetGlobalVars();
		LOGGER.info("Successfully drew lines!");
		LOGGER.info("Initialization finished!");
		LOGGER.info(BasicGameFunctionsHelper.getPrintBar());
	}

	/**
	 * Function to add the buttons to the button pane and assign their
	 * functionality.</br>
	 * Each button represents a field of the game.
	 *
	 * @param x     position of current button in gamefield array
	 * @param y     position of current button in gamefield array
	 * @param posX  x position of the button
	 * @param posY  y position of the button
	 * @param index index of the button
	 */
	private void buildButtons(final int x, final int y, int posX, int posY, final int index) {
		buttons[index] = new Button();
		buttonPane.getChildren().add(buttons[index]);
		buttons[index].setLayoutX(posX);
		buttons[index].setLayoutY(posY);
		buttons[index].setPrefWidth(30);
		buttons[index].setPrefHeight(30);
		buttons[index].setPadding(fieldButtonInsets);
		buttons[index].setStyle("-fx-font-size: 17px; -fx-font-weight: bold;");
		buttons[index].setBackground(BasicGameFunctionsHelper.getChecked());

		// click events
		buttons[index].setOnMouseClicked(event -> {
			if (!running) {
				infoUser("Please start the game!");
				return;
			}
			if (event.getButton() == MouseButton.PRIMARY) {
				primaryButtonAction(index, x, y);
			} else if (event.getButton() == MouseButton.SECONDARY) {
				secondaryButtonAction(index);
			}
			bombsLabel.setText("Bombs: " + bombs);
			if (running)
				if (checkEnd()) {
					return;
				}
		});
	}

	/**
	 * Opens the field if not already open and shows the user the result of his
	 * action </br>
	 * Left clicked button with mouse </br>
	 * 
	 * @param btnIndex representing the (array-) index of the given button
	 * @param x        representing the x value of the button in gamefield array
	 * @param y        representing the y value of the button in gamefield array
	 */
	public void primaryButtonAction(int btnIndex, int x, int y) {
		Button currentBtn = buttons[btnIndex];
		// hit the user a bomb? --> end the game
		if (gamefield[x][y] == 9) {
			currentBtn.setBackground(BasicGameFunctionsHelper.getBomb());
			timer.cancel();
			running = false;
			infoUser("Game Over, you hit a bomb!");
			showSolution();
			return;
		}

		// field already opened?
		if (isChecked(currentBtn.getBackground())) {
			return;
		}

		// no bomb nearby --> user opened a zero field
		if (gamefield[x][y] == 0) {
			// open all zero fields nearby
			currentBtn.setBackground(getChecked());
			openFields(btnIndex, x, y);
			return;
		}

		// bomb nearby --> user opened a game relevant field
		// open the clicked field && fit the right color to the value
		if (gamefield[x][y] == 1) {
			currentBtn.setTextFill(Color.rgb(56, 0, 254));
		} else if (gamefield[x][y] == 2) {
			currentBtn.setTextFill(Color.rgb(0, 107, 4));
		} else {
			currentBtn.setTextFill(Color.rgb(142, 11, 0));
		}
		currentBtn.setBackground(getChecked());
		currentBtn.setText(gamefield[x][y] + "");
	}

	/**
	 * Visually checks or unchecks a field, depending on if it was already checked
	 * or not</br>
	 * Right clicked button with mouse </br>
	 * 
	 * @param btnIndex representing the (array-) index of the given button
	 */
	public void secondaryButtonAction(int btnIndex) {
		Button btn = buttons[btnIndex];
		if (isBomb(btn.getBackground())) {
			btn.setBackground(BasicGameFunctionsHelper.getUnchecked());
			btn.setGraphic(null);
			bombs++;
		} else {
			btn.setBackground(BasicGameFunctionsHelper.getBomb());
			btn.setGraphic(new ImageView(ImageProvider.getFlagIMG()));
			bombs--;
		}
	}

	/**
	 * Resets or starts the game depending on the game state
	 */
	public void resetButtonClick() {
		// visually reset all the fields
		for (int i = 1; i < SIZE; i++) {
			buttons[i].setBackground(BasicGameFunctionsHelper.getUnchecked());
			buttons[i].setGraphic(null);
			buttons[i].setText("");
		}

		// reset the number of bombs
		bombs = 10;
		bombsLabel.setText("Bombs: " + bombs);

		if (running) {
			running = false;
			timer.cancel();
			resetButton.setText("START GAME");
			return;
		}

		createField();
		running = true;
		resetButton.setText("RESTART GAME");
		startTimer();
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
	 * Checks whether the game ended with the last choice or not
	 */
	public boolean checkEnd() {
		for (int i = 1; i < SIZE; i++) {
			if (isUnchecked(buttons[i].getBackground())) {
				return false;
			}
		}
		timer.cancel();
		running = false;
		PopUp ending = new PopUp();
		ending.createWinningPopUp(
				"Congratulations, " + "you" + " won the game!\nThank you for playing!\nHave a nice day :)");
		ending.showPopUp();
		return true;
	}

	/**
	 * Tests for a user marked field
	 */
	public boolean isChecked(Background input) {
		return BasicGameFunctionsHelper.isChecked(input);
	}

	/**
	 * Tests for a bomb marked field
	 */
	public boolean isBomb(Background input) {
		return BasicGameFunctionsHelper.isBomb(input);
	}

	/**
	 * Tests for a raw field
	 */
	public boolean isUnchecked(Background input) {
		return BasicGameFunctionsHelper.isUnchecked(input);
	}

	/**
	 * Background to visually show a user marked field
	 */
	public Background getChecked() {
		return BasicGameFunctionsHelper.getChecked();
	}

	/**
	 * set gamefield with 10 bombs and all values
	 */
	public void createField() {
		gamefield = BasicGameFunctionsHelper.buildGamefield(SIZE);
	}

	/**
	 * Function to open up all the neighbor zero fields </br>
	 * This function is called whenever the player hits a zero field </br>
	 * 
	 * @param btnIndex representing the (array-) index of the given button
	 * @param x        representing the x value of the button in gamefield array
	 * @param y        representing the y value of the button in gamefield array
	 */
	public void openFields(int btnIndex, int x, int y) {
		// up
		if (gamefield[x][y - 1] == 0) {
			gamefield[x][y - 1] = -1;
			buttons[btnIndex - 9].setBackground(getChecked());
			openFields(btnIndex - 9, x, y - 1);
		}
		// right up
		if (gamefield[x + 1][y - 1] == 0) {
			gamefield[x + 1][y - 1] = -1;
			buttons[btnIndex - 9 + 1].setBackground(getChecked());
			openFields(btnIndex - 9 + 1, x + 1, y - 1);
		}
		// right
		if (gamefield[x + 1][y] == 0) {
			gamefield[x + 1][y] = -1;
			buttons[btnIndex + 1].setBackground(getChecked());
			openFields(btnIndex + 1, x + 1, y);
		}
		// right down
		if (gamefield[x + 1][y + 1] == 0) {
			gamefield[x + 1][y + 1] = -1;
			buttons[btnIndex + 9 + 1].setBackground(getChecked());
			openFields(btnIndex + 9 + 1, x + 1, y + 1);
		}
		// down
		if (gamefield[x][y + 1] == 0) {
			gamefield[x][y + 1] = -1;
			buttons[btnIndex + 9].setBackground(getChecked());
			openFields(btnIndex + 9, x, y + 1);
		}
		// left down
		if (gamefield[x - 1][y + 1] == 0) {
			gamefield[x - 1][y + 1] = -1;
			buttons[btnIndex + 9 - 1].setBackground(getChecked());
			openFields(btnIndex + 9 - 1, x - 1, y + 1);
		}
		// left
		if (gamefield[x - 1][y] == 0) {
			gamefield[x - 1][y] = -1;
			buttons[btnIndex - 1].setBackground(getChecked());
			openFields(btnIndex - 1, x - 1, y);
		}
		// left up
		if (gamefield[x - 1][y - 1] == 0) {
			gamefield[x - 1][y - 1] = -1;
			buttons[btnIndex - 9 - 1].setBackground(getChecked());
			openFields(btnIndex - 9 - 1, x - 1, y - 1);
		}

	}

	/**
	 * Shows the solution of the game, opens all fields</br>
	 * This function is called after the player hit a bomb! </br>
	 */
	public void showSolution() {
		int btnIndex = 1;
		for (int y = 1; y < 10; y++) {
			for (int x = 1; x < 10; x++) {
				// Value of the field, representing how many bombs are around the field or if it
				// is a bomb
				int fieldValue = gamefield[x][y];

				// field contains a value, is nearby bomb
				if (fieldValue != -1 && fieldValue != 0 && fieldValue != 9) {
					// arrange fitting (value-related) color and value
					if (fieldValue == 1) {
						buttons[btnIndex].setTextFill(Color.rgb(56, 0, 254));
					} else if (fieldValue == 2) {
						buttons[btnIndex].setTextFill(Color.rgb(0, 107, 4));
					} else {
						buttons[btnIndex].setTextFill(Color.rgb(142, 11, 0));
					}
					buttons[btnIndex].setText(fieldValue + "");
					buttons[btnIndex].setBackground(getChecked());

					// field is a bomb
				} else if (fieldValue == 9) {
					buttons[btnIndex].setBackground(BasicGameFunctionsHelper.getBomb());
					buttons[btnIndex].setGraphic(new ImageView(ImageProvider.getBombRedIMG()));

					// field is a zero, has no bomb nearby
				} else {
					buttons[btnIndex].setBackground(getChecked());
				}
				btnIndex++;
			}
		}
	}

	/**
	 * Starts the timer counting from 0 to whenever the game ends
	 */
	public void startTimer() {
		timer = new Timer();

		// create a new task for the timer
		TimerTask timerTask = new TimerTask() {
			final int[] counter = { 0 };

			@Override
			public void run() {
				counter[0]++;
				Platform.runLater(() -> {
					timeLabel.setText("Time: " + counter[0]);
				});
			}
		};

		// set the task to the timer and schedule it
		timer.scheduleAtFixedRate(timerTask, 1000, 1000);
	}

//====================================================================================================
//==																								==	
//==Exiting game functions:        															        ==
//==																								==	
//====================================================================================================		

	/**
	 * Button to minimize the application
	 */
	public void minButtonClicked() {
		Stage tempStage = (Stage) backgroundPane.getScene().getWindow();
		tempStage.setIconified(true);
	}

	/**
	 * Button to close the application
	 */
	public void closeButtonClicked() {
		Stage temp = (Stage) backgroundPane.getScene().getWindow();

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