package com.israteneda.notekeeper

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.israteneda.notekeeper.databinding.ActivityItemsBinding


class ItemsActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener,
    NoteRecycleAdapter.OnNoteSelectedListener{

    /* bindings */
    private lateinit var binding: ActivityItemsBinding

    private lateinit var fab: FloatingActionButton
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var navView: NavigationView
    private lateinit var listItems: RecyclerView
    /* bindings */

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
        ViewModelProvider(this)[ItemsActivityViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityItemsBinding.inflate(layoutInflater)

        fab = binding.appBarItems.fab
        drawerLayout = binding.drawerLayout
        toolbar = binding.appBarItems.toolbar
        navView = binding.navView
        listItems = binding.appBarItems.contentItems.listItems

        setContentView(binding.root)
        setSupportActionBar(toolbar)


        fab.setOnClickListener {
            startActivity(Intent(this, NoteActivity::class.java))
        }

        if (viewModel.isNewlyCreated && savedInstanceState != null)
            viewModel.restoreState(savedInstanceState)

        viewModel.isNewlyCreated = false
        handleDisplaySelection(viewModel.navDrawerDisplaySelection)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val menu = navView.menu

        val menuItem = menu.findItem(R.id.nav_how_many)

        menuItem.title = "Notes: ${DataManager.notes.size} | Courses: ${DataManager.courses.size}"

        navView.setNavigationItemSelectedListener(this)

    }


    private fun displayNotes() {
        listItems.layoutManager = noteLayoutManager
        listItems.adapter = noteRecycleAdapter

        navView.menu.findItem(R.id.nav_notes).isCheckable = true
    }

    private fun displayCourses() {
        listItems.layoutManager = courseLayoutManager
        listItems.adapter = courseRecycleAdapter

        navView.menu.findItem(R.id.nav_courses).isCheckable = true
    }

    private fun displayRecentlyNotes() {
        listItems.layoutManager = noteLayoutManager
        listItems.adapter = recentlyViewNotesRecycleAdapter

        navView.menu.findItem(R.id.nav_courses).isCheckable = true
    }

    override fun onResume() {
        super.onResume()
        listItems.adapter?.notifyDataSetChanged()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
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

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun handleDisplaySelection(itemId: Int) {
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
