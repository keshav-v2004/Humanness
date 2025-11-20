# Sample Tasks KMM + Compose Multiplatform Prototype

This repository contains a Kotlin Multiplatform (KMM) + Compose (Android) prototype that guides users through sample data collection tasks: Text Reading, Image Description, and Photo Capture. It includes a simulated Noise Test, and a local Task History to review completed tasks.

> Note: The Android app is implemented with Compose UI. The shared KMM module hosts data models/utilities. iOS stubs exist but are not fully implemented in this prototype.

---

## Features

- Start Screen
  - Heading: "Let’s start with a Sample Task for practice."
  - Sub-text: "Pehele hum ek sample task karte hain."
  - Button navigates to Noise Test.

- Noise Test (simulated)
  - Displays an animated decibel level (0–60 dB simulated).
  - If average < 40 dB → "Good to proceed" and auto-navigate.
  - If ≥ 40 dB → "Please move to a quieter place".

- Task Selection
  - Text Reading Task
  - Image Description Task
  - Photo Capture Task

- Text Reading Task
  - Fetches a passage from `https://dummyjson.com/products` (product description).
  - Press-and-hold microphone to record; stops on release.
  - Validates duration (min 10s, max 20s) with inline errors.
  - Checkboxes required before submission:
    - No background noise
    - No mistakes while reading
    - Beech me koi galti nahi hai
  - On submit, stores a `TaskItem` with audio path, duration, timestamp.

- Image Description Task
  - Shows an image from the DummyJSON CDN.
  - Press-and-hold microphone to record; validates 10–20s length.
  - On submit, stores `task_type = image_description`, image URL, audio path, duration, timestamp.

- Photo Capture Task
  - Requests camera permission, opens system camera, shows a preview.
  - Text field for a written description.
  - Optional press-and-hold recording (10–20s validation).
  - Retake Photo button.
  - On submit, stores `task_type = photo_capture`, image file path, optional audio path, duration, timestamp.

- Task History
  - Header shows total tasks and total recorded duration.
  - List of tasks with ID, type, duration, timestamp, and preview (text snippet or image thumbnail).

- Permission UX (Accompanist)
  - Clear, card-based UI for requesting microphone/camera permissions.
  - Permanently denied state offers "Open App Settings" fallback.
 
<p float="left">
  <img alt="image" src="https://github.com/user-attachments/assets/7d9fc22f-95a0-4277-81f4-760ce1f6f062" width="20%"/>
  <img width="20%" alt="image" src="https://github.com/user-attachments/assets/9cb8790d-79a7-4e08-b376-e9773a71b653" />
  <img width="20%" alt="image" src="https://github.com/user-attachments/assets/d318a4ab-03ee-4ed3-a960-792e0d6ef81e" />
  <img width="20%" alt="image" src="https://github.com/user-attachments/assets/594e2d17-1613-4cf9-b309-b360bdc428df" />
  <img width="20%" alt="image" src="https://github.com/user-attachments/assets/e44bb6d2-68f1-4b97-a409-8d53a8c52f62" />
  <img width="20%" alt="image" src="https://github.com/user-attachments/assets/a43bd2cb-aeaa-431d-86bd-3b55487d5c9f" />
  <img width="20%" alt="image" src="https://github.com/user-attachments/assets/792de729-4e15-48b3-bd72-272efb537c71" />
  <img width="20%" alt="image" src="https://github.com/user-attachments/assets/cbdfb41e-7f79-4939-a60b-2015daea54cf" />
  <img width="20%" alt="image" src="https://github.com/user-attachments/assets/a7633a85-48d4-4a24-9c2d-c82c6621a809" />


</p>

---

## Architecture

- Modules
  - `androidApp/`: Compose Android application, navigation, screens, permissions, storage adapter.
  - `shared/`: KMM module with `TaskItem` model and utilities.
  - `iosApp/`: Xcode project scaffold (not implemented in this prototype).

- Navigation
  - `AppNavHost` wires routes:
    - `Start → NoiseTest → TaskSelection → (TextReading | ImageDescription | PhotoCapture) → TaskHistory`

- Data Model
  - `shared/src/commonMain/kotlin/model/TaskItem.kt`
  - Kotlinx Serialization annotated and persisted as JSON list.

