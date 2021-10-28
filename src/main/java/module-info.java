module de.ifdgmbh.mad.minesweeper {

	opens de.ifdgmbh.mad.minesweeper.main to javafx.fxml;
	opens de.ifdgmbh.mad.minesweeper.controller to javafx.fxml, javafx.graphics;

	exports de.ifdgmbh.mad.minesweeper.main to javafx.graphics;

	requires transitive javafx.graphics;
	requires javafx.base;
	requires javafx.controls;
	requires javafx.fxml;
	requires java.desktop;

}