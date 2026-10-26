package com.carlauncher.dash

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap

data class AppItem(val label: String, val pkg: String, val icon: ImageBitmap)

object AppRepo {

    fun load(ctx: Context): List<AppItem> {
        val pm = ctx.packageManager
        val query = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        return pm.queryIntentActivities(query, 0)
            .filter { it.activityInfo.packageName != ctx.packageName }
            .distinctBy { it.activityInfo.packageName }
            .map { ri ->
                AppItem(
                    label = ri.loadLabel(pm).toString(),
                    pkg = ri.activityInfo.packageName,
                    icon = ri.loadIcon(pm).toBitmap(96, 96).asImageBitmap()
                )
            }
            .sortedBy { it.label.lowercase() }
    }

    fun launch(ctx: Context, pkg: String) {
        val intent = ctx.packageManager.getLaunchIntentForPackage(pkg)
        if (intent == null) {
            Toast.makeText(ctx, "เปิดแอปนี้ไม่ได้", Toast.LENGTH_SHORT).show()
            return
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            ctx.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(ctx, "เปิดแอปนี้ไม่ได้", Toast.LENGTH_SHORT).show()
        }
    }

    fun openNavigation(ctx: Context) {
        val intent = ctx.packageManager.getLaunchIntentForPackage("com.google.android.apps.maps")
            ?: Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q="))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            ctx.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(ctx, "ไม่พบแอปนำทางในเครื่อง", Toast.LENGTH_LONG).show()
        }
    }

    fun openListenerSettings(ctx: Context) {
        try {
            ctx.startActivity(
                Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        } catch (e: Exception) {
            Toast.makeText(
                ctx,
                "เครื่องนี้ไม่มีหน้าตั้งค่านี้ ให้ใช้คำสั่ง adb ตามคู่มือ",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
