# Grooming, Beauty & Fitness — Android starter app

An offline personal routine tracker. It has **Grooming**, **Beauty**, **Fitness**, and **Outfits** tabs, completion buttons, last-completed dates, current progress and local reminders. No account or internet permission is required.

## Build on Windows 11

1. Extract the project ZIP. Open its extracted folder (the one containing `settings.gradle`) in Android Studio.
2. Let Android Studio download the Android Gradle plugin and Android SDK platform 35 if prompted. Select JDK 17 if asked. This project does not bundle a Gradle wrapper, so install Gradle 8.9 or use Android Studio's configured Gradle distribution. A GitHub Actions workflow also builds an APK after pushing to `main`.
3. Select **Build > Build APK(s)**. Android Studio places the debug APK at `app/build/outputs/apk/debug/app-debug.apk`.
4. Connect your Android phone with USB debugging enabled and use **Run**, or transfer the APK to the phone and install it after allowing installation from that source.
5. Grant notification permission when the app first opens. Reminders use your phone's local time and are rescheduled after a restart.

## Included routine

- Haircut: every 30 days after marking complete; nail trim and gentle skin-care task: every 7 days.
- Daily: optional honey, oats, ten almonds, one egg, two fruits, two dates, an affordable protein serving, and 3,000 steps **or** ten minutes of treadmill/cycling.
- A task starts due until first marked. A daily task resets on the next calendar day; a 7- or 30-day task becomes due after that many calendar days. Marking it again restarts its interval.
- Reminders run at the preset times shown on each card. Android may delay inexact alarms to save battery. Completion and reminder settings are stored only on this device; deleting app data loses them.
- Outfits: add shirts, trousers, T-shirts and night pants with their colors. The app suggests shirt + trousers and T-shirt + night pants pairs using simple color rules. Tap Remove to delete an item. Clothing stays on this device.

## Current limitations and health notes

This first version does not count phone steps, edit tasks or notification times, export data, sync between devices, analyze clothing photos, or include the unclear spoken item after “honey.” Tap the activity button yourself after walking, cycling or using a treadmill. Building an APK requires Android Studio or the included GitHub Actions workflow; this workspace has neither Android SDK nor Gradle.

Honey is optional and is a source of added/free sugar, not a protein food or a hair-growth treatment. De-tan treatments can irritate skin: the app labels this as gentle skin care. If hair loss is noticeable or persistent, a clinician can assess causes; take iron/zinc/biotin supplements only when a deficiency is established. Adjust food reminders to allergies and medical advice.

Sources: [American Academy of Dermatology — hair loss diagnosis](https://www.aad.org/public/diseases/hair-loss/treatment/diagnosis-treat), [NHS — balanced diet](https://www.nhs.uk/live-well/eat-well/how-to-eat-a-balanced-diet/eating-a-balanced-diet/).
