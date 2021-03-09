package ie.cm.citynavigation.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ie.cm.citynavigation.EditNote
import ie.cm.citynavigation.R
import ie.cm.citynavigation.entities.Note

class NoteCardAdapter internal constructor(
  context: Context
) : RecyclerView.Adapter<NoteCardAdapter.NoteViewHolder>() {
  private val inflater: LayoutInflater = LayoutInflater.from(context)
  private var notes = emptyList<Note>()

  class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val noteIdView: TextView = itemView.findViewById(R.id.noteId)
    val noteTitleView: TextView = itemView.findViewById(R.id.noteTitle)
    val noteTextView: TextView = itemView.findViewById(R.id.noteText)

    init {
      itemView.setOnClickListener { v: View ->
        val i = Intent(v.context, EditNote::class.java).apply {
          //putEXTRA (EXTRA_MESSAGE, message)
          putExtra(noteTitle, noteTitleView.text)
          putExtra(noteText, noteTextView.text)
          putExtra(noteId, noteIdView.text)
        }
        v.context.startActivity(i)
      }
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
    val itemView = inflater.inflate(R.layout.recycler_note_item, parent, false)
    return NoteViewHolder(itemView)
  }

  override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
    val current = notes[position]

    holder.noteIdView.text = current.noteId.toString()
    holder.noteTitleView.text = current.noteTitle
    holder.noteTextView.text = current.noteText
  }

  internal fun setNotes(notes: List<Note>) {
    this.notes = notes
    notifyDataSetChanged()
  }

  override fun getItemCount() = notes.size

  companion object {
    const val EXTRA_DETAILS = "com.example.andorid.wordlistsql.REPLY"
    const val noteId = "noteId"
    const val noteTitle = "noteTitle"
    const val noteText = "noteText"
  }
}