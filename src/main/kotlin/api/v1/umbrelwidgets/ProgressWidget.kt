package api.v1.umbrelwidgets

import kotlinx.serialization.Serializable

@Serializable
data class ProgressWidget(
    val title: String = "Fortschritt",
    val text: String = "0%",
    val subtext: String = "0 Platten",
    val progressLabel: String = "500 berbleibende Platten",
    val progress: Double = 0.0
)
