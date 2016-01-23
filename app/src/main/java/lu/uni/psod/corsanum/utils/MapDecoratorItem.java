package lu.uni.psod.corsanum.utils;

import android.content.Context;
import android.util.Log;

import com.directions.route.Route;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import lu.uni.psod.corsanum.R;
import lu.uni.psod.corsanum.models.fit.Position;

/**
 * Created by rlopez on 14/12/15.
 */
public class MapDecoratorItem {

    private static final String TAG = "MapDecoratorItem";

    private Context context;

    private Position startPos = null;
    private Position endPos   = null;

    private String firstActionName  = null;
    private String secondActionName = null;

    private MarkerOptions firstMarker  = null;
    private MarkerOptions secondMarker = null;

    private PolylineOptions polylineRoute = null;

    private boolean hasSecondMarker = false;

    MapDecoratorItem(Context ctx, Position startPos, Position endPos, String firstActionName, String secondActionName, boolean hasSecondMarker) {

        this.context = ctx;

        this.firstActionName  = firstActionName;
        this.secondActionName = secondActionName;

        this.startPos  = startPos;
        this.endPos    = endPos;

        this.hasSecondMarker = hasSecondMarker;

        LatLng sPos = new LatLng(this.startPos.getLat(), this.startPos.getLong());
        LatLng ePos = new LatLng(this.endPos.getLat(),   this.endPos.getLong());

        firstMarker = new MarkerOptions().position(sPos).title(this.firstActionName);

        if (hasSecondMarker)
            secondMarker = new MarkerOptions().position(ePos).title(this.secondActionName);

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

                for (int j=0; j<routes.size(); ++j) {
                    polylineRoute = new PolylineOptions();
                    polylineRoute.color(context.getResources().getColor(R.color.route_default));
                    polylineRoute.width(10);
                    polylineRoute.addAll(routes.get(j).getPoints());

                }
            }

            @Override
            public void onRoutingCancelled() {
                Log.i(TAG, "Routing was cancelled.");

            }
        };

        Routing routing = new Routing.Builder()
                .travelMode(Routing.TravelMode.WALKING)
                .withListener(routingListener)
                .waypoints(sPos, ePos)
                .build();
        routing.execute();

    }

}
