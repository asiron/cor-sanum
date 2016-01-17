package lu.uni.psod.corsanum.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataSourcesResult;

import java.util.concurrent.TimeUnit;

public class GoogleFitService extends Service {

    public static final String TAG = "GoogleFitService";

    private GoogleApiClient mGoogleApiFitnessClient;

    private boolean mTryingToConnect = false;

    private int mInitialStepCount = -1;

    public static final String SERVICE_REQUEST_TYPE = "requestType";
    public static final int TYPE_REQUEST_CONNECTION = 2;

    public static final String HISTORY_INTENT = "fitHistory";
    public static final String HISTORY_EXTRA_STEPS_TODAY = "stepsToday";

    public static final String FIT_LOCATION_DATA_INTENT = "fitLocationDataIntent";
    public static final String FIT_LOCATION_DATA_EXTRA_LAT = "fitLocationDataLatitude";
    public static final String FIT_LOCATION_DATA_EXTRA_LONG = "fitLocationDataLongitude";

    public static final String FIT_STEPS_DATA_INTENT = "fitStepsDataIntent";
    public static final String FIT_STEPS_DATA_EXTRA_STEPS = "fitStepsDataSteps";

    public static final String FIT_NOTIFY_INTENT = "fitStatusUpdateIntent";
    public static final String FIT_LOGIN_INTENT = "fitLoginIntent";
    public static final String FIT_EXTRA_CONNECTION_MESSAGE = "fitFirstConnection";
    public static final String FIT_EXTRA_NOTIFY_FAILED_STATUS_CODE = "fitExtraFailedStatusCode";
    public static final String FIT_EXTRA_NOTIFY_FAILED_INTENT = "fitExtraFailedIntent";

    private OnDataPointListener mLocationDataPointListener = new OnDataPointListener() {
        @Override
        public void onDataPoint(DataPoint dataPoint) {
            float longitude = 0, latitude = 0;

            for (Field field : dataPoint.getDataType().getFields()) {
                Value val = dataPoint.getValue(field);
                try {
                    if (field.getName().equals("longitude")) {
                        Log.i(TAG, "Received Longitude value from GoogleFit: " + val.asFloat());
                        longitude = val.asFloat();

                    } else if (field.getName().equals("latitude")) {
                        Log.i(TAG, "Received Latitude value from GoogleFit: " + val.asFloat());
                        latitude = val.asFloat();
                    }
                } catch (IllegalStateException e) {
                    Log.i(TAG, "Exception when receiving data point" + e.getMessage());
                    return;
                }
            }

            notifyUiFitLocationData(latitude, longitude);
        }
    };

    private OnDataPointListener mStepsDataPointListener = new OnDataPointListener() {
        @Override
        public void onDataPoint(DataPoint dataPoint) {
            for (Field field : dataPoint.getDataType().getFields()) {
                if (field.getName().equals("steps")) {
                    int currentSteps = dataPoint.getValue(field).asInt();
                    if (mInitialStepCount == -1) {
                        mInitialStepCount = currentSteps;
                    }
                    notifyUiFitStepsData(currentSteps - mInitialStepCount);
                    Log.i(TAG, "Received Steps from GoogleFit: " + String.valueOf(currentSteps - mInitialStepCount));
                }
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleLogin(intent);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "GoogleFitService destroyed");
        if (mGoogleApiFitnessClient.isConnected()) {
            Log.d(TAG, "Disconnecting Google Fit.");
            unregisterFitnessDataListener("steps", mStepsDataPointListener);
            unregisterFitnessDataListener("location", mLocationDataPointListener);
            mGoogleApiFitnessClient.disconnect();
        }

        Log.i(TAG, "Unregistering login request receiver");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mFitLoginRequestReceiver);

        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        buildFitnessClient();
        Log.d(TAG, "GoogleFitService created");
        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(mFitLoginRequestReceiver, new IntentFilter(GoogleFitService.FIT_LOGIN_INTENT));
    }

    public void connectGoogleFit() {

        if (!mGoogleApiFitnessClient.isConnected()) {
            mTryingToConnect = true;
            mGoogleApiFitnessClient.connect();

            while (mTryingToConnect) {
                try {
                    Thread.sleep(20000, 0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void buildFitnessClient() {
        mGoogleApiFitnessClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.SENSORS_API)
                .addScope(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ_WRITE))
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {

                            @Override
                            public void onConnected(Bundle bundle) {
                                Log.i(TAG, "Google Fit connected.");
                                mTryingToConnect = false;
                                Log.d(TAG, "Notifying the UI that we're connected.");
                                notifyUiFitConnected();
                                findFitnessDataSources();
                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                // If your connection to the sensor gets lost at some point,
                                // you'll be able to determine the reason and react to it here.
                                mTryingToConnect = false;
                                if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                    Log.i(TAG, "Google Fit Connection lost.  Cause: Network Lost.");
                                } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                    Log.i(TAG, "Google Fit Connection lost.  Reason: Service Disconnected");
                                }
                            }
                        }
                )
                .addOnConnectionFailedListener(
                        new GoogleApiClient.OnConnectionFailedListener() {
                            // Called whenever the API client fails to connect.
                            @Override
                            public void onConnectionFailed(ConnectionResult result) {
                                mTryingToConnect = false;
                                notifyUiFailedConnection(result);
                            }
                        }
                )
                .build();
    }

