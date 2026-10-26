package com.carlauncher.dash

import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationManagerCompat

/** อ่านเพลงที่กำลังเล่นจริงผ่าน MediaSession และสั่ง Play/Pause/Next/Previous */
class MusicController(private val context: Context) {

    private val msm = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
    private val component = ComponentName(context, MediaListenerService::class.java)
    private var controller: MediaController? = null
    private var listening = false

    var hasAccess: Boolean by mutableStateOf(false)
        private set
    var title: String by mutableStateOf("")
        private set
    var artist: String by mutableStateOf("")
        private set
    var artwork: Bitmap? by mutableStateOf(null)
        private set
    var playing: Boolean by mutableStateOf(false)
        private set
    var sourcePackage: String? by mutableStateOf(null)
        private set

    private val callback = object : MediaController.Callback() {
        override fun onMetadataChanged(metadata: MediaMetadata?) {
            applyMetadata(metadata)
        }

        override fun onPlaybackStateChanged(state: PlaybackState?) {
            playing = state?.state == PlaybackState.STATE_PLAYING
        }

        override fun onSessionDestroyed() {
            select(safeSessions())
        }
    }

    private val sessionsListener =
        MediaSessionManager.OnActiveSessionsChangedListener { list -> select(list.orEmpty()) }

    private fun safeSessions(): List<MediaController> = try {
        msm.getActiveSessions(component)
    } catch (e: SecurityException) {
        hasAccess = false
        emptyList()
    }

    private fun select(list: List<MediaController>) {
        controller?.unregisterCallback(callback)
        val pick = list.firstOrNull { it.playbackState?.state == PlaybackState.STATE_PLAYING }
            ?: list.firstOrNull()
        controller = pick
        if (pick == null) {
            title = ""
            artist = ""
            artwork = null
            playing = false
            sourcePackage = null
            return
        }
        pick.registerCallback(callback)
        sourcePackage = pick.packageName
        playing = pick.playbackState?.state == PlaybackState.STATE_PLAYING
        applyMetadata(pick.metadata)
    }

    private fun applyMetadata(m: MediaMetadata?) {
        title = m?.getString(MediaMetadata.METADATA_KEY_TITLE)
            ?: m?.getString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE) ?: ""
        artist = m?.getString(MediaMetadata.METADATA_KEY_ARTIST)
            ?: m?.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST) ?: ""
        artwork = m?.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART)
            ?: m?.getBitmap(MediaMetadata.METADATA_KEY_ART)
            ?: m?.getBitmap(MediaMetadata.METADATA_KEY_DISPLAY_ICON)
    }

    fun start() {
        hasAccess = NotificationManagerCompat.getEnabledListenerPackages(context)
            .contains(context.packageName)
        if (!hasAccess || listening) return
        try {
            msm.addOnActiveSessionsChangedListener(sessionsListener, component)
            listening = true
            select(safeSessions())
        } catch (e: SecurityException) {
            hasAccess = false
        }
    }

    fun stop() {
        if (listening) {
            msm.removeOnActiveSessionsChangedListener(sessionsListener)
            listening = false
        }
        controller?.unregisterCallback(callback)
        controller = null
    }

    fun playPause() {
        val c = controller ?: return
        if (playing) c.transportControls.pause() else c.transportControls.play()
    }

    fun next() {
        controller?.transportControls?.skipToNext()
    }

    fun previous() {
        controller?.transportControls?.skipToPrevious()
    }
}
