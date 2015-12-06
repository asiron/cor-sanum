package lu.uni.psod.corsanum.models;

/**
 * Created by asiron on 12/6/15.
 */
public class Position {

    private double mLongitude;
    private double mLatitude;

    public Position() {
    }

    public Position(double mLongitude, double mLatitude) {
        this.mLongitude = mLongitude;
        this.mLatitude = mLatitude;
    }

    public double getmLongitude() {
        return mLongitude;
    }

    public void setmLongitude(double mLongitude) {
        this.mLongitude = mLongitude;
    }

    public double getmLatitude() {
        return mLatitude;
    }

    public void setmLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
    }
}
