package com.example.photo.data

import com.example.photo.R

/**
 * [PhotoDataSource] provides a list of sample [Photo] objects 
 * using drawable resources
 */
class PhotoDataSource {
    fun loadPhotos(): List<Photo> {
        return listOf(
            Photo(
                resourceId = R.drawable.image1,
                title = "Nature View",
                description = ""
            ),
            Photo(
                resourceId = R.drawable.image2,
                title = "Beach Sunset",
                description = ""
            ),
            Photo(
                resourceId = R.drawable.image3,
                title = "City Skyline",
                description = ""
            ),
            Photo(
                resourceId = R.drawable.image4,
                title = "Forest Trail",
                description = ""
            ),
            Photo(
                resourceId = R.drawable.image5,
                title = "Mountain Peak",
                description = ""
            ),
            Photo(
                resourceId = R.drawable.image6,
                title = "Desert Landscape",
                description = ""
            ),
            Photo(
                resourceId = R.drawable.image7,
                title = "Waterfall",
                description = ""
            ),
            Photo(
                resourceId = R.drawable.image8,
                title = "Autumn Colors",
                description = ""
            ),
            Photo(
                resourceId = R.drawable.image9,
                title = "Lake",
                description = ""
            ),
            Photo(
                resourceId = R.drawable.image10,
                title = "Starry Night",
                description = ""
            )
        )
    }
}
