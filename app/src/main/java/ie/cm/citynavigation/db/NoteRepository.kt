package ie.cm.citynavigation.db

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import ie.cm.citynavigation.dao.NoteDao
import ie.cm.citynavigation.entities.Note

class NoteRepository(private val noteDao: NoteDao) {
    val allNotes: LiveData<List<Note>> = noteDao.getNotes()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(note: Note) {
        noteDao.insert(note)
    }

    suspend fun updateById(noteTitle: String, noteText: String, noteId: String) {
        noteDao.updateById(noteTitle, noteText, noteId)
    }

    suspend fun deleteById(noteId: String) {
        noteDao.deleteById(noteId)
    }
}