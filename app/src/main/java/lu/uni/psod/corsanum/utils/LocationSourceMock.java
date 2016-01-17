package lu.uni.psod.corsanum.utils;

import android.location.Location;
import android.os.Handler;

import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.model.LatLng;

import java.util.concurrent.TimeUnit;

/**
 * Created by rlopez on 17/01/16.
 */
public class LocationSourceMock implements LocationSource {

    private Handler mHandler = null;
    private MapDecorator mMapDecorator = null;
    private OnLocationChangedListener listener = null;

    private LatLng mCurrentLocation = null;

    private double mSpeed = 15.0;

    private final long UPDATE_PERIOD = TimeUnit.SECONDS.toMillis(2);


    private final Runnable updateLocationRunnable = new Runnable() {

        @Override
        public void run() {
            Location nextLocation = getNextLocation();
            listener.onLocationChanged(nextLocation);
            scheduleNewFix();
        }
    };

    LocationSourceMock(MapDecorator mapDecorator, double speed) {
        this.mMapDecorator = mapDecorator;
        this.mSpeed = (speed * 1000.0) / 3600.0;
    }

    LocationSourceMock(MapDecorator mapDecorator) {
        this(mapDecorator, 15.0);
    }

    private Location getNextLocation() {

        // Returng location
        return null;
    }

    private void scheduleNewFix() {
        mHandler.postDelayed(updateLocationRunnable, UPDATE_PERIOD);
    }

    private void initLocation() {
        // Init location here
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        this.listener = onLocationChangedListener;
        initLocation();
        scheduleNewFix();
    }

    @Override
    public void deactivate() {
        mHandler.removeCallbacks(updateLocationRunnable);
    }
}
