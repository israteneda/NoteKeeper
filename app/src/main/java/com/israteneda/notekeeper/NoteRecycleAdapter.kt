package com.israteneda.notekeeper

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NoteRecycleAdapter(private val context: Context, private val notes: List<NoteInfo>)
    : RecyclerView.Adapter<NoteRecycleAdapter.ViewHolder>() {

    private val layoutInflater = LayoutInflater.from(context)
    private var onNoteSelectedListener: OnNoteSelectedListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = layoutInflater.inflate(R.layout.item_note_list, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount() = notes.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = notes[position]
        holder.textCourse.text = note.course?.title
        holder.textTitle.text = note.title
        holder.notePosition = position
    }

    fun setOnSelectedListener(listener: OnNoteSelectedListener) {
        onNoteSelectedListener = listener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textCourse: TextView = itemView.findViewById(R.id.textCourse)
        val textTitle: TextView = itemView.findViewById(R.id.textTitle)
        var notePosition = 0
        init {
            itemView.setOnClickListener {
                onNoteSelectedListener?.onNoteSelected(notes[notePosition])
                val intent = Intent(context, NoteActivity::class.java)
                intent.putExtra(NOTE_POSITION, notePosition)
                context.startActivity(intent)
            }
        }
    }

    interface OnNoteSelectedListener {
        fun onNoteSelected(note: NoteInfo)
    }
}