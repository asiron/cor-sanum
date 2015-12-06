package lu.uni.psod.corsanum.models;

/**
 * Created by asiron on 12/6/15.
 */
public class Action {

    private Position mStartPos;
    private Position mEndPos;

    private double mExpectedDuration;

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
}
