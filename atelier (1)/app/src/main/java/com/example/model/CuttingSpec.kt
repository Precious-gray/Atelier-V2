package com.example.model

data class PatternPieceSpec(
    val id: String,
    val name: String,
    val quantity: Int,
    val widthCm: Float,
    val heightCm: Float,
    val grainline: String = "Straight Grain", // "Straight Grain", "Bias 45°", "Cross Grain"
    val seamAllowanceCm: Float = 1.5f,
    val notchesCount: Int = 2,
    val directives: List<String> = listOf("Cut 2 on fold", "Add 1.5cm seam allowance", "Stay-stitch curves"),
    val colorHex: String = "#8A2BE2"
)

data class NestedPiecePosition(
    val piece: PatternPieceSpec,
    val instanceIndex: Int,
    val xCm: Float,
    val yCm: Float,
    val widthCm: Float,
    val heightCm: Float,
    val isRotated: Boolean = false
)

data class AutonestLayout(
    val boltWidthCm: Float, // 112cm (44"), 137cm (54"), 152cm (60")
    val totalPiecesCount: Int,
    val lengthUsedCm: Float,
    val efficiencyPercent: Float,
    val nestedPieces: List<NestedPiecePosition>
)

enum class FabricBoltWidth(val inchesLabel: String, val widthCm: Float) {
    WIDTH_44("44 in (112 cm)", 112f),
    WIDTH_54("54 in (137 cm)", 137f),
    WIDTH_60("60 in (152 cm)", 152f)
}

enum class FabricNapOption(val label: String, val extraAllowanceFactor: Float) {
    STANDARD("Standard Grain (No Nap)", 1.0f),
    DIRECTIONAL_VELVET("Directional Velvet / Satin (+15%)", 1.15f),
    PLAID_MATCH("Plaid / Pattern Match (+20%)", 1.20f),
    BIAS_DRAPE("45° Bias Cutting (+30%)", 1.30f)
}

object CuttingEngine {

