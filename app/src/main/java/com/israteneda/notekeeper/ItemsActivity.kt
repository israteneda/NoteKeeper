package com.israteneda.notekeeper

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_items.*
import kotlinx.android.synthetic.main.app_bar_items.*
import kotlinx.android.synthetic.main.content_items.*

class ItemsActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener,
    NoteRecycleAdapter.OnNoteSelectedListener{



    private val noteLayoutManager by lazy { LinearLayoutManager(this) }

    private val noteRecycleAdapter by lazy {
        val adapter = NoteRecycleAdapter(this, DataManager.loadNotes())
        adapter.setOnSelectedListener(this)
        adapter
    }

    private val courseLayoutManager by lazy { GridLayoutManager(this, resources.getInteger(R.integer.course_grid_span)) }

    private val courseRecycleAdapter by lazy { CourseRecycleAdapter(this, DataManager.courses.values.toList()) }

    private val recentlyViewNotesRecycleAdapter by lazy {
        val adapter = NoteRecycleAdapter(this, viewModel.recentlyViewNotes)
        adapter.setOnSelectedListener(this)
        adapter
    }

    private val viewModel by lazy {
        ViewModelProviders.of(this)[ItemsActivityViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items)
        setSupportActionBar(toolbar)

//        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            startActivity(Intent(this, NoteActivity::class.java))
        }

        if (viewModel.isNewlyCreated && savedInstanceState != null)
            viewModel.restoreState(savedInstanceState)
        viewModel.isNewlyCreated = false
        handleDisplaySelection(viewModel.navDrawerDisplaySelection)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        val menu = nav_view.menu

        val menuItem = menu.findItem(R.id.nav_how_many)

        menuItem.title = "Notes: ${DataManager.notes.size} | Courses: ${DataManager.courses.size}"

        nav_view.setNavigationItemSelectedListener(this)
    }


    private fun displayNotes() {
        listItems.layoutManager = noteLayoutManager
        listItems.adapter = noteRecycleAdapter

        nav_view.menu.findItem(R.id.nav_notes).isCheckable = true
    }

    private fun displayCourses() {
        listItems.layoutManager = courseLayoutManager
        listItems.adapter = courseRecycleAdapter

        nav_view.menu.findItem(R.id.nav_courses).isCheckable = true
    }

    private fun displayRecentlyNotes() {
        listItems.layoutManager = noteLayoutManager
        listItems.adapter = recentlyViewNotesRecycleAdapter

        nav_view.menu.findItem(R.id.nav_courses).isCheckable = true
    }

    override fun onResume() {
        super.onResume()
        listItems.adapter?.notifyDataSetChanged()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.findItem(R.id.nav_how_many)
        menuInflater.inflate(R.menu.items, menu)
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_notes,
            R.id.nav_courses,
            R.id.nav_recently_notes -> {
                handleDisplaySelection(item.itemId)
                viewModel.navDrawerDisplaySelection = item.itemId
            }
            R.id.nav_share -> {
                handleSelection(R.string.nav_share_message)
            }
            R.id.nav_send -> {
                handleSelection(R.string.nav_send_message)
            }
            R.id.nav_how_many -> {
                val message = getString(R.string.nav_how_many_message_format,
                DataManager.notes.size, DataManager.courses.size)
                Snackbar.make(listItems, message, Snackbar.LENGTH_LONG).show()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun handleDisplaySelection(itemId: Int) {
        when(itemId){
            R.id.nav_notes -> {
                displayNotes()
            }
            R.id.nav_courses -> {
                displayCourses()
            }
            R.id.nav_recently_notes -> {
                displayRecentlyNotes()
            }
        }
    }

    private fun handleSelection(stringId: Int) {
        Snackbar.make(listItems, stringId, Snackbar.LENGTH_LONG).show()
    }

    override fun onNoteSelected(note: NoteInfo) {
        viewModel.addToRecentlyViewedNotes(note)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if(outState != null)
            viewModel.saveState(outState)
    }
}
