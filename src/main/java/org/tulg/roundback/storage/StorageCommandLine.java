package org.tulg.roundback.storage;

import org.apache.commons.cli.*;
import org.tulg.roundback.core.CoreCommandLine;
import org.tulg.roundback.core.RoundBackConfig;


/**
 * Created by jasonw on 9/24/2016.
 */
class StorageCommandLine extends CoreCommandLine {
    //private final Options options = new Options();
    //private CommandLine commandLine;

    protected void parseToConfig(CommandLine commandLine, RoundBackConfig rBackConfig ) {
        // parse to config
        if(commandLine.hasOption("port")) {
            rBackConfig.setStoragePort(commandLine.getOptionValue("port"));
        }

        if(commandLine.hasOption("maxThreads")){
            rBackConfig.setMaxThreads(Integer.valueOf(commandLine.getOptionValue("maxThreads")));
        }

        if(commandLine.hasOption("maxDataPort")){
            rBackConfig.setMaxDataPort(Integer.valueOf(commandLine.getOptionValue("maxDataPort")));
        }

        if(commandLine.hasOption("minDataPort")){
            rBackConfig.setMinDataPort(Integer.valueOf(commandLine.getOptionValue("minDataPort")));
        }

        // This call should be at the bottom of your paseToConfig
        super.parseToConfig(commandLine, rBackConfig);

    }

    protected void buildOptions() {
        Option tmpOpt;

        tmpOpt = Option.builder("p")
                .longOpt("port")
                .numberOfArgs(1)
                .required(false)
                .type(Integer.class)
                .desc("Port to listen on")
                .build();
        options.addOption(tmpOpt);

        tmpOpt = Option.builder("t")
                .longOpt("maxThreads")
                .numberOfArgs(1)
                .required(false)
                .type(Integer.class)
                .desc("Maximum number of concurrent threads allowed from a single client")
                .build();
        options.addOption(tmpOpt);

        tmpOpt = Option.builder("n")
                .longOpt("minDataPort")
                .numberOfArgs(1)
                .required(false)
                .type(Integer.class)
                .desc("Minimum data port to use.")
                .build();
        options.addOption(tmpOpt);

        tmpOpt = Option.builder("x")
                .longOpt("maxDataPort")
                .numberOfArgs(1)
                .required(false)
                .type(Integer.class)
                .desc("Maximum data port to use.")
                .build();
        options.addOption(tmpOpt);

        // This should be at the bottom of buildOptions()
        super.buildOptions();

    }
/*     public StorageCommandLine(String[] args, RoundBackConfig rBackConfig){
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

        parseToConfig(commandLine, rBackConfig);

    } */

    
/*     private void printUsage(){
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("storage-server",options);
        System.exit(1);
    }
 */

}