- Persistence
  - `TaskStorageAndroid` uses `SharedPreferences` to store and read a JSON list of tasks.

- Recording
  - `AudioRecorder` wraps `MediaRecorder` with safe start/stop handling to avoid native `stop failed` crashes on short recordings.

- Image Loading
  - Coil’s `SubcomposeAsyncImage` with crossfade, hardware decoding disabled, and a manual bitmap fallback if decoding fails.

---

## Data Saved (Examples)

- Text Reading Task
```json
{
  "task_type": "text_reading",
  "text": "Mega long lasting fragrance...",
  "audio_path": "/data/user/0/com.example.josh.android/files/read_1731300000000.m4a",
  "duration_sec": 15,
  "timestamp": "2025-11-12T10:00:00Z"
}
```

- Image Description Task
```json
{
  "task_type": "image_description",
  "image_url": "https://cdn.dummyjson.com/product-images/14/2.jpg",
  "audio_path": "/data/user/0/com.example.josh.android/files/img_desc_1731300600000.m4a",
  "duration_sec": 12,
  "timestamp": "2025-11-12T10:10:00Z"
}
```

- Photo Capture Task
```json
{
  "task_type": "photo_capture",
  "image_path": "/data/user/0/com.example.josh.android/files/photo_1731300900000.jpg",
  "audio_path": "/data/user/0/com.example.josh.android/files/photo_desc_1731300900000.m4a",
  "duration_sec": 18,
  "timestamp": "2025-11-12T10:15:00Z"
}
```

> Actual fields persisted match `TaskItem` (id, taskType, text?, imageUrl?, imagePath?, audioPath?, durationSec, timestamp).

---

## Permissions

- Required
  - `RECORD_AUDIO` for all recording and noise testing.
  - `CAMERA` for photo capture.
  - `INTERNET` for fetching images/passage.

- UX Behavior
  - If temporarily denied → shows rationale and a Grant button.
  - If permanently denied → shows settings card with an "Open App Settings" button.

---

## Build & Run (Android)

Using Android Studio (recommended):
- Open the project (`josh/`).
- Sync Gradle.
- Run the `androidApp` configuration on a device/emulator (API 24+).

Using command line (Windows PowerShell):

```powershell
# From repository root
./gradlew.bat :androidApp:assembleDebug
./gradlew.bat :androidApp:installDebug
```

Then launch the app on the device.

---

## Troubleshooting

- Image not showing
  - Ensure network connectivity and `INTERNET` permission (declared).
  - The app tries Coil first; on decode error, a manual bitmap fallback is attempted.
  - If still failing, try another sample URL/CDN or check Logcat for `ImageLoad`/exception messages.

- Recorder crash / stop failed
  - This is guarded in `AudioRecorder`; very short taps may still produce empty/short files. Press-and-hold ≥ 10s for valid submissions.

- Permissions denied permanently
  - Use the built-in Settings button to open App Settings and enable Camera/Microphone.

- Clearing tasks
  - Clear app storage or uninstall the app to reset the stored task list.

---

## Known Limitations / Next Steps

- AudioPlayer UI is a visual placeholder; add actual playback controls using `MediaPlayer` or ExoPlayer.
- Noise Test uses a simulated dB value (microphone sampling could be added later).
- iOS UI is not implemented; only the shared module can be reused.
- Robust error states/analytics/logging can be expanded as needed.

---

## Key Files

- Android UI
  - `androidApp/src/main/java/com/example/josh/android/navigation/AppNavHost.kt`
  - `androidApp/src/main/java/com/example/josh/android/screens/*.kt`
  - `androidApp/src/main/java/com/example/josh/android/ui/components/CommonComponents.kt`
  - `androidApp/src/main/java/com/example/josh/android/permissions/Permissions.kt`
- Recording & Camera
  - `androidApp/src/main/java/com/example/josh/android/recorder/AudioRecorder.kt`
- Storage & Model
  - `androidApp/src/main/java/com/example/josh/android/storage/TaskStorageAndroid.kt`
  - `shared/src/commonMain/kotlin/model/TaskItem.kt`

---

## Libraries

- Compose BOM & Material 3
- Navigation Compose
- Coil (image loading)
- Accompanist Permissions
- Kotlinx Serialization (JSON)

---

## License

This prototype is for demonstration purposes. No license is attached; adapt as needed for your project.
