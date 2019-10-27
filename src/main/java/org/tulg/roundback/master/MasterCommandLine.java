package org.tulg.roundback.master;

import org.apache.commons.cli.*;
import org.tulg.roundback.core.RoundBackConfig;

/**
 * Created by jasonw on 9/24/2016.
 */
class MasterCommandLine {

    private static final Options options = new Options();
    private static CommandLine commandLine;

    public MasterCommandLine(String[] args, RoundBackConfig rBackConfig) throws Exception{
        Exception e = new Exception("Error: MasterCommandLine is not instantiable.");
        throw e;
    }

    private static void initParser(String[] args){
        buildOptions();
        CommandLineParser commandLineParser = new DefaultParser();


        try {
            commandLine = commandLineParser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            printUsage();
            //e.printStackTrace();
        }

        if(commandLine.hasOption("help")) {
            printUsage();
        }

    }

    static public void parseToConfig(String[] args,RoundBackConfig rBackConfig ) {
        initParser(args);
        parseToConfig(commandLine, rBackConfig);
    }

    static private void parseToConfig(CommandLine commandLine, RoundBackConfig rBackConfig ) {
        // parse to config
        if(commandLine.hasOption("port")) {
            rBackConfig.setMasterPort(commandLine.getOptionValue("port"));
        }

        if(commandLine.hasOption("UseEncryption")){
            if(commandLine.getOptionValue("UseEncryption").compareToIgnoreCase("y") == 0 ){
                rBackConfig.setEncrypted(true);
            } else {
                if (commandLine.getOptionValue("UseEncryption").compareToIgnoreCase("n") == 0) {
                    rBackConfig.setEncrypted(false);
                } else {
                    System.err.println("Error: Unrecognized argument to 'UseEncryption'");
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

    static private void printUsage(){
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("master-server",options);
        System.exit(1);

    }

    static private void buildOptions() {
        Option tmpOpt;

        tmpOpt = Option.builder("p")
                .longOpt("port")
                .numberOfArgs(1)
                .required(false)
                .type(Integer.class)
                .desc("Port to listen on")
                .build();
        options.addOption(tmpOpt);

        tmpOpt = Option.builder("e")
                .longOpt("UseEncryption")
                .numberOfArgs(1)
                .required(false)
                .type(boolean.class)
                .desc("Should we use encryption")
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

        // XXX: Add new option blocks here.

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
