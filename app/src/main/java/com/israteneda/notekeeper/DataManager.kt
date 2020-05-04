package com.israteneda.notekeeper

object DataManager {
    val courses = HashMap<String, CourseInfo>()
    val notes = ArrayList<NoteInfo>()

    init {
        initializeCourses()
        initializeNotes()
    }

    fun loadNotes(vararg noteIds: Int): List<NoteInfo> {
        simulateLoadDelay()
        val noteList: List<NoteInfo>

        if(noteIds.isEmpty())
            noteList = notes
        else {
            noteList = ArrayList<NoteInfo>(noteIds.size)
            for(noteId in noteIds)
                noteList.add(notes[noteId])
        }
        return noteList
    }

    fun loadNote(noteId: Int) = notes[noteId]

    fun isLastNoteId(noteId: Int) = noteId == notes.lastIndex

    fun idOfNote(note: NoteInfo) = notes.indexOf(note)

    fun noteIdsAsIntArray(notes: List<NoteInfo>): IntArray {
        val noteIds = IntArray(notes.size)
        for(index in 0..notes.lastIndex)
            noteIds[index] = DataManager.idOfNote(notes[index])
        return noteIds
    }

    private fun simulateLoadDelay() {
        Thread.sleep(1000)
    }

    fun addNote(course: CourseInfo, noteTitle: String, noteText: String): Int {
        val note = NoteInfo(course, noteTitle, noteText)
        notes.add(note)
        return notes.lastIndex
    }

    fun findNote(course: CourseInfo, noteTitle: String, noteText: String): NoteInfo? {
        for (note in notes)
            if (course == note.course && noteTitle == note.title && noteText == note.text)
                return note
        return null
    }

    fun initializeCourses() {
        var course = CourseInfo(title = "Java Fundamentals: The Java Language", courseId = "java_lang")
        courses.set(course.courseId, course)

        course = CourseInfo("android_intents", "Android Programming with Intents")
        courses.set(course.courseId, course)

        course = CourseInfo(courseId = "android_async", title = "Android Async Programming and Services")
        courses.set(course.courseId, course)


        course = CourseInfo("java_core", "Java Fundamentals: The Core Platform")
        courses.set(course.courseId, course)
    }

    fun initializeNotes() {
        var note = NoteInfo(courses["android_intents"] as CourseInfo, "Dynamic intent resolution", "Wow intents allow components to be resolved at runtime")
        notes.add(note)

        note = NoteInfo(courses["android_intents"] as CourseInfo, "Deleting intents", "PendingIntents are powerful, they delegate much more than just a component invocation")
        notes.add(note)

        note = NoteInfo(courses["android_async"] as CourseInfo, "Service default threads", "Did you know that by default an Android Service will tie up the UI thread")
        notes.add(note)

        note = NoteInfo(courses["java_lang"] as CourseInfo, "Parameters", "Leverage variable-length parameters list")
        notes.add(note)

        note = NoteInfo(courses["java_lang"] as CourseInfo, "Anonymous classes", "Anonymous classes simplify implementing one use type")
        notes.add(note)

        note = NoteInfo(courses["java_core"] as CourseInfo, "Compiler options", "The .jar options isn't compatible with the -cp option")
        notes.add(note)

        note = NoteInfo(courses["java_core"] as CourseInfo, "Serialization", "Remember to include Serial/VersionUID to assure version compatibility")
        notes.add(note)
    }
}