package com.example.photo.data

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class PhotoRepository(private val context: Context) {
    
    fun getAllPhotos(): Flow<List<Photo>> = flow {
        val photoList = mutableListOf<Photo>()
        val contentResolver = context.contentResolver
        
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED
        )
        
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
        
        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
            
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val dateAdded = cursor.getLong(dateAddedColumn)
                
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                
                photoList.add(
                    Photo(
                        id = id.toString(),
                        uri = contentUri,
                        title = name,
                        timestamp = dateAdded * 1000 // Convert to milliseconds
                    )
                )
            }
        }
        
        emit(photoList)
    }.flowOn(Dispatchers.IO)
    
    // Function to add a new photo (e.g., after taking a picture)
    fun addPhoto(uri: Uri, title: String = "") = flow {
        val photo = Photo(
            uri = uri,
            title = title
        )
        emit(photo)
    }.flowOn(Dispatchers.IO)
}
