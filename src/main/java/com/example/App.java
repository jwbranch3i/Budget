package com.example;

import com.example.data.DataSource;
import com.example.controllers.PrimaryController;

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

			PrimaryController controller = loader.getController();
	
	
		//	controller.getActuals();

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
