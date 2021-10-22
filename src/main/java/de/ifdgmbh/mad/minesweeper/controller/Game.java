/* 
 * Copyright (c) 2021 iFD GmbH Chemnitz http://www.ifd-gmbh.com
 */
package de.ifdgmbh.mad.minesweeper.controller;

import java.util.Timer;
import java.util.TimerTask;

import de.ifdgmbh.mad.minesweeper.helper.BasicGameFunctionsHelper;
import de.ifdgmbh.mad.minesweeper.level.Level;
import de.ifdgmbh.mad.minesweeper.logger.MinesweeperLogger;
import de.ifdgmbh.mad.minesweeper.main.FileProvider;
import de.ifdgmbh.mad.minesweeper.main.FxmlOpener;
import de.ifdgmbh.mad.minesweeper.main.ImageProvider;
import de.ifdgmbh.mad.minesweeper.main.PopUp;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * SimpleMinesweeper game class
 * 
 * @author mad
 */
public class Game {
	/** Main container for all other elements */
	private AnchorPane backgroundPane;
	/** Container for all game field buttons */
	private AnchorPane buttonPane;
	/** Array of buttons used within the buttonPane */
	private Button[] buttons;
	/** Button to start or reset the game */
	private Button resetButton;
	/** Label presenting the elapsed time after the game started */
	private Label timeLabel;
	/** Label showing the bombs left */
	private Label bombsLabel;
	/**
	 * Label representing the topBar where the user has the ability to drag the
	 * window
	 */
	private Label topBar;

	/** the stage represents the game window */
	private Stage window;
	// window location
	private double xOffset = 0;
	private double yOffset = 0;

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
	private int bombsLeft = 10;

	/** game level/ difficulty */
	private static Level level;

	private Timer timer;
	private int maxTimer = 0;

	/** SIZE (x & y) of each button/ game field */
	static final int BUTTON_SIZE = 30;

	/** insets for all field buttons */
	static final Insets fieldButtonInsets = new Insets(0);

	// constant text
	private static final String BUTTON_START_TEXT = "START GAME";

	static final MinesweeperLogger LOGGER = MinesweeperLogger.getLogger(Game.class);

//====================================================================================================
//==																								==	
//==Initialization:														                 	        ==
//==																								==	
//====================================================================================================		

	public Game(String value) {
		initialize(value);
	}

	public void parseSettings(String value) {
		// parse the input data
		if ((level = setUpGame(value)) == null) {
			LOGGER.error("Could not set up game!");
			infoUser("Could not set up game!\nStarting in easy mode!");
			level = Level.getEasyLevel();
		}
	}

	public void initialize(String value) {
		LOGGER.info(BasicGameFunctionsHelper.getPrintBar());
		LOGGER.info("==============Local Multiplayer===============");
		LOGGER.info(BasicGameFunctionsHelper.getPrintBar());
		LOGGER.info("Starting initialization...");

		this.window = new Stage();
		parseSettings(value);
		buildGameWindow();

		LOGGER.info("Starting to build the gamefield...");
		buildGameFields(level.getFieldCount());
		LOGGER.info("Finished building gamefield!");

		LOGGER.info("Initialization finished!");
		LOGGER.info(BasicGameFunctionsHelper.getPrintBar());
	}

