package com.israteneda.notekeeper

import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.hamcrest.Matchers.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NextThroughNotes{

    @Rule @JvmField
    val noteListActivity = ActivityTestRule(NoteListActivity::class.java)

    @Test
    fun nextThroughNotes() {
        onData(allOf(instanceOf(NoteInfo::class.java), equalTo(DataManager.notes[0]))).perform(click())

        for(index in 0..DataManager.notes.lastIndex){
            val note = DataManager.notes[index]

            onView(withId(R.id.spinnerCourses)).check(
                matches(withSpinnerText(note.course?.title))
            )
            onView(withId(R.id.textNoteTitle)).check(
                matches(withText(note.title))
            )
            onView(withId(R.id.textNoteText)).check(
                matches(withText(note.text))
            )

            if(index != DataManager.notes.lastIndex)
                onView(allOf(withId(R.id.action_next), isEnabled())).perform(click())
        }

        onView(withId(R.id.action_next)).check(matches(isEnabled()))

    }


}