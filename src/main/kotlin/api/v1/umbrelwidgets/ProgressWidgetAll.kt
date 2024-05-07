package api.v1.umbrelwidgets

import kotlinx.serialization.Serializable

@Serializable
data class ProgressWidgetAll(
    val type: String = "four-stats",
    val refresh: String = "1m",
    val link: String = "/informations",
    val items: List<ProgressWidgetAllItem> = emptyList()
)

@Serializable
data class ProgressWidgetAllItem(
    val title: String,
    val text: String,
    val subtext: String
)