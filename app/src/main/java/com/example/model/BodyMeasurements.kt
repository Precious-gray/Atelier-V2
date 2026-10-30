package com.example.model

enum class MeasurementUnit {
    CM,
    INCHES
}

enum class BodyMeasurementCategory(val title: String) {
    HEAD_NECK("Head & Neck"),
    TORSO_BUST("Torso, Bust & Chest"),
    WAIST_ABDOMEN("Waist & Midriff"),
    HIPS_PELVIS("Hips & Pelvic Frame"),
    BACK_SHOULDER("Back & Shoulders"),
    ARMS_WRISTS("Arms & Sleeves"),
    LEGS_INSEAM("Legs & Inseams"),
    VERTICAL_HEIGHTS("Vertical Couture Heights")
}

data class MeasurementDefinition(
    val id: String,
    val name: String,
    val category: BodyMeasurementCategory,
    val defaultCm: Float,
    val anatomicalDescription: String,
    val standardGarmentTypes: List<String> = emptyList()
) {
    fun toInches(cm: Float): Float = cm / 2.54f
    fun toCm(inches: Float): Float = inches * 2.54f
}

data class ClientMeasurementValue(
    val definitionId: String,
    val valueCm: Float,
    val isCustomAdded: Boolean = false
)

object MasterBodyMeasurements {
    val ALL_49: List<MeasurementDefinition> = listOf(
        // 1-3: Head & Neck
        MeasurementDefinition(
            id = "head_circ",
            name = "Head Circumference",
            category = BodyMeasurementCategory.HEAD_NECK,
            defaultCm = 56f,
            anatomicalDescription = "Circumference measured around widest point of cranium above brow line.",
            standardGarmentTypes = listOf("Hood", "Cowl", "Avant-Garde Headpiece")
        ),
        MeasurementDefinition(
            id = "neck_base_circ",
            name = "Neck Base Circumference",
            category = BodyMeasurementCategory.HEAD_NECK,
            defaultCm = 37f,
            anatomicalDescription = "Circumference at base of neck resting on clavicle and C7 cervical vertebra.",
            standardGarmentTypes = listOf("Haute Couture", "Bodice", "Tailored Suit", "Evening Gown", "Coat")
        ),
        MeasurementDefinition(
            id = "mid_neck_circ",
            name = "Mid-Neck Circumference",
            category = BodyMeasurementCategory.HEAD_NECK,
            defaultCm = 34f,
            anatomicalDescription = "Around slender midpoint of neck for mandarin collars and high chokers.",
            standardGarmentTypes = listOf("High Collar", "Corset", "Avant-Garde")
        ),

        // 4-10: Torso, Bust & Chest
        MeasurementDefinition(
            id = "high_bust",
            name = "Upper Chest / High Bust",
            category = BodyMeasurementCategory.TORSO_BUST,
            defaultCm = 84f,
            anatomicalDescription = "Horizontal plane directly across upper chest under armpits above breast curve.",
            standardGarmentTypes = listOf("Haute Couture", "Bodice", "Evening Gown", "Corset")
        ),
        MeasurementDefinition(
            id = "full_bust",
            name = "Full Bust Circumference",
            category = BodyMeasurementCategory.TORSO_BUST,
            defaultCm = 88f,
            anatomicalDescription = "Circumference taken across fullest projection of bust apexes with tape level.",
            standardGarmentTypes = listOf("Haute Couture", "Bodice", "Evening Gown", "Tailored Suit", "Dress", "Corset")
        ),
        MeasurementDefinition(
            id = "underbust",
            name = "Underbust / Ribcage",
            category = BodyMeasurementCategory.TORSO_BUST,
            defaultCm = 73f,
            anatomicalDescription = "Snug horizontal plane right beneath the mammary fold.",
            standardGarmentTypes = listOf("Bodice", "Corset", "Evening Gown", "Empire Waist")
        ),
        MeasurementDefinition(
            id = "bust_span",
            name = "Bust Span (Apex to Apex)",
            category = BodyMeasurementCategory.TORSO_BUST,
            defaultCm = 18f,
            anatomicalDescription = "Direct horizontal distance between right and left bust apex points.",
            standardGarmentTypes = listOf("Haute Couture", "Bodice", "Corset", "Princess Seam")
        ),
        MeasurementDefinition(
            id = "shoulder_to_apex",
            name = "Shoulder to Bust Apex",
            category = BodyMeasurementCategory.TORSO_BUST,
            defaultCm = 25f,
            anatomicalDescription = "Distance from base of neck along shoulder slope down to bust point.",
            standardGarmentTypes = listOf("Haute Couture", "Bodice", "Evening Gown", "Corset")
        ),
        MeasurementDefinition(
            id = "nape_to_apex",
            name = "Nape to Bust Apex",
            category = BodyMeasurementCategory.TORSO_BUST,
            defaultCm = 34f,
            anatomicalDescription = "From prominent C7 vertebra curving over shoulder to apex.",
            standardGarmentTypes = listOf("Haute Couture", "Tailoring")
        ),
        MeasurementDefinition(
            id = "front_midriff",
            name = "Front Midriff Span",
            category = BodyMeasurementCategory.TORSO_BUST,
            defaultCm = 29f,
            anatomicalDescription = "Horizontal span across front torso midway between underbust and waist.",
            standardGarmentTypes = listOf("Corset", "Bespoke Vest")
        ),

        // 11-14: Waist & Midriff
        MeasurementDefinition(
            id = "natural_waist",
            name = "Natural Waist Circumference",
            category = BodyMeasurementCategory.WAIST_ABDOMEN,
            defaultCm = 66f,
            anatomicalDescription = "Narrowest point of natural torso, approximately 2.5cm above naval.",
            standardGarmentTypes = listOf("Haute Couture", "Bodice", "Evening Gown", "Tailored Suit", "Skirt", "Trousers", "Corset")
        ),
        MeasurementDefinition(
            id = "high_waist",
            name = "High Waist (Lower Ribs)",
            category = BodyMeasurementCategory.WAIST_ABDOMEN,
            defaultCm = 70f,
            anatomicalDescription = "Torso circumference taken across lowest floating rib level.",
            standardGarmentTypes = listOf("Corset", "High-Rise Trousers")
        ),
        MeasurementDefinition(
            id = "low_waist",
            name = "Low Waist / Waistband Level",
            category = BodyMeasurementCategory.WAIST_ABDOMEN,
            defaultCm = 74f,
            anatomicalDescription = "Where modern trouser/skirt waistband settles below natural curve.",
            standardGarmentTypes = listOf("Trousers", "Low-Rise Skirt", "Tailored Suit")
        ),
        MeasurementDefinition(
            id = "abdomen_circ",
            name = "Abdominal Extension",
            category = BodyMeasurementCategory.WAIST_ABDOMEN,
            defaultCm = 78f,
            anatomicalDescription = "Circumference across fullest projection of lower stomach.",
            standardGarmentTypes = listOf("Tailored Suit", "Trousers", "Fitted Sheath")
        ),

        // 15-17: Hips & Pelvic Frame
        MeasurementDefinition(
            id = "high_hip",
            name = "High Hip (Pelvic Crest)",
            category = BodyMeasurementCategory.HIPS_PELVIS,
            defaultCm = 84f,
            anatomicalDescription = "Taken horizontally across upper iliac crest hip bone level.",
            standardGarmentTypes = listOf("Haute Couture", "Skirt", "Trousers", "Evening Gown", "Corset")
        ),
        MeasurementDefinition(
            id = "low_hip",
            name = "Full Low Hip (Seat)",
            category = BodyMeasurementCategory.HIPS_PELVIS,
            defaultCm = 94f,
            anatomicalDescription = "Fullest projection of buttocks/seat with feet together.",
            standardGarmentTypes = listOf("Haute Couture", "Skirt", "Trousers", "Evening Gown", "Tailored Suit", "Coat")
        ),
        MeasurementDefinition(
            id = "waist_to_hip_depth",
            name = "Waist to Low Hip Depth",
            category = BodyMeasurementCategory.HIPS_PELVIS,
            defaultCm = 20f,
            anatomicalDescription = "Vertical distance from natural waist to fullest low hip line.",
            standardGarmentTypes = listOf("Haute Couture", "Skirt", "Trousers", "Evening Gown")
        ),

        // 18-24: Back & Shoulders
        MeasurementDefinition(
            id = "across_shoulder",
            name = "Across Shoulder (Biacromial)",
            category = BodyMeasurementCategory.BACK_SHOULDER,
            defaultCm = 39f,
            anatomicalDescription = "From bony acromion edge of left shoulder to right shoulder.",
            standardGarmentTypes = listOf("Haute Couture", "Tailored Suit", "Coat", "Bodice", "Jacket")
        ),
        MeasurementDefinition(
            id = "across_back",
            name = "Across Back Width",
            category = BodyMeasurementCategory.BACK_SHOULDER,
            defaultCm = 35f,
            anatomicalDescription = "Taken across center scapular level between posterior armhole folds.",
            standardGarmentTypes = listOf("Tailored Suit", "Coat", "Bodice", "Jacket")
        ),
        MeasurementDefinition(
            id = "across_front_chest",
            name = "Across Front Chest Width",
            category = BodyMeasurementCategory.BACK_SHOULDER,
            defaultCm = 33f,
            anatomicalDescription = "Taken across anterior chest between anterior armhole folds.",
            standardGarmentTypes = listOf("Tailored Suit", "Coat", "Bodice", "Haute Couture")
        ),
        MeasurementDefinition(
            id = "center_front_length",
            name = "Center Front Length (Neck to Waist)",
            category = BodyMeasurementCategory.BACK_SHOULDER,
            defaultCm = 36f,
            anatomicalDescription = "From jugular hollow at neck base down to natural waistline.",
            standardGarmentTypes = listOf("Bodice", "Haute Couture", "Evening Gown", "Corset")
        ),
        MeasurementDefinition(
            id = "center_back_length",
            name = "Center Back Length (Nape to Waist)",
            category = BodyMeasurementCategory.BACK_SHOULDER,
            defaultCm = 41f,
            anatomicalDescription = "From prominent C7 cervical vertebra down along spine to natural waist.",
            standardGarmentTypes = listOf("Bodice", "Tailored Suit", "Coat", "Evening Gown", "Haute Couture")
        ),
        MeasurementDefinition(
            id = "shoulder_slope",
            name = "Shoulder Slope Drop",
            category = BodyMeasurementCategory.BACK_SHOULDER,
            defaultCm = 5.5f,
            anatomicalDescription = "Vertical angle drop from horizontal neck base plane to shoulder tip.",
            standardGarmentTypes = listOf("Tailored Suit", "Coat", "Avant-Garde")
        ),
        MeasurementDefinition(
            id = "shoulder_seam_length",
            name = "Shoulder Seam Length",
            category = BodyMeasurementCategory.BACK_SHOULDER,
            defaultCm = 12.5f,
            anatomicalDescription = "From base of neck along top ridge to acromion shoulder tip.",
            standardGarmentTypes = listOf("Tailored Suit", "Coat", "Bodice", "Haute Couture")
        ),

        // 25-32: Arms & Sleeves
        MeasurementDefinition(
            id = "armscye_circ",
            name = "Armscye (Armhole) Circumference",
            category = BodyMeasurementCategory.ARMS_WRISTS,
            defaultCm = 42f,
            anatomicalDescription = "Circumference through armpit over shoulder tip bone.",
            standardGarmentTypes = listOf("Tailored Suit", "Sleeve", "Coat", "Bodice")
        ),
        MeasurementDefinition(
            id = "armscye_depth",
            name = "Armscye Depth (Nape to Armpit)",
            category = BodyMeasurementCategory.ARMS_WRISTS,
            defaultCm = 21f,
            anatomicalDescription = "Vertical distance from C7 nape down to armpit horizontal level.",
            standardGarmentTypes = listOf("Tailored Suit", "Coat", "Bodice", "Haute Couture")
        ),
        MeasurementDefinition(
            id = "total_arm_length",
            name = "Total Arm Length (Shoulder to Wrist)",
            category = BodyMeasurementCategory.ARMS_WRISTS,
            defaultCm = 59f,
            anatomicalDescription = "From acromion bone down outer elbow to wrist bone with arm slightly bent.",
            standardGarmentTypes = listOf("Tailored Suit", "Sleeve", "Coat", "Jacket")
        ),
        MeasurementDefinition(
            id = "bicep_circ",
            name = "Upper Arm Bicep Circumference",
            category = BodyMeasurementCategory.ARMS_WRISTS,
            defaultCm = 27f,
            anatomicalDescription = "Circumference at fullest part of relaxed bicep muscle.",
            standardGarmentTypes = listOf("Tailored Suit", "Sleeve", "Coat", "Jacket")
        ),
        MeasurementDefinition(
            id = "elbow_circ",
            name = "Elbow Circumference (Bent 90°)",
            category = BodyMeasurementCategory.ARMS_WRISTS,
            defaultCm = 25f,
            anatomicalDescription = "Taken directly over olecranon bone with forearm bent at 90 degrees.",
            standardGarmentTypes = listOf("Tailored Suit", "Sleeve", "Coat")
        ),
        MeasurementDefinition(
            id = "forearm_circ",
            name = "Forearm Circumference",
            category = BodyMeasurementCategory.ARMS_WRISTS,
            defaultCm = 23f,
            anatomicalDescription = "Circumference at fullest flesh of upper forearm.",
            standardGarmentTypes = listOf("Sleeve", "Fitted Glove")
        ),
        MeasurementDefinition(
            id = "wrist_circ",
            name = "Wrist Circumference",
            category = BodyMeasurementCategory.ARMS_WRISTS,
            defaultCm = 15.5f,
            anatomicalDescription = "Around styloid process bones of wrist.",
            standardGarmentTypes = listOf("Tailored Suit", "Sleeve", "Coat", "Fitted Cuff")
        ),
        MeasurementDefinition(
            id = "underarm_sleeve_length",
            name = "Underarm Sleeve Inseam",
            category = BodyMeasurementCategory.ARMS_WRISTS,
            defaultCm = 46f,
            anatomicalDescription = "From armpit fold straight down to inner wrist crease.",
            standardGarmentTypes = listOf("Sleeve", "Coat", "Tailored Suit")
        ),

        // 33-43: Legs & Inseams
        MeasurementDefinition(
            id = "waist_to_knee",
            name = "Waist to Knee Outseam",
            category = BodyMeasurementCategory.LEGS_INSEAM,
            defaultCm = 58f,
            anatomicalDescription = "From natural waistline down lateral side of leg to knee crease level.",
            standardGarmentTypes = listOf("Skirt", "Trousers", "Evening Gown")
        ),
        MeasurementDefinition(
            id = "waist_to_floor",
            name = "Waist to Floor (Full Outseam)",
            category = BodyMeasurementCategory.LEGS_INSEAM,
            defaultCm = 104f,
            anatomicalDescription = "From natural waistline down outside contour to floor barefoot.",
            standardGarmentTypes = listOf("Trousers", "Evening Gown", "Haute Couture")
        ),
        MeasurementDefinition(
            id = "crotch_depth",
            name = "Crotch Depth (Seated Rise)",
            category = BodyMeasurementCategory.LEGS_INSEAM,
            defaultCm = 26f,
            anatomicalDescription = "Seated upright on flat stool, vertical distance from waist to stool surface.",
            standardGarmentTypes = listOf("Trousers", "Jumpsuit", "Tailored Suit")
        ),
        MeasurementDefinition(
            id = "total_crotch_length",
            name = "Total Crotch Length (Straddle)",
            category = BodyMeasurementCategory.LEGS_INSEAM,
            defaultCm = 68f,
            anatomicalDescription = "From front waist through crotch fork up to center back waist.",
            standardGarmentTypes = listOf("Trousers", "Jumpsuit", "Bodysuit")
        ),
        MeasurementDefinition(
            id = "leg_inseam",
            name = "Leg Inseam (Crotch to Floor)",
            category = BodyMeasurementCategory.LEGS_INSEAM,
            defaultCm = 79f,
            anatomicalDescription = "From highest groin fork down medial leg straight to floor.",
            standardGarmentTypes = listOf("Trousers", "Jumpsuit", "Tailored Suit")
        ),
        MeasurementDefinition(
            id = "upper_thigh_circ",
            name = "Upper Thigh Circumference",
            category = BodyMeasurementCategory.LEGS_INSEAM,
            defaultCm = 54f,
            anatomicalDescription = "Circumference at highest junction of leg and gluteal fold.",
            standardGarmentTypes = listOf("Trousers", "Fitted Sheath")
        ),
        MeasurementDefinition(
            id = "mid_thigh_circ",
            name = "Mid-Thigh Circumference",
            category = BodyMeasurementCategory.LEGS_INSEAM,
            defaultCm = 48f,
            anatomicalDescription = "Midpoint circumference between crotch and patella.",
            standardGarmentTypes = listOf("Trousers", "Mini Skirt")
        ),
        MeasurementDefinition(
            id = "knee_circ",
            name = "Knee Circumference (Straight)",
            category = BodyMeasurementCategory.LEGS_INSEAM,
            defaultCm = 36f,
            anatomicalDescription = "Circumference over mid-patella with leg straight.",
            standardGarmentTypes = listOf("Trousers", "Mermaid Gown")
        ),
        MeasurementDefinition(
            id = "below_knee_circ",
            name = "Below-Knee Circumference",
            category = BodyMeasurementCategory.LEGS_INSEAM,
            defaultCm = 32f,
            anatomicalDescription = "At smallest indentation directly under patella.",
            standardGarmentTypes = listOf("Trousers", "Bespoke Bootcut")
        ),
        MeasurementDefinition(
            id = "calf_circ",
            name = "Calf Circumference",
            category = BodyMeasurementCategory.LEGS_INSEAM,
            defaultCm = 35f,
            anatomicalDescription = "Circumference around maximum projection of calf muscle.",
            standardGarmentTypes = listOf("Trousers", "Pencil Skirt")
        ),
        MeasurementDefinition(
            id = "ankle_circ",
            name = "Ankle Circumference",
            category = BodyMeasurementCategory.LEGS_INSEAM,
            defaultCm = 21f,
            anatomicalDescription = "Circumference directly above ankle malleolus bones.",
            standardGarmentTypes = listOf("Trousers", "Legging", "Ankle Hem")
        ),

        // 44-49: Vertical Couture Heights
        MeasurementDefinition(
            id = "nape_to_floor",
            name = "Nape to Finished Floor (Dress Length)",
            category = BodyMeasurementCategory.VERTICAL_HEIGHTS,
            defaultCm = 142f,
            anatomicalDescription = "From C7 cervical nape down along back contour to floor barefoot.",
            standardGarmentTypes = listOf("Evening Gown", "Haute Couture", "Cape", "Coat")
        ),
        MeasurementDefinition(
            id = "shoulder_to_hem",
            name = "Shoulder Tip to Finished Hem",
            category = BodyMeasurementCategory.VERTICAL_HEIGHTS,
            defaultCm = 138f,
            anatomicalDescription = "From acromion shoulder tip straight down over bust apex to hemline.",
            standardGarmentTypes = listOf("Evening Gown", "Haute Couture", "Dress")
        ),
        MeasurementDefinition(
            id = "waist_to_hem",
            name = "Waist to Finished Hem (Skirt)",
            category = BodyMeasurementCategory.VERTICAL_HEIGHTS,
            defaultCm = 85f,
            anatomicalDescription = "From natural waistline to intended skirt or gown bottom edge.",
            standardGarmentTypes = listOf("Skirt", "Evening Gown", "Haute Couture")
        ),
        MeasurementDefinition(
            id = "back_neck_to_elbow",
            name = "Back Neck to Elbow (CB to Sleeve)",
            category = BodyMeasurementCategory.VERTICAL_HEIGHTS,
            defaultCm = 46f,
            anatomicalDescription = "From C7 nape along shoulder line and down outer bent arm to elbow.",
            standardGarmentTypes = listOf("Tailored Suit", "Raglan Sleeve", "Kimono")
        ),
        MeasurementDefinition(
            id = "side_seam_length",
            name = "Side Seam Length (Armpit to Waist)",
            category = BodyMeasurementCategory.VERTICAL_HEIGHTS,
            defaultCm = 19.5f,
            anatomicalDescription = "From lower axillary crease straight down flank to natural waist.",
            standardGarmentTypes = listOf("Bodice", "Corset", "Haute Couture")
        ),
        MeasurementDefinition(
            id = "high_hip_to_hem",
            name = "High Hip to Finished Hem",
            category = BodyMeasurementCategory.VERTICAL_HEIGHTS,
            defaultCm = 75f,
            anatomicalDescription = "From pelvic high hip crest down outer contour to hem.",
            standardGarmentTypes = listOf("Skirt", "Low-Rise Dress")
        )
    )

