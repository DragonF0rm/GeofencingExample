package com.example.geofencingexample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;
import java.util.Locale;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    protected final String TAG = "GeofenceReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = getErrorString(geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            Toast.makeText(context,
                    "Got geofence event with error: " + errorMessage,
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            String geofenceTransitionDetails = getGeofenceTransitionDetails(
                    geofenceTransition,
                    triggeringGeofences
            );

            // Send notification and log the transition details.
            Log.i(TAG, geofenceTransitionDetails);
            Toast.makeText(context,
                    "Got geofence event: " + geofenceTransitionDetails,
                    Toast.LENGTH_LONG).show();
        } else {
            // Log the error.
            Log.e(TAG, "Geofence transition has invalid type");
            Toast.makeText(context,
                    "Geofence transition has invalid type",
                    Toast.LENGTH_LONG).show();
        }
    }

    private String getGeofenceTransitionDetails(int geofenceTransition, List<Geofence> geofences) {
        String transitionType;
        switch (geofenceTransition) {
            case Geofence.GEOFENCE_TRANSITION_ENTER: {
                transitionType = "GEOFENCE_TRANSITION_ENTER";
                break;
            }
            case Geofence.GEOFENCE_TRANSITION_DWELL: {
                transitionType = "GEOFENCE_TRANSITION_DWELL";
                break;
            }
            case Geofence.GEOFENCE_TRANSITION_EXIT: {
                transitionType = "GEOFENCE_TRANSITION_EXIT";
                break;
            }
            default: {
                transitionType = String.format(Locale.getDefault(),
                        "Unknown geofence transition <%d>",
                        geofenceTransition);
                break;
            }
        }

        StringBuilder geofencesIDs = new StringBuilder(geofences.get(0).getRequestId());
        for (int i = 1; i < geofences.size(); i++) {
            geofencesIDs.append(", ");
            geofencesIDs.append(geofences.get(i).getRequestId());
        }

        return String.format(Locale.getDefault(), "%s triggered for %s",
                transitionType, geofencesIDs.toString());
    }

    String getErrorString(int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "GEOFENCE_NOT_AVAILABLE";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "GEOFENCE_TOO_MANY_GEOFENCES";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "GEOFENCE_TOO_MANY_PENDING_INTENTS";
            default:
                return "Unknown geofence error";
        }
    }
}
