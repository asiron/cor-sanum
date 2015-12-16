package lu.uni.psod.corsanum;

import android.os.Bundle;
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

import lu.uni.psod.corsanum.fragments.ExerciseDetailHeaderFragment;
import lu.uni.psod.corsanum.models.fit.Action;
import lu.uni.psod.corsanum.models.fit.Exercise;
import lu.uni.psod.corsanum.models.fit.Position;

public class ExerciseDetailActivity extends BaseActivity
        implements OnMapReadyCallback, ExerciseDetailHeaderFragment.OnActionSelectedListener
{

    private final String TAG = "ExerciseDetailActivity";

    private int mCurrentExerciseIndex = 0;
    private Exercise mCurrentExercise = null;

    private MapFragment mMapFragment                     = null;
    private ExerciseDetailHeaderFragment mHeaderFragment = null;

    private HashMap<Integer, Marker> mMarkers;

    private GoogleMap mMap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_detail);

        mCurrentExerciseIndex = getIntent().getIntExtra(getString(R.string.current_exercise_idx),0);
        mCurrentExercise = mExerciseList.get(mCurrentExerciseIndex);

        mHeaderFragment = (ExerciseDetailHeaderFragment) getFragmentManager()
                        .findFragmentById(R.id.exercise_detail_header);

        mMapFragment = (MapFragment) getFragmentManager()
                        .findFragmentById(R.id.exercise_detail_map);

        mMarkers = new HashMap<>() ;

        mMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        initMap();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHeaderFragment.updateAdapterDataset(mExerciseList.get(mCurrentExerciseIndex).getActions());
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
        LatLngBounds bounds = builder.build();
        return bounds;
    }

    public int getCurrentExerciseIndex() {
        return mCurrentExerciseIndex;
    }

    public Exercise getCurrentExercise() {
        return mCurrentExercise;
    }

    @Override
    public void onActionSelected(int position) {
        mMarkers.get(0).setRotation(90.0f);
    }
}
