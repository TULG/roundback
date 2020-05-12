package org.tulg.roundback.master;

import org.apache.commons.cli.*;
import org.tulg.roundback.core.CoreCommandLine;
import org.tulg.roundback.core.Logger;
import org.tulg.roundback.core.RoundBackConfig;

/**
 * Created by jasonw on 9/24/2016.
 */
class MasterCommandLine extends CoreCommandLine {


    protected void parseToConfig(CommandLine commandLine, RoundBackConfig rBackConfig ) {

        // parse to config
        if(commandLine.hasOption("port")) {
            rBackConfig.setMasterPort(commandLine.getOptionValue("port"));
        }

        // This call should be at the bottom of your paseToConfig
        super.parseToConfig(commandLine, rBackConfig);


    }

    @Override
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
                .longOpt("timeout")
                .numberOfArgs(1)
                .required(false)
                .type(Integer.class)
                .desc("Session Timeout in seconds, default 3600")
                .build();
        options.addOption(tmpOpt);
        

        super.buildOptions();
    }

}
