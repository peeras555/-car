package com.carlauncher.dash

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/** เก็บรายชื่อแอปโปรด (เรียงตามลำดับที่ปัก) ใน SharedPreferences */
class FavStore(ctx: Context) {
    private val sp = ctx.getSharedPreferences("favorites", Context.MODE_PRIVATE)

    var list: List<String> by mutableStateOf(
        (sp.getString("list", "") ?: "").split(",").filter { it.isNotBlank() }
    )
        private set

    fun toggle(pkg: String) {
        list = if (pkg in list) list - pkg else list + pkg
        sp.edit().putString("list", list.joinToString(",")).apply()
    }
}
