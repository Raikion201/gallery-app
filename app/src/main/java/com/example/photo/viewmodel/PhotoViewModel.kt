package com.example.photo.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.photo.data.Photo
import com.example.photo.data.PhotoDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PhotoViewModel(application: Application) : AndroidViewModel(application) {
    
    private val photoDataSource = PhotoDataSource()
    
    private val _photos = MutableStateFlow<List<Photo>>(emptyList())
    val photos: StateFlow<List<Photo>> = _photos.asStateFlow()
    
    private val _selectedPhotoIndex = MutableStateFlow<Int?>(null)
    val selectedPhotoIndex: StateFlow<Int?> = _selectedPhotoIndex.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _currentPage = MutableStateFlow(0)
    private val pageSize = 20
    
    init {
        loadPhotos()
    }
    
    fun loadPhotos() {
        viewModelScope.launch {
            _isLoading.value = true
            
            // For demonstration, still loading all at once from DataSource
            // In a real app, you'd fetch just the current page from the repository
            val allPhotos = photoDataSource.loadPhotos()
            
            // Take only items for current page
            val startIndex = _currentPage.value * pageSize
            val endIndex = minOf(startIndex + pageSize, allPhotos.size)
            val currentPagePhotos = if (startIndex < allPhotos.size) {
                allPhotos.subList(startIndex, endIndex)
            } else {
                emptyList()
            }
            
            _photos.value = currentPagePhotos
            _isLoading.value = false
        }
    }
    
    fun loadNextPage() {
        _currentPage.value += 1
        loadPhotos()
    }
    
    fun selectPhoto(index: Int) {
        _selectedPhotoIndex.value = index
    }
    
    fun clearSelection() {
        _selectedPhotoIndex.value = null
    }
    
    fun getSelectedPhoto(): Photo? {
        val index = _selectedPhotoIndex.value ?: return null
        return _photos.value.getOrNull(index)
    }
    
    fun nextPhoto() {
        val currentIndex = _selectedPhotoIndex.value ?: return
        val nextIndex = (currentIndex + 1) % _photos.value.size
        _selectedPhotoIndex.value = nextIndex
    }
    
    fun previousPhoto() {
        val currentIndex = _selectedPhotoIndex.value ?: return
        val previousIndex = if (currentIndex > 0) currentIndex - 1 else _photos.value.size - 1
        _selectedPhotoIndex.value = previousIndex
    }
    
    fun toggleFavorite() {
        val currentIndex = _selectedPhotoIndex.value ?: return
        toggleFavorite(currentIndex)
    }

    fun toggleFavorite(index: Int) {
        if (index in _photos.value.indices) {
            val currentPhotos = _photos.value.toMutableList()
            val photo = currentPhotos[index]
            currentPhotos[index] = photo.copy(isFavorite = !photo.isFavorite)
            _photos.value = currentPhotos
        }
    }
    
    fun addNewPhoto(uri: Uri, title: String = "") {
        val newPhoto = Photo(
            uri = uri,
            title = title.ifEmpty { "Photo ${_photos.value.size + 1}" },
            timestamp = System.currentTimeMillis()
        )
        
        _photos.update { currentList ->
            val updatedList = currentList.toMutableList()
            // Add new photo at the beginning of the list
            updatedList.add(0, newPhoto)
            updatedList
        }
    }
    
    fun deletePhoto(index: Int) {
        if (index in _photos.value.indices) {
            _photos.update { currentList ->
                val updatedList = currentList.toMutableList()
                updatedList.removeAt(index)
                updatedList
            }
            
            // If the currently selected photo is deleted, clear selection
            if (_selectedPhotoIndex.value == index) {
                clearSelection()
            }
            // If a photo after the deleted one was selected, adjust the index
            else if (_selectedPhotoIndex.value != null && _selectedPhotoIndex.value!! > index) {
                _selectedPhotoIndex.value = _selectedPhotoIndex.value!! - 1
            }
        }
    }
}
