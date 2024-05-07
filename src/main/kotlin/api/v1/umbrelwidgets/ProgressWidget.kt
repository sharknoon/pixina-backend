package api.v1.umbrelwidgets

import kotlinx.serialization.Serializable

@Serializable
data class ProgressWidget(
    val type: String = "text-with-progress",
    val refresh: String = "1m",
    val link: String = "/informations",
    val title: String = "Fortschritt",
    val text: String = "0%",
    val subtext: String = "0 Platten",
    val progressLabel: String = "500 berbleibende Platten",
    val progress: Double = 0.0
)
