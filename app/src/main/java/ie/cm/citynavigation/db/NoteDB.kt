package ie.cm.citynavigation.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import ie.cm.citynavigation.dao.NoteDao
import ie.cm.citynavigation.entities.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = arrayOf(Note::class), version = 1, exportSchema = false)

public abstract class NoteDB : RoomDatabase() {
  abstract fun noteDao(): NoteDao

  private class NoteDatabaseCallback(private val scope: CoroutineScope) :
    RoomDatabase.Callback() {
    override fun onOpen(db: SupportSQLiteDatabase) {
      super.onOpen(db)
      INSTANCE?.let { database ->
        scope.launch {
          var noteDao = database.noteDao()

          /* Insert all
          var note = Note(1, (R.string.lorem).toString(), (R.string.lorem).toString())
          noteDao.insert(note)
          */
        }
      }
    }
  }

  companion object {
    @Volatile
    private var INSTANCE: NoteDB? = null

    fun getDatabase(context: Context, scope: CoroutineScope): NoteDB {
      val tempInstance = INSTANCE
      if (tempInstance != null) {
        return tempInstance
      }

      synchronized(this) {
        val instance = Room.databaseBuilder(
            context.applicationContext,
            NoteDB::class.java,
            "notes_database",
        )
          .addCallback(NoteDatabaseCallback(scope))
          .build()

        INSTANCE = instance
        return instance
      }
    }
  }
}