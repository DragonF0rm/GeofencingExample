package com.example.geofencingexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    protected final String TAG = "GeofenceActivity";
    private GeofencingClient geofencingClient;
    private ArrayList<Geofence> geofenceList = new ArrayList<>();
    private PendingIntent geofencePendingIntent;

    private EditText keyView;
    private EditText latitudeView;
    private EditText longitudeView;
    private EditText radiusView;
    private EditText ttlView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        geofencingClient = LocationServices.getGeofencingClient(this);

        keyView = findViewById(R.id.key);
        latitudeView = findViewById(R.id.latitude);
        longitudeView = findViewById(R.id.longitude);
        radiusView = findViewById(R.id.radius);
        ttlView = findViewById(R.id.ttl);

        Button addBtn = findViewById(R.id.addBtn);
        Button startBtn = findViewById(R.id.startBtn);
        Button stopBtn = findViewById(R.id.stopBtn);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = keyView.getText().toString();
                double latitude = Double.parseDouble(latitudeView.getText().toString());
                double longitude = Double.parseDouble(longitudeView.getText().toString());
                float radius = Float.parseFloat(radiusView.getText().toString());
                long ttl = Long.parseLong(ttlView.getText().toString());

                add(key, latitude, longitude, radius, ttl);
            }
        });

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMonitoring();
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopMonitoring();
            }
        });
    }

    private void startMonitoring() {
        //Telling Android to generate transitions for geofences stored in geofence list
        final GeofencingRequest request = getGeofencingRequest();
        geofencingClient.addGeofences(request, getGeofencePendingIntent())
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        String msg = String.format(
                                Locale.getDefault(),
                                "Sucssessfully started monitoring of %d geofences",
                                request.getGeofences().size());
                        Toast.makeText(
                                getApplicationContext(),
                                msg,
                                Toast.LENGTH_SHORT).show();
                        Log.i(TAG, msg);
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String msg = String.format(
                                Locale.getDefault(),
                                "Start monitoring failed with exception: %s",
                                e.toString());
                        Toast.makeText(
                                getApplicationContext(),
                                msg,
                                Toast.LENGTH_SHORT).show();
                        Log.e(TAG, msg);
                    }
                });
    }

    private void stopMonitoring() {
        geofencingClient.removeGeofences(getGeofencePendingIntent())
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        String msg = "Sucssessfully stopped monitoring of geofences";
                        Toast.makeText(
                                getApplicationContext(),
                                msg,
                                Toast.LENGTH_SHORT).show();
                        Log.i(TAG, msg);
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String msg = String.format(
                                Locale.getDefault(),
                                "Stop monitoring failed with exception: %s",
                                e.toString());
                        Toast.makeText(
                                getApplicationContext(),
                                msg,
                                Toast.LENGTH_SHORT).show();
                        Log.e(TAG, msg);
                    }
                });
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling startMonitoring() and stopMonitoring().
        geofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        return builder.build();
    }

    private void add(String key, double latitude, double longitude, float radius, long ttl) {
        geofenceList.add(new Geofence.Builder()
                .setRequestId(key)
                .setCircularRegion(
                        latitude,
                        longitude,
                        radius //in meters
                )
                .setExpirationDuration(ttl) //in ms or -1 for not expiring geofence
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());
        String msg = "Added geofences with key " + key;
        Toast.makeText(
                getApplicationContext(),
                msg,
                Toast.LENGTH_SHORT).show();
        Log.i(TAG, msg);
    }
}
