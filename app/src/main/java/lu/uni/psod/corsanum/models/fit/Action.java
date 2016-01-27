package lu.uni.psod.corsanum.models.fit;

import com.google.gson.annotations.SerializedName;

/**
 * Created by asiron on 12/6/15.
 */
public class Action {

    @SerializedName("start_pos")
    private Position mStartPos;

    @SerializedName("end_pos")
    private Position mEndPos;

    @SerializedName("expected_dur")
    private double mExpectedDuration;

    @SerializedName("action_type")
    private ActionType mActionType;

    public Action() {
        this( new Position(), new Position(), 0.0, ActionType.WALK);
    }

    public Action(Position mStartPos, Position mEndPos, double mExpectedDuration, ActionType mActionType) {

        this.mEndPos = mEndPos;
        this.mStartPos = mStartPos;
        this.mExpectedDuration = mExpectedDuration;
        this.mActionType = mActionType;
    }

    public Position getStartPos() {
        return mStartPos;
    }

    public void setStartPos(Position startPos) {
        this.mStartPos = startPos;
    }

    public Position getEndPos() {
        return mEndPos;
    }

    public void setEndPos(Position endPos) {
        this.mEndPos = endPos;
    }

    public double getExpectedDuration() {
        return mExpectedDuration;
    }

    public void setExpectedDuration(double dur) {
        this.mExpectedDuration = dur;
    }

    public ActionType getActionType() {
        return mActionType;
    }

    public void setActionType(ActionType actionType) {
        this.mActionType = actionType;
    }

    public String getFullDesc() {

        String fullDesc = mActionType.getName();

        if (mExpectedDuration != 0.0 && mActionType == ActionType.STRETCH) {
            int durationSeconds = ((int)mExpectedDuration) % 60;
            int durationMinutes = ((int)mExpectedDuration) / 60;
            fullDesc += (
                    " " + String.valueOf(durationMinutes)
                            + "m " + String.valueOf(durationSeconds) + "s");
        }
        return fullDesc;
    }
}
