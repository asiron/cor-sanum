package lu.uni.psod.corsanum;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import lu.uni.psod.corsanum.fragments.ExerciseDetailHeaderFragment;

public class ExerciseDetailActivity extends BaseActivity implements OnMapReadyCallback{

    TextView tv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_detail);

        ExerciseDetailHeaderFragment headerFragment =
                (ExerciseDetailHeaderFragment) getFragmentManager()
                        .findFragmentById(R.id.exercise_detail_header);

        MapFragment mapFragment =
                (MapFragment) getFragmentManager()
                        .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        LatLng sydney = new LatLng(-34, 151);
        map.addMarker(new MarkerOptions()
                .position(sydney).title("Marker in Sydney"));
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    public void initMap() {

    }
}
