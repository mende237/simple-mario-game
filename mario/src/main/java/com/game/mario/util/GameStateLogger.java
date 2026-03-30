package com.game.mario.util;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Utility class for logging game state to a file.
 */
public class GameStateLogger {
    
    private static final String LOG_DIR = "logs";
    private static final String LOG_FILE = "mario_state.log";
    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    static {
        // Create logs directory if it doesn't exist
        try {
            Files.createDirectories(Paths.get(LOG_DIR));
        } catch (IOException e) {
            System.err.println("Failed to create logs directory: " + e.getMessage());
        }
    }
    
    /**
     * Write Mario state to log file
     */
    public static void logMarioState(MarioState state, Object value) {
        try (FileWriter fw = new FileWriter(getLogFilePath(), true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            
            String timestamp = LocalDateTime.now().format(TIMESTAMP);
            String logEntry = String.format("[%s] %s: %s%n", timestamp, state.name(), value);
            bw.write(logEntry);
            
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }
    
    /**
     * Write a full state snapshot to log file
     */
    public static void logFullStateSnapshot(java.util.Map<MarioState, ?> states) {
        try (FileWriter fw = new FileWriter(getLogFilePath(), true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            
            String timestamp = LocalDateTime.now().format(TIMESTAMP);
            bw.write("=================== State of Mario: ==========================================\n");
            bw.write("[" + timestamp + "]\n");
            
            for (MarioState state : MarioState.values()) {
                Object value = states.get(state);
                bw.write(String.format("%s: %s%n", state.name(), value));
            }
            
            bw.write("===========================================================================\n");
            bw.write("\n");
            
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }
    
    /**
     * Clear the existing log file
     */
    public static void clearLog() {
        try {
            new FileWriter(getLogFilePath()).close();
            System.out.println("Log file cleared: " + getLogFilePath());
        } catch (IOException e) {
            System.err.println("Failed to clear log file: " + e.getMessage());
        }
    }
    
    /**
     * Get the full path to the log file
     */
    public static String getLogFilePath() {
        return LOG_DIR + File.separator + LOG_FILE;
    }
}
