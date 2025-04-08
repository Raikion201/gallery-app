package com.example.photo.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.photo.data.Photo
import com.example.photo.data.PhotoDataSource
import com.example.photo.viewmodel.PhotoViewModel

@Composable
fun PhotoApp(viewModel: PhotoViewModel) {
    val layoutDirection = LocalLayoutDirection.current
    val photos by viewModel.photos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedIndex by viewModel.selectedPhotoIndex.collectAsState()
    
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(
                start = WindowInsets.safeDrawing.asPaddingValues()
                    .calculateStartPadding(layoutDirection),
                end = WindowInsets.safeDrawing.asPaddingValues()
                    .calculateEndPadding(layoutDirection),
            ),
    ) {
        // If a photo is selected, show the detail view
        if (selectedIndex != null && photos.isNotEmpty()) {
            PhotoDetailScreen(
                photo = photos[selectedIndex!!],
                onBackClick = { viewModel.clearSelection() },
                onPreviousClick = { viewModel.previousPhoto() },
                onNextClick = { viewModel.nextPhoto() },
                onFavoriteClick = { viewModel.toggleFavorite() }
            )
        } else {
            // Otherwise show the grid
            PhotoGridLayout(
                photos = photos,
                onPhotoClick = { viewModel.selectPhoto(it) },
                onAddPhotoClick = { /* Handle add photo */ }
            )
        }
    }
}

@Composable
fun PhotoGridLayout(
    photos: List<Photo>,
    onPhotoClick: (Int) -> Unit,
    onAddPhotoClick: () -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddPhotoClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Photo")
            }
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            itemsIndexed(photos) { index, photo ->
                PhotoCard(
                    photo = photo,
                    onPhotoClick = { onPhotoClick(index) },
                    onFavoriteClick = { /* Toggle favorite in VM */ },
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun PhotoCard(
    photo: Photo,
    onPhotoClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onPhotoClick),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column {
            // Image from resource ID or URI
            if (photo.resourceId != 0) {
                Image(
                    painter = painterResource(id = photo.resourceId),
                    contentDescription = photo.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(200.dp),
                    contentScale = ContentScale.Crop
                )
            } else if (photo.uri != null) {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(200.dp)
                ) {
                    coil.compose.AsyncImage(
                        model = photo.uri,
                        contentDescription = photo.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            
            // Title and favorite button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = photo.title,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                IconButton(onClick = onFavoriteClick) {
                    Icon(
                        imageVector = if (photo.isFavorite) 
                            Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (photo.isFavorite) 
                            "Remove from favorites" else "Add to favorites"
                    )
                }
            }
        }
    }
}

@Composable
fun PhotoDetailScreen(
    photo: Photo,
    onBackClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Top bar with back button and favorite
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_close_clear_cancel),
                    contentDescription = "Back"
                )
            }
            
            Text(
                text = photo.title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            
            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (photo.isFavorite) 
                        Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (photo.isFavorite) 
                        "Remove from favorites" else "Add to favorites"
                )
            }
        }
        
        // Full image
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (photo.resourceId != 0) {
                Image(
                    painter = painterResource(id = photo.resourceId),
                    contentDescription = photo.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            } else if (photo.uri != null) {
                coil.compose.AsyncImage(
                    model = photo.uri,
                    contentDescription = photo.title,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            // Navigation buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onPreviousClick) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_media_previous),
                        contentDescription = "Previous"
                    )
                }
                
                IconButton(onClick = onNextClick) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_media_next),
                        contentDescription = "Next"
                    )
                }
            }
        }
        
        // Description
        if (photo.description.isNotEmpty()) {
            Text(
                text = photo.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
