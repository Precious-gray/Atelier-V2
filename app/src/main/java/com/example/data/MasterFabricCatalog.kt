package com.example.data

import com.example.model.FabricItem

object MasterFabricCatalog {

    val FABRIC_CATEGORIES = listOf(
        "All",
        "Silk & Satin",
        "Lace & Sheer",
        "Wool & Tailoring",
        "Velvet & Heavy",
        "Cotton & Linen",
        "Leather & Specialty",
        "Synthetics & Tech Mesh",
        "Knits & Stretch"
    )

    val FABRICS_200: List<FabricItem> by lazy {
        generate200Fabrics()
    }

    private fun generate200Fabrics(): List<FabricItem> {
        val list = mutableListOf<FabricItem>()

        // 1. Silk & Satin (30 items)
        val silks = listOf(
            "Silk Charmeuse", "Silk Dupioni", "Silk Georgette", "Silk Organza", "Silk Crepe de Chine",
            "Duchesse Satin", "Silk Habotai", "Silk Chiffon", "Silk Taffeta", "Silk Mikado",
            "Silk Barathea", "Silk Shantung", "Silk Twill", "Silk Radzimir", "Silk Zibeline",
            "Silk Gazar", "Silk Foulard", "Silk Faille", "Silk Matelassé", "Silk Damask",
            "Silk Brocade", "Silk Velvet", "Silk Noil", "Silk Ottoman", "Silk Crêpe Back Satin",
            "Silk Slipper Satin", "Silk Satin Face Organza", "Silk Jacquard", "Silk Ottoman Faille", "Silk Lamé"
        )
        silks.forEachIndexed { idx, name ->
            list.add(
                FabricItem(
                    id = "fab_silk_$idx",
                    name = name,
                    category = "Silk & Satin",
                    composition = "100% Cultivated Mulberry Silk",
                    weightGsm = 80 + (idx * 5) % 120,
                    weaveType = if (name.contains("Satin")) "Satin Weave" else if (name.contains("Taffeta")) "Plain Rib" else "Couture Weave",
                    drapability = if (name.contains("Organza") || name.contains("Gazar")) "Crisp & Sculptural" else "Fluid & Liquid",
                    stretchPercentage = "Non-Stretch",
                    bestForGarments = listOf("Evening Gowns", "Haute Couture Corsets", "Bridal Train"),
                    careInstructions = "Specialist Dry Clean Only"
                )
            )
        }

        // 2. Lace & Sheer (25 items)
        val laces = listOf(
            "Chantilly Lace", "Guipure Lace", "Alençon Lace", "Venise Lace", "Corded Lace",
            "Beaded Metallic Lace", "Laser-Cut Floral Lace", "Point d'Esprit Tulle", "Illusion Mesh",
            "Broderie Anglaise", "English Net", "Chantilly Metallic Tulle", "Macramé Lace",
            "Valenciennes Lace", "Lyon Lace", "Filet Lace", "Needlepoint Lace", "Soutache Lace",
            "Appliqué Ribbon Lace", "Sequined Tulle", "Cutwork Organza", "Embroidered Silk Net",
            "Re-Embroidered Alençon", "Guipure Ribbon Motif", "Textured Honeycomb Mesh"
        )
        laces.forEachIndexed { idx, name ->
            list.add(
                FabricItem(
                    id = "fab_lace_$idx",
                    name = name,
                    category = "Lace & Sheer",
                    composition = "70% Silk Net, 30% Rayon / Metallic Thread",
                    weightGsm = 45 + (idx * 4) % 80,
                    weaveType = "Openwork Lace Lattice",
                    drapability = "Ultra Soft & Ethereal",
                    stretchPercentage = "Light Mechanical",
                    bestForGarments = listOf("Overlay Sleeves", "Couture Veil", "Bridal Bodice"),
                    careInstructions = "Delicate Hand Wash / Dry Clean"
                )
            )
        }

        // 3. Wool & Tailoring (30 items)
        val wools = listOf(
            "Super 150s Merino Wool", "Savile Row Barathea", "Harris Tweed", "Cashmere Broadcloth",
            "Wool Gabardine", "Venetian Wool", "Donegal Tweed", "Wool Flannel", "Melton Wool",
            "Tropical Worsted Wool", "Camel Hair Cloth", "Mohair Suitings", "Vicuña Fleece Blend",
            "Bouclé Tweed", "Prince of Wales Check Wool", "Hounds-Tooth Wool", "Bedford Cord Wool",
            "Wool Serge", "Double-Faced Wool Crepe", "Cashmere Zibeline", "Alpaca Blend Melton",
            "Wool Ottoman", "Birdseye Worsted Wool", "Sharkskin Wool", "Cavalry Twill",
            "Whipcord Wool", "Herringbone Tweed", "Felted Loden Wool", "Wool Covert Cloth", "Worsted Flannel"
        )
        wools.forEachIndexed { idx, name ->
            list.add(
                FabricItem(
                    id = "fab_wool_$idx",
                    name = name,
                    category = "Wool & Tailoring",
                    composition = "100% Virgin Merino Wool / Cashmere",
                    weightGsm = 220 + (idx * 8) % 200,
                    weaveType = "Twill & Worsted Weave",
                    drapability = "Tailored & Structured",
                    stretchPercentage = "Natural Comfort Stretch",
                    bestForGarments = listOf("Tailored Blazer", "Haute Overcoat", "Trousers"),
                    careInstructions = "Steam & Dry Clean Only"
                )
            )
        }

        // 4. Velvet & Heavy (25 items)
        val velvets = listOf(
            "Panne Velvet", "Crushed Silk Velvet", "Cotton Velvet", "Lyons Velvet", "Embroidery Velvet",
            "Brocade Velvet", "Devoré Velvet (Burnout)", "Plush Velour", "Ribbed Corduroy Velvet",
            "Metallic Thread Velvet", "Flocked Velvet", "Chenille Weave", "Jacquard Velvet",
            "Double Velvet", "Printed Ombré Velvet", "Silk Pile Velvet", "Stretch Velvet",
            "Panné Rayon Velvet", "Gaufre Embossed Velvet", "Cut Velvet Motif", "Tapestry Weave",
            "Heavy Gobelin Tapestry", "Metallic Brocade Velvet", "Plissé Velvet", "Sculpted Velvet"
        )
        velvets.forEachIndexed { idx, name ->
            list.add(
                FabricItem(
                    id = "fab_velvet_$idx",
                    name = name,
                    category = "Velvet & Heavy",
                    composition = "80% Rayon / 20% Silk Pile",
                    weightGsm = 260 + (idx * 6) % 150,
                    weaveType = "Cut Pile Weave",
                    drapability = "Rich & Sumptuous Heavy Drape",
                    stretchPercentage = if (name.contains("Stretch")) "20% Spandex" else "Non-Stretch",
                    bestForGarments = listOf("Winter Opera Coat", "Evening Jacket", "Corset"),
                    careInstructions = "Steam Only - Do Not Iron Directly"
                )
            )
        }

        // 5. Cotton & Linen (25 items)
        val cottonLinen = listOf(
            "Belgian Flax Linen", "Egyptian Giza Cotton Poplin", "Cotton Lawn", "Pima Cotton Voile",
            "Irish Linen Canvas", "Cotton Twill", "Crisp Cotton Organdy", "Seersucker Cotton",
            "Cotton Broderie", "Linen Cambric", "Heavy Linen Duck", "Cotton Sateen",
            "French Terry Cotton", "Cotton Piqué", "Linen Gauze", "Slubbed Linen",
            "Mercerized Cotton", "Cotton Batiste", "Checked Gingham Linen", "Chambray Linen",
            "Waffle Weave Cotton", "Cross-Dyed Linen", "Cotton Drill", "Oatmeal Linen", "Cotton Corduroy"
        )
        cottonLinen.forEachIndexed { idx, name ->
            list.add(
                FabricItem(
                    id = "fab_cotton_$idx",
                    name = name,
                    category = "Cotton & Linen",
                    composition = "100% Organic Long-Staple Cotton / Flax",
                    weightGsm = 110 + (idx * 5) % 130,
                    weaveType = "Plain & Poplin Weave",
                    drapability = "Crisp & Breathable",
                    stretchPercentage = "Non-Stretch",
                    bestForGarments = listOf("Summer Shirting", "Structure Resort Dress", "Tailored Shorts"),
                    careInstructions = "Machine Wash Gentle / Iron High"
                )
            )
        }

        // 6. Leather & Specialty (25 items)
        val specialty = listOf(
            "Nappa Lambskin Leather", "Patent Calfskin Leather", "Italian Suede Kidskin", "Perforated Leather",
            "Metallic Foil Leather", "Embossed Crocodile Leather", "Ostrich Grain Leather", "Slinky Liquid Lamé",
            "Mylar Metallic Mesh", "Iridescent Sequin Mesh", "Beaded Chainmail", "Alcantara Faux Suede",
            "Vegan Mushroom Leather (Mycelium)", "Aniline Cowhide", "Shearling Fleece", "Quilted Metallic Nylon",
            "Plexiglass Vinyl Sheeting", "Sculptural Horsehair Braid", "Crinkled Foil Taffeta", "Holographic PVC",
            "Paillette Sequin Net", "Mirror Disc Chainmail", "Feather Trimmed Net", "Rubberized Tech Latex", "Laser-Cut Synthetic Suede"
        )
        specialty.forEachIndexed { idx, name ->
            list.add(
                FabricItem(
                    id = "fab_leather_$idx",
                    name = name,
                    category = "Leather & Specialty",
                    composition = "100% Italian Nappa / Specialty Poly",
                    weightGsm = 280 + (idx * 10) % 200,
                    weaveType = "Genuine Leather Skin / Specialty Grid",
                    drapability = "Sculptural & Avant-Garde",
                    stretchPercentage = "Fixed Form",
                    bestForGarments = listOf("Avant-Garde Jacket", "Couture Corsetry", "Showpiece Skirt"),
                    careInstructions = "Professional Leather Specialist Only"
                )
            )
        }

        // 7. Synthetics & Tech Mesh (20 items)
        val tech = listOf(
            "3D Spacer Tech Mesh", "Bonded Neoprene Scuba", "Power Mesh", "Recycled Ocean Nylon Taffeta",
            "Poly-Ripstop Tech", "Waterproof Breathable Membrane", "Pleated Organza Synthetic", "Monofilament Sheer Net",
            "Poly-Chiffon Microfiber", "Memory Shape Polyester", "Reflective Thread Nylon", "Carbon Fiber Composite Weave",
            "UV-Blocking Tech Jersey", "Metallic Lurex Sheer", "Conductive Smart Fabric", "Bacteriostatic Micro-Mesh",
            "High-Tenacity Cordura", "Thermo-Chromic Nylon", "High-Gloss Latex Poly", "Structural Horsehair Netting"
        )
        tech.forEachIndexed { idx, name ->
            list.add(
                FabricItem(
                    id = "fab_tech_$idx",
                    name = name,
                    category = "Synthetics & Tech Mesh",
                    composition = "85% Recycled Polyamide, 15% Elastane",
                    weightGsm = 140 + (idx * 7) % 160,
                    weaveType = "Knit & Bonded Scuba Tech",
                    drapability = "Architectural & Bouncy",
                    stretchPercentage = "4-Way High Stretch",
                    bestForGarments = listOf("Architectural Sleeve", "Avant-Garde Bodysuit", "Athleisure Couture"),
                    careInstructions = "Cool Wash / Line Dry"
                )
            )
        }

        // 8. Knits & Stretch (20 items)
        val knits = listOf(
            "Fine Gauge Cashmere Knit", "Merino Wool Rib Knit", "Rayon Bamboo Viscose Jersey", "Silk Interlock Knit",
            "Modal Spandex Jersey", "Milano Rib Stretch Knit", "Ponte de Roma Heavy Knit", "French Terry Loopback",
            "Lurex Sparkle Knit", "Pointelle Cotton Knit", "Jacquard Patterned Knit", "Double Knit Scuba",
            "Seamless Mesh Knit", "Brushed Fleece Knit", "Cashmere Silk Jersey", "Textured Ottoman Knit",
            "Thermal Honeycomb Knit", "Micro-Modal Jersey", "Compression Power Jersey", "Angora Blend Knit"
        )
        knits.forEachIndexed { idx, name ->
            list.add(
                FabricItem(
                    id = "fab_knit_$idx",
                    name = name,
                    category = "Knits & Stretch",
                    composition = "90% Bamboo Viscose, 10% Spandex",
                    weightGsm = 180 + (idx * 6) % 140,
                    weaveType = "Single & Double Jersey Knit",
                    drapability = "Soft, Hugging & Fluid",
                    stretchPercentage = "4-Way 50% Stretch",
                    bestForGarments = listOf("Bodycon Evening Gown", "Draped Top", "Lounge Suit"),
                    careInstructions = "Machine Wash Cold / Dry Flat"
                )
            )
        }

        return list
    }
}
