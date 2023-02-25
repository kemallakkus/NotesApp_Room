package com.kemalakkus.notes

import android.content.Context
import android.content.SharedPreferences

class SharedPref(context:Context) {

    internal var sharedPreferences: SharedPreferences

    init {
        sharedPreferences=context.getSharedPreferences("fillname", Context.MODE_PRIVATE)

    }

    fun setNightModeState(state:Boolean?){
        val editor=sharedPreferences.edit()
        editor.putBoolean("Night Mode",state!!)
        editor.apply()

    }

    fun setLayoutModeState(state:Boolean?){
        val editor=sharedPreferences.edit()
        editor.putBoolean("layout",state!!)
        editor.apply()

    }
    fun loadNightModeState():Boolean?{
        return sharedPreferences.getBoolean("Night Mode",false)
    }

    fun loadLayoutModeState():Boolean?{
        return sharedPreferences.getBoolean("layout",false)
    }


}