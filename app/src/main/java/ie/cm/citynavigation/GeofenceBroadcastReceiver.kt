package ie.cm.citynavigation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.droidman.ktoasty.KToasty
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context?, intent: Intent?) {
    val notificationHelper = NotificationHelper(context)
    val geofencingEvent = GeofencingEvent.fromIntent(intent)
    if (geofencingEvent.hasError()) {
      Log.d("****Geofence", "onReceive: Error receiving geofence event...")
      return
    }
    val geofenceList = geofencingEvent.triggeringGeofences
    for (geofence in geofenceList) {
      Log.d("****Geofence", "onReceive: " + geofence.requestId)
    }
    //        Location location = geofencingEvent.getTriggeringLocation();
    when (geofencingEvent.geofenceTransition) {
      Geofence.GEOFENCE_TRANSITION_ENTER -> {
        if (context != null) {
          KToasty.info(context, context.getString(R.string.geofenceEnter), Toast.LENGTH_SHORT).show()

          notificationHelper.sendHighPriorityNotification(
            context.getString(R.string.geofence), context.getString(R.string.geofenceEnter),
            Map::class.java
          )
        }
      }
      Geofence.GEOFENCE_TRANSITION_DWELL -> {
        if (context != null) {
          KToasty.info(context, context.getString(R.string.geofenceDwell), Toast.LENGTH_SHORT).show()

          notificationHelper.sendHighPriorityNotification(
            context.getString(R.string.geofence), context.getString(R.string.geofenceDwell),
            Map::class.java
          )
        }
      }
      Geofence.GEOFENCE_TRANSITION_EXIT -> {
        if (context != null) {
          KToasty.info(context, context.getString(R.string.geofenceExit), Toast.LENGTH_SHORT).show()

          notificationHelper.sendHighPriorityNotification(
            context.getString(R.string.geofence), context.getString(R.string.geofenceExit),
            Map::class.java
          )
        }
      }
    }
  }
}