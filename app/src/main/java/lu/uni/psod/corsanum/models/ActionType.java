package lu.uni.psod.corsanum.models;

/**
 * Created by asiron on 12/6/15.
 */
public enum ActionType {
    WALK("Walking"),
    WALK_FAST("Walking fast"),
    WALK_SLOW("Walking slow"),
    RUN("Running"),
    RUN_FAST("Running fast"),
    STRETCH("Stretching"),
    UNKNOWN("Unknown action");

    private final String description;

    private ActionType(String value) {
        description = value;
    }

    public String getName() {
        return description;
    }
}
