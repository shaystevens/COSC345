package com.example.alarmapp

import android.app.*
import android.app.PendingIntent.FLAG_MUTABLE
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.alarmapp.databinding.ActivityMainBinding
import android.icu.util.Calendar
import android.icu.util.Calendar.*
import androidx.annotation.RequiresApi

/**
 * Main class, used for our notification channel, allowing
 * the OS to recognise when an alarm pops, and give the
 * user a notification, as well as acting as the main base
 * for all of our controls. Where you add an alarm etc
 *
 * @author Shay Stevens, Dougal Colquhoun, Liam Iggo, Austin Donnelly
 */
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private lateinit var alarmMgr: AlarmManager
    private lateinit var alarmIntent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        createNotificationChannel()
    }

    /**
     * Function to create notification channel
     * which allows alarms to sound
     */
    fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name : CharSequence = "Alarms"
            val description = "Channel for alarms to ring"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("alarmApp", name, importance)
            channel.description = description
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Function to set alarm
     * displays a short message
     * @param hour the hour when alarm should go off
     * @param minute the minute when alarm should go off
     * @param id the private id for alarm (used as request code)
     */
    fun setAlarm(hour : Int, minute: Int, id: Int, name: String){
        val daily = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(HOUR_OF_DAY, hour)
                set(MINUTE, minute)
            }
            alarmMgr = this.getSystemService(ALARM_SERVICE) as AlarmManager
            val intent = Intent(this, myBroadcastReceiver::class.java)
            intent.putExtra("NAME", name)

            val rightNow = Calendar.getInstance()
            // if alarm is set in the past add a day onto it so it rings same time next day
            if(calendar.before(rightNow)){
                calendar.add(DATE, 1)
            }
            alarmIntent = PendingIntent.getBroadcast(this, id, intent, FLAG_MUTABLE)
            if(daily){
                alarmMgr.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    alarmIntent
                )
            }else{
                alarmMgr.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    alarmIntent
                )
            }
            Toast.makeText(this, "Alarm $id set successfully"
                , Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Function to cancel the alarm
     * displays a short message
     * @param id the private id for alarm (used as request code)
     *      -- NEEDS to be the same as was used to set alarm
     */
    fun cancelAlarm(id : Int){
        alarmMgr = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, myBroadcastReceiver::class.java)

        alarmIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(this, id, intent, FLAG_MUTABLE)
        }else{
            PendingIntent.getBroadcast(this, id, intent, 0)
        }

        alarmMgr.cancel(alarmIntent)
        Toast.makeText(this, "Alarm $id deleted", Toast.LENGTH_LONG).show()
    }

    /**
     * Initialize the contents of the Activity's standard options menu.
     * @param menu, The options menu in which the items are placed
     * @return true for the menu to be displayed
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    /**
     * Called whenever an item in the options menu is selected
     * @param item, The menu item that was selected.
     * @return Return false to allow normal menu processing to proceed, true to consume it here.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * This method is called whenever the user chooses to navigate Up
     * within application's activity hierarchy from the action bar.
     */
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}