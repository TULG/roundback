package org.tulg.roundback.core;

/**
 * Created by jasonw on 4/24/2017.
 */

public class BackupStatus {
    public enum Status{
        PENDING,
        STARTED,
        RUNNING,
        SUCCESS,
        ERROR
    }

    public static Status fromInt(int statusInt) {
        switch (statusInt) {
            case 0:
                return Status.PENDING;

            case 1:
                return Status.STARTED;

            case 2:
                return Status.RUNNING;

            case 3:
                return Status.SUCCESS;

            case 4:
                return Status.ERROR;

            default:
                return null;

        }

    }

    public static int toInt(Status status){
        switch (status) {
            case PENDING:
                return 0;

            case STARTED:
                return 1;

            case RUNNING:
                return 2;

            case SUCCESS:
                return 3;

            case ERROR:
                return 4;

            default:
                return -1;

        }
    }

    public static String toString(Status status) {

        switch (status) {
            case PENDING:
                return "Pending";

            case STARTED:
                return "Started";

            case RUNNING:
                return "Running";

            case SUCCESS:
                return "Completed, Success";

            case ERROR:
                return "ERROR";

            default:
                return "";

        }
    }
}
