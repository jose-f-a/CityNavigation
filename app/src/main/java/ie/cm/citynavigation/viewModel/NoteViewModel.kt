package ie.cm.citynavigation.viewModel

import android.app.Application
import androidx.lifecycle.*
import ie.cm.citynavigation.db.NoteDB
import ie.cm.citynavigation.db.NoteRepository
import ie.cm.citynavigation.entities.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {
  private val repository: NoteRepository
  val allNotes: LiveData<List<Note>>

  init {
    val notesDao = NoteDB.getDatabase(application, viewModelScope).noteDao()
    repository = NoteRepository(notesDao)
    allNotes = repository.allNotes
  }

  fun insert(note: Note) = viewModelScope.launch(Dispatchers.IO) {
    repository.insert(note)
  }

  fun updateById(noteTitle: String, noteText: String, noteId: String) = viewModelScope.launch {
    repository.updateById(noteTitle, noteText, noteId)
  }

  fun deleteById(noteId: String) = viewModelScope.launch {
    repository.deleteById(noteId)
  }
}