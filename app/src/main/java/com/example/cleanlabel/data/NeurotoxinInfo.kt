package com.example.cleanlabel.data

data class NeurotoxinInfo(
    val name: String,
    val category: String,
    val description: String,
    val severity: String,
    val effects: List<String>,
    val sourceUrl: String
)

object NeurotoxinDatabase {
    val neurotoxinInfo = mapOf(
        "lead" to NeurotoxinInfo(
            name = "Lead",
            category = "Heavy Metals",
            description = "A highly toxic metal that can cause severe neurological damage",
            severity = "High Risk",
            effects = listOf(
                "Brain development impairment",
                "Cognitive function deterioration",
                "Behavioral problems",
                "Nervous system damage"
            ),
            sourceUrl = "https://www.epa.gov/lead"
        ),
        "mercury" to NeurotoxinInfo(
            name = "Mercury",
            category = "Heavy Metals",
            description = "Toxic metal that can severely impact the nervous system",
            severity = "High Risk",
            effects = listOf(
                "Brain damage",
                "Vision and hearing problems",
                "Memory loss",
                "Tremors"
            ),
            sourceUrl = "https://www.epa.gov/mercury"
        ),
        "aluminum" to NeurotoxinInfo(
            name = "Aluminum",
            category = "Heavy Metals",
            description = "Metal that can accumulate in brain tissue",
            severity = "Medium Risk",
            effects = listOf(
                "Memory impairment",
                "Learning difficulties",
                "Coordination problems"
            ),
            sourceUrl = "https://www.atsdr.cdc.gov/toxprofiles/tp22.pdf"
        ),
        "aspartame" to NeurotoxinInfo(
            name = "Aspartame",
            category = "Artificial Sweeteners",
            description = "Controversial artificial sweetener with potential neurological effects",
            severity = "Medium Risk",
            effects = listOf(
                "Headaches",
                "Dizziness",
                "Mood changes",
                "Memory problems"
            ),
            sourceUrl = "https://www.fda.gov/food/food-additives-petitions/aspartame-artificial-sweetener"
        ),
        "msg" to NeurotoxinInfo(
            name = "Monosodium Glutamate (MSG)",
            category = "Flavor Enhancers",
            description = "Food additive that may affect sensitive individuals",
            severity = "Low Risk",
            effects = listOf(
                "Headaches",
                "Numbness or tingling",
                "Flushing",
                "Muscle weakness"
            ),
            sourceUrl = "https://www.fda.gov/food/food-additives-petitions/questions-and-answers-monosodium-glutamate-msg"
        )
    )
} 