package com.example.photo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.photo.ui.screens.PhotoDetailScreen
import com.example.photo.ui.screens.PhotoGridScreen
import com.example.photo.ui.theme.PhotoTheme
import com.example.photo.viewmodel.PhotoViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    
    private val viewModel: PhotoViewModel by viewModels()
    private lateinit var tempPhotoUri: Uri
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            PhotoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val photos by viewModel.photos.collectAsState()
                    val isLoading by viewModel.isLoading.collectAsState()
                    val selectedPhotoIndex by viewModel.selectedPhotoIndex.collectAsState()
                    val context = LocalContext.current
                    
                    // Gallery picker launcher
                    val galleryLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.PickVisualMedia()
                    ) { uri ->
                        if (uri != null) {
                            viewModel.addNewPhoto(uri)
                        }
                    }
                    
                    // Camera launcher - Define this first before using it in cameraPermissionLauncher
                    val takePictureResultLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.TakePicture()
                    ) { success ->
                        if (success) {
                            viewModel.addNewPhoto(tempPhotoUri)
                        }
                    }
                    
                    // Camera permission launcher
                    val cameraPermissionLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission()
                    ) { isGranted ->
                        if (isGranted) {
                            // Create a file to save the photo
                            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                            val photoFile = File(filesDir, "PHOTO_${timeStamp}.jpg")
                            tempPhotoUri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.fileprovider",
                                photoFile
                            )
                            
                            // Launch camera
                            takePictureResultLauncher.launch(tempPhotoUri)
                        }
                    }
                    
                    if (selectedPhotoIndex != null && photos.isNotEmpty()) {
                        // Show detail screen
                        PhotoDetailScreen(
                            photos = photos,
                            initialIndex = selectedPhotoIndex!!,
                            onNavigateBack = { viewModel.clearSelection() },
                            onPreviousPhoto = { viewModel.previousPhoto() },
                            onNextPhoto = { viewModel.nextPhoto() },
                            onToggleFavorite = { viewModel.toggleFavorite() }
                        )
                    } else {
                        // Show grid screen
                        PhotoGridScreen(
                            photos = photos,
                            isLoading = isLoading,
                            onPhotoClick = { viewModel.selectPhoto(it) },
                            onAddPhotoFromGallery = {
                                galleryLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            onTakePhoto = {
                                when {
                                    ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.CAMERA
                                    ) == PackageManager.PERMISSION_GRANTED -> {
                                        // Create a file to save the photo
                                        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                                        val photoFile = File(filesDir, "PHOTO_${timeStamp}.jpg")
                                        tempPhotoUri = FileProvider.getUriForFile(
                                            context,
                                            "${context.packageName}.fileprovider",
                                            photoFile
                                        )
                                        
                                        // Launch camera
                                        takePictureResultLauncher.launch(tempPhotoUri)
                                    }
                                    else -> {
                                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                    }
                                }
                            },
                            onOpenSettings = {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", packageName, null)
                                }
                                startActivity(intent)
                            },
                            onToggleFavorite = { index ->
                                viewModel.toggleFavorite(index)
                            },
                            onDeletePhoto = { index ->
                                viewModel.deletePhoto(index)
                            }
                        )
                    }
                }
            }
        }
    }
}