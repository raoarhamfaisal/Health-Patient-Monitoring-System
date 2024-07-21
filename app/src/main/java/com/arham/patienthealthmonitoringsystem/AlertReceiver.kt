package com.arham.patienthealthmonitoringsystem
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager

//class AlertReceiver : BroadcastReceiver() {
//    override fun onReceive(context: Context, intent: Intent) {
//        Toast.makeText(context, "Your vaccination is due tomorrow!", Toast.LENGTH_LONG).show()
//    }
//}
class AlertReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val localIntent = Intent("UPDATE_NOTIFICATION")
        localIntent.putExtra("MESSAGE", "Your next vaccination is due tomorrow!")
        LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent)
    }
}
