package lu.uni.psod.corsanum.utils;

import android.content.Context;
import android.util.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

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

    private String finishLineString;

    //private ArrayList<Marker> mMarkers = null;
    //private HashMap<Integer, Polyline> mPolylines = null;

    private Integer mCurrentSelectedRoute = -1;

    public MapDecorator(Context ctx, GoogleMap mMap, ObservableList<Action> model) {
        this.mCtx   = ctx;
        this.mMap   = mMap;
        this.mModel = model;
        //this.mMarkers   = new ArrayList<>();
        //this.mPolylines = new HashMap<>();

        this.finishLineString = mCtx.getResources().getString(R.string.finish_map_label);

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
                        mModel.get(i),
                        this,
                        i,
                        mModel.get(i).getActionType().getName(),
                        finishLineString,
                        false));
            }
            addLastAction();
        }

        for (MapDecoratorItem item : mDecoratorItemsOptions) {
            item.addActionToMap(mMap);
        }

        zoomInCamera();
    }

    public void selectPartialRoute(int index) {
        if (mCurrentSelectedRoute != -1) {
            mDecoratorItemsOptions.get(mCurrentSelectedRoute).setRouteColor(mCtx.getResources().getColor(R.color.route_default));
        }

        mCurrentSelectedRoute = index;
        mDecoratorItemsOptions.get(index).setRouteColor(mCtx.getResources().getColor(R.color.route_highlighted));
    }

    public void deleteAction(int index) {

        if (index == 0) {
            mDecoratorItemsOptions.get(0).deleteActionFromMap();
        } else if (index == mDecoratorItemsOptions.size() - 1) {
            // delete the last one + spawn second marker on the previous
            int lastIndex = mDecoratorItemsOptions.size()-1;
            mDecoratorItemsOptions.get(lastIndex).deleteActionFromMap();
            mDecoratorItemsOptions.get(lastIndex-1).spawnSecondMarker(mMap);
        } else {
            mDecoratorItemsOptions.get(index-1).reroute();
        }

        mDecoratorItemsOptions.get(index).deleteActionFromMap();
        mDecoratorItemsOptions.remove(index);
    }

    private void addLastAction() {

        int secondLast = mModel.size() - 1;

        Log.i(TAG, "Adding marker options " + secondLast + " with name " + mModel.get(secondLast).getActionType().getName());


        mDecoratorItemsOptions.add(new MapDecoratorItem(
                mCtx,
                mModel.get(secondLast),
                this,
                secondLast,
                mModel.get(secondLast).getActionType().getName(),
                finishLineString,
                true));
    }

    private  LatLngBounds getBoundingBox() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (MapDecoratorItem item : mDecoratorItemsOptions) {
            builder.include(item.getFirstMarker().getPosition());
        }
        return builder.include(
                mDecoratorItemsOptions.
                        get(mDecoratorItemsOptions.size() - 1).
                        getSecondMarker().
                        getPosition())
                .build();
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
    public Polyline addPolylineToMap(PolylineOptions polylineOptions) {
        return mMap.addPolyline(polylineOptions);
    }
}
