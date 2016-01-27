package lu.uni.psod.corsanum.utils.map;

import android.content.Context;
import android.util.Log;

import com.directions.route.Route;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import lu.uni.psod.corsanum.R;
import lu.uni.psod.corsanum.models.fit.Action;
import lu.uni.psod.corsanum.models.fit.Position;

/**
 * Created by rlopez on 14/12/15.
 */
public class MapDecoratorItem {

    private static final String TAG = "MapDecoratorItem";

    private Context context;

    private RoutingSucceededListener mListener;

    private Integer mActionIndex = -1;
    private Integer mCurrentColor = -1;

    private Action action = null;

    private LatLng startPos = null;
    private LatLng endPos   = null;

    private String firstActionName  = null;
    private String secondActionName = null;

    private MarkerOptions firstMarker  = null;
    private MarkerOptions secondMarker = null;

    private PolylineOptions polylineRoute = null;

    private Marker firstMapMarker  = null;
    private Marker secondMapMarker = null;

    private Polyline mapPolyline = null;

    MapDecoratorItem(Context ctx, Action action,
                     RoutingSucceededListener listener,
                     Integer actionIndex,
                     String firstActionName, String secondActionName,
                     boolean hasSecondMarker) {

        this.context = ctx;
        this.action = action;
        this.mActionIndex = actionIndex;

        this.mCurrentColor = context.getResources().getColor(R.color.route_default);

        this.mListener = listener;

        this.firstActionName  = firstActionName;
        this.secondActionName = secondActionName;

        this.startPos = new LatLng(action.getStartPos().getLat(), action.getStartPos().getLong());
        this.endPos = new LatLng(action.getEndPos().getLat(),   action.getEndPos().getLong());

        firstMarker = new MarkerOptions().position(startPos).title(this.firstActionName);

        if (hasSecondMarker)
            secondMarker = new MarkerOptions().position(endPos).title(this.secondActionName);

        buildRouter(startPos, endPos).execute();
    }

    public void addActionToMap(GoogleMap map) {
        firstMapMarker = map.addMarker(firstMarker);
        if (secondMarker != null)
            secondMapMarker = map.addMarker(secondMarker);
    }

    public void deleteActionFromMap() {
        firstMapMarker.remove();
        if (secondMapMarker != null) {
            secondMapMarker.remove();
        }
        if (mapPolyline != null) {
            mapPolyline.remove();
        }
    }

    public void reroute() {
        mapPolyline.remove();
        recalcPositions();
        buildRouter(startPos, endPos).execute();

    }

    public void setRouteColor(int color) {
        mCurrentColor = color;
        if (mapPolyline != null) {
            mapPolyline.setColor(color);
        }
    }

    public void setMarkerColor(float color) {
        if (secondMapMarker != null) {
            secondMapMarker.setIcon(BitmapDescriptorFactory.
                    defaultMarker(color));
        }
        firstMapMarker.setIcon(BitmapDescriptorFactory.defaultMarker(color));
    }

    public void spawnSecondMarker(GoogleMap map) {
        secondMarker = new MarkerOptions().position(endPos).title(this.secondActionName);
        secondMapMarker = map.addMarker(secondMarker);
    }

    public void deleteSecondMarker() {
        secondMapMarker.remove();
        secondMarker = null;
    }

    public MarkerOptions getFirstMarker() {
        return firstMarker;
    }

    public MarkerOptions getSecondMarker() {
        return secondMarker;
    }

    public void shake() {
        MarkerAnimation.shake(firstMapMarker);
        if(secondMapMarker != null)
            MarkerAnimation.shake(secondMapMarker);
    }

    public PolylineOptions getPolylineRoute() {
        return polylineRoute;
    }

    private RoutingListener mRoutingListener = new RoutingListener() {

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

            int routesCount = routes.size();
            if (routesCount <= 0) {
                Log.i(TAG, "No routes found, probably to close objects to each other");
                return;
            } else if (routesCount > 1) {
                Log.i(TAG, "Found more than route choosing the first one");
            }

            Log.i(TAG, "Route found");

            polylineRoute = new PolylineOptions();
            polylineRoute.color(mCurrentColor);
            polylineRoute.width(10);
            polylineRoute.addAll(routes.get(0).getPoints());

            mapPolyline =  mListener.addPolylineToMap(polylineRoute);
        }

        @Override
        public void onRoutingCancelled() {
            Log.i(TAG, "Routing was cancelled.");

        }
    };

    private Routing buildRouter(LatLng sPos, LatLng ePos) {
        return new Routing.Builder()
                .travelMode(Routing.TravelMode.WALKING)
                .withListener(mRoutingListener)
                .waypoints(sPos, ePos)
                .build();
    }

    private void recalcPositions() {
        this.startPos = new LatLng(action.getStartPos().getLat(), action.getStartPos().getLong());
        this.endPos   = new LatLng(action.getEndPos().getLat(),   action.getEndPos().getLong());
    }


    public void setDraggable(boolean value) {
        firstMapMarker.setDraggable(value);
        if (secondMapMarker != null)
            secondMapMarker.setDraggable(value);
    }

    public void updateModelPositionFromMarker(Marker m, PositionType pos) {

        if (pos == PositionType.START) {
            this.startPos = m.getPosition();
            action.setStartPos(new Position(startPos.latitude, startPos.longitude));
        } else if (pos == PositionType.END) {
            this.endPos = m.getPosition();
            action.setEndPos(new Position(endPos.latitude, endPos.longitude));
        }
    }

    public enum PositionType {
        START, END
    }
}
