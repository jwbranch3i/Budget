package com.budget;

import org.slf4j.LoggerFactory;

/**
 * Global variables and constants accessible throughout the application.
 * This class provides centralized control over application-wide settings including logging.
 */
public final class GlobalVariables {
    
    // Private constructor to prevent instantiation
    private GlobalVariables() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    
    // ========================= APPLICATION CONSTANTS =========================
    
    /** Application version */
    public static final String APP_VERSION = "1.0.0";
    
    /** Default directory for file operations */
    public static final String DEFAULT_DIRECTORY = "C:\\";
    
    /** File path storage location */
    public static final String FILE_PATH_STORAGE = "filePath.txt";
    
    /** UI window titles */
    public static final String EDIT_CATEGORY_TITLE = "Edit Category";
    public static final String EDIT_ITEM_TITLE = "Edit Item";
    public static final String TOTAL_LABEL = "Total";
    
    /** Date format pattern */
    public static final String MONTH_YEAR_FORMAT = "MMMM yyyy";
    
    // ========================= GLOBAL STATE VARIABLES =========================
    
    /** Current application theme */
    public static volatile String currentTheme = "default";
    
    /** Debug mode flag */
    public static volatile boolean debugMode = true;
    
    /** Current logging level */
    public static volatile String logLevel = "DEBUG";
    
    /** Last selected file path for imports */
    public static volatile String lastImportPath = "";
    
    /** Application-wide loading state */
    public static volatile boolean isLoading = false;
    
    /** Auto-save interval in minutes */
    public static volatile int autoSaveInterval = 5;
    
    /** Show confirmation dialogs */
    public static volatile boolean showConfirmationDialogs = true;
    
    // ========================= LOGGING CONTROL METHODS =========================
    
    /**
     * Change the logging level for the entire application
     * This method attempts to set the logging level using reflection to avoid direct dependencies
     * @param level The logging level (TRACE, DEBUG, INFO, WARN, ERROR)
     */
    public static void setLoggingLevel(String level) {
        if (level == null || level.trim().isEmpty()) {
            throw new IllegalArgumentException("Log level cannot be null or empty");
        }
        
        String normalizedLevel = level.trim().toUpperCase();
        
        // Validate the level
        if (!isValidLogLevel(normalizedLevel)) {
            System.err.println("Invalid log level: " + level + ". Valid levels are: TRACE, DEBUG, INFO, WARN, ERROR");
            throw new IllegalArgumentException("Invalid log level: " + level);
        }
        
        try {
            // Use reflection to avoid direct dependency on logback classes
            Object rootLogger = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
            
            // Try to set level using reflection if it's a logback logger
            if (rootLogger.getClass().getName().contains("logback")) {
                Class<?> levelClass = Class.forName("ch.qos.logback.classic.Level");
                Object levelObj = levelClass.getMethod("valueOf", String.class).invoke(null, normalizedLevel);
                rootLogger.getClass().getMethod("setLevel", levelClass).invoke(rootLogger, levelObj);
                
                logLevel = normalizedLevel;
                debugMode = "DEBUG".equals(logLevel) || "TRACE".equals(logLevel);
                
                System.out.println("Logging level changed to: " + logLevel);
            } else {
                // Fallback: just update our internal state
                logLevel = normalizedLevel;
                debugMode = "DEBUG".equals(logLevel) || "TRACE".equals(logLevel);
                System.out.println("Logging level state updated to: " + logLevel + " (runtime change may not be supported)");
            }
            
        } catch (Exception e) {
            // Fallback: just update our internal variables
            logLevel = normalizedLevel;
            debugMode = "DEBUG".equals(logLevel) || "TRACE".equals(logLevel);
            System.out.println("Logging level state updated to: " + logLevel + " (runtime change failed: " + e.getMessage() + ")");
        }
    }
    
    /**
     * Validate if the given log level is valid
     * @param level The log level to validate
     * @return true if valid, false otherwise
     */
    private static boolean isValidLogLevel(String level) {
        return "TRACE".equals(level) || "DEBUG".equals(level) || "INFO".equals(level) || 
               "WARN".equals(level) || "ERROR".equals(level);
    }
    
    /**
     * Enable debug logging (sets level to DEBUG)
     */
    public static void enableDebugLogging() {
        setLoggingLevel("DEBUG");
    }
    
    /**
     * Enable trace logging (most verbose)
     */
    public static void enableTraceLogging() {
        setLoggingLevel("TRACE");
    }
    
    /**
     * Set logging to INFO level (normal operation)
     */
    public static void setInfoLogging() {
        setLoggingLevel("INFO");
    }
    
    /**
     * Set logging to WARN level (warnings and errors only)
     */
    public static void setWarnLogging() {
        setLoggingLevel("WARN");
    }
    
    /**
     * Set logging to ERROR level (errors only)
     */
    public static void setErrorLogging() {
        setLoggingLevel("ERROR");
    }
    
    /**
     * Get current logging level
     * @return Current logging level as string
     */
    public static String getLoggingLevel() {
        return logLevel;
    }
    
    /**
     * Check if debug mode is enabled
     * @return true if debug or trace logging is enabled
     */
    public static boolean isDebugMode() {
        return debugMode;
    }
    
    /**
     * Check if trace logging is enabled
     * @return true if trace logging is enabled
     */
    public static boolean isTraceMode() {
        return "TRACE".equals(logLevel);
    }
    
    /**
     * Get debug status as string for logging
     * @return Debug status description
     */
    public static String getDebugStatus() {
        return debugMode ? "DEBUG MODE ON (" + logLevel + ")" : "RELEASE MODE (" + logLevel + ")";
    }
    
    // ========================= UTILITY METHODS =========================
    
    /**
     * Reset all global variables to their default values
     */
    public static void resetToDefaults() {
        currentTheme = "default";
        debugMode = true;
        lastImportPath = "";
        isLoading = false;
        autoSaveInterval = 5;
        showConfirmationDialogs = true;
        setLoggingLevel("DEBUG"); // This will also update debugMode
    }
    
    /**
     * Print current application settings to console
     */
    public static void printCurrentSettings() {
        System.out.println("=== Global Application Settings ===");
        System.out.println("App Version: " + APP_VERSION);
        System.out.println("Current Theme: " + currentTheme);
        System.out.println("Logging Level: " + logLevel);
        System.out.println("Debug Mode: " + debugMode);
        System.out.println("Auto-save Interval: " + autoSaveInterval + " minutes");
        System.out.println("Show Confirmations: " + showConfirmationDialogs);
        System.out.println("Last Import Path: " + (lastImportPath.isEmpty() ? "None" : lastImportPath));
        System.out.println("Is Loading: " + isLoading);
        System.out.println("===================================");
    }
}