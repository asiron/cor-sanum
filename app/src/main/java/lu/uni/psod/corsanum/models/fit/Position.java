package lu.uni.psod.corsanum.models.fit;

import com.google.gson.annotations.SerializedName;

/**
 * Created by asiron on 12/6/15.
 */
public class Position {


    @SerializedName("longitude")
    private double mLongitude;

    @SerializedName("latitude")
    private double mLatitude;

    public Position() {
    }

    public Position(double mLatitude, double mLongitude) {
        this.mLatitude  = mLatitude;
        this.mLongitude = mLongitude;
    }

    public double getLong() {
        return mLongitude;
    }

    public void setLong(double mLongitude) {
        this.mLongitude = mLongitude;
    }

    public double getLat() {
        return mLatitude;
    }

    public void setLat(double mLatitude) {
        this.mLatitude = mLatitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Position position = (Position) o;
        if (Double.compare(position.mLongitude, mLongitude) != 0)
            return false;
        return Double.compare(position.mLatitude, mLatitude) == 0;

    }

}
