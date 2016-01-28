package lu.uni.psod.corsanum.utils.map;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
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
public class MapController implements RoutingSucceededListener {

    public interface MyLocationChangedListener {
        void onMyLocationChanged(LatLng loc);
    }

    private static final String TAG = "MapController";
    private GoogleMap mMap = null;
    private Context mCtx = null;

    private MyLocationChangedListener mMyLocationChangedCallback;

    private ObservableList<Action> mModel = null;
    private ArrayList<MapDecoratorItem> mDecoratorItemsOptions = null;

    private String finishLineString;

    private Integer mCurrentSelectedRoute = -1;
    private int routeCount = 0;

    private boolean mMockReady = false;

    public MapController(Context ctx, GoogleMap mMap, ObservableList<Action> model, boolean useMock) {
        this.mCtx   = ctx;
        this.mMap   = mMap;
        this.mModel = model;
        this.finishLineString = mCtx.getResources().getString(R.string.finish_map_label);

        this.mDecoratorItemsOptions = new ArrayList<>();
    }

    public void initMapController() {

        if (mModel.size() == 0) {
            setFollowPosition(true, 1);
            return;
        } else if (mModel.size() == 1) {
            addLastAction();
        } else {

            for (int i=0; i<mModel.size()-1; i++) {

                Log.i(TAG, "Adding marker options " + i + " with name " + mModel.get(i).getFullDesc());

                mDecoratorItemsOptions.add(new MapDecoratorItem(
                        mCtx,
                        mModel.get(i),
                        this,
                        i,
                        mModel.get(i).getFullDesc(),
                        finishLineString,
                        false));
            }
            addLastAction();
        }

        for (MapDecoratorItem item : mDecoratorItemsOptions) {
            item.addActionToMap(mMap);
        }

        zoomInMarkers();
    }

    public void setMyLocationChangedCallback(MyLocationChangedListener callback) {
        mMyLocationChangedCallback = callback;
    }

    public void selectPartialRoute(int index) {
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
                lastIndex+1, newAction.getFullDesc(), finishLineString, true);

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
        setFollowPosition(true, 100);
    }

    public MapDecoratorItem getItem(int index) { return mDecoratorItemsOptions.get(index); }

    public int getActionCount () { return mDecoratorItemsOptions.size(); }

    private void shakeMarker(int index) {
        mDecoratorItemsOptions.get(index).shake();
    }

    private void setDraggable(final int index, boolean value) {
        mDecoratorItemsOptions.get(index).setDraggable(value);

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
            }

            @Override
            public void onMarkerDrag(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

                MapDecoratorItem currentActionItem = mDecoratorItemsOptions.get(index);

                if (marker.getTitle().equals(finishLineString)) {

                    Log.i(TAG, "Dumping end marker " + String.valueOf(index));

                    currentActionItem.updateModelPositionFromMarker(marker, MapDecoratorItem.PositionType.END);
                    currentActionItem.reroute();
                    return;
                }

                Log.i(TAG, "Dumping start marker " + String.valueOf(index));

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

        Log.i(TAG, "Adding marker options " + secondLast + " with name " + mModel.get(secondLast).getFullDesc());

        mDecoratorItemsOptions.add(new MapDecoratorItem(
                mCtx,
                mModel.get(secondLast),
                this,
                secondLast,
                mModel.get(secondLast).getFullDesc(),
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

    private void zoomInMarkers() {
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

    public void setFollowPosition(boolean value, final int rate) {
        if (value) {
            Log.i(TAG, "Starting - to follow current position with camera");
            mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

                int counter = 0;

                @Override
                public void onMyLocationChange(Location location) {

                    if (mMyLocationChangedCallback != null) {
                        LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                        mMyLocationChangedCallback.onMyLocationChanged(loc);
                    }

                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);

                    counter++;
                    if (counter % rate == 0) {
                        counter = 0;
                        mMap.animateCamera(cameraUpdate);

                    }
                }
            });
        } else {
            Log.i(TAG, "Stopping - to follow current position with camera");
            mMap.setOnMyLocationChangeListener(null);
        }
    }

    @Override
    public Polyline addPolylineToMap(PolylineOptions polylineOptions) {
        routeCount++;
        Log.i(TAG, "Increasing route count; now - " + String.valueOf(routeCount));
        if (routeCount >= mModel.size()) {
            Log.i(TAG, "Ready to set location source to mock");
            mMockReady = true;
        }
        return mMap.addPolyline(polylineOptions);
    }

    public void trySetMock(LocationSourceMock mock) {
        if (mMockReady)
            mMap.setLocationSource(mock);
    }

    public void disableMock() {
        mMap.setLocationSource(null);
    }

    public void updateMapControllersModel(ObservableList<Action> actions) { mModel = actions; }

    public void updateAction(int index) {
        mDecoratorItemsOptions.get(index).updateTitle();
    }
}
