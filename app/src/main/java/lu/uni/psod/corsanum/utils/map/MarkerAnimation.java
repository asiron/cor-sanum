package lu.uni.psod.corsanum.utils.map;

import android.util.Log;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by rlopez on 23/01/16.
 */
public class MarkerAnimation {

    static void shake(final Marker marker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final float durationInMs = 5000;

        final Interpolator interpolator = new CycleInterpolator(4);


        handler.post(new Runnable() {
            long elapsed;
            float t;
            int dir = 1;
            @Override
            public void run() {
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;

                float v = interpolator.getInterpolation(t) * 10;
                marker.setRotation(v);

                if (t < 1) {
                    handler.postDelayed(this, 16);
                }
            }
        });
    }
}
