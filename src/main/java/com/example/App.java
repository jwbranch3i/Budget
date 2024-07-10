package com.example;

import java.util.ArrayList;

import com.example.data.DB;
import com.example.data.Item;
import com.example.data.ReadCSVFile;

public class App {
    public static void main(String[] args) {

        ArrayList<Item> items = new ArrayList<Item>();


        items = ReadCSVFile.readDataLineByLine(DB.CSV_FILE);
    }

 

}

/**
 * JavaFX App
 */
// public class App extends Application {

// private static Scene scene;

// @Override
// public void start(Stage stage) throws IOException {
// scene = new Scene(loadFXML("primary"), 640, 480);
// stage.setScene(scene);
// stage.show();
// }

// static void setRoot(String fxml) throws IOException {
// scene.setRoot(loadFXML(fxml));
// }

// private static Parent loadFXML(String fxml) throws IOException {
// FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml +
// ".fxml"));
// return fxmlLoader.load();
// }

// public static void main(String[] args) {
// launch();
// }

// }
