package lu.uni.psod.corsanum.utils.map;

import android.content.Context;
import android.util.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import lu.uni.psod.corsanum.R;
import lu.uni.psod.corsanum.models.fit.Action;
import lu.uni.psod.corsanum.models.fit.ActionType;
import lu.uni.psod.corsanum.models.fit.Position;
import lu.uni.psod.corsanum.utils.ObservableList;

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

    private Integer mCurrentSelectedRoute = -1;

    public MapDecorator(Context ctx, GoogleMap mMap, ObservableList<Action> model) {
        this.mCtx   = ctx;
        this.mMap   = mMap;
        this.mModel = model;

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
        /*if (mCurrentSelectedRoute != -1 && mCurrentSelectedRoute < mDecoratorItemsOptions.size()-1 ) {
            mDecoratorItemsOptions.get(mCurrentSelectedRoute).setRouteColor(mCtx.getResources().getColor(R.color.route_default));
        }

        mCurrentSelectedRoute = index;


        mDecoratorItemsOptions.get(index).setRouteColor(mCtx.getResources().getColor(R.color.route_highlighted));
        */
        int defaultColor = mCtx.getResources().getColor(R.color.route_default);
        for (MapDecoratorItem item : mDecoratorItemsOptions)
        {
            item.setRouteColor(defaultColor);
        }
        mDecoratorItemsOptions.get(index).setRouteColor(mCtx.getResources().getColor(R.color.route_highlighted));

    }

    public void addAction(ActionType actionType, double duration) {

        int lastIndex = mDecoratorItemsOptions.size() - 1;
        Action newAction = null;

        if (lastIndex == -1)
        {
            LatLng newPos  = mMap.getCameraPosition().target;
            LatLng newPos2 = new LatLng(newPos.latitude + 0.001, newPos.longitude + 0.01);
            newAction = new Action(
                    new Position(newPos.latitude, newPos.longitude),
                    new Position(newPos2.latitude,  newPos2.longitude),
                    duration,
                    actionType
            );
            mModel.add(newAction);
        }
        else
        {
            MapDecoratorItem lastItem = mDecoratorItemsOptions.get(lastIndex);
            LatLng lastPos = lastItem.getSecondMarker().getPosition();
            LatLng newPos = mMap.getCameraPosition().target;
            newAction = new Action(
                    new Position(lastPos.latitude, lastPos.longitude),
                    new Position(newPos.latitude,  newPos.longitude),
                    duration,
                    actionType
            );
            mModel.add(newAction);
            lastItem.deleteSecondMarker();
        }

        MapDecoratorItem newItem = new MapDecoratorItem(mCtx, newAction, this,
                lastIndex+1, actionType.getName(), finishLineString, true);

        mDecoratorItemsOptions.add(newItem);
        newItem.addActionToMap(mMap);
        newItem.setDraggable(true);
        newItem.setMarkerColor(BitmapDescriptorFactory.HUE_ORANGE);

        startShowingEdit(lastIndex + 1);
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

    public void startShowingEdit(int index) {
        selectPartialRoute(index);
        shakeMarker(index);
        setDraggable(index, true);
        float color = BitmapDescriptorFactory.HUE_VIOLET;
        mDecoratorItemsOptions.get(index).setMarkerColor(color);

    }

    public void finishShowingEdit() {
        for (MapDecoratorItem item : mDecoratorItemsOptions) {
            item.setDraggable(false);
            item.setMarkerColor(BitmapDescriptorFactory.HUE_RED);
        }
    }

    private void shakeMarker(int index) {
        mDecoratorItemsOptions.get(index).shake();
    }

    private void setDraggable(final int index, boolean value) {
        mDecoratorItemsOptions.get(index).setDraggable(value);

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {}

            @Override
            public void onMarkerDrag(Marker marker) {}

            @Override
            public void onMarkerDragEnd(Marker marker) {

                MapDecoratorItem currentActionItem = mDecoratorItemsOptions.get(index);

                if (marker.getTitle().equals(finishLineString)) {
                    currentActionItem.updateModelPositionFromMarker(marker, MapDecoratorItem.PositionType.END);
                    currentActionItem.reroute();
                    return;
                }

                currentActionItem.updateModelPositionFromMarker(marker, MapDecoratorItem.PositionType.START);
                currentActionItem.reroute();

                if (index != 0) {
                    mDecoratorItemsOptions.get(index - 1).
                            updateModelPositionFromMarker(marker, MapDecoratorItem.PositionType.END);
                    mDecoratorItemsOptions.get(index - 1).reroute();
                }
            }
        });
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
