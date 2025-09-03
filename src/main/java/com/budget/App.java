/**
 * The App class is the main entry point of the Budget application. It extends
 * the JavaFX Application class and manages the application lifecycle. This
 * class handles: - Primary stage and scene setup - Database initialization and
 * connection management - Application shutdown procedures - Error handling and
 * logging
 */
package com.budget;

import com.budget.dataModel.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

/**
 * Main application class for the Budget application. Manages JavaFX lifecycle
 * and database connections.
 */
public class App extends Application {

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    // ========================= CONSTANTS =========================

    private static final String FXML_RESOURCE = "primary.fxml";
    private static final String CSS_RESOURCE = "testfile.css";
    private static final String APPLICATION_TITLE = "Budget Manager";
   
    private static final int MIN_WINDOW_WIDTH = 800;
    private static final int MIN_WINDOW_HEIGHT = 600;

    // Error messages
    private static final String FATAL_DB_ERROR = "FATAL ERROR: Couldn't connect to database";
    private static final String FXML_LOAD_ERROR = "Error loading FXML";
    private static final String CSS_LOAD_ERROR = "Warning: Could not load CSS stylesheet";
    private static final String DB_INIT_ERROR = "Database initialization failed";
    private static final String SHUTDOWN_ERROR = "Error during application shutdown";

    // ========================= APPLICATION LIFECYCLE =========================

    /**
     * Initializes the application before the start() method is called. Sets up
     * database connection and performs pre-startup checks.
     * 
     * @throws Exception if initialization fails
     */
    @Override
    public void init() throws Exception {
        super.init();
        logger.info("Initializing Budget application...");

        try {
            // Initialize database connection
            if (!DataSource.getInstance().open()) {
                logger.error(FATAL_DB_ERROR);
                throw new RuntimeException(DB_INIT_ERROR);
            }

        }
        catch (Exception e) {
            logger.error("Failed to initialize application", e);
            // Show error dialog to user before exiting
            Platform.runLater(() -> showFatalErrorDialog(DB_INIT_ERROR, e.getMessage()));
            throw e;
        }
    }

    /**
     * Starts the JavaFX application and sets up the primary stage.
     * 
     * @param primaryStage The primary stage for this application
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize global logging settings based on environment
            initializeLogging();

            logger.info("Starting Budget application UI...");

            // GlobalVariables.enableDebugLogging();
            GlobalVariables.setInfoLogging();
            // GlobalVariables.setWarnLogging();
            // GlobalVariables.setErrorLogging();

            // Configure primary stage
            configurePrimaryStage(primaryStage);

            // Load FXML and create scene
            Scene scene = createMainScene();

            // Set scene and show stage
            primaryStage.setScene(scene);
            primaryStage.show();

            // Print debug info if in debug mode
            if (GlobalVariables.isDebugMode()) {
                logger.debug("Application startup completed in debug mode");
                GlobalVariables.printCurrentSettings();
            }

        }
        catch (Exception e) {
            logger.error(FXML_LOAD_ERROR, e);
            showErrorDialog("Application Startup Error",
                    "Failed to start the application. Please check the logs for details.", e.getMessage());
            Platform.exit();
        }
    }

    /**
     * Stops the application and performs cleanup operations.
     * 
     * @throws Exception if cleanup fails
     */
    @Override
    public void stop() throws Exception {
        try {
            // Close database connection
            DataSource.getInstance().close();

            logger.info("Budget application shutdown completed");

        }
        catch (Exception e) {
            logger.error(SHUTDOWN_ERROR, e);
            throw e;
        }
        finally {
            super.stop();
        }
    }

    // ========================= SETUP METHODS =========================

