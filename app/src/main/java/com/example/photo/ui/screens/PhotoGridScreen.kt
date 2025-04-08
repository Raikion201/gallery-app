package com.example.photo.ui.screens

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.photo.data.Photo
import com.example.photo.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhotoGridScreen(
    photos: List<Photo>,
    isLoading: Boolean,
    onPhotoClick: (Int) -> Unit,
    onAddPhotoFromGallery: () -> Unit,
    onTakePhoto: () -> Unit,
    onOpenSettings: () -> Unit,
    onToggleFavorite: (Int) -> Unit = {},
    onDeletePhoto: (Int) -> Unit = {}
) {
    var expandedFab by remember { mutableStateOf(false) }
    
    Scaffold(
        floatingActionButton = {
            ExpandableFab(
                expanded = expandedFab,
                onExpandedChange = { expandedFab = it },
                onGalleryClick = {
                    expandedFab = false
                    onAddPhotoFromGallery()
                },
                onCameraClick = {
                    expandedFab = false
                    onTakePhoto()
                },
                onSettingsClick = {
                    expandedFab = false
                    onOpenSettings()
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    itemsIndexed(photos) { index, photo ->
                        PhotoGridItem(
                            photo = photo,
                            onClick = { onPhotoClick(index) },
                            onFavoriteClick = { onToggleFavorite(index) },
                            onDeleteClick = { onDeletePhoto(index) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExpandableFab(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transition = updateTransition(targetState = expanded, label = "FAB transition")
    val rotationAngle by transition.animateFloat(label = "FAB rotation") { state ->
        if (state) 45f else 0f
    }
    
    Column(
        horizontalAlignment = Alignment.End,
        modifier = modifier.padding(bottom = 16.dp, end = 16.dp)
    ) {
        // Sub FABs that show only when expanded
        if (expanded) {
            // Gallery Option
            SmallFloatingActionButton(
                onClick = onGalleryClick,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.photo_library_24px),
                    contentDescription = "Add from Gallery"
                )
            }
            
            // Camera Option
            SmallFloatingActionButton(
                onClick = onCameraClick,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.photo_camera_24px),
                    contentDescription = "Take Photo"
                )
            }
            
            // Settings Option
            SmallFloatingActionButton(
                onClick = onSettingsClick,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.settings_24px),
                    contentDescription = "Settings"
                )
            }
        }
        
        // Main FAB
        FloatingActionButton(
            onClick = { onExpandedChange(!expanded) }
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "More Options",
                modifier = Modifier.rotate(rotationAngle)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhotoGridItem(
    photo: Photo,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.5f)
            .padding(horizontal = 8.dp)
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = { showMenu = true }
            )
    ) {
        // Photo content
        if (photo.resourceId != 0) {
            // Load from drawable resource
            Image(
                painter = painterResource(id = photo.resourceId),
                contentDescription = photo.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else if (photo.uri != null) {
            // Load from URI
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(photo.uri)
                    .crossfade(true)
                    .build(),
                contentDescription = photo.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        
        // Title overlay at bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(8.dp)
        ) {
            Text(
                text = photo.title,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
        }
        
        // Show favorite indicator if photo is marked as favorite
        if (photo.isFavorite) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Favorite",
                tint = Color.Red,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(28.dp)
            )
        }
        
        // Context menu on long press
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text(if (photo.isFavorite) "Remove from Favorites" else "Add to Favorites") },
                leadingIcon = {
                    Icon(
                        imageVector = if (photo.isFavorite) Icons.Default.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = null
                    )
                },
                onClick = {
                    onFavoriteClick()
                    showMenu = false
                }
            )
            
            DropdownMenuItem(
                text = { Text("Delete") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null
                    )
                },
                onClick = {
                    onDeleteClick()
                    showMenu = false
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PhotoGridScreenPreview() {
    val samplePhotos = listOf(
        Photo(
            resourceId = android.R.drawable.ic_menu_gallery,
            title = "Sample Photo 1"
        ),
        Photo(
            resourceId = android.R.drawable.ic_menu_camera,
            title = "Sample Photo 2"
        ),
        Photo(
            resourceId = android.R.drawable.ic_menu_report_image,
            title = "Sample Photo 3"
        )
    )
    
    PhotoGridScreen(
        photos = samplePhotos,
        isLoading = false,
        onPhotoClick = {},
        onAddPhotoFromGallery = {},
        onTakePhoto = {},
        onOpenSettings = {},
        onToggleFavorite = {},
        onDeletePhoto = {}
    )
}

@Preview(showBackground = true)
@Composable
fun ExpandableFabPreview() {
    var expanded by remember { mutableStateOf(true) }
    
    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            ExpandableFab(
                expanded = expanded,
                onExpandedChange = { expanded = it },
                onGalleryClick = {},
                onCameraClick = {},
                onSettingsClick = {},
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}
