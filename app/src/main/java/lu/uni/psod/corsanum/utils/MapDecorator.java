package lu.uni.psod.corsanum.utils;

import android.content.Context;
import android.util.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;

import lu.uni.psod.corsanum.R;
import lu.uni.psod.corsanum.models.fit.Action;

/**
 * Created by rlopez on 14/12/15.
 */
public class MapDecorator implements RoutingSucceededListener {

    private static final String TAG = "MapDecorator";
    private GoogleMap mMap = null;
    private Context mCtx = null;

    private ObservableList<Action> mModel = null;
    private ArrayList<MapDecoratorItem> mDecoratorItemsOptions = null;
    private ArrayList<Marker> mMarkers = null;
    private HashMap<Integer, Polyline> mPolylines = null;

    private Integer mCurrentSelectedRoute = -1;



    public MapDecorator(Context ctx, GoogleMap mMap, ObservableList<Action> model) {
        this.mCtx   = ctx;
        this.mMap   = mMap;
        this.mModel = model;
        this.mMarkers   = new ArrayList<>();
        this.mPolylines = new HashMap<>();
        this.mDecoratorItemsOptions = new ArrayList<>();
    }

    public void initMapDecorator() {

        if (mModel.size() == 0) {
            return;
        } else if (mModel.size() == 1) {
            addLastAction();
        } else {

            for (int i=0; i<mModel.size()-1; i++) {

                Log.i(TAG, "Adding marker options " + i + " with name " + mModel.get(i).getActionType().getName());

                mDecoratorItemsOptions.add(new MapDecoratorItem(
                        mCtx,
                        this,
                        i,
                        mModel.get(i).getStartPos(),
                        mModel.get(i).getEndPos(),
                        mModel.get(i).getActionType().getName(),
                        null,
                        false));
            }
            addLastAction();
        }

        for (MapDecoratorItem item : mDecoratorItemsOptions) {
            Log.i(TAG, "Adding decorator item");
            MarkerOptions firstMarkerOpt  = item.getFirstMarker();
            MarkerOptions secondMarkerOpt = item.getSecondMarker();

            mMarkers.add(mMap.addMarker(firstMarkerOpt));
            if (secondMarkerOpt != null)
                mMarkers.add(mMap.addMarker(secondMarkerOpt));
        }

        zoomInCamera();
    }

    public void selectPartialRoute(int index) {
        if (mCurrentSelectedRoute != -1) {
            mPolylines.get(mCurrentSelectedRoute).setColor(mCtx.getResources().getColor(R.color.route_default));
        }

        mCurrentSelectedRoute = index;
        mPolylines.get(index).setColor(mCtx.getResources().getColor(R.color.route_highlighted));
    }

    private void addLastAction() {

        int secondLast = mModel.size() - 1;

        Log.i(TAG, "Adding marker options " + secondLast + " with name " + mModel.get(secondLast).getActionType().getName());

        String finishLineString = mCtx.getResources().getString(R.string.finish_map_label);

        mDecoratorItemsOptions.add(new MapDecoratorItem(
                mCtx,
                this,
                secondLast,
                mModel.get(secondLast).getStartPos(),
                mModel.get(secondLast).getEndPos(),
                mModel.get(secondLast).getActionType().getName(),
                finishLineString,
                true));
    }

    private  LatLngBounds getBoundingBox() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : mMarkers) {
            builder.include(marker.getPosition());
        }
        return builder.build();
    }

    private void zoomInCamera() {
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

    @Override
    public void addPolylineToMap(int index, PolylineOptions polylineOptions) {
        mPolylines.put(index, mMap.addPolyline(polylineOptions));
    }
}
