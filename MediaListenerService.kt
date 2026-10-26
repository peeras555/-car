package com.carlauncher.dash

import android.service.notification.NotificationListenerService

/**
 * ไม่ได้ทำงานอะไรเอง ใช้เป็นตัวระบุสิทธิ์ "Notification access"
 * ซึ่ง Android กำหนดว่าต้องมี จึงจะอ่าน MediaSession (เพลงที่กำลังเล่น) ได้
 */
class MediaListenerService : NotificationListenerService()
