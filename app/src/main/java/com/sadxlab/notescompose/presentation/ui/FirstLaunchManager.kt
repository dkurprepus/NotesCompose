package com.sadxlab.notescompose.presentation.ui

import android.content.Context

object FirstLaunchManager {
    private const val PREF_NAME = "first_launch_pref"
    private const val KEY_FIRST_LAUNCH_HOME = "is_first_time_launch"

    fun isFirstLaunch(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return !prefs.getBoolean(KEY_FIRST_LAUNCH_HOME, false)

    }

    fun setFirstLaunchDone(context: Context) {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        pref.edit().putBoolean(KEY_FIRST_LAUNCH_HOME, true).apply()
    }
}