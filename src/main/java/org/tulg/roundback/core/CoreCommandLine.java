package org.tulg.roundback.core;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;


/**
 * Used to parse general, not instance specific, arguments.
 * 
 * @author Jason Williams <jasonw@tulg.org>
 */
public class CoreCommandLine {

    protected Options options = new Options();
    private CommandLine commandLine;

    public CoreCommandLine() {
    }

    public CoreCommandLine(String[] args, RoundBackConfig rBackConfig){
    }

    /**
     * Parse the commandline options into the MasterConfig
     * object passed in
     *
     * @param  args         commandline string to parse.
     * @param  masterConfig the config to populate with the options
     */
    public void initParser(String[] args){

        buildOptions();
        CommandLineParser commandLineParser = new DefaultParser();


        try {
            commandLine = commandLineParser.parse(options, args);
        } catch (org.apache.commons.cli.ParseException e) {
            Logger.log(Logger.LOG_LEVEL_CRITICAL, e);
            printUsage();
        }

        if(commandLine.hasOption("help")) {
            printUsage();
        }

    }

    public void parseToConfig(String[] args,RoundBackConfig rBackConfig ) {
        initParser(args);
        parseToConfig(commandLine, rBackConfig);
    }

    protected void parseToConfig(CommandLine commandLine, RoundBackConfig rBackConfig ) {
        // parse to config

        if(commandLine.hasOption("loglevel")){
            rBackConfig.setDefaultLogLevel(Integer.parseInt(commandLine.getOptionValue("loglevel")));
        }


        if(commandLine.hasOption("UseEncryption")){
            if(commandLine.getOptionValue("UseEncryption").compareToIgnoreCase("y") == 0 ){
                rBackConfig.setEncrypted(true);
            } else {
                if (commandLine.getOptionValue("UseEncryption").compareToIgnoreCase("n") == 0) {
                    rBackConfig.setEncrypted(false);
                } else {
                    Logger.log(Logger.LOG_LEVEL_ERROR, "Unrecognized argument to 'UseEncryption'");
                }
            }
        }

        if(commandLine.hasOption("EncryptionKey")){
            rBackConfig.setEncryptionKey(commandLine.getOptionValue("EncryptionKey"));
        }

        // This check should be at the end of this function.
        if(commandLine.hasOption("save")) {
            rBackConfig.save();
        }

    }

    private void printUsage(){
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(RoundBack.getInstanceString(),options);
        System.exit(1);

    }

    protected void buildOptions() {
        Option tmpOpt;

        tmpOpt = Option.builder("e")
                .longOpt("UseEncryption")
                .numberOfArgs(1)
                .required(false)
                .type(boolean.class)
                .desc("<y|n> - Should we use encryption")
                .build();
        options.addOption(tmpOpt);

        tmpOpt = Option.builder("k")
                .longOpt("EncryptionKey")
                .numberOfArgs(1)
                .required(false)
                .type(String.class)
                .desc("Key for Encryption")
                .build();
        options.addOption(tmpOpt);

        tmpOpt = Option.builder("l")
                 .longOpt("loglevel")
                 .numberOfArgs(1)
                 .required(false)
                 .type(boolean.class)
                 .desc("Loglevel for logging. 0 - critial only, to 4 for debug, default 3")
                 .build();
        options.addOption(tmpOpt);

        tmpOpt = Option.builder("s")
                .longOpt("save")
                .numberOfArgs(0)
                .required(false)
                .type(boolean.class)
                .desc("Save settings to persistent after parsing commandline.")
                .build();
        options.addOption(tmpOpt);

        tmpOpt = Option.builder("h")
                .longOpt("help")
                .numberOfArgs(0)
                .required(false)
                .type(boolean.class)
                .desc("Print this help page.")
                .build();
        options.addOption(tmpOpt);

    }

}