    fun getRequiredIdsForGarment(category: String, silhouette: String): Set<String> {
        val catUpper = category.uppercase()
        val silUpper = silhouette.uppercase()

        val baseKeys = mutableSetOf(
            "full_bust",
            "natural_waist",
            "low_hip"
        )

        when {
            catUpper.contains("CORSET") || silUpper.contains("CORSET") || silUpper.contains("CINCH") -> {
                baseKeys.addAll(listOf(
                    "underbust",
                    "high_bust",
                    "bust_span",
                    "shoulder_to_apex",
                    "high_waist",
                    "front_midriff",
                    "high_hip",
                    "center_front_length",
                    "center_back_length",
                    "side_seam_length"
                ))
            }
            catUpper.contains("TROUSER") || catUpper.contains("PANT") || silUpper.contains("PANT") || silUpper.contains("TAILORED TROUSER") -> {
                baseKeys.addAll(listOf(
                    "low_waist",
                    "high_hip",
                    "low_hip",
                    "waist_to_hip_depth",
                    "crotch_depth",
                    "total_crotch_length",
                    "leg_inseam",
                    "waist_to_floor",
                    "upper_thigh_circ",
                    "knee_circ",
                    "ankle_circ"
                ))
            }
            catUpper.contains("SKIRT") || silUpper.contains("SKIRT") || silUpper.contains("MERMAID") -> {
                baseKeys.addAll(listOf(
                    "high_hip",
                    "low_hip",
                    "waist_to_hip_depth",
                    "waist_to_knee",
                    "waist_to_hem"
                ))
            }
            catUpper.contains("COAT") || catUpper.contains("JACKET") || catUpper.contains("SUIT") || silUpper.contains("TAILORED") -> {
                baseKeys.addAll(listOf(
                    "neck_base_circ",
                    "across_shoulder",
                    "across_back",
                    "across_front_chest",
                    "center_back_length",
                    "armscye_circ",
                    "armscye_depth",
                    "total_arm_length",
                    "bicep_circ",
                    "wrist_circ"
                ))
            }
            else -> {
                // Ballgown / Haute Couture dress default
                baseKeys.addAll(listOf(
                    "neck_base_circ",
                    "high_bust",
                    "underbust",
                    "across_shoulder",
                    "shoulder_to_apex",
                    "center_front_length",
                    "center_back_length",
                    "high_hip",
                    "waist_to_floor",
                    "nape_to_floor"
                ))
            }
        }
        return baseKeys
    }
}
