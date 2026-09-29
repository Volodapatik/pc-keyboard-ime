package com.pckeyboard.ime

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn_enable).setOnClickListener {
            startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
        }

        findViewById<TextView>(R.id.tv_info).text =
            "1. Натисни кнопку нижче\n" +
            "2. Увімкни «PC Keyboard»\n" +
            "3. Вибери її як поточну клавіатуру\n" +
            "4. Готово — тепер є F1–F12 і PC-розкладка"
    }
}
