package org.tulg.roundback.storage;


import org.apache.commons.cli.*;


/**
 * Created by jasonw on 9/24/2016.
 */
class StorageCommandLine {
    private final Options options = new Options();
    private CommandLine commandLine;

    public StorageCommandLine(String[] args, StorageConfig storageConfig){
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

        parseToConfig(commandLine, storageConfig);

    }

    private void parseToConfig(CommandLine commandLine, StorageConfig storageConfig ) {
        // parse to config
        if(commandLine.hasOption("port")) {
            storageConfig.setPort(commandLine.getOptionValue("port"));
        }

        if(commandLine.hasOption("maxThreads")){
            storageConfig.setMaxThreads(Integer.valueOf(commandLine.getOptionValue("maxThreads")));
        }

        if(commandLine.hasOption("maxDataPort")){
            storageConfig.setMaxDataPort(Integer.valueOf(commandLine.getOptionValue("maxDataPort")));
        }

        if(commandLine.hasOption("minDataPort")){
            storageConfig.setMinDataPort(Integer.valueOf(commandLine.getOptionValue("minDataPort")));
        }

        if(commandLine.hasOption("UseEncryption")){
            if(commandLine.getOptionValue("UseEncryption").compareToIgnoreCase("y") == 0 ){
                storageConfig.setEncrypted(true);
            } else {
                if (commandLine.getOptionValue("UseEncryption").compareToIgnoreCase("n") == 0) {
                    storageConfig.setEncrypted(false);
                } else {
                    System.err.println("Error: Unrecognized argument to 'UseEncryption'");
                }
            }
        }

        if(commandLine.hasOption("EncryptionKey")){
            storageConfig.setEncryptionKey(commandLine.getOptionValue("EncryptionKey"));
        }

        // XXX: This check should be at the end of this function.
        if(commandLine.hasOption("save")) {
                storageConfig.save();
        }



    }

    private void printUsage(){
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("storage-server",options);
        System.exit(1);
    }

    private void buildOptions() {
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
