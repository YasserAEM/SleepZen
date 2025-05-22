# SleepZen

**SleepZen** is an Android application (Java, MVVM) that helps users monitor and improve their sleep quality through intelligent cycle detection, smart alarms, relaxation exercises and habit tracking.

---

## Table of Contents

- [Features](#features)  
- [Architecture](#architecture)  
- [Prerequisites](#prerequisites)  
- [Project Structure](#project-structure)  
- [Installation](#installation)  
- [Usage](#usage)   

---

## Features

- **Sleep Tracking**  
  - Detects light/deep phases and awakenings via device sensors  
  - Records ambient audio to spot snoring or disturbances  

- **Smart Alarm**  
  - Wakes you during a light-sleep window around your target time  
  - Full-screen Stop/Snooze UI that turns on the screen and dismisses keyguard  

- **Relax & Meditate**  
  - Built-in library of guided meditations and soothing sounds  
  - Play/pause controller persistent across fragments  

- **Habit Tracker**  
  - Preloaded habits (“Drink water”, “No caffeine”) + custom habits  
  - Check/uncheck state resets every 8 hours automatically  

- **History Calendar**  
  - Color-coded calendar view of sleep quality per day  
  - Clickable days show session duration & quality details  

- **Dark Mode Scheduler**  
  - Set a time to automatically enable Do-Not-Disturb and dark theme  

---

## Architecture

- **Pattern**: MVVM (Model-View-ViewModel)  
- **Data Layer**: Room (local database) + WorkManager for periodic tasks  
- **Scheduling**: AlarmManager with exact alarms & full-screen notifications  
- **Audio**: MediaRecorder (night recording) + MediaPlayer (relax sounds & alarm)  
- **UI**: Fragments + Material Components + BottomNavigationView  

---

## Prerequisites

- Android Studio 2022.3 or later  
- Android SDK Platform 31+  
- Gradle 7.4+  
- Java 8 or higher  
- Device/emulator API level ≥ 26  

---

## Project Structure

app/
├── src/main/
│ ├── java/com/drwich/sleepzen/
│ │ ├── data/ # Room database, DAOs, repositories
│ │ ├── model/ # Entity & domain classes
│ │ ├── ui/
│ │ │ ├── sleep/ # SleepFragment, ViewModel, scheduler, AlarmActivity
│ │ │ ├── relax/ # RelaxContainer, Meditate & Sounds fragments & adapters
│ │ │ ├── habits/ # HabitsFragment & ViewModel
│ │ │ └── history/ # HistoryFragment, ViewModel & decorators
│ │ └── MainActivity.java
│ └── res/
│ ├── layout/ # XML layouts
│ ├── drawable/ # Icons & vector assets
│ ├── mipmap/ # Launcher icons
│ ├── values/ # themes, colors, strings
│ └── raw/ # alarm_sound.mp3
└── build.gradle.kts

## Installation

Simply download the .apk file and run it.

## Usage

### 📱 Sleep Tab
- Pick your target **wake-up time**.
- Tap **Start Sleep** (grant `RECORD_AUDIO` permission).
- The app records your sleep and schedules *smart wake* alarms using phase checks.

### 🧘 Relax Tab
- Toggle between **Meditate** and **Sounds** modes.
- Tap any item to **start/stop** playback.

### ✅ Habits Tab
- Check off **built-in habits** or add your own.
- Habit states **reset every 8 hours** automatically.

### 📅 History Tab
- View a **color-coded calendar** of your sleep quality.
- Tap any day to see **duration & quality** details.

### 🌙 Dark Mode Scheduler
- Set a time to **automatically enable Do-Not-Disturb** and **dark theme**.
