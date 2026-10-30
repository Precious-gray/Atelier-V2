package com.example.model

data class ClientDossier(
    val id: String = "",
    val name: String = "",
    val vipTier: String = "Haute VIP",
    val email: String = "",
    val phone: String = "",
    val measurements: String = "Bust: 34in | Waist: 26in | Hips: 36in",
    val fittingNotes: String = "",
    val photoUrl: String? = null,
    val nextFittingDate: String = "Nov 15, 2026",
    val updatedAt: Long = System.currentTimeMillis(),
    val chosenGarments: List<Garment> = emptyList()
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "name" to name,
        "vipTier" to vipTier,
        "email" to email,
        "phone" to phone,
        "measurements" to measurements,
        "fittingNotes" to fittingNotes,
        "photoUrl" to photoUrl,
        "nextFittingDate" to nextFittingDate,
        "updatedAt" to updatedAt,
        "chosenGarments" to chosenGarments.map { it.toMap() }
    )

    companion object {
        fun fromMap(id: String, map: Map<String, Any?>): ClientDossier {
            @Suppress("UNCHECKED_CAST")
            val rawGarmentList = map["chosenGarments"] as? List<Map<String, Any?>>
            val parsedGarments = rawGarmentList?.mapNotNull { gMap ->
                val gId = gMap["id"] as? String ?: return@mapNotNull null
                Garment.fromMap(gId, gMap)
            } ?: emptyList()

            return ClientDossier(
                id = id,
                name = map["name"] as? String ?: "Anonymous Client",
                vipTier = map["vipTier"] as? String ?: "Haute VIP",
                email = map["email"] as? String ?: "",
                phone = map["phone"] as? String ?: "",
                measurements = map["measurements"] as? String ?: "Standard Sizing",
                fittingNotes = map["fittingNotes"] as? String ?: "",
                photoUrl = map["photoUrl"] as? String,
                nextFittingDate = map["nextFittingDate"] as? String ?: "TBD",
                updatedAt = (map["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                chosenGarments = parsedGarments
            )
        }
    }
}