    /**
     * Configures the primary stage with title, icon, and minimum size.
     * 
     * @param primaryStage The stage to configure
     */
    private void configurePrimaryStage(Stage primaryStage) {
        primaryStage.setTitle(APPLICATION_TITLE);
        primaryStage.setMinWidth(MIN_WINDOW_WIDTH);
        primaryStage.setMinHeight(MIN_WINDOW_HEIGHT);

        // // Set application icon if available
        // try {
        //     Image icon = new Image(getClass().getResourceAsStream(ICON_RESOURCE));
        //     primaryStage.getIcons().add(icon);
        // }
        // catch (Exception e) {
        //     logger.debug("Application icon not found or could not be loaded: " + ICON_RESOURCE);
        // }

        // Configure close behavior
        primaryStage.setOnCloseRequest(event -> {
            logger.info("Application close requested");
            Platform.exit();
        });
    }

    /**
     * Creates the main scene by loading FXML and applying CSS.
     * 
     * @return The configured Scene
     * @throws Exception if FXML loading fails
     */
    private Scene createMainScene() throws Exception {
        // Load FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_RESOURCE));
        if (loader.getLocation() == null) {
            throw new RuntimeException("FXML resource not found: " + FXML_RESOURCE);
        }

        Parent root = loader.load();
        Scene scene = new Scene(root);

        // Apply CSS stylesheet
       // applyCssStylesheet(scene);

        return scene;
    }

    /**
     * Applies CSS stylesheet to the scene.
     * 
     * @param scene The scene to apply CSS to
     */
    @SuppressWarnings("unused")
    private void applyCssStylesheet(Scene scene) {
        try {
            String cssUrl = getClass().getResource(CSS_RESOURCE).toExternalForm();
            scene.getStylesheets().add(cssUrl);
            logger.debug("CSS stylesheet applied successfully: " + CSS_RESOURCE);

        }
        catch (Exception e) {
            logger.warn(CSS_LOAD_ERROR + ": " + CSS_RESOURCE, e);
            // Application can continue without CSS
        }
    }

    // ========================= ERROR HANDLING =========================

    /**
     * Shows a fatal error dialog and exits the application.
     * 
     * @param title   The error title
     * @param message The error message
     */
    private void showFatalErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Fatal Error");
        alert.setHeaderText(title);
        alert.setContentText(message + "\n\nThe application will now exit.");
        alert.showAndWait();
        Platform.exit();
    }

    /**
     * Shows an error dialog to the user.
     * 
     * @param title   The dialog title
     * @param header  The header text
     * @param content The content text
     */
    private void showErrorDialog(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // ========================= MAIN METHOD =========================

    /**
     * Main entry point of the Budget application.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        try {
            logger.info("Budget application starting...");

            // Launch JavaFX application
            launch(args);

        }
        catch (Exception e) {
            logger.error("Failed to start Budget application", e);
            System.err.println("Failed to start Budget application: " + e.getMessage());
            System.exit(1);
        }
        finally {
            logger.info("*** Budget application finished ***");
        }
    }

    // ========================= UTILITY METHODS =========================

    /**
     * Checks if the application is running in development mode.
     * 
     * @return true if in development mode, false otherwise
     */
    public static boolean isDevelopmentMode() {
        return "development".equals(System.getProperty("app.environment"));
    }

    /**
     * Initialize logging settings based on environment and system properties.
     * Can be controlled via system properties or environment variables.
     */
    private void initializeLogging() {
        try {
            // Check for system property to set log level
            String logLevel = System.getProperty("app.log.level");

            if (logLevel != null && !logLevel.trim().isEmpty()) {
                // Use system property if provided
                GlobalVariables.setLoggingLevel(logLevel);
                logger.info("Logging level set from system property: " + logLevel);
            }
            else if (isDevelopmentMode()) {
                // Development mode - enable debug logging
                GlobalVariables.enableDebugLogging();
                logger.info("Development mode detected - Debug logging enabled");
            }
            else {
                // Production mode - use INFO level
                GlobalVariables.setInfoLogging();
                logger.info("Production mode - Info logging enabled");
            }

            // Log the current settings
            logger.info("Application logging initialized - Level: " + GlobalVariables.getLoggingLevel());

        }
        catch (Exception e) {
            logger.warn("Failed to initialize custom logging settings, using defaults: " + e.getMessage());
            // Fallback to safe defaults
            GlobalVariables.setInfoLogging();
        }
    }
}