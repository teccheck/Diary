package io.github.teccheck.diary

import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

abstract class DiaryBaseActivity : AppCompatActivity() {
    protected lateinit var diaryStorage: DiaryStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            navigationBarStyle = SystemBarStyle.auto(
                getColor(R.color.nav_bar_color_light),
                getColor(R.color.nav_bar_color_dark)
            )
        )
        super.onCreate(savedInstanceState)
        diaryStorage = (application as DiaryApp).getDiaryStorage()
    }
}