    private void notifyUiFitConnected() {
        Intent intent = new Intent(FIT_NOTIFY_INTENT);
        intent.putExtra(FIT_EXTRA_CONNECTION_MESSAGE, FIT_EXTRA_CONNECTION_MESSAGE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void notifyUiFailedConnection(ConnectionResult result) {
        Intent intent = new Intent(FIT_NOTIFY_INTENT);
        intent.putExtra(FIT_EXTRA_NOTIFY_FAILED_STATUS_CODE, result.getErrorCode());
        intent.putExtra(FIT_EXTRA_NOTIFY_FAILED_INTENT, result.getResolution());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void notifyUiFitLocationData(float latitude, float longitude) {
        Intent intent = new Intent(FIT_LOCATION_DATA_INTENT);
        intent.putExtra(FIT_LOCATION_DATA_EXTRA_LAT, latitude);
        intent.putExtra(FIT_LOCATION_DATA_EXTRA_LONG, longitude);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void notifyUiFitStepsData(int steps) {
        Intent intent = new Intent(FIT_STEPS_DATA_INTENT);
        intent.putExtra(FIT_STEPS_DATA_EXTRA_STEPS, steps);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void handleLogin(Intent intent) {
        Log.i(TAG, "Received login/logout intent");
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                connectGoogleFit();
            }
        }).start();
    }

    private BroadcastReceiver mFitLoginRequestReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            handleLogin(intent);
        }
    };

    private void findFitnessDataSources() {
        Fitness.SensorsApi.findDataSources(mGoogleApiFitnessClient, new DataSourcesRequest.Builder()
                .setDataTypes(
                        DataType.TYPE_LOCATION_SAMPLE,
                        DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .build())
                .setResultCallback(new ResultCallback<DataSourcesResult>() {
                    @Override
                    public void onResult(DataSourcesResult dataSourcesResult) {
                        Log.i(TAG, "Result: " + dataSourcesResult.getStatus().toString());
                        for (DataSource dataSource : dataSourcesResult.getDataSources()) {

                            Log.i(TAG, "Data source found: " + dataSource.toString());
                            Log.i(TAG, "Data Source type: " + dataSource.getDataType().getName());

                            //Let's register a listener to receive Activity data!
                            if (dataSource.getDataType().equals(DataType.TYPE_LOCATION_SAMPLE)) {
                                Log.i(TAG, "Data source for LOCATION_SAMPLE found!  Registering.");
                                registerFitnessDataListener(
                                        dataSource,
                                        DataType.TYPE_LOCATION_SAMPLE,
                                        mLocationDataPointListener
                                );
                            }

                            if (dataSource.getDataType().equals(DataType.TYPE_STEP_COUNT_CUMULATIVE)) {
                                Log.i(TAG, "Data source for STEP_COUNT_CUMULATIVE found!  Registering.");
                                registerFitnessDataListener(
                                        dataSource,
                                        DataType.TYPE_STEP_COUNT_CUMULATIVE,
                                        mStepsDataPointListener);
                            }

                        }
                    }
                });
    }

    private void registerFitnessDataListener(DataSource dataSource, final DataType dataType, OnDataPointListener listener) {
        Fitness.SensorsApi.add(
                mGoogleApiFitnessClient,
                new SensorRequest.Builder()
                        .setDataSource(dataSource)
                        .setDataType(dataType)
                        .setSamplingRate(1, TimeUnit.SECONDS)
                        .build(),
                listener)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "Lisener for " + dataType + "registered!");
                        } else {
                            Log.i(TAG, "Listener for " + dataType + "not registered.");
                        }
                    }
                });
    }

    private void unregisterFitnessDataListener(final String listenerName, OnDataPointListener listener) {
        if (listener == null) {
            return;
        }

        Fitness.SensorsApi.remove(
                mGoogleApiFitnessClient,
                listener)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "Listener " + listenerName + " was removed!");
                        } else {
                            Log.i(TAG, "Listener " + listenerName + " was not removed.");
                        }
                    }
                });
    }
}