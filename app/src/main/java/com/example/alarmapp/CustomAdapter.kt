package com.example.alarmapp

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.RecyclerView
import kotlin.properties.Delegates

/**
 * Custom adapter for the alarm database. The adapter acts as a bridge between the
 * database and UI. It converts data from the database to a UI component.
 *
 * @author Shay Stevens, Dougal Colquhoun, Liam Iggo, Austin Donnelly
 */
class CustomAdapter(
    private var context: Context?,
    private var alarm_ids: ArrayList<Int>,
    private var alarm_names: ArrayList<String>,
    private var alarm_hours: ArrayList<Int>,
    private var alarm_minutes: ArrayList<Int>
) : RecyclerView.Adapter<CustomAdapter.MyViewHolder>() {

    private lateinit var parent: ViewGroup

    /**
    * Creates a new view holder when there is no existing view holders
    * @param parent, The recyclerView group
    * @param viewType, Set of views
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.my_row, parent, false)
        this.parent = parent
        return MyViewHolder(view)
    }

    /**
    * Sets the correct time of the alarm.
    * Uses 24 hour format in order to do so.
    * @param holder, item view and metadata about its place within the RecyclerView
    * @param position, The position of the item within the adapter's data set.
     */
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val timeString : String
        val isSystem24Hour = DateFormat.is24HourFormat(context)
        val hour = alarm_hours[position]
        val minute = alarm_minutes[position]
        val minuteString = if(minute < 10) "0$minute" else "$minute"

        if(!isSystem24Hour){
            val meridiemIndicator = if(hour > 11) "PM" else "AM"
            val hourString = if(hour == 0) "12" else if(hour > 12) hour - 12 else "$hour"
            timeString = "$hourString:$minuteString $meridiemIndicator"
        }else{
            val hourString = if(hour < 10) "0$hour" else "$hour"
            timeString = "$hourString:$minuteString"
        }

        if(alarm_names[position] != ""){
            holder.alarmTime.text = alarm_names[position]
        }else{
            holder.alarmTime.text = timeString
        }

        var switch_state = holder.switch.isEnabled
        holder.switch.setOnClickListener {
            switch_state = switch_state != true
        }

        holder.itemView.setOnLongClickListener{
            val alarmName = alarm_names.get(position)
            val alarm_hour = alarm_hours.get(position)
            val alarm_min = alarm_minutes.get(position)
            val dialog = EditDialog(alarmName, alarm_hour, alarm_min, timeString, !switch_state)

            parent.findFragment<FirstFragment>()
                .fragmentManager?.let { it1 -> dialog.show(it1, "test") }
            true
        }
    }

    /**
    * Gets the count of the items in the database
    * @return alarm names.size, the amount of alarms in a users app.
     */
    override fun getItemCount(): Int {
        return alarm_names.size
    }

    /**
     * MyViewHolder is a custom class which describes an item view and metadata
     * about its place within the RecyclerView
     */
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val alarmName: TextView = itemView.findViewById(R.id.alarm_name)
        val alarmTime: TextView = itemView.findViewById(R.id.alarm_time)
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        val switch: Switch = itemView.findViewById(R.id.alarm_switch)
    }

}