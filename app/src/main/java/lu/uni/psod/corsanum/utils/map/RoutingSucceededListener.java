package lu.uni.psod.corsanum.utils.map;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Created by rlopez on 23/01/16.
 */
public interface RoutingSucceededListener {
    Polyline addPolylineToMap(PolylineOptions polylineOptions);
}
