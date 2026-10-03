package com.example.vocabulario

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vista = TextView(this)
        vista.text = "Hola mundo"
        vista.textSize = 24f
        vista.setPadding(48, 96, 48, 48)
        setContentView(vista)
    }
}
