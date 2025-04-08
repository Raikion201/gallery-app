package com.example.photo.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.photo.data.Photo
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PhotoDetailScreen(
    photos: List<Photo>,
    initialIndex: Int,
    onNavigateBack: () -> Unit,
    onPreviousPhoto: () -> Unit,
    onNextPhoto: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val pagerState = rememberPagerState(initialPage = initialIndex) { photos.size }
    val coroutineScope = rememberCoroutineScope()
    
    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage != initialIndex) {
            if (pagerState.currentPage > initialIndex) {
                onNextPhoto()
            } else {
                onPreviousPhoto()
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(photos.getOrNull(pagerState.currentPage)?.title ?: "") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    val currentPhoto = photos.getOrNull(pagerState.currentPage)
                    IconButton(onClick = onToggleFavorite) {
                        if (currentPhoto?.isFavorite == true) {
                            Icon(Icons.Default.Favorite, contentDescription = "Remove from favorites")
                        } else {
                            Icon(Icons.Default.FavoriteBorder, contentDescription = "Add to favorites")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f),
                    beyondBoundsPageCount = 2
                ) { page ->
                    val photo = photos.getOrNull(page)
                    if (photo != null) {
                        ZoomableImage(
                            photo = photo,
                            onSwipeLeft = {
                                if (page < photos.size - 1) {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(page + 1)
                                    }
                                }
                            },
                            onSwipeRight = {
                                if (page > 0) {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(page - 1)
                                    }
                                }
                            }
                        )
                    }
                }
                
                val currentPhoto = photos.getOrNull(pagerState.currentPage)
                if (currentPhoto?.description?.isNotEmpty() == true) {
                    androidx.compose.material3.Text(
                        text = currentPhoto.description,
                        style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            val newPage = (pagerState.currentPage - 1).coerceAtLeast(0)
                            pagerState.animateScrollToPage(newPage)
                        }
                    }) {
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Previous")
                    }
                    
                    IconButton(onClick = {
                        coroutineScope.launch {
                            val newPage = (pagerState.currentPage + 1).coerceAtMost(photos.size - 1)
                            pagerState.animateScrollToPage(newPage)
                        }
                    }) {
                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Next")
                    }
                }
            }
        }
    }
}

@Composable
fun ZoomableImage(
    photo: Photo,
    onSwipeLeft: () -> Unit = {},
    onSwipeRight: () -> Unit = {},
) {
    val context = LocalContext.current
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    
    var dragStartX by remember { mutableFloatStateOf(0f) }
    var dragEndX by remember { mutableFloatStateOf(0f) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(1f, 3f)
                    if (scale > 1f) {
                        offsetX += pan.x
                        offsetY += pan.y
                    } else {
                        offsetX = 0f
                        offsetY = 0f
                    }
                }
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { dragStartX = it.x },
                    onDragEnd = {
                        val swipeDistance = dragEndX - dragStartX
                        if (scale <= 1f) {
                            when {
                                swipeDistance > 50f -> onSwipeRight()
                                swipeDistance < -50f -> onSwipeLeft()
                            }
                        }
                    },
                    onDragCancel = { },
                    onHorizontalDrag = { change, _ -> dragEndX = change.position.x }
                )
            }
    ) {
        if (photo.resourceId != 0) {
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = photo.resourceId),
                contentDescription = photo.title,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = offsetX
                        translationY = offsetY
                    }
            )
        } else if (photo.uri != null) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(photo.uri)
                    .crossfade(true)
                    .build(),
                contentDescription = photo.title,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = offsetX
                        translationY = offsetY
                    }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PhotoDetailScreenPreview() {
    val samplePhotos = listOf(
        Photo(
            resourceId = android.R.drawable.ic_menu_gallery,
            title = "Sample Photo 1",
            description = "This is a sample description for the first photo"
        ),
        Photo(
            resourceId = android.R.drawable.ic_menu_camera,
            title = "Sample Photo 2",
            description = "Description for the second photo in the preview"
        ),
        Photo(
            resourceId = android.R.drawable.ic_menu_report_image,
            title = "Sample Photo 3",
            description = "Another description for preview purposes"
        )
    )
    
    PhotoDetailScreen(
        photos = samplePhotos,
        initialIndex = 0,
        onNavigateBack = {},
        onPreviousPhoto = {},
        onNextPhoto = {},
        onToggleFavorite = {}
    )
}

@Preview(showBackground = true)
@Composable
fun ZoomableImagePreview() {
    val samplePhoto = Photo(
        resourceId = android.R.drawable.ic_menu_gallery,
        title = "Sample Zoomable Image",
        description = "This is a preview of the zoomable image component"
    )
    
    ZoomableImage(photo = samplePhoto)
}
