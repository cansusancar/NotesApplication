package com.example.notesapplication
import com.google.firebase.database.IgnoreExtraProperties
@IgnoreExtraProperties
data class Note(    var note_id : String? = "",
                    var note_name:String? ="",
                    var note_key:String? =""
) {

}