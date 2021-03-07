package ie.cm.citynavigation

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ie.cm.citynavigation.adapter.NoteCardAdapter
import ie.cm.citynavigation.entities.Note
import ie.cm.citynavigation.viewModel.NoteViewModel

class NoteList : AppCompatActivity() {

    private lateinit var noteViewModel: NoteViewModel
    private val newNoteActivityRequestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_list)

        //Recycler view
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerNotes)
        val adapter = NoteCardAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        //View model
        noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)
        noteViewModel.allNotes.observe(this, { notes ->
            notes?.let{adapter.setNotes(it)}
        })

        val fab = findViewById<FloatingActionButton>(R.id.notesFab)
        fab.setOnClickListener{
            val intent = Intent(this, NewNote::class.java)
            startActivityForResult(intent, newNoteActivityRequestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == newNoteActivityRequestCode && resultCode == RESULT_OK) {
            data?.getStringArrayExtra(NewNote.EXTRA_REPLY)?.let {
                val note = Note(noteTile = it[0], noteText = it[1])
                noteViewModel.insert(note)
            }
        } else {
            Toast.makeText(applicationContext, R.string.lorem, Toast.LENGTH_SHORT).show()
        }
    }
}