	private void buildGameFields(final int fieldSize) {
		// set all Buttons
		buttons = new Button[fieldSize + 1];

		// get number of fields per column/ row (and add 2 for frame)
		final int LENGTH = level.getFieldLength();

		// build all buttons (sqrt(SIZE) gamefield)
		LOGGER.info("Building the gamefield... with {} fields...", String.valueOf(fieldSize));
		/* x position of the button */
		int posX = 0;
		/* y position of the button */
		int posY = 0;
		/* Button index */
		int index = 1;
		for (int i = 1; i <= LENGTH; i++) {
			for (int t = 1; t <= LENGTH; t++) {
				buildButtons(t, i, posX, posY, index);
				index++;
				posX += BUTTON_SIZE;
			}
			posX = 0;
			posY += BUTTON_SIZE;
		}
		LOGGER.info("Successfully built the field!");
		LOGGER.info("Drawing lines...");
		// draw game field lines
		for (int l = 0; l <= (LENGTH * BUTTON_SIZE); l += BUTTON_SIZE) {
			Line line = new Line(0, 0, 0, (LENGTH * BUTTON_SIZE));
			line.setLayoutX(l);
			buttonPane.getChildren().add(line);
			Line line2 = new Line(0, 0, (LENGTH * BUTTON_SIZE), 0);
			line2.setLayoutY(l);
			buttonPane.getChildren().add(line2);
		}
		LOGGER.info("Successfully drew lines!");
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
			bombsLabel.setText("Bombs: " + bombsLeft);

			checkEnd();
		});
	}

	/**
	 * Opens the field if not already open and shows the user the result of his
	 * action. </br>
	 * This could either be a number, clear field or bomb.</br>
	 * Left clicked button with mouse. </br>
	 * 
	 * @param btnIndex representing the (array-) index of the given button
	 * @param x        representing the x value of the button in gamefield array
	 * @param y        representing the y value of the button in gamefield array
	 */
	public void primaryButtonAction(int btnIndex, int x, int y) {
		Button currentBtn = buttons[btnIndex];

		// field already opened?
		if (isChecked(currentBtn))
			return;

		// value of the game field
		int fieldValue = gamefield[x][y];

		if (fieldValue > 9 || fieldValue < 0) {
			running = false;
			infoUser("A problem occured, you may restart the game!");
			LOGGER.error("Value of field [x:{},y:{}] (idx:{}) was [{}] - stopped game!", String.valueOf(x),
					String.valueOf(y), String.valueOf(btnIndex), String.valueOf(fieldValue));
			return;
		}

		switch (fieldValue) {
		case 9:
			// bomb hit
			currentBtn.setBackground(BasicGameFunctionsHelper.getBomb());
			timer.cancel();
			running = false;
			infoUser("Game Over, you hit a bomb!");
			showSolution();
			return;
		case 0:
			// zero field/ no bomb nearby
			// open all surrounding zero fields
			currentBtn.setBackground(getChecked());
			openFields(btnIndex, x, y);
			return;

		// relevant number fields
		case 1:
			currentBtn.setTextFill(Color.rgb(56, 0, 254));
			break;
		case 2:
			currentBtn.setTextFill(Color.rgb(0, 107, 4));
			break;
		default:
			currentBtn.setTextFill(Color.rgb(142, 11, 0));
			break;
		}

		currentBtn.setBackground(getChecked());
		currentBtn.setText(String.valueOf(fieldValue));
	}

	/**
	 * Visually checks or unchecks a field with bomb marker, depending on if it was
	 * already checked or not</br>
	 * Right clicked button with mouse </br>
	 * 
	 * @param btnIndex representing the (array-) index of the given button
	 */
	public void secondaryButtonAction(int btnIndex) {
		if (bombsLeft <= 0)
			return;

		Button clickedBtn = buttons[btnIndex];
		if (isBombMarked(clickedBtn)) {
			clickedBtn.setBackground(BasicGameFunctionsHelper.getUnchecked());
			clickedBtn.setGraphic(null);
			bombsLeft++;
		} else {
			clickedBtn.setBackground(BasicGameFunctionsHelper.getBomb());
			clickedBtn.setGraphic(new ImageView(ImageProvider.getFlagIMG()));
			bombsLeft--;
		}
	}

	/**
	 * Scans for empty fields and reveals one (and surrounded)
	 */
	private void helpButtonClicked() {
		if (!running)
			return;

		int idx = 0;
		for (int y = 1; y <= level.getFieldLength(); y++)
			for (int x = 1; x <= level.getFieldLength(); x++) {
				idx++;
				if (gamefield[x][y] == 0 && isUnchecked(buttons[idx])) {
					openFields(idx, x, y);
					return;
				}
			}
	}

	/**
	 * Button to go back to the menu
	 */
	private void backButtonClicked() {
		try {
			FxmlOpener newFXML = new FxmlOpener(FileProvider.getStartFileURL(), 0, null,
					FileProvider.getStartStyleURL().toString());

			// open
			if (!newFXML.open())
				LOGGER.error("Error on opening file!");
			else {
				LOGGER.info("Success... closing current window...");
				this.window.close();
			}
		} catch (Exception e) {
			LOGGER.error("Failed to build FxmlOpener - {}", e.getMessage());
		}
	}

	/**
	 * Resets or starts the game depending on the game state
	 */
	private void resetButtonClicked() {
		// visually reset all the fields
		for (int i = 1; i <= level.getFieldCount(); i++) {
			buttons[i].setBackground(BasicGameFunctionsHelper.getUnchecked());
			buttons[i].setGraphic(null);
			buttons[i].setText("");
		}

		// reset the number of bombs
		bombsLeft = level.getBombCount();
		bombsLabel.setText("Bombs: " + bombsLeft);

		if (running) {
			running = false;
			timer.cancel();
			resetButton.setText(BUTTON_START_TEXT);
			return;
		}

		createField();
		running = true;
		resetButton.setText("RE" + BUTTON_START_TEXT);
		startTimer();
	}

	/**
	 * 
	 * @return
	 */
	public boolean start() {
		if (this.window != null) {
			window.show();
			return true;
		} else
			return false;
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
	 * Checks whether the game ended with the last choice or not.</br>
	 * And ends the game if it should.
	 */
	public boolean checkEnd() {
		for (int i = 1; i <= level.getFieldCount(); i++) {
			if (isUnchecked(buttons[i])) {
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
	 * Parses the passed message and sets the game up.</br>
	 * Initializes the level.</br>
	 * Restrictions for timer. (if TTTT)</br>
	 * Note that we assume here, that all data is valid. We just look for msg
	 * validity.</br>
	 * 
	 * @param msg the message to be parsed</br>
	 *            Format: |XX|YY|ZZ|TTTT|</br>
	 *            XX - level (01/02/03/04)</br>
	 *            YY - number fields per row and column</br>
	 *            ZZ - number bombs</br>
	 *            TTTT - time in seconds (2 - 9999)</br>
	 * 
	 * @return true on success, false on failure
	 */
	private Level setUpGame(String msg) {
		LOGGER.info("Parsing [{}]...", msg);
		if (msg.length() != 10) {
			LOGGER.error("String length ({}) invalid!", String.valueOf(msg.length()));
			return null;
		}
		try {
			// set max timer
			this.maxTimer = Integer.parseInt(msg.substring(6));

			// set the level
			switch (msg.substring(0, 2)) {
			case "01":
				return Level.getEasyLevel();
			case "02":
				return Level.getIntermediateLevel();
			case "03":
				return Level.getHardLevel();
			case "04":
				final int bombCount = Integer.parseInt(msg.substring(2, 4));
				final int fieldCount = Integer.parseInt(msg.substring(4, 6));
				return Level.getCustomLevel(bombCount, fieldCount);
			default:
				return null;
			}
		} catch (NumberFormatException e) {
			LOGGER.info("NumberFormatException while parsing! - {}", e.getMessage());
			return null;
		}
	}

	/**
	 * Constructs a dynamically sized window for the game.
	 * 
	 * @return Stage Object representing the game
	 */
	private void buildGameWindow() {
		final int BUTTON_PANE_SIZE = level.getFieldLength() * BUTTON_SIZE;
		final int WINDOW_WIDTH = BUTTON_PANE_SIZE + (2 * 65);
		final int WINDOW_HEIGHT = 80 + BUTTON_PANE_SIZE + 40;
		final int topBarHeight = 26;
		final int resetButtonHeight = 35;
		final int titleButtonsWidth = 30;
		final int labelHeight = 20;
		final int labelWidth = 100;
		// timeLabel layout x
		final double timeLLayX = (WINDOW_WIDTH / 4.0) - (labelWidth / 2.0);

		// all nodes we need
		backgroundPane = new AnchorPane();
		buttonPane = new AnchorPane();
		/** Button to minimize the stage */
		Button minButton = new Button("_");
		/** Button to close the whole application */
		Button closeButton = new Button("X");
		resetButton = new Button(BUTTON_START_TEXT);
		/** button to open a random zero field */
		Button helpButton = new Button();
		/** button to return to the home menu */
		Button backButton = new Button();
		timeLabel = new Label("Time: 0");
		bombsLabel = new Label("Bombs: " + level.getBombCount());
		topBar = new Label("SimpleMinesweeper");

		// panes
		buttonPane = (AnchorPane) BasicGameFunctionsHelper.fixSize(buttonPane, BUTTON_PANE_SIZE, BUTTON_PANE_SIZE);
		buttonPane = (AnchorPane) BasicGameFunctionsHelper.fixLoc(buttonPane, "buttonPane",
				(WINDOW_WIDTH / 2.0) - (BUTTON_PANE_SIZE / 2.0), 80);

		backgroundPane = (AnchorPane) BasicGameFunctionsHelper.fixSize(backgroundPane, WINDOW_WIDTH, WINDOW_HEIGHT);
		backgroundPane = (AnchorPane) BasicGameFunctionsHelper.fixLoc(backgroundPane, "backgroundPane", 0, 0);

		// buttons
		resetButton = (Button) BasicGameFunctionsHelper.fixSize(resetButton, 139, resetButtonHeight);
		resetButton = (Button) BasicGameFunctionsHelper.fixLoc(resetButton, "resetButton",
				(WINDOW_WIDTH / 2.0) - (139 / 2.0), topBarHeight + 4.0);
		resetButton.setOnMouseClicked(e -> {
			resetButtonClicked();
		});

		helpButton = (Button) BasicGameFunctionsHelper.fixSize(helpButton, resetButtonHeight, resetButtonHeight);
		helpButton = (Button) BasicGameFunctionsHelper.fixLoc(helpButton, "helpButton",
				((WINDOW_WIDTH / 2.0) - (BUTTON_PANE_SIZE / 2.0) - resetButtonHeight) / 2, topBarHeight + 9.0);
		helpButton.setGraphic(new ImageView(ImageProvider.getHelpIMG()));
		helpButton.setOnMouseClicked(e -> {
			helpButtonClicked();
		});

		backButton = (Button) BasicGameFunctionsHelper.fixSize(backButton, resetButtonHeight, resetButtonHeight);
		backButton = (Button) BasicGameFunctionsHelper.fixLoc(backButton, "backButton",
				buttonPane.getLayoutX() + BUTTON_PANE_SIZE + (buttonPane.getLayoutX() / 2 - resetButtonHeight / 2.0),
				topBarHeight + 9.0);
		backButton.setGraphic(new ImageView(ImageProvider.getBackIMG()));
		backButton.setOnMouseClicked(e -> {
			backButtonClicked();
		});

		closeButton = (Button) BasicGameFunctionsHelper.fixSize(closeButton, titleButtonsWidth, topBarHeight - 1.0);
		closeButton = (Button) BasicGameFunctionsHelper.fixLoc(closeButton, "closeButton",
				WINDOW_WIDTH - titleButtonsWidth, 0);
		closeButton.setOnMouseClicked(e -> {
			closeButtonClicked();
		});

		minButton = (Button) BasicGameFunctionsHelper.fixSize(minButton, titleButtonsWidth, topBarHeight - 1.0);
		minButton = (Button) BasicGameFunctionsHelper.fixLoc(minButton, "minButton",
				WINDOW_WIDTH - (titleButtonsWidth * 2.0), 0);
		minButton.setOnMouseClicked(e -> {
			minButtonClicked();
		});

		// labels
		topBar = (Label) BasicGameFunctionsHelper.fixSize(topBar, WINDOW_WIDTH, topBarHeight);
		topBar = (Label) BasicGameFunctionsHelper.fixLoc(topBar, "topBar", 0, 0);

		timeLabel = (Label) BasicGameFunctionsHelper.fixSize(timeLabel, labelWidth, labelHeight);
		timeLabel = (Label) BasicGameFunctionsHelper.fixLoc(timeLabel, "timeLabel", timeLLayX, topBarHeight + 10);

		bombsLabel = (Label) BasicGameFunctionsHelper.fixSize(bombsLabel, labelWidth, labelHeight);
		bombsLabel = (Label) BasicGameFunctionsHelper.fixLoc(bombsLabel, "bombsLabel", WINDOW_WIDTH * 0.75,
				topBarHeight + 10);

		// add to container
		backgroundPane.getChildren().add(buttonPane);
		backgroundPane.getChildren().add(topBar);
		backgroundPane.getChildren().add(minButton);
		backgroundPane.getChildren().add(closeButton);
		backgroundPane.getChildren().add(resetButton);
		backgroundPane.getChildren().add(helpButton);
		backgroundPane.getChildren().add(backButton);
		backgroundPane.getChildren().add(timeLabel);
		backgroundPane.getChildren().add(bombsLabel);

		final Scene scene = new Scene(backgroundPane);
		// set transparent background
		scene.setFill(Color.TRANSPARENT);
		scene.getStylesheets().add(FileProvider.getGameStyleFileURL().toString());

		// save x and y mouse coordinates of scene
		backgroundPane.setOnMousePressed(event -> {
			this.xOffset = event.getSceneX();
			this.yOffset = event.getSceneY();
		});

		// move the stage, if the user drags the topBar-Label (height 30) of the scene
		backgroundPane.setOnMouseDragged(event -> {
			if (this.yOffset < topBarHeight) {
				this.window.setX(event.getScreenX() - xOffset);
				this.window.setY(event.getScreenY() - yOffset);
			}
		});

		// show the stage, apply transparent style, icon and a style-sheet
		this.window.initStyle(StageStyle.TRANSPARENT);
		this.window.setScene(scene);
		this.window.setResizable(false);

		// add icon if there is one
		this.window.getIcons().add(ImageProvider.getBombRedIMG());
	}

	/**
	 * Tests for a user marked field
	 */
	public boolean isChecked(Button input) {
		return BasicGameFunctionsHelper.isChecked(input.getBackground());
	}

	/**
	 * Tests for a bomb marked field
	 */
	public boolean isBombMarked(Button input) {
		return BasicGameFunctionsHelper.isBomb(input.getBackground());
	}

	/**
	 * Tests for a raw field
	 */
	public boolean isUnchecked(Button input) {
		return BasicGameFunctionsHelper.isUnchecked(input.getBackground());
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
		gamefield = BasicGameFunctionsHelper.buildGamefield(level.getFieldCount(), level.getBombCount());
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
			buttons[btnIndex - level.getFieldLength()].setBackground(getChecked());
			openFields(btnIndex - level.getFieldLength(), x, y - 1);
		}
		// right up
		if (gamefield[x + 1][y - 1] == 0) {
			gamefield[x + 1][y - 1] = -1;
			buttons[btnIndex - level.getFieldLength() + 1].setBackground(getChecked());
			openFields(btnIndex - level.getFieldLength() + 1, x + 1, y - 1);
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
			buttons[btnIndex + level.getFieldLength() + 1].setBackground(getChecked());
			openFields(btnIndex + level.getFieldLength() + 1, x + 1, y + 1);
		}
		// down
		if (gamefield[x][y + 1] == 0) {
			gamefield[x][y + 1] = -1;
			buttons[btnIndex + level.getFieldLength()].setBackground(getChecked());
			openFields(btnIndex + level.getFieldLength(), x, y + 1);
		}
		// left down
		if (gamefield[x - 1][y + 1] == 0) {
			gamefield[x - 1][y + 1] = -1;
			buttons[btnIndex + level.getFieldLength() - 1].setBackground(getChecked());
			openFields(btnIndex + level.getFieldLength() - 1, x - 1, y + 1);
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
			buttons[btnIndex - level.getFieldLength() - 1].setBackground(getChecked());
			openFields(btnIndex - level.getFieldLength() - 1, x - 1, y - 1);
		}

	}

	/**
	 * Shows the solution of the game, opens all fields</br>
	 * This function is called after the player hit a bomb! </br>
	 */
	public void showSolution() {
		int btnIndex = 1;
		for (int y = 1; y <= level.getFieldLength(); y++) {
			for (int x = 1; x <= level.getFieldLength(); x++) {
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
