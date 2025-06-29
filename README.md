# 📝 Only Notes – Fast, Clean & Offline Note Taking App

Only Notes is a **modern Jetpack Compose Android app** for creating and managing notes, completely offline. Designed with minimal UI and smooth performance, it follows **MVVM + Clean Architecture** and uses **Room** for local persistence.


---

## 🚀 Features

- ➕ Add, 📝 Edit, and 🗑 Delete notes easily
- 🎨 Choose from multiple soft background colors
- 📱 Fully responsive grid layout with Compose
- 🧠 Offline-first using **Room database**
- ❗ Confirmation dialog before delete
- ✏️ Edit screen with pre-filled data and delete button
- ⚙️ Built on **MVVM + Clean Architecture**
- 🔒 No internet or account required — **100% private**
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


### 📁 Test Location

- Location: `com.sadxlab.notescompose.presentation.viewmodel.NoteViewModelTest`

### 🔧 Tools Used

- `kotlinx.coroutines.test` – Coroutine testing utilities
- `Mockito / Mockito-Kotlin` – For mocking repository behavior
- `JUnit4` – Test framework

### 🧪 Sample Test Case

```kotlin
@Test
fun `update Note should call addNote and load updated notes`() = runTest {
    val updatedNote = Note(id = 1, title = "Updated", content = "Updated content")

    whenever(repository.addNote(updatedNote)).thenReturn(Unit)
    whenever(repository.getNotes()).thenReturn(flowOf(listOf(updatedNote)))

    viewModel.updateNote(updatedNote)
    advanceUntilIdle()

    verify(repository).addNote(updatedNote)
    val result = viewModel.notes.first()
    assertEquals(listOf(updatedNote), result)
}
```

### 📸 Screenshots
<p align="center">
  <img src="https://github.com/user-attachments/assets/1b7431b5-8200-4065-a3ea-43b2f024fd45" width="200"/>
  <img src="https://github.com/user-attachments/assets/8a073ac9-3e78-4d73-99ca-80b1fcf55d84" width="200"/>
  <img src="https://github.com/user-attachments/assets/a599a0e1-7354-4f6f-913d-57d681fee10b" width="200"/>
  <img src="https://github.com/user-attachments/assets/e6c6071d-f38a-4924-9f8a-62890b82bc52" width="200"/>
</p>



### 🔐 Privacy
Only Notes does not collect or share any personal data.
Privacy Policy: [Read here](https://sadxproductionlab.blogspot.com/2025/06/only-notes-fast-clean-notes.html)

### 📲 Play Store
Rate & support us on the Google Play Store ⭐

<a href="https://play.google.com/store/apps/details?id=com.sadxlab.notescompose" target="_blank">
  <img src="https://github.com/user-attachments/assets/5f117bc4-8643-49df-87b8-1c5ef0812ae4" alt="Download on Play Store" width="150">
</a>


### 👨‍💻 Author
Developed by [Darshan Khatri](https://github.com/dkurprepus)

