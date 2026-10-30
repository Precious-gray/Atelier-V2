package com.example.model

import java.net.URLEncoder

enum class AiResearchTarget(val label: String) {
    GARMENT_INSPIRATION("Garment & Silhouette Concept"),
    TEXTILE_PATTERN("Textile, Fabric & Pattern Drafting")
}

enum class AiPlatform(
    val displayName: String,
    val iconKey: String,
    val category: String,
    val baseUrl: String,
    val promptFormatDescription: String
) {
    GOOGLE_IMAGES("Google Images", "G", "Visual Search", "https://www.google.com/search?tbm=isch&q=", "High-fashion editorial runway photography query"),
    PINTEREST("Pinterest", "PIN", "Visual Moodboard", "https://www.pinterest.com/search/pins/?q=", "Curated couture aesthetic, atelier moodboard tags"),
    YOUTUBE("YouTube", "YT", "Video & Draping", "https://www.youtube.com/results?search_query=", "Couture draping tutorial, pattern drafting, runway breakdown"),
    CLAUDE("Claude (Anthropic)", "CLD", "Technical Reasoning", "https://claude.ai", "In-depth couture garment construction specification and tailoring guidelines"),
    MS_COPILOT("MS Copilot", "MS", "Creative Assistant", "https://copilot.microsoft.com/?q=", "Fashion tech brief, fabrication breakdown, client presentation notes"),
    ADOBE_FIREFLY("Adobe Firefly", "FLY", "Generative Creative", "https://firefly.adobe.com", "Commercial photo-real fashion shoot with studio lighting parameters"),
    CHATGPT("ChatGPT", "GPT", "Conversational AI", "https://chatgpt.com/?q=", "Couture pattern drafting directives, ease calculations, bill of materials"),
    GEMINI("Gemini", "GEM", "Multimodal AI", "https://gemini.google.com", "Historical haute couture analysis, material science of optic fibers & lamé"),
    CANVA_MAGIC("Canva Magic", "CNV", "Layout & Presentation", "https://www.canva.com", "Lookbook moodboard collage, editorial fashion magazine layout prompt"),
    IDEOGRAM("Ideogram", "IDG", "Typography & Design", "https://ideogram.ai", "Graphic garment lettering, atelier brand insignia, typography rendering"),
    KREA_AI("Krea AI", "KRA", "Real-Time Generation", "https://www.krea.ai", "Real-time canvas generation of futuristic garment drapery and silhouette lines"),
    PATTERNED_AI("Patterned.ai", "PAT", "Seamless Patterns", "https://www.patterned.ai", "Seamless repeating 2D textile print, vector jacquard tile, high-res fabric repeat"),
    LEONARDO_AI("Leonardo.ai", "LEO", "High-Craft Stylization", "https://leonardo.ai", "Photorealistic haute couture, volumetric lighting, cyber-couture luxury"),
    MIDJOURNEY("Midjourney", "MJ", "Cinematic Diffusion", "https://discord.com", "Cinematic 8K editorial fashion shoot, dramatic runway lighting --ar 9:16 --v 6.1"),
    RECRAFT_AI("Recraft.ai", "REC", "Vector & Flat Sketch", "https://www.recraft.ai", "Vector fashion flats, 2D technical specification drawing, CAD line art"),
    PATRON_IA("PatronIA", "PIA", "AI Pattern Drafting", "https://patronia.com", "Parametric 2D pattern sloper generation, grainline & dart optimization"),
    AICLO("Aiclo", "AIC", "3D Digital Prototyping", "https://aiclo.co", "3D cloth physics draping, tension stress map on avatar, digital twin fit")
}

data class GeneratedAiPrompt(
    val platform: AiPlatform,
    val target: AiResearchTarget,
    val formattedPrompt: String,
    val searchUrl: String
)

object AiResearchEngine {

    val GARMENT_STYLE_TAGS = listOf(
        "Bioluminescent Optical Fibers",
        "Liquid Silver Lamé",
        "Sculptural Avant-Garde",
        "Micro-Pneumatic Cinch Corset",
        "Fluid Bias Drape",
        "Architectural Shoulders",
        "Savile Row Obsidian Tuxedo",
        "Kinetic Origami Cape"
    )

    val TEXTILE_STYLE_TAGS = listOf(
        "Cybernetic Jacquard Weave",
        "Seamless Monogram Damask",
        "Thermochromic Velvet Repeat",
        "Metallic Bullion Embroidery",
        "Geometric Origami Pleating",
        "Bioluminescent Micro-Mesh",
        "Fractal Houndstooth Vector"
    )

