package com.example.cookup.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Comment(
    val senderId: String = "",
    val text: String = "",
    val timestamp: Timestamp = Timestamp.now()
) : Parcelable