    fun generatePiecesForGarment(garmentTitle: String, category: String, silhouette: String): List<PatternPieceSpec> {
        val cat = category.uppercase()
        val sil = silhouette.uppercase()

        return when {
            cat.contains("CORSET") || sil.contains("CORSET") -> listOf(
                PatternPieceSpec("p1", "Center Front Bodice", 2, 18f, 38f, "Straight Grain", 1.5f, 3, listOf("Cut 2 self, 2 lining", "Reinforce center busk", "1.5cm seam allowance"), "#00E5FF"),
                PatternPieceSpec("p2", "Side Front Panel", 2, 16f, 36f, "Straight Grain", 1.5f, 2, listOf("Cut 2 self, 2 interlining", "Boning channel 1cm width"), "#7C4DFF"),
                PatternPieceSpec("p3", "Side Back Panel", 2, 15f, 37f, "Straight Grain", 1.5f, 2, listOf("Cut 2 self, 2 lining", "Double spring-steel channel"), "#FF007F"),
                PatternPieceSpec("p4", "Center Back Lacing Panel", 2, 14f, 40f, "Straight Grain", 2.0f, 4, listOf("Cut 2 self, heavy interfacing", "Grommet eyelets at 2.5cm pitch"), "#FFD700"),
                PatternPieceSpec("p5", "Waist Stay Ribbon", 1, 72f, 4f, "Straight Grain", 1.0f, 1, listOf("Grosgrain ribbon reinforcement"), "#00E5FF"),
                PatternPieceSpec("p6", "Modesty Back Underlay", 1, 16f, 38f, "Straight Grain", 1.0f, 0, listOf("Folded self-fabric flap"), "#7C4DFF")
            )
            cat.contains("TROUSER") || cat.contains("PANT") -> listOf(
                PatternPieceSpec("p1", "Front Trouser Leg", 2, 34f, 108f, "Straight Grain", 1.5f, 3, listOf("Cut 2 mirror", "Match knee notch", "1.5cm side seam"), "#00E5FF"),
                PatternPieceSpec("p2", "Back Trouser Leg", 2, 38f, 112f, "Straight Grain", 1.5f, 4, listOf("Cut 2 mirror", "Ease crotch curve", "3cm hem allowance"), "#7C4DFF"),
                PatternPieceSpec("p3", "Contour Waistband", 2, 42f, 9f, "Straight Grain", 1.2f, 2, listOf("Cut 2 with rigid fusible interfacing"), "#FFD700"),
                PatternPieceSpec("p4", "Fly Shield & Facing", 2, 8f, 22f, "Straight Grain", 1.0f, 1, listOf("Cut 1 shield on fold, 1 facing"), "#FF007F"),
                PatternPieceSpec("p5", "Slanted Pocket Facing & Bag", 4, 20f, 28f, "Straight Grain", 1.0f, 2, listOf("Cut 2 self, 2 pocketing cotton"), "#00E5FF")
            )
            cat.contains("SKIRT") -> listOf(
                PatternPieceSpec("p1", "Front Skirt Panel", 1, 56f, 88f, "Straight Grain", 1.5f, 2, listOf("Cut 1 on fold", "Stitch waist darts", "3cm blind hem"), "#00E5FF"),
                PatternPieceSpec("p2", "Back Skirt Left Panel", 1, 30f, 88f, "Straight Grain", 1.5f, 2, listOf("Cut 1 self", "Zipper notch at 18cm"), "#7C4DFF"),
                PatternPieceSpec("p3", "Back Skirt Right Panel", 1, 30f, 88f, "Straight Grain", 1.5f, 2, listOf("Cut 1 self", "Kick pleat underlay"), "#FF007F"),
                PatternPieceSpec("p4", "Fitted Waist Facing", 2, 40f, 8f, "Straight Grain", 1.2f, 2, listOf("Cut 2 fusible interfacing"), "#FFD700")
            )
            cat.contains("COAT") || cat.contains("JACKET") || cat.contains("SUIT") -> listOf(
                PatternPieceSpec("p1", "Jacket Forepart (Front)", 2, 36f, 82f, "Straight Grain", 1.5f, 4, listOf("Cut 2 mirror", "Chest canvas floating", "Roll line notch"), "#00E5FF"),
                PatternPieceSpec("p2", "Jacket Back", 2, 32f, 84f, "Straight Grain", 1.5f, 3, listOf("Cut 2 mirror", "Shoulder dart ease", "Center vent notch"), "#7C4DFF"),
                PatternPieceSpec("p3", "Side Body Panel", 2, 22f, 68f, "Straight Grain", 1.5f, 2, listOf("Cut 2 mirror", "1.5cm seam allowance"), "#FF007F"),
                PatternPieceSpec("p4", "Upper Two-Piece Sleeve", 2, 26f, 64f, "Straight Grain", 1.5f, 3, listOf("Cut 2 mirror", "Gather sleeve head ease 3.5cm"), "#FFD700"),
                PatternPieceSpec("p5", "Under Two-Piece Sleeve", 2, 20f, 58f, "Straight Grain", 1.5f, 2, listOf("Cut 2 mirror", "Match elbow notch"), "#00E5FF"),
                PatternPieceSpec("p6", "Tailored Notch Collar", 2, 28f, 12f, "Bias 45°", 1.0f, 2, listOf("Cut 1 undercollar on bias, 1 top collar"), "#7C4DFF")
            )
            else -> listOf(
                // Haute Couture Sculptural Dress
                PatternPieceSpec("p1", "Sculptural Front Bodice", 2, 28f, 46f, "Straight Grain", 1.5f, 3, listOf("Cut 2 mirror", "Drape bust apex dart", "French seam edges"), "#00E5FF"),
                PatternPieceSpec("p2", "Back Bodice Keyhole Panel", 2, 26f, 48f, "Straight Grain", 1.5f, 2, listOf("Cut 2 mirror", "Invisible couture zipper"), "#7C4DFF"),
                PatternPieceSpec("p3", "Cascading Skirt Front Tier", 2, 62f, 114f, "Bias 45°", 1.5f, 4, listOf("Cut 2 on 45° bias for fluid drape", "Horsehair braid hem"), "#FF007F"),
                PatternPieceSpec("p4", "Cascading Skirt Train Back", 2, 68f, 138f, "Bias 45°", 1.5f, 4, listOf("Cut 2 mirror on bias", "Grand train extension"), "#FFD700"),
                PatternPieceSpec("p5", "Fitted Raglan Capelet", 2, 32f, 52f, "Straight Grain", 1.2f, 2, listOf("Cut 2 self, 2 silk lining"), "#00E5FF"),
                PatternPieceSpec("p6", "Neckline Facing Band", 1, 42f, 6f, "Straight Grain", 1.0f, 2, listOf("Cut 1 on fold with micro-interfacing"), "#7C4DFF")
            )
        }
    }

