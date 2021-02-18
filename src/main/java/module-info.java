module SimpleMinesweeper {
	opens de.ifdgmbh.mad.SimpleMinesweeper.main;
	opens de.ifdgmbh.mad.SimpleMinesweeper.controller;
	
	exports de.ifdgmbh.mad.SimpleMinesweeper.main;
	exports de.ifdgmbh.mad.SimpleMinesweeper.controller;
	
	requires transitive javafx.graphics;
	requires javafx.base;
	requires javafx.controls;
	requires javafx.fxml;
	
}