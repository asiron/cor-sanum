package lu.uni.psod.corsanum.models;

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

    }

    public Action(Position mEndPos, Position mStartPos, double mExpectedDuration, ActionType mActionType) {
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
}
