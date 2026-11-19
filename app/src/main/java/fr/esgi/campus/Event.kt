package fr.esgi.campus

/*
* 19/11/2026 — Données affichées par le catalogue Campus
* */
data class Event(
    val id: Int,
    val title: String,
    val category: String,
    val location: String?,
    val capacity: Int,
    val description: String
)

/*
* 19/11/2026 — Échantillon local couvrant les cas demandés
* */
val sampleEvents = listOf(
    Event(1, "Conférence Kotlin : construire des applications modernes", "Conférence", "Amphithéâtre A", 42,
        "Une rencontre autour de Kotlin, de ses usages actuels et des bonnes pratiques pour construire des applications fiables."),
    Event(2, "Atelier Jetpack Compose", "Atelier", "Salle 204", 18,
        "Prenez en main les composables, les layouts et les previews pour créer une interface Android déclarative."),
    Event(3, "Rencontre des associations étudiantes", "Campus", null, 75,
        "Découvrez les associations du campus et les projets ouverts aux étudiants cette année."),
    Event(4, "Hackathon Campus : idées et prototypes", "Innovation", "Espace projets", 12,
        "Formez une équipe, choisissez un défi et imaginez un prototype en une journée de collaboration."),
    Event(5, "Design system Android : atelier pratique", "Atelier", "Studio numérique", 0,
        "Un atelier de conception consacré aux composants, aux couleurs et à l'accessibilité des interfaces mobiles.")
)
