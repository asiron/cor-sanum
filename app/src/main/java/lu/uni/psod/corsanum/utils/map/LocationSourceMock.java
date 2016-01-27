package lu.uni.psod.corsanum.utils.map;

import android.location.Location;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by rlopez on 17/01/16.
 */
public class LocationSourceMock implements LocationSource {

    abstract class LocationSourceMockException extends Exception {
        public LocationSourceMockException(String err) { super(err); }
    }

    class RouteFinishedException extends LocationSourceMockException {
        public RouteFinishedException(String err) { super(err); }
    }

    class EmptyActionRoute extends LocationSourceMockException {
        public EmptyActionRoute(String err) { super(err); }
    }

    public interface OnPartialRouteCompletedListener {
        void onPartialRouteCompleted();
        void onFullRouteCompleted();
    }

    private static final String TAG = "LocationSourceMock";
    private static final float ACCURACY = 1; // Meters

    private boolean mDeactivated = true;

    private Handler mHandler = null;
    private MapController mMapController = null;
    private OnLocationChangedListener mOnLocationChangedListener = null;
    private OnPartialRouteCompletedListener  mOnPartialRouteCompletedListener = null;

    private int mCurrentActionIndex = 0;
    private int mCurrentRoutePointIndex = 0;
    private double mCurrentInterpolationDistance = 0;
    private double mCurrentInterpolationDistanceTraveled = 0;


    private List<LatLng> mCurrentRoute = null;
    private LatLng mCurrentLocation = null;
    private LatLng mNextPointLocation = null;
    private LatLng mCurrentPointLocation = null;


    private double mSpeed = 200.0;
    private double mTotalRouteLength = 0.0;
    private long UPDATE_PERIOD = TimeUnit.MILLISECONDS.toMillis(16);

    private final Runnable updateLocationRunnable = new Runnable() {

        @Override
        public void run() {
            Location nextLocation = getNextLocation();
            mOnLocationChangedListener.onLocationChanged(nextLocation);
            if (!mDeactivated) scheduleNewFix();
        }
    };

    public LocationSourceMock(MapController mapController, double speed, OnPartialRouteCompletedListener listener) {
        this.mMapController = mapController;
        this.mSpeed = (speed * 1000.0) / 3600.0;
        this.mOnPartialRouteCompletedListener = listener;
        this.mHandler = new Handler();
    }

    public LocationSourceMock(MapController mapController, OnPartialRouteCompletedListener listener) {
        this(mapController, 400.0, listener);
    }

    private Location getNextLocation() {

        if (MapUtils.distBetween(mCurrentLocation, mNextPointLocation) <= 2) {
            acceptNextInterval();
        }

        if (mCurrentLocation == null) {
            Log.i(TAG, "Current actual location was null");
            return null;
        }
        else if (mNextPointLocation == null) {
            Log.i(TAG, "Current actual location was null");
            return null;
        }


        mCurrentInterpolationDistanceTraveled += (mSpeed * (UPDATE_PERIOD / 1000.0f));

        double fractionOfDistanceTraveled = mCurrentInterpolationDistanceTraveled
            / mCurrentInterpolationDistance;

        LatLng nextPoint;

        if (fractionOfDistanceTraveled > 1.0)
            nextPoint = mNextPointLocation;
        else
            nextPoint = MapUtils.interpolate(
                    mCurrentPointLocation, mNextPointLocation, fractionOfDistanceTraveled
            );

        mCurrentLocation = nextPoint;

        Location location = new Location(getClass().getSimpleName());
        location.setTime(System.currentTimeMillis());
        location.setAccuracy(ACCURACY);
        location.setSpeed((float) mSpeed);
        location.setLatitude(nextPoint.latitude);
        location.setLongitude(nextPoint.longitude);
        //location.setBearing(randomizer.nextInt(360));

        return location;
    }

    private void scheduleNewFix() {
        mHandler.postDelayed(updateLocationRunnable, UPDATE_PERIOD);
    }

    private void initLocation() {
        Log.i(TAG, "Location mock initiated");
        mCurrentRoute = mMapController
                .getItem(0).getPolylineRoute().getPoints();

        for (int i=0; i<mMapController.getActionCount(); ++i) {
            mTotalRouteLength +=  MapUtils.routeLength(
                    mMapController.getItem(i).getPolylineRoute().getPoints()
            );
        }

        Log.i(TAG, "Total route distance " + String.valueOf(mTotalRouteLength));

        acceptNextInterval();
    }

    private void acceptNextInterval() {
        try {
            mCurrentLocation = acceptNextPoint();
        } catch (LocationSourceMockException e) {
            Log.i(TAG, "Exception caught: " + e.getMessage());
        }
        mNextPointLocation = peekNextPoint();
        mCurrentPointLocation = mCurrentLocation;
        mCurrentInterpolationDistance =
                MapUtils.distBetween(mCurrentLocation, mNextPointLocation);
        Log.i(TAG, "Distance for this interval is " + String.valueOf(mCurrentInterpolationDistance));
        mCurrentInterpolationDistanceTraveled = 0.0;
    }

    private LatLng peekNextPoint() {
        if (mCurrentRoutePointIndex < mCurrentRoute.size() &&
                mCurrentActionIndex < mMapController.getActionCount()) {
            Log.i(TAG, "Peeking at point of idx: "
                    + String.valueOf(mCurrentRoutePointIndex)
                    + " and action index: "
                    + String.valueOf(mCurrentActionIndex));
            return mCurrentRoute.get(mCurrentRoutePointIndex);
        } else if (mCurrentRoutePointIndex >= mCurrentRoute.size() &&
                mCurrentActionIndex < mMapController.getActionCount()-1) {
            Log.i(TAG, "Peeking exceeded action, getting second action "
                    + String.valueOf(mCurrentActionIndex+1));
            return mMapController.
                    getItem(mCurrentActionIndex+1).
                    getPolylineRoute().
                    getPoints().
                    get(0);
        } else return mCurrentLocation;
    }

    private LatLng acceptNextPoint() throws LocationSourceMockException {

        if (mCurrentRoutePointIndex >= mCurrentRoute.size()) {
            // Partial route finished, check if full route finished
            Log.i(TAG, "Partial route " + String.valueOf(mCurrentActionIndex) + " finished!");
            mOnPartialRouteCompletedListener.onPartialRouteCompleted();
            mCurrentActionIndex++;

            if (mCurrentActionIndex >= mMapController.getActionCount() ) {
                // Full route finished, handle it
                Log.i(TAG, "Full route finished!");
                mOnPartialRouteCompletedListener.onFullRouteCompleted();
                deactivate();
                throw new RouteFinishedException("Route finished");
            } else {
                mCurrentRoute = mMapController
                        .getItem(mCurrentActionIndex).getPolylineRoute().getPoints();
                mCurrentRoutePointIndex = 0;
            }
        }
        Log.i(TAG, "Getting new point: " + String.valueOf(mCurrentRoutePointIndex));

        if (mCurrentRoutePointIndex >= mCurrentRoute.size()) {
            // couldnt retrieve new point, something is wrong
            Log.i(TAG, "New action has an empty route");
            throw new EmptyActionRoute("Action doesn't have route");
        } else {
            LatLng nextPoint = mCurrentRoute.get(mCurrentRoutePointIndex);
            mCurrentRoutePointIndex++;
            return nextPoint;
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mDeactivated = false;
        this.mOnLocationChangedListener = onLocationChangedListener;
        initLocation();
        scheduleNewFix();
    }

    @Override
    public void deactivate() {
        mDeactivated = true;
        mHandler.removeCallbacks(updateLocationRunnable);
    }
}
