package com.example.model

data class Garment(
    val id: String = "",
    val title: String = "",
    val category: String = "Haute Couture",
    val description: String = "",
    val imageUrl: String = "",
    val fabric: String = "",
    val designer: String = "Atelier Studio",
    val silhouette: String = "Sculptural",
    val isCustom: Boolean = false,
    val createdBy: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val likes: Int = 0,
    val customMeasurements: Map<String, Float> = emptyMap()
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "title" to title,
        "category" to category,
        "description" to description,
        "imageUrl" to imageUrl,
        "fabric" to fabric,
        "designer" to designer,
        "silhouette" to silhouette,
        "isCustom" to isCustom,
        "createdBy" to createdBy,
        "createdAt" to createdAt,
        "likes" to likes,
        "customMeasurements" to customMeasurements
    )

    companion object {
        fun fromMap(id: String, map: Map<String, Any?>): Garment {
            @Suppress("UNCHECKED_CAST")
            val rawMeasurements = map["customMeasurements"] as? Map<String, Any?>
            val parsedMeasurements = rawMeasurements?.mapNotNull { (k, v) ->
                val num = (v as? Number)?.toFloat()
                if (num != null) k to num else null
            }?.toMap() ?: emptyMap()

            return Garment(
                id = id,
                title = map["title"] as? String ?: "Untitled Garment",
                category = map["category"] as? String ?: "Haute Couture",
                description = map["description"] as? String ?: "",
                imageUrl = map["imageUrl"] as? String ?: "",
                fabric = map["fabric"] as? String ?: "Silk & Tech-Mesh",
                designer = map["designer"] as? String ?: "Atelier Studio",
                silhouette = map["silhouette"] as? String ?: "Sculptural",
                isCustom = (map["isCustom"] as? Boolean) ?: false,
                createdBy = map["createdBy"] as? String,
                createdAt = (map["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                likes = (map["likes"] as? Number)?.toInt() ?: 0,
                customMeasurements = parsedMeasurements
            )
        }
    }
}
