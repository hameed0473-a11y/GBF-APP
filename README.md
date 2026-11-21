# GBF — Grooming, Beauty & Fitness

An offline personal routine tracker for Android. It has **Grooming**, **Beauty**, **Fitness** and **Outfits** tabs, completion buttons, next-due dates, progress and local reminders. No account or internet permission is needed.

## Build on Windows 11

The project includes a Gradle wrapper (Gradle 8.13, Android Gradle Plugin 8.7.3, compile/target SDK 35, min SDK 26 / Android 8.0).

- **Android Studio:** open this folder (the one containing `settings.gradle`), let it sync, then use **Build > Build APK(s)**.
- **Command line:** with `JAVA_HOME` pointing at Android Studio's bundled JDK:

  ```powershell
  $env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
  .\gradlew.bat assembleDebug
  ```

The debug APK is written to `app/build/outputs/apk/debug/app-debug.apk`. `local.properties` (SDK path) is created by Android Studio and is not committed. A GitHub Actions workflow also builds the APK after each push to `main`.

Install on a phone: copy the APK across and open it, allowing installation from that source. Grant notification permission when the app first opens.

## Routine

You can add and delete tasks in every category. Use **+ Add … task** at the bottom of Grooming or Beauty, or **+ Add exercise** / **+ Add diet item** in Fitness. Each new task has a name, optional details, a repeat interval (1–365 days, with Daily / 7 / 15 / 30 shortcuts) and a reminder time. **Delete** on a task card removes the task, its reminder and its completion history after you confirm. Tasks in each section are listed in reminder-time order.

The app starts with this default routine, and every item in it can be deleted:

| Section | Task | Repeats | Reminder |
|---|---|---|---|
| Grooming | Haircut | every 30 days | 10:00 |
| Grooming | Trim nails | every 7 days | 18:00 |
| Beauty | De-tan / skin care | every 7 days | 18:00 |
| Fitness · Diet | Honey + kalonji (empty stomach) | daily | 06:30 |
| Fitness · Diet | 10 soaked almonds (badam) | daily | 07:30 |
| Fitness · Diet | Oats for breakfast | daily | 08:30 |
| Fitness · Diet | 1 egg | daily | 13:00 |
| Fitness · Diet | Protein serving (dal, chana, rajma, soya, paneer, curd, sprouts) | daily | 13:30 |
| Fitness · Diet | 2 fruits (guava, orange, apple, banana) | daily | 16:00 |
| Fitness · Diet | 2 dates | daily | 16:30 |
| Fitness · Exercise | 3,000+ steps **or** 10 min treadmill/cycling | daily | 18:30 |

- A task is due until you first mark it. Daily tasks reset the next calendar day. 7- and 30-day tasks show their next due date and become due again after that many days. Marking a task again restarts its interval.
- A reminder fires at its time only while the task is due. Android may delay these alarms to save battery. Reminders are rescheduled after a restart or a time/time-zone change.
- The Fitness tab has an expandable **Best affordable protein for hair growth** guide: eggs, soya chunks, dal, chana/rajma, sprouts, peanuts, curd/paneer, fish/chicken; seeds, greens and vitamin-C fruit as helpers.
- Outfits: add shirts, trousers, T-shirts and night pants with their colours, and the app suggests pairs using simple colour rules.
- All data stays on this device. Clearing app data deletes it.

## Limitations and health notes

This version does not count steps automatically, edit an existing task (delete it and add it again instead), restore the default tasks after deleting them, export data or sync between devices. Tap the exercise task yourself after walking, cycling or using a treadmill.

Honey is a source of added sugar, so keep it to about a teaspoon. Kalonji (nigella / black seed) is a traditional remedy, and a small pinch is enough. Neither is a proven hair-growth treatment. De-tan products can irritate skin. If hair loss is heavy or persistent, see a clinician, and take iron, zinc, biotin or vitamin D supplements only when a test shows a deficiency. Adjust food items for allergies and medical advice.

Sources: [American Academy of Dermatology — hair loss diagnosis](https://www.aad.org/public/diseases/hair-loss/treatment/diagnosis-treat), [NHS — balanced diet](https://www.nhs.uk/live-well/eat-well/how-to-eat-a-balanced-diet/eating-a-balanced-diet/).
