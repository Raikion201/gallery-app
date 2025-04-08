package com.example.photo.data

import android.net.Uri
import androidx.annotation.DrawableRes
import java.util.UUID

data class Photo(
    val id: String = UUID.randomUUID().toString(),
    val uri: Uri? = null,
    @DrawableRes val resourceId: Int = 0,
    val title: String = "",
    val description: String = "",
    val isFavorite: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
) {
    // Secondary constructor for resource-based photos
    constructor(
        @DrawableRes resourceId: Int,
        title: String,
        description: String = "",
        isFavorite: Boolean = false
    ) : this(
        uri = null,
        resourceId = resourceId,
        title = title,
        description = description,
        isFavorite = isFavorite
    )

    // Secondary constructor for URI-based photos
    constructor(
        uri: Uri,
        title: String,
        description: String = "",
        isFavorite: Boolean = false
    ) : this(
        uri = uri,
        resourceId = 0,
        title = title,
        description = description,
        isFavorite = isFavorite
    )
}