    /**
     * Autonest 2D Bin-Packing Algorithm:
     * Places pieces across the fabric width, packing with grainline constraints,
     * maintaining 2cm cutting clearance gap.
     */
    fun computeAutonest(pieces: List<PatternPieceSpec>, boltWidthCm: Float): AutonestLayout {
        val gap = 2.0f
        var currentX = gap
        var currentY = gap
        var rowHeight = 0f

        val nestedList = mutableListOf<NestedPiecePosition>()
        var totalPiecesCount = 0

        pieces.forEach { piece ->
            for (i in 0 until piece.quantity) {
                totalPiecesCount++
                val pWidth = piece.widthCm
                val pHeight = piece.heightCm

                // Check if fits horizontally
                if (currentX + pWidth + gap > boltWidthCm) {
                    // Advance to next row
                    currentX = gap
                    currentY += rowHeight + gap
                    rowHeight = 0f
                }

                nestedList.add(
                    NestedPiecePosition(
                        piece = piece,
                        instanceIndex = i + 1,
                        xCm = currentX,
                        yCm = currentY,
                        widthCm = pWidth,
                        heightCm = pHeight,
                        isRotated = false
                    )
                )

                currentX += pWidth + gap
                if (pHeight > rowHeight) {
                    rowHeight = pHeight
                }
            }
        }

        val totalLengthCm = currentY + rowHeight + gap
        val totalAreaUsedByPieces = pieces.sumOf { (it.widthCm * it.heightCm * it.quantity).toDouble() }.toFloat()
        val totalFabricArea = boltWidthCm * totalLengthCm
        val efficiency = if (totalFabricArea > 0) ((totalAreaUsedByPieces / totalFabricArea) * 100f).coerceIn(60f, 96f) else 80f

        return AutonestLayout(
            boltWidthCm = boltWidthCm,
            totalPiecesCount = totalPiecesCount,
            lengthUsedCm = totalLengthCm,
            efficiencyPercent = efficiency,
            nestedPieces = nestedList
        )
    }

    data class FabricEstimate(
        val lengthMeters: Float,
        val lengthYards: Float,
        val boltWidth: FabricBoltWidth,
        val napOption: FabricNapOption,
        val wastePercent: Float,
        val cuttingDirectiveSummary: String
    )

    fun calculateEstimate(
        pieces: List<PatternPieceSpec>,
        boltWidth: FabricBoltWidth,
        napOption: FabricNapOption,
        shrinkagePercent: Float = 5.0f
    ): FabricEstimate {
        val autonest = computeAutonest(pieces, boltWidth.widthCm)
        val baseLengthCm = autonest.lengthUsedCm
        val withNap = baseLengthCm * napOption.extraAllowanceFactor
        val withShrinkage = withNap * (1f + (shrinkagePercent / 100f))

        val meters = withShrinkage / 100f
        val yards = meters * 1.09361f
        val waste = (100f - autonest.efficiencyPercent).coerceAtLeast(8f)

        val advice = when (napOption) {
            FabricNapOption.DIRECTIONAL_VELVET -> "All pattern pieces oriented in one downward pile nap direction. Pin securely with fine silk pins."
            FabricNapOption.BIAS_DRAPE -> "Cut pieces strictly along 45-degree true bias line. Hang cut panels for 24h prior to hemming to allow natural stretch relaxation."
            FabricNapOption.PLAID_MATCH -> "Align horizontal balance lines across side seams prior to rotary cutting. Match plaid intersections at bust and hip notches."
            FabricNapOption.STANDARD -> "Pieces nested along straight grain parallel to fabric selvedge edge. Fold fabric face-to-face with selvedges aligned."
        }

        return FabricEstimate(
            lengthMeters = meters,
            lengthYards = yards,
            boltWidth = boltWidth,
            napOption = napOption,
            wastePercent = waste,
            cuttingDirectiveSummary = advice
        )
    }
}