    fun buildPrompt(
        platform: AiPlatform,
        target: AiResearchTarget,
        garmentTitle: String,
        silhouette: String,
        fabric: String,
        selectedTags: Set<String>
    ): GeneratedAiPrompt {
        val tagsString = if (selectedTags.isNotEmpty()) selectedTags.joinToString(", ") else "Couture craftsmanship"

        val prompt = when (target) {
            AiResearchTarget.GARMENT_INSPIRATION -> {
                when (platform) {
                    AiPlatform.MIDJOURNEY ->
                        "/imagine prompt: Haute couture high-fashion runway editorial of a $silhouette, crafted from $fabric and $tagsString. Volumetric moody atelier lighting, cyber-couture detailing, photographed by Nick Knight, 8k resolution, photorealistic, intricate drapery --ar 9:16 --v 6.1 --stylize 350"

                    AiPlatform.LEONARDO_AI ->
                        "Cinematic high-fashion editorial of $garmentTitle, featuring $silhouette silhouette, fabricated in $fabric, $tagsString. Leonardo Diffusion XL, dynamic runway lighting, sharp focus, hyper-detailed texture."

                    AiPlatform.RECRAFT_AI ->
                        "Technical vector flat sketch of a $silhouette $garmentTitle with front and back views, clean black outlines on white background, accurate seam allowances, darts, topstitching details, professional fashion CAD drawing."

                    AiPlatform.PATRON_IA, AiPlatform.AICLO ->
                        "Parametric 2D/3D couture drafting blueprint for $garmentTitle ($silhouette). Include dart rotation, ease allowance, $fabric drape physics, zero-waste cutting layout, and tension stress points across bust apex and waist cinch."

                    AiPlatform.IDEOGRAM ->
                        "Avant-garde fashion editorial titled '$garmentTitle' in minimalist luxury serif typography at top, featuring a dramatic model wearing $silhouette made of $fabric with $tagsString, cinematic lighting, 4k."

                    AiPlatform.PATTERNED_AI ->
                        "Seamless repeating textile print for haute couture lining: intricate luxury motifs inspired by $garmentTitle and $tagsString, seamless 2D vector tile, tileable fabric repeat."

                    AiPlatform.CLAUDE, AiPlatform.CHATGPT, AiPlatform.GEMINI, AiPlatform.MS_COPILOT ->
                        "Provide a master dressmaker's couture tech pack and tailoring specification for '$garmentTitle'. Silhouette: $silhouette. Primary fabric: $fabric. Aesthetic accents: $tagsString. Include step-by-step drafting directives, balance lines, grainlines, boning placement, and seam finishing techniques."

                    AiPlatform.YOUTUBE ->
                        "Couture draping tutorial $silhouette $fabric pattern drafting atelier techniques"

                    AiPlatform.PINTEREST ->
                        "Haute couture runway $silhouette $fabric atelier moodboard $tagsString"

                    AiPlatform.GOOGLE_IMAGES ->
                        "Haute couture $silhouette $fabric runway editorial high fashion photography"

                    AiPlatform.ADOBE_FIREFLY ->
                        "Studio photograph of model in avant-garde $silhouette constructed from $fabric and $tagsString, editorial lighting, medium full shot, clean studio backdrop, 35mm lens."

                    AiPlatform.CANVA_MAGIC ->
                        "High fashion atelier moodboard presentation collage for $garmentTitle, luxury cyber-couture color palette, fabric swatch textures of $fabric."

                    AiPlatform.KREA_AI ->
                        "Futuristic high-fashion runway model wearing $silhouette $garmentTitle made from $fabric, fluid light refraction, cyber gold and neon violet accents."
                }
            }

            AiResearchTarget.TEXTILE_PATTERN -> {
                when (platform) {
                    AiPlatform.PATTERNED_AI ->
                        "Seamless repeating luxury textile pattern tile for couture gown: $tagsString, high contrast geometric micro-filigree, tileable horizontally and vertically, 4k vector precision."

                    AiPlatform.RECRAFT_AI ->
                        "Vector seamless pattern swatch of $tagsString for $fabric fabrication, clean line work, CMYK print ready, fashion apparel textile design."

                    AiPlatform.MIDJOURNEY ->
                        "/imagine prompt: Top-down macro flat photograph of seamless luxury couture textile swatch, $tagsString, interwoven $fabric, intricate metallic threads, tactile texture, soft directional studio light --tile --v 6.1"

                    AiPlatform.LEONARDO_AI ->
                        "Seamless textile texture of $tagsString, fine weave of $fabric, hyper-detailed surface fibers, studio macro shot, repeating tileable pattern."

                    AiPlatform.PATRON_IA, AiPlatform.AICLO ->
                        "Pattern piece grading and grainline layout for $fabric with $tagsString. Metric layout for 150cm bolt width, bias angle alignment, pattern matching across center back seam."

                    AiPlatform.GOOGLE_IMAGES ->
                        "Seamless luxury fabric texture swatch $tagsString $fabric high resolution"

                    AiPlatform.PINTEREST ->
                        "Couture fabric manipulation pleating embroidery textile design $tagsString"

                    AiPlatform.YOUTUBE ->
                        "Couture fabric manipulation techniques pleating tambours embroidery tutorial"

                    else ->
                        "Generate a detailed technical specification for manufacturing a bespoke luxury textile: $tagsString, substrate: $fabric. Detail thread count, yarn denier, weaving technique (jacquard/brocade), and dye fixation."
                }
            }
        }

        val encodedPrompt = try {
            URLEncoder.encode(prompt, "UTF-8")
        } catch (e: Exception) {
            prompt.replace(" ", "+")
        }

        val finalUrl = when {
            platform.baseUrl.contains("q=") -> "${platform.baseUrl}$encodedPrompt"
            platform.baseUrl.contains("search_query=") -> "${platform.baseUrl}$encodedPrompt"
            else -> platform.baseUrl
        }

        return GeneratedAiPrompt(
            platform = platform,
            target = target,
            formattedPrompt = prompt,
            searchUrl = finalUrl
        )
    }
}
