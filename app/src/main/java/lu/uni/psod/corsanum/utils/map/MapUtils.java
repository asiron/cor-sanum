package lu.uni.psod.corsanum.utils.map;

import com.google.android.gms.maps.model.LatLng;
import static java.lang.Math.*;

import java.util.List;

/**
 * Created by rlopez on 27/01/16.
 */

public class MapUtils {

    public static double distBetween(LatLng pos1, LatLng pos2) {
        return distBetween(pos1.latitude, pos1.longitude,
                pos2.latitude, pos2.longitude);
    }

    /** distance in meters **/
    public static double distBetween(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2)
                * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;

        int meterConversion = 1609;

        return (dist * meterConversion);
    }

    public static double routeLength(List<LatLng> route) {
        double routeDistance = 0.0;

        if (route.size() < 2)
            return 0.0;

        for (int i=0; i<route.size()-1; i++) {
            LatLng p1 = new LatLng(route.get(i  ).latitude, route.get(i  ).longitude);
            LatLng p2 = new LatLng(route.get(i+1).latitude, route.get(i+1).longitude);
            routeDistance += MapUtils.distBetween(p1,p2);
        }
        return routeDistance;
    }

    public static LatLng interpolate(LatLng from, LatLng to, double fraction) {
        double fromLat = toRadians(from.latitude);
        double fromLng = toRadians(from.longitude);
        double toLat = toRadians(to.latitude);
        double toLng = toRadians(to.longitude);
        double cosFromLat = cos(fromLat);
        double cosToLat = cos(toLat);

        double angle = computeAngleBetween(from, to);
        double sinAngle = sin(angle);
        if (sinAngle < 1E-6) {
            return from;
        }
        double a = sin((1 - fraction) * angle) / sinAngle;
        double b = sin(fraction * angle) / sinAngle;

        // Converts from polar to vector and interpolate.
        double x = a * cosFromLat * cos(fromLng) + b * cosToLat * cos(toLng);
        double y = a * cosFromLat * sin(fromLng) + b * cosToLat * sin(toLng);
        double z = a * sin(fromLat) + b * sin(toLat);

        // Converts interpolated vector back to polar.
        double lat = atan2(z, sqrt(x * x + y * y));
        double lng = atan2(y, x);
        return new LatLng(toDegrees(lat), toDegrees(lng));
    }

    private static double distanceRadians(double lat1, double lng1, double lat2, double lng2) {
        return arcHav(havDistance(lat1, lat2, lng1 - lng2));
    }

    static double computeAngleBetween(LatLng from, LatLng to) {
        return distanceRadians(toRadians(from.latitude), toRadians(from.longitude),
                toRadians(to.latitude), toRadians(to.longitude));
    }

    static double hav(double x) {
        double sinHalf = sin(x * 0.5);
        return sinHalf * sinHalf;
    }

    static double arcHav(double x) {
        return 2 * asin(sqrt(x));
    }

    static double havDistance(double lat1, double lat2, double dLng) {
        return hav(lat1 - lat2) + hav(dLng) * cos(lat1) * cos(lat2);
    }


}