RoundBack
=========

RoundBack Backup Software started as a result of my disdain for any and all
OpenSource or "Enterprise" backup software available.  I never found one that
did everything I needed, so I decided to write one.  The true path of any
OpenSource developer.

Still in its infancy, RoundBack currently has no actual use as a backup
software at this point.  Most of this is me working through coding the
ideas in my head as proof of concept to myself.

This repository is a package that can be compiled into a simple .jar
file and run from any machine that supports java.  The .jar contains all
the code needed to run the three components of RoundBack:

    - org.tulg.roundback.master - the master server.  Handles the DB and schedules/handles backups/restore jobs.
    - org.tulg.roundback.storage - the storage server.  Clients connect here to store files. master keeps track of them
    in the database

    - org.tulg.roundback.testClient - used internally for development purposes.
    - org.tulg.roundback.client - houses the main network protocol stack for the client.
    - org.tulg.roundback.rbadmin - administrative interface for admining the master server.
    - org.tulg.roundback.core - some core shared functionality
