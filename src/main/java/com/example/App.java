/**
 * The App class is the main class of the application.
 * It extends the Application class from the JavaFX library.
 * The start() method is overridden to set up the primary stage and scene of the application.
 * The init() method is overridden to initialize the data source and check the connection to the database.
 * The stop() method is overridden to close the data source when the application is stopped.
 * The main() method is the entry point of the application.
 */
package com.example;

import com.example.data.DataSource;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("primary.fxml"));
			Parent root;

			root = loader.load();

			Scene scene = new Scene(root, 1420, 800);

			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			primaryStage.setScene(scene);

			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void init() throws Exception {
		super.init();
		if (!DataSource.getInstance().open()) {
			System.out.println("FATAL ERROR: Couldn't connect to database");
			Platform.exit();
		}
	}

	@Override
	public void stop() throws Exception {
		super.stop();
		DataSource.getInstance().close();
	}

	public static void main(String[] args) {
		launch(args);
		System.out.println("*** finish ***");
	}

}
