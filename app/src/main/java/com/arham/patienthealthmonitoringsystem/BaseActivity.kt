package com.arham.patienthealthmonitoringsystem

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.localbroadcastmanager.content.LocalBroadcastManager

abstract class BaseActivity : AppCompatActivity() {

    private lateinit var notificationBanner: LinearLayout
    private lateinit var tvNotification: TextView
    private lateinit var btnCloseNotification: ImageView

    abstract val REQUEST_CODE_ADD_HISTORY: Int

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val message = intent?.getStringExtra("MESSAGE")
            message?.let { showNotification(it) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())

        setupToolbar()
        setupNotificationBanner() // Set up the notification banner

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, IntentFilter("UPDATE_NOTIFICATION"))
    }

    abstract fun getLayoutId(): Int

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = getToolbarTitle()
    }

    abstract fun getToolbarTitle(): String

    protected fun setupNotificationBanner() {
        notificationBanner = findViewById(R.id.notificationBanner)
        tvNotification = findViewById(R.id.tvNotification)
        btnCloseNotification = findViewById(R.id.btnCloseNotification)

        btnCloseNotification.setOnClickListener {
            notificationBanner.visibility = View.GONE // Hide the notification banner
        }
    }

    protected fun showNotification(message: String) {
        tvNotification.text = message
        notificationBanner.visibility = View.VISIBLE
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
    }
}


