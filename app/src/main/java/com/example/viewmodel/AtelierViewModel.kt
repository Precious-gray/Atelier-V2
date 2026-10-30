package com.example.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.FirebaseAtelierRepository
import com.example.model.ClientDossier
import com.example.model.Garment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AtelierViewModel(application: Application) : AndroidViewModel(application) {

    val repository = FirebaseAtelierRepository(application.applicationContext)

    val currentUser = repository.currentUser
    val clients = repository.clients
    val isSyncing = repository.isSyncing
    val syncMessage = repository.syncMessage

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedGarment = MutableStateFlow<Garment?>(null)
    val selectedGarment: StateFlow<Garment?> = _selectedGarment.asStateFlow()

    private val _activeResearchGarment = MutableStateFlow<Garment?>(null)
    val activeResearchGarment: StateFlow<Garment?> = _activeResearchGarment.asStateFlow()

    fun setResearchGarment(garment: Garment?) {
        _activeResearchGarment.value = garment
        if (garment != null) {
            _toastMessage.value = "'${garment.title}' linked to AI Research Hub!"
        }
    }

    private val prefs = application.getSharedPreferences("atelier_prefs", android.content.Context.MODE_PRIVATE)

    private val _userName = MutableStateFlow(
        prefs.getString("atelier_user_name", "Haute Couture Designer") ?: "Haute Couture Designer"
    )
    val userName: StateFlow<String> = _userName.asStateFlow()

    fun setUserName(newName: String) {
        val trimmed = newName.trim().ifBlank { "Haute Couture Designer" }
        _userName.value = trimmed
        prefs.edit().putString("atelier_user_name", trimmed).apply()
        _toastMessage.value = "Profile name updated to '$trimmed'"
    }

    private val _currentThemePreset = MutableStateFlow(com.example.ui.theme.AtelierThemePreset.CYBER_LUMINESCENCE)
    val currentThemePreset: StateFlow<com.example.ui.theme.AtelierThemePreset> = _currentThemePreset.asStateFlow()

    private val _customAccentColor = MutableStateFlow<androidx.compose.ui.graphics.Color?>(null)
    val customAccentColor: StateFlow<androidx.compose.ui.graphics.Color?> = _customAccentColor.asStateFlow()

    fun setThemePreset(preset: com.example.ui.theme.AtelierThemePreset) {
        _currentThemePreset.value = preset
        _toastMessage.value = "Atelier theme switched to '${preset.title}'"
    }

    fun setCustomAccentColor(color: androidx.compose.ui.graphics.Color?) {
        _customAccentColor.value = color
        if (color != null) {
            _toastMessage.value = "Custom accent color applied"
        }
    }

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private val _isUploadingImage = MutableStateFlow(false)
    val isUploadingImage: StateFlow<Boolean> = _isUploadingImage.asStateFlow()

    // Seamless Merged Feed combining Public /garments & Private /users/{uid}/custom_garments,
    // with reactive category filtering & keyword search
    val filteredFeed: StateFlow<List<Garment>> = combine(
        repository.mergedFeed,
        _searchQuery,
        _selectedCategory
    ) { garments, query, category ->
        garments.filter { garment ->
            val matchesCategory = when (category) {
                "All" -> true
                "My Creations" -> garment.isCustom
                else -> garment.category.equals(category, ignoreCase = true)
            }
            val matchesQuery = if (query.isBlank()) {
                true
            } else {
                garment.title.contains(query, ignoreCase = true) ||
                garment.description.contains(query, ignoreCase = true) ||
                garment.fabric.contains(query, ignoreCase = true) ||
                garment.designer.contains(query, ignoreCase = true)
            }
            matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customGarmentsOnly: StateFlow<List<Garment>> = repository.customGarments

    val categories: StateFlow<List<String>> = repository.categories

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun addCategory(categoryName: String, selectAfterAdding: Boolean = true): Boolean {
        val trimmed = categoryName.trim()
        if (trimmed.isBlank()) {
            _toastMessage.value = "Category name cannot be blank."
            return false
        }
        val success = repository.addCategory(trimmed)
        if (success) {
            _toastMessage.value = "Category '$trimmed' added to collection!"
            if (selectAfterAdding) {
                _selectedCategory.value = trimmed
            }
        } else {
            _toastMessage.value = "Category '$trimmed' already exists."
        }
        return success
    }

    fun selectGarment(garment: Garment?) {
        _selectedGarment.value = garment
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun isUserOwner(garment: Garment): Boolean {
        return garment.isCustom && (garment.createdBy == repository.getCurrentUid() || garment.createdBy != null)
    }

    fun createCustomGarment(
        title: String,
        category: String,
        description: String,
        imageUri: Uri?,
        fabric: String,
        silhouette: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isUploadingImage.value = true
            try {
                var finalImageUrl = "https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=800&q=80"
                if (imageUri != null) {
                    finalImageUrl = repository.processAndUploadImage(imageUri)
                }

                val result = repository.addCustomGarment(
                    title = title.ifBlank { "Bespoke Couture Creation" },
                    category = category,
                    description = description,
                    imageUrl = finalImageUrl,
                    fabric = fabric.ifBlank { "Silk & Micro-fiber" },
                    silhouette = silhouette.ifBlank { "Sculptural" }
                )

                if (result.isSuccess) {
                    _toastMessage.value = "Custom garment created & saved to your private cloud!"
                    onSuccess()
                } else {
                    _toastMessage.value = "Notice: ${result.exceptionOrNull()?.message}"
                }
            } finally {
                _isUploadingImage.value = false
            }
        }
    }

    fun updateCustomGarment(garment: Garment, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val result = repository.updateCustomGarment(garment)
            if (result.isSuccess) {
                _toastMessage.value = "Garment updated in private cloud!"
                _selectedGarment.value = garment
                onSuccess()
            } else {
                _toastMessage.value = "Cannot edit: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun deleteCustomGarment(garment: Garment, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = repository.deleteCustomGarment(garment.id)
            if (result.isSuccess) {
                _toastMessage.value = "Garment deleted from private cloud."
                _selectedGarment.value = null
                onSuccess()
            } else {
                _toastMessage.value = "Cannot delete: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun addClient(
        name: String,
        vipTier: String,
        email: String,
        phone: String,
        measurements: String,
        fittingNotes: String,
        photoUri: Uri?,
        fittingDate: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            var photoUrl: String? = null
            if (photoUri != null) {
                photoUrl = repository.processAndUploadImage(photoUri)
            }
            val client = ClientDossier(
                name = name.ifBlank { "Private Client" },
                vipTier = vipTier,
                email = email,
                phone = phone,
                measurements = measurements.ifBlank { "Standard Sizing" },
                fittingNotes = fittingNotes,
                photoUrl = photoUrl,
                nextFittingDate = fittingDate.ifBlank { "Dec 1, 2026" }
            )
            repository.addClient(client)
            _toastMessage.value = "Client dossier saved to /users/{uid}/clients"
            onSuccess()
        }
    }

    fun deleteClient(clientId: String) {
        viewModelScope.launch {
            repository.deleteClient(clientId)
            _toastMessage.value = "Client dossier removed."
        }
    }

    fun addGarmentToClient(client: ClientDossier, garment: Garment, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val updatedGarments = if (client.chosenGarments.any { it.id == garment.id }) {
                client.chosenGarments.map { if (it.id == garment.id) garment else it }
            } else {
                client.chosenGarments + garment
            }
            val updatedClient = client.copy(chosenGarments = updatedGarments)
            repository.addClient(updatedClient)
            _toastMessage.value = "Added '${garment.title}' to ${client.name}'s Dossier!"
            onSuccess()
        }
    }

    fun removeGarmentFromClient(client: ClientDossier, garmentId: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val updatedGarments = client.chosenGarments.filter { it.id != garmentId }
            val updatedClient = client.copy(chosenGarments = updatedGarments)
            repository.addClient(updatedClient)
            _toastMessage.value = "Garment removed from ${client.name}'s Dossier."
            onSuccess()
        }
    }

    fun updateClientDossier(client: ClientDossier, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            repository.addClient(client)
            _toastMessage.value = "Client dossier for ${client.name} updated!"
            onSuccess()
        }
    }

    fun seedPublicCatalog() {
        repository.seedPublicGarments()
    }

    override fun onCleared() {
        super.onCleared()
        repository.cleanup()
    }
}
