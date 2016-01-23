package lu.uni.psod.corsanum;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.directions.route.Route;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lu.uni.psod.corsanum.fragments.ControlExerciseFragment;
import lu.uni.psod.corsanum.models.fit.Action;
import lu.uni.psod.corsanum.models.fit.Exercise;
import lu.uni.psod.corsanum.models.fit.Position;
import lu.uni.psod.corsanum.services.GoogleFitService;
import lu.uni.psod.corsanum.services.MessageType;

public class ExerciseActivity extends BaseActivity implements OnMapReadyCallback {

    private final String TAG = "ExerciseActivity";

    private MapFragment mMapFragment = null;
    private ControlExerciseFragment mControlExerciseFragment  = null;

    private GoogleMap mMap = null;

    private HashMap<Integer, Marker> mMarkers;

    private int mCurrentExerciseIndex = 0;
    private Exercise mCurrentExercise = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);


        mMarkers = new HashMap<Integer, Marker>();

        mCurrentExerciseIndex = getIntent().getIntExtra(getString(R.string.current_exercise_idx), 0);
        mCurrentExercise = mExerciseList.get(mCurrentExerciseIndex);

        mControlExerciseFragment = (ControlExerciseFragment) getFragmentManager()
                .findFragmentById(R.id.control_exercise);

        mMapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.control_exercise_map);

        mMapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        initMap();
    }

    public void initMap() {
        initMarkers();
        initRoute();
    }

    public void placeMarker(Integer idx, Position pos, String title) {
        LatLng markerPos = new LatLng(pos.getLat(), pos.getLong());
        MarkerOptions newMarker = new MarkerOptions().position(markerPos).title(title);
        Marker m = mMap.addMarker(newMarker);
        mMarkers.put(idx, m);

    }

    public void initRoute() {


        RoutingListener routingListener = new RoutingListener() {
            @Override
            public void onRoutingFailure() {
                Log.i(TAG, "Routing failed.");
            }

            @Override
            public void onRoutingStart() {
                Log.i(TAG, "Routing was started.");
            }

            @Override
            public void onRoutingSuccess(ArrayList<Route> routes, int i) {

                Log.d(TAG, String.valueOf(routes.size()));

                for (int j=0; j<routes.size(); ++j) {
                    PolylineOptions polyoptions = new PolylineOptions();
                    if (j % 2 == 0 ) {
                        polyoptions.color(getResources().getColor(R.color.route_default));
                    } else {
                        polyoptions.color(getResources().getColor(R.color.route_highlighted));
                    }

                    polyoptions.width(10);
                    polyoptions.addAll(routes.get(j).getPoints());
                    mMap.addPolyline(polyoptions);
                }
            }

            @Override
            public void onRoutingCancelled() {
                Log.i(TAG, "Routing was cancelled.");

            }
        };

        List<LatLng> waypoints = new ArrayList<LatLng>();

        for(int i=0; i<mMarkers.size(); ++i) {
            waypoints.add(mMarkers.get(i).getPosition());
        }

        Routing routing = new Routing.Builder()
                .travelMode(Routing.TravelMode.WALKING)
                .withListener(routingListener)
                .waypoints(waypoints)
                .build();
        routing.execute();

    }

    public void initMarkers() {
        int i = 0;
        for (Action a : mCurrentExercise.getActions()) {
            placeMarker(i++, a.getStartPos(), a.getActionType().getName());
        }
        int lastIndex     = mCurrentExercise.getActions().size() - 1;
        Action lastAction = mCurrentExercise.getActions().get(lastIndex);

        placeMarker(i, lastAction.getEndPos(), lastAction.getActionType().getName());
        LatLngBounds boundingBox = getBoundingBox();

        Log.i(TAG, "LAT : "
                        + boundingBox.northeast.latitude + " "
                        + boundingBox.southwest.latitude + " LONG :  "
                        + boundingBox.northeast.longitude + " "
                        + boundingBox.southwest.longitude
        );

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

            @Override
            public void onCameraChange(CameraPosition arg0) {
                // Move camera.
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(getBoundingBox(), 15));
                // Remove listener to prevent position reset on camera move.
                mMap.setOnCameraChangeListener(null);
            }
        });
    }

    public LatLngBounds getBoundingBox() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : mMarkers.values()) {
            builder.include(marker.getPosition());
        }
        return builder.build();
    }

    public int getCurrentExerciseIndex() {
        return mCurrentExerciseIndex;
    }

    public Exercise getCurrentExercise() {
        return mCurrentExercise;
    }


}
