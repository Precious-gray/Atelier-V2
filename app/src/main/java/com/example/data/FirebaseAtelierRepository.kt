package com.example.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.example.model.ClientDossier
import com.example.model.Garment
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirebaseAtelierRepository(private val context: Context) {

    // Safely acquire Firebase instances without throwing if google-services.json is pending
    private val auth: FirebaseAuth? by lazy {
        try {
            if (FirebaseApp.getApps(context).isNotEmpty()) {
                FirebaseAuth.getInstance()
            } else {
                null
            }
        } catch (e: Throwable) {
            Log.w("AtelierRepo", "Firebase Auth unavailable: ${e.message}")
            null
        }
    }

    private val firestore: FirebaseFirestore? by lazy {
        try {
            if (FirebaseApp.getApps(context).isNotEmpty()) {
                FirebaseFirestore.getInstance()
            } else {
                null
            }
        } catch (e: Throwable) {
            Log.w("AtelierRepo", "Firebase Firestore unavailable: ${e.message}")
            null
        }
    }

    private val storage: FirebaseStorage? by lazy {
        try {
            if (FirebaseApp.getApps(context).isNotEmpty()) {
                FirebaseStorage.getInstance()
            } else {
                null
            }
        } catch (e: Throwable) {
            Log.w("AtelierRepo", "Firebase Storage unavailable: ${e.message}")
            null
        }
    }

    private val scope = CoroutineScope(Dispatchers.IO)

    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _publicGarments = MutableStateFlow<List<Garment>>(BaselineGarments.getInitialBaseline())
    val publicGarments: StateFlow<List<Garment>> = _publicGarments.asStateFlow()

    private val _customGarments = MutableStateFlow<List<Garment>>(emptyList())
    val customGarments: StateFlow<List<Garment>> = _customGarments.asStateFlow()

    private val _mergedFeed = MutableStateFlow<List<Garment>>(BaselineGarments.getInitialBaseline())
    val mergedFeed: StateFlow<List<Garment>> = _mergedFeed.asStateFlow()

    private val _clients = MutableStateFlow<List<ClientDossier>>(getDefaultClients())
    val clients: StateFlow<List<ClientDossier>> = _clients.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _syncMessage = MutableStateFlow<String?>(null)
    val syncMessage: StateFlow<String?> = _syncMessage.asStateFlow()

    private val defaultCategories = listOf(
        "Haute Couture",
        "Evening Wear",
        "Tailoring",
        "Avant-Garde",
        "Cyberpunk",
        "Corsetry",
        "Bridal",
        "Accessories"
    )

    private val _categories = MutableStateFlow<List<String>>(loadInitialCategories())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    private var categoriesListener: ListenerRegistration? = null
    private var customGarmentsListener: ListenerRegistration? = null
    private var publicGarmentsListener: ListenerRegistration? = null
    private var clientsListener: ListenerRegistration? = null

    init {
        initializeAuth()
    }

    private fun loadInitialCategories(): List<String> {
        return try {
            val prefs = context.getSharedPreferences("atelier_prefs", Context.MODE_PRIVATE)
            val saved = prefs.getStringSet("custom_categories", emptySet()) ?: emptySet()
            (defaultCategories + saved).distinct()
        } catch (e: Throwable) {
            defaultCategories
        }
    }

    private fun initializeAuth() {
        try {
            val fbAuth = auth
            if (fbAuth != null) {
                val user = fbAuth.currentUser
                if (user != null) {
                    _currentUser.value = user
                    setupFirestoreListeners(user.uid)
                } else {
                    fbAuth.signInAnonymously()
                        .addOnSuccessListener { authResult ->
                            val newUser = authResult.user
                            _currentUser.value = newUser
                            newUser?.let { setupFirestoreListeners(it.uid) }
                        }
                        .addOnFailureListener { error ->
                            Log.w("AtelierRepo", "Anonymous Auth note: ${error.message}. Running in local guest mode.")
                            recomputeMergedFeed()
                        }
                }
            } else {
                Log.d("AtelierRepo", "Firebase not initialized yet. Using secure local guest profile.")
                recomputeMergedFeed()
            }
        } catch (e: Throwable) {
            Log.w("AtelierRepo", "Safe auth notice: ${e.message}")
            recomputeMergedFeed()
        }
    }

    fun getCurrentUid(): String {
        return _currentUser.value?.uid ?: getPersistentGuestUid()
    }

    private fun getPersistentGuestUid(): String {
        val prefs = context.getSharedPreferences("atelier_prefs", Context.MODE_PRIVATE)
        var uid = prefs.getString("guest_uid", null)
        if (uid.isNullOrEmpty()) {
            uid = "anon_" + UUID.randomUUID().toString().replace("-", "").take(10)
            prefs.edit().putString("guest_uid", uid).apply()
        }
        return uid
    }

    private fun setupFirestoreListeners(uid: String) {
        val fs = firestore ?: return

        // 1. Listen to Public Baseline /garments
        try {
            publicGarmentsListener?.remove()
            publicGarmentsListener = fs.collection("garments")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w("AtelierRepo", "Public garments listener note: ${error.message}")
                        return@addSnapshotListener
                    }
                    if (snapshot != null && !snapshot.isEmpty) {
                        val items = snapshot.documents.mapNotNull { doc ->
                            doc.data?.let { Garment.fromMap(doc.id, it) }
                        }
                        _publicGarments.value = items
                    } else {
                        _publicGarments.value = BaselineGarments.getInitialBaseline()
                    }
                    recomputeMergedFeed()
                }
        } catch (e: Throwable) {
            Log.w("AtelierRepo", "Firestore public collection init note: ${e.message}")
        }

        // 2. Listen to Private /users/{uid}/custom_garments
        try {
            customGarmentsListener?.remove()
            customGarmentsListener = fs.collection("users")
                .document(uid)
                .collection("custom_garments")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w("AtelierRepo", "Custom garments listener note: ${error.message}")
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val items = snapshot.documents.mapNotNull { doc ->
                            doc.data?.let { Garment.fromMap(doc.id, it).copy(isCustom = true, createdBy = uid) }
                        }
                        _customGarments.value = items
                    }
                    recomputeMergedFeed()
                }
        } catch (e: Throwable) {
            Log.w("AtelierRepo", "Firestore custom garments init note: ${e.message}")
        }

        // 3. Listen to Private /users/{uid}/clients
        try {
            clientsListener?.remove()
            clientsListener = fs.collection("users")
                .document(uid)
                .collection("clients")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w("AtelierRepo", "Clients listener note: ${error.message}")
                        return@addSnapshotListener
                    }
                    if (snapshot != null && !snapshot.isEmpty) {
                        val items = snapshot.documents.mapNotNull { doc ->
                            doc.data?.let { ClientDossier.fromMap(doc.id, it) }
                        }
                        _clients.value = items
                    }
                }
        } catch (e: Throwable) {
            Log.w("AtelierRepo", "Firestore clients init note: ${e.message}")
        }

        // 4. Listen to Private /users/{uid}/categories
        try {
            categoriesListener?.remove()
            categoriesListener = fs.collection("users")
                .document(uid)
                .collection("categories")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w("AtelierRepo", "Categories listener note: ${error.message}")
                        return@addSnapshotListener
                    }
                    if (snapshot != null && !snapshot.isEmpty) {
                        val remoteCats = snapshot.documents.mapNotNull { it.getString("name") }
                        if (remoteCats.isNotEmpty()) {
                            val merged = (_categories.value + remoteCats).distinct()
                            _categories.value = merged
                            try {
                                val prefs = context.getSharedPreferences("atelier_prefs", Context.MODE_PRIVATE)
                                val saved = (prefs.getStringSet("custom_categories", emptySet()) ?: emptySet()).toMutableSet()
                                saved.addAll(remoteCats)
                                prefs.edit().putStringSet("custom_categories", saved).apply()
                            } catch (e: Throwable) {
                                Log.w("AtelierRepo", "Prefs save note: ${e.message}")
                            }
                        }
                    }
                }
        } catch (e: Throwable) {
            Log.w("AtelierRepo", "Firestore categories init note: ${e.message}")
        }
    }

    private fun recomputeMergedFeed() {
        val publicList = _publicGarments.value
        val customList = _customGarments.value
        _mergedFeed.value = customList + publicList
    }

    private fun getDefaultClients(): List<ClientDossier> = listOf(
        ClientDossier(
            id = "sample_client_1",
            name = "Contessa Vespera",
            vipTier = "Runway Exclusive",
            email = "vespera@venicecouture.it",
            phone = "+39 041 555 9821",
            measurements = "Bust: 34in | Waist: 25in | Hips: 36in | Height: 5'11\"",
            fittingNotes = "Requires elongated train for the Met Gala entrance. Prefers matte silk lining to reduce runway friction.",
            nextFittingDate = "Nov 12, 2026"
        ),
        ClientDossier(
            id = "sample_client_2",
            name = "Lord Julian Drake",
            vipTier = "Haute VIP",
            email = "jdrake@mayfair.co.uk",
            phone = "+44 20 7946 0912",
            measurements = "Chest: 42in | Waist: 32in | Inseam: 34in | Neck: 16in",
            fittingNotes = "Neon Cyan Tuxedo fitting. Narrow shoulders requested with sculpted padding.",
            nextFittingDate = "Nov 20, 2026"
        )
    )

    // Secure URI handling & Firebase Storage upload
    suspend fun processAndUploadImage(imageUri: Uri): String {
        try {
            context.contentResolver.takePersistableUriPermission(
                imageUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } catch (e: Exception) {
            Log.d("AtelierRepo", "Persistable permission notice: ${e.message}")
        }

        val uid = getCurrentUid()
        val filename = "images/users/$uid/${UUID.randomUUID()}.jpg"

        val st = storage
        if (st == null) {
            return imageUri.toString()
        }

        return try {
            val storageRef = st.reference.child(filename)
            val uploadTask = storageRef.putFile(imageUri).await()
            val downloadUrl = uploadTask.storage.downloadUrl.await().toString()
            downloadUrl
        } catch (e: Exception) {
            Log.w("AtelierRepo", "Firebase Storage upload fallback (using persistable local URI): ${e.message}")
            imageUri.toString()
        }
    }

    // Add private custom garment
    suspend fun addCustomGarment(
        title: String,
        category: String,
        description: String,
        imageUrl: String,
        fabric: String,
        silhouette: String
    ): Result<Garment> {
        return try {
            val uid = getCurrentUid()
            val id = UUID.randomUUID().toString()
            val garment = Garment(
                id = id,
                title = title,
                category = category,
                description = description,
                imageUrl = imageUrl,
                fabric = fabric,
                designer = "You (Private Studio)",
                silhouette = silhouette,
                isCustom = true,
                createdBy = uid,
                createdAt = System.currentTimeMillis()
            )

            val fs = firestore
            if (fs != null) {
                try {
                    fs.collection("users")
                        .document(uid)
                        .collection("custom_garments")
                        .document(id)
                        .set(garment.toMap())
                        .await()
                } catch (e: Exception) {
                    Log.w("AtelierRepo", "Firestore set notice: ${e.message}")
                }
            }

            val updated = listOf(garment) + _customGarments.value.filter { it.id != id }
            _customGarments.value = updated
            recomputeMergedFeed()

            Result.success(garment)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Edit private custom garment (Strict rule: baseline garments CANNOT be edited)
    suspend fun updateCustomGarment(garment: Garment): Result<Unit> {
        val uid = getCurrentUid()
        if (!garment.isCustom || garment.createdBy != uid) {
            return Result.failure(IllegalAccessException("Cannot edit global baseline garments."))
        }

        return try {
            val fs = firestore
            if (fs != null) {
                try {
                    fs.collection("users")
                        .document(uid)
                        .collection("custom_garments")
                        .document(garment.id)
                        .update(garment.toMap())
                        .await()
                } catch (e: Exception) {
                    Log.w("AtelierRepo", "Firestore update notice: ${e.message}")
                }
            }

            _customGarments.value = _customGarments.value.map {
                if (it.id == garment.id) garment else it
            }
            recomputeMergedFeed()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Delete private custom garment (Strict rule: baseline garments CANNOT be deleted)
    suspend fun deleteCustomGarment(garmentId: String): Result<Unit> {
        val uid = getCurrentUid()
        val target = _customGarments.value.find { it.id == garmentId }
        if (target == null || !target.isCustom || target.createdBy != uid) {
            return Result.failure(IllegalAccessException("Baseline garments cannot be deleted."))
        }

        return try {
            val fs = firestore
            if (fs != null) {
                try {
                    fs.collection("users")
                        .document(uid)
                        .collection("custom_garments")
                        .document(garmentId)
                        .delete()
                        .await()
                } catch (e: Exception) {
                    Log.w("AtelierRepo", "Firestore delete notice: ${e.message}")
                }
            }

            _customGarments.value = _customGarments.value.filter { it.id != garmentId }
            recomputeMergedFeed()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Client Dossiers CRUD
    suspend fun addClient(client: ClientDossier): Result<Unit> {
        val uid = getCurrentUid()
        val docId = if (client.id.isBlank()) UUID.randomUUID().toString() else client.id
        val newClient = client.copy(id = docId, updatedAt = System.currentTimeMillis())

        return try {
            val fs = firestore
            if (fs != null) {
                try {
                    fs.collection("users")
                        .document(uid)
                        .collection("clients")
                        .document(docId)
                        .set(newClient.toMap())
                        .await()
                } catch (e: Exception) {
                    Log.w("AtelierRepo", "Firestore client set notice: ${e.message}")
                }
            }

            _clients.value = listOf(newClient) + _clients.value.filter { it.id != docId }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteClient(clientId: String): Result<Unit> {
        val uid = getCurrentUid()
        return try {
            val fs = firestore
            if (fs != null) {
                try {
                    fs.collection("users")
                        .document(uid)
                        .collection("clients")
                        .document(clientId)
                        .delete()
                        .await()
                } catch (e: Exception) {
                    Log.w("AtelierRepo", "Firestore client delete notice: ${e.message}")
                }
            }
            _clients.value = _clients.value.filter { it.id != clientId }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Seed baseline garments to Firestore /garments collection
    fun seedPublicGarments() {
        val fs = firestore
        if (fs == null) {
            _syncMessage.value = "Connect google-services.json to sync live Firestore."
            return
        }

        scope.launch {
            _isSyncing.value = true
            _syncMessage.value = "Pushing baseline garments to public /garments..."
            try {
                val batch = fs.batch()
                val baseline = BaselineGarments.getInitialBaseline()
                for (item in baseline) {
                    val docRef = fs.collection("garments").document(item.id)
                    batch.set(docRef, item.toMap())
                }
                batch.commit().await()
                _syncMessage.value = "Successfully synchronized baseline garments!"
            } catch (e: Exception) {
                _syncMessage.value = "Sync note: ${e.localizedMessage}"
            } finally {
                _isSyncing.value = false
            }
        }
    }

    fun addCategory(categoryName: String): Boolean {
        val trimmed = categoryName.trim()
        if (trimmed.isBlank()) return false
        val current = _categories.value
        if (current.any { it.equals(trimmed, ignoreCase = true) }) {
            return false
        }
        val updated = current + trimmed
        _categories.value = updated

        // Persist locally
        try {
            val prefs = context.getSharedPreferences("atelier_prefs", Context.MODE_PRIVATE)
            val saved = (prefs.getStringSet("custom_categories", emptySet()) ?: emptySet()).toMutableSet()
            saved.add(trimmed)
            prefs.edit().putStringSet("custom_categories", saved).apply()
        } catch (e: Throwable) {
            Log.w("AtelierRepo", "Prefs write note: ${e.message}")
        }

        // Sync to Firestore if available
        val fs = firestore
        val uid = getCurrentUid()
        if (fs != null) {
            scope.launch {
                try {
                    val docId = trimmed.lowercase().replace("\\s+".toRegex(), "_")
                    fs.collection("users")
                        .document(uid)
                        .collection("categories")
                        .document(docId)
                        .set(
                            mapOf(
                                "name" to trimmed,
                                "createdAt" to System.currentTimeMillis()
                            )
                        )
                        .await()
                } catch (e: Exception) {
                    Log.w("AtelierRepo", "Firestore category sync note: ${e.message}")
                }
            }
        }
        return true
    }

    fun cleanup() {
        categoriesListener?.remove()
        customGarmentsListener?.remove()
        publicGarmentsListener?.remove()
        clientsListener?.remove()
    }
}
