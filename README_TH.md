# Car Launcher (Android 10+) — คู่มือสั้น

## 1) Build เป็น APK ด้วย GitHub (ไม่ต้องลง Android Studio)
1. สมัคร/ล็อกอิน github.com แล้วสร้าง Repository ใหม่ (Private ก็ได้)
2. แตกไฟล์ zip นี้ แล้วอัปโหลดไฟล์ทั้งหมดขึ้น Repo (Add file → Upload files)
   - ถ้าโฟลเดอร์ `.github` ไม่ถูกอัปโหลด (โฟลเดอร์ซ่อน) ให้ Add file → Create new file ตั้งชื่อ
     `.github/workflows/build.yml` แล้วคัดลอกเนื้อหาจาก `GITHUB_WORKFLOW_build.yml.txt` ไปวาง
3. แท็บ **Actions** → เลือก **Build APK** → **Run workflow**
4. รอประมาณ 5–10 นาที เมื่อเสร็จ เข้าไปที่ run นั้น เลื่อนลงล่างสุด ส่วน **Artifacts**
   โหลด `CarLauncher-apk` (ได้ไฟล์ zip มี `app-debug.apk` และ `app-release.apk`)
   - ใช้ `app-release.apk` (เบา/ลื่นกว่า) ถ้าไม่มีหรือมีปัญหาให้ใช้ `app-debug.apk`

## 2) ติดตั้งลงจอรถ
- **ทาง USB/แฟลชไดรฟ์:** คัดลอก APK ลงแฟลชไดรฟ์ → เสียบจอรถ → เปิดด้วยแอปจัดการไฟล์ →
  อนุญาต "ติดตั้งจากแหล่งที่ไม่รู้จัก" ให้แอปนั้น → ติดตั้ง
- **ทาง ADB:** `adb install -r app-release.apk`  (ไม่แตะ/ไม่ปิด Launcher เดิม)

## 3) ให้เพลงขึ้นใน Music Widget
เปิดแอป Car Launcher → กด "เปิดหน้าตั้งค่า" → เปิดสวิตช์ **Car Launcher** ในหน้า Notification access
(ไม่ต้องใช้ API Key) ถ้าจอรถไม่มีหน้านี้ ให้ต่อ ADB แล้วรัน
`adb shell cmd notification allow_listener com.carlauncher.dash/com.carlauncher.dash.MediaListenerService`

## 4) ตั้งเป็น Launcher หลัก
กดปุ่ม Home ของจอรถ → เลือก **Car Launcher** → **เสมอ (Always)**
(บางรุ่นอยู่ที่ Settings → Apps → Default apps → Home app)

## 5) กลับไปใช้ Launcher เดิม
- Settings → Apps → Default apps → Home app → เลือก Launcher เดิม
- หรือ Settings → Apps → Car Launcher → "ล้างค่าเริ่มต้น / Clear defaults" แล้วกด Home เลือกตัวเดิม
- หรือถอนการติดตั้ง Car Launcher ได้เลย (ระบบจะกลับไปใช้ตัวเดิมเอง)
