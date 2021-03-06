package lu.uni.psod.corsanum.models.fit;

/**
 * Created by asiron on 12/6/15.
 */
public enum ActionType {
    WALK("Walking"),
    WALK_FAST("Walking fast"),
    WALK_SLOW("Walking slow"),
    RUN("Running"),
    RUN_FAST("Running fast"),
    STRETCH("Stretching");

    private final String des;

    ActionType(String d) {
        des = d;
    }

    public String getName() {
        return des;
    }

    public String toString() {return des; }
}
