package com.example.model

data class FabricItem(
    val id: String = "",
    val name: String = "",
    val category: String = "Silk & Satin",
    val composition: String = "100% Pure Silk",
    val weightGsm: Int = 120,
    val weaveType: String = "Satin Weave",
    val drapability: String = "Fluid & Flowing",
    val stretchPercentage: String = "Non-Stretch",
    val bestForGarments: List<String> = listOf("Evening Gowns", "Couture Blouses", "Bridal"),
    val careInstructions: String = "Dry Clean Only",
    val isCustom: Boolean = false
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "name" to name,
        "category" to category,
        "composition" to composition,
        "weightGsm" to weightGsm,
        "weaveType" to weaveType,
        "drapability" to drapability,
        "stretchPercentage" to stretchPercentage,
        "bestForGarments" to bestForGarments,
        "careInstructions" to careInstructions,
        "isCustom" to isCustom
    )

    companion object {
        fun fromMap(id: String, map: Map<String, Any?>): FabricItem {
            @Suppress("UNCHECKED_CAST")
            val bestList = (map["bestForGarments"] as? List<String>) ?: listOf("Haute Couture")

            return FabricItem(
                id = id,
                name = map["name"] as? String ?: "Silk Charmeuse",
                category = map["category"] as? String ?: "Silk & Satin",
                composition = map["composition"] as? String ?: "100% Silk",
                weightGsm = (map["weightGsm"] as? Number)?.toInt() ?: 120,
                weaveType = map["weaveType"] as? String ?: "Plain",
                drapability = map["drapability"] as? String ?: "Soft",
                stretchPercentage = map["stretchPercentage"] as? String ?: "None",
                bestForGarments = bestList,
                careInstructions = map["careInstructions"] as? String ?: "Dry Clean",
                isCustom = (map["isCustom"] as? Boolean) ?: false
            )
        }
    }
}
