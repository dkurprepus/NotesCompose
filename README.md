# 📝 Offline Notes App

An offline-first notes application built using **Jetpack Compose**, **MVVM**, **Clean Architecture**, **Room**, and **Hilt**.

---

## 🚀 Features

- ➕ Add, 📝 Edit, and 🗑 Delete notes
- 🎨 Choose custom soft background colors for each note
- 🧱 Modern UI built entirely with **Jetpack Compose**
- 🗂 Notes displayed in a responsive **grid layout**
- 🔄 **Undo support** via Snackbar when deleting notes
- ❗ **Confirmation dialog** before deleting notes
- 📄 **Edit screen** with pre-filled content and delete option
- ✅ Offline support using Room database
- 🧠 Architecture: MVVM + Clean + Hilt

---

## 🧱 Architecture

The app follows **Clean Architecture**:

UI (Compose)
↓
ViewModel (MVVM)
↓
Use Cases (Domain Layer)
↓
Repository Interface
↓
Repository Implementation (Room DB)


---

## 🛠️ Tech Stack

- **Kotlin**
- **Jetpack Compose** (Material 3)
- **MVVM + Clean Architecture**
- **Room** (local DB)
- **Hilt** (DI)
- **Navigation Compose**
- **State Management** with `remember`, `mutableStateOf`, and `viewModel`

---
## 🗓 Upcoming Enhancements

- 📭 Show **empty state screen** if there are no notes
- 🔍 Add **search bar** to filter notes by title/content
- 🔐 Optional **biometric/PIN lock**
- ☁️ Firebase sync for backup (optional)
- 🧪 Add unit/UI tests

Let me know when you're ready to push — or want to make it a multi-module project later 🔥
