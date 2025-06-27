# 📝 Offline Notes App

An offline-first notes application built using **Jetpack Compose**, **MVVM**, **Clean Architecture**, **Room**, and **Hilt**.

---

## 🚀 Features

- ➕ Add, 📝 Edit, and 🗑 Delete notes
- 🎨 Choose custom soft background colors for each note
- 🧱 Modern UI built entirely with **Jetpack Compose**
- 🗂 Notes displayed in a responsive **grid layout**
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

## 🧪 Unit Testing

This project uses **JUnit4**, **Mockito**, and **Kotlin Coroutines Test** for ViewModel testing.

### ✅ ViewModel Test Coverage

| Function         | Description                          |
|------------------|--------------------------------------|
| `addNote()`      | Adds a new note and refreshes list   |
| `updateNote()`   | Updates an existing note (via add)   |
| `deleteNote()`   | Deletes a note and reloads notes     |
| `loadNotes()`    | Loads all notes into state           |
| `loadNoteById()` | Loads a single note into `editingNote` |

### 📁 Test File

- Location:
