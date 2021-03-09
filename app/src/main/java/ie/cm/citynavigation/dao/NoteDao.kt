package ie.cm.citynavigation.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ie.cm.citynavigation.entities.Note

@Dao
interface NoteDao {
  @Query("SELECT * FROM note ORDER BY noteId ASC")
  fun getNotes(): LiveData<List<Note>>

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun insert(note: Note)

  @Query("UPDATE note SET noteTitle = :noteTitle, noteText = :noteText where noteId == :noteId")
  suspend fun updateById(noteTitle: String, noteText: String, noteId: String)

  @Query("DELETE FROM note")
  suspend fun deleteAll()

  @Query("DELETE FROM note WHERE noteId == :noteId")
  suspend fun deleteById(noteId: String)
}