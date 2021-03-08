package ie.cm.citynavigation.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note")

class Note (
    @PrimaryKey(autoGenerate = true) val noteId: Int? = null,
    @ColumnInfo(name = "noteTitle") val noteTitle: String?,
    @ColumnInfo(name = "noteText") val noteText: String?
)
