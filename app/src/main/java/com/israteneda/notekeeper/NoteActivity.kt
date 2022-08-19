package com.israteneda.notekeeper

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.israteneda.notekeeper.databinding.ActivityMainBinding

class NoteActivity : AppCompatActivity() {
    private val tag = this::class.simpleName
    private var notePosition = POSITION_NOT_SET

    private val noteGetTogetherHelper = NoteGetTogetherHelper(this, lifecycle)

    private val locManager = PseudoLocationManager(this) { lat, lon ->
        Log.d(tag, "Location Callback Lat:$lat Lon:$lon")
    }

    /* bindings */
    private lateinit var binding: ActivityMainBinding

    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var spinnerCourses: Spinner
    private lateinit var textNoteTitle: EditText
    private lateinit var textNoteText: EditText

    /* bindings */

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        toolbar = binding.toolbar
        spinnerCourses = binding.contentMain.spinnerCourses
        textNoteTitle = binding.contentMain.textNoteTitle
        textNoteText = binding.contentMain.textNoteText

        setContentView(binding.root)
        setSupportActionBar(toolbar)

        val adapterCourses = ArrayAdapter(this,
            android.R.layout.simple_spinner_item,
            DataManager.courses.values.toList())
        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerCourses.adapter = adapterCourses

        notePosition = savedInstanceState?.getInt(NOTE_POSITION, POSITION_NOT_SET) ?:
            intent.getIntExtra(NOTE_POSITION, POSITION_NOT_SET)


        if(notePosition != POSITION_NOT_SET) {
            displayNote()
        }
        else {
            createNote()
        }
        Log.d(tag, "onCreate")
    }

    override fun onStart() {
        super.onStart()
        locManager.start()
    }

    override fun onStop() {
        locManager.stop()
        super.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(NOTE_POSITION, notePosition)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (notePosition >= DataManager.notes.lastIndex){
            val menuItem = menu?.findItem(R.id.action_next)
            if (menuItem != null){
                menuItem.icon = getDrawable(R.drawable.ic_block_with_24dp)
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_settings -> true
            R.id.action_next -> {
                if(notePosition < DataManager.notes.lastIndex){
                    moveNext()
                } else {
                    val message = "No more notes"
                    showMessage(message).show()
                }
                true
            }
            R.id.action_get_together -> {
                noteGetTogetherHelper.sendMessage(DataManager.loadNote(notePosition))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPause() {
        super.onPause()
        saveNote()
        Log.d(tag, "onPause")
    }

    private fun saveNote() {
        val note = DataManager.notes[notePosition]
        note.title = textNoteTitle.text.toString()
        note.text = textNoteText.text.toString()
        note.course = spinnerCourses.selectedItem as CourseInfo
    }

    private fun displayNote() {
        if(notePosition > DataManager.notes.lastIndex) {
            showMessage("Note not found").show()
            Log.e(tag, "Invalid note position $notePosition, max valid position ${DataManager.notes.lastIndex}")
            return
        }

        Log.i(tag, "Displaying note for position $notePosition")
        val note = DataManager.notes[notePosition]
        (textNoteTitle as TextView).text = note.title
        (textNoteText as TextView).text = note.text

        val coursePosition = DataManager.courses.values.indexOf(note.course)
        spinnerCourses.setSelection(coursePosition)
    }

    private fun moveNext() {
        ++notePosition
        displayNote()
        invalidateOptionsMenu()
    }

    private fun createNote() {
        DataManager.notes.add(NoteInfo())
        notePosition = DataManager.notes.lastIndex
    }

    private fun showMessage(message: String) =
        Snackbar.make(textNoteTitle, message, Snackbar.LENGTH_LONG)

}
