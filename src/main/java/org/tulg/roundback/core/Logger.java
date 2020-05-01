package org.tulg.roundback.core;

/**
 * Logger - Handles simple logging to stdout and stderr using log leve.s
 * @author Jason Williams <jasonw@tulg.org>
 *
 * 
 */
public class Logger {
    public static int LOG_LEVEL_CRITICAL=0;
    public static int LOG_LEVEL_ERROR=1;
    public static int LOG_LEVEL_WARN=2;
    public static int LOG_LEVEL_INFO=3;
    public static int LOG_LEVEL_DEBUG=4;

    private static int logLevel=Logger.LOG_LEVEL_INFO;
    private static boolean stderrToStdOut=false;
    private static String logFile=""; // FUTURE


    /**
     * Logs a message at a specific level.  If the level passed higher then the 
     * logLevel set, the message is surpressed
     * <p>
     * Supported levels are:
     * <ul>
     * <li> Logger.LOG_LEVEL_CRITICAL=0</li>
     * <li> Logger.LOG_LEVEL_ERROR=1</li>
     * <li> Logger.LOG_LEVEL_WARN=2</li>
     * <li> Logger.LOG_LEVEL_INFO=3</li>
     * <li>Logger.LOG_LEVEL_DEBUG=4</li>
     * </ul>
     * <p>
     * All methods and fields are static, class is meant to be used statically.
     * <p>
     * This instance of the method automatically adds a newline. 
     *
     * @param  level        The level to log this message at.
     * @param  message      The message to log
     */
    public static void log(int level, String message){
        Logger.log(level, message, true);
    }

        /**
     * Logs a message at a specific level.  If the level passed higher then the 
     * logLevel set, the message is surpressed
     * <p>
     * Supported levels are:
     * <ul>
     * <li> Logger.LOG_LEVEL_CRITICAL=0</li>
     * <li> Logger.LOG_LEVEL_ERROR=1</li>
     * <li> Logger.LOG_LEVEL_WARN=2</li>
     * <li> Logger.LOG_LEVEL_INFO=3</li>
     * <li>Logger.LOG_LEVEL_DEBUG=4</li>
     * </ul>
     * <p>
     * All methods and fields are static, class is meant to be used statically.
     * <p>
     *
     * @param  level        The level to log this message at.
     * @param  message      The message to log
     * @param  newline      Boolean to specify if we should add a newline
     */
    public static void log(int level, String message, boolean newline){

        // TODO: If logFile is set to anything other than "" try to open it, 
        // and use it to log to.  If open fails, clear it and default to 
        // stdout/stderr

        // convert level to string
        String logLevelStr = "";
        switch (level){
            case 0:
                logLevelStr = "CRIT: ";
                break;
            case 1:
                logLevelStr = "ERR : ";
                break;
            case 2:
                logLevelStr = "WARN: ";
                break;
            case 3:
                logLevelStr = "INFO: ";
                break;
            case 4:
                logLevelStr = "DEBG: ";
                break;
            default:
                logLevelStr="     ";
                break;

        }

        if(newline){
            message = message + "\n";
        }
        if(!stderrToStdOut){
            if(level < Logger.LOG_LEVEL_ERROR){
                if(level <= Logger.logLevel) {
                    System.err.print(String.format("%s%s", logLevelStr, message));
                }
                return;
            }
        }
        if(level <= Logger.logLevel){
            System.out.print(message);
        }

    }

    public static int getLogLevel(){
        return logLevel;
    }

    public static void setLogLevel(int logLevel) {
        Logger.logLevel = logLevel;

    }
}