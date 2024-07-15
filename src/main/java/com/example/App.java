package com.example;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.example.data.DataSource;

public class App extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("primary.fxml"));
			Parent root;

			root = loader.load();

			PrimaryController controller = loader.getController();

			Scene scene = new Scene(root, 1120, 1000);

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

