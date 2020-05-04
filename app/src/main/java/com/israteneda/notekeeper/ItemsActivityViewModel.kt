package com.israteneda.notekeeper

import android.os.Bundle
import androidx.lifecycle.ViewModel

class ItemsActivityViewModel : ViewModel() {
    var isNewlyCreated = true

    var navDrawerDysplaySelectionName =
        "com.israteneda.notekeeper.ItemsActivity.navDrawerDisplaySelection"
    var recentlyViewedNoteIdsName =
        "com.israteneda.notekeeper.ItemsActivityViewModel.recentlyViewedNoteIds"

    var navDrawerDisplaySelection = R.id.nav_notes

    private val maxRecentlyViewNotes = 5
    val recentlyViewNotes = ArrayList<NoteInfo>(maxRecentlyViewNotes)

    fun addToRecentlyViewedNotes(note: NoteInfo) {
        val existingIndex = recentlyViewNotes.indexOf(note)
        if (existingIndex == -1) {
            recentlyViewNotes.add(0, note)
            for (index in recentlyViewNotes.lastIndex downTo maxRecentlyViewNotes)
                recentlyViewNotes.removeAt(index)
        } else {
            for (index in (existingIndex - 1) downTo 0)
                recentlyViewNotes[index + 1] = recentlyViewNotes[index]
            recentlyViewNotes[0] = note
        }
    }

    fun saveState(outState: Bundle) {
        outState.putInt(navDrawerDysplaySelectionName, navDrawerDisplaySelection)
        val notesId = DataManager.noteIdsAsIntArray(recentlyViewNotes)
        outState.putIntArray(recentlyViewedNoteIdsName, notesId)
    }

    fun restoreState(savedInstanceState: Bundle) {
        navDrawerDisplaySelection = savedInstanceState.getInt(navDrawerDysplaySelectionName)
        val notesIds = savedInstanceState.getIntArray(recentlyViewedNoteIdsName)
        val noteList = notesIds?.let { DataManager.loadNotes(*it) }
        if (noteList != null) {
            recentlyViewNotes.addAll(noteList)
        }
    }
}