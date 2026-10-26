package com.carlauncher.dash

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue

class MainActivity : ComponentActivity() {

    private lateinit var music: MusicController
    private lateinit var status: StatusMonitor
    private lateinit var favs: FavStore

    private var refreshKey by mutableIntStateOf(0)
    private var homeTick by mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        music = MusicController(applicationContext)
        status = StatusMonitor(applicationContext)
        favs = FavStore(applicationContext)
        setContent {
            LauncherTheme {
                LauncherScreen(music, status, favs, refreshKey, homeTick)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        music.start()
        status.start()
        refreshKey++ // โหลดรายการแอปใหม่ (กรณีติดตั้ง/ถอนแอป)
    }

    override fun onPause() {
        music.stop()
        status.stop()
        super.onPause()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        homeTick++ // กดปุ่ม Home ซ้ำ = กลับหน้าหลัก
    }
}
