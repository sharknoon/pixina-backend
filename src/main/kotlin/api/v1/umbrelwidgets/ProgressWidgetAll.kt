package api.v1.umbrelwidgets

import kotlinx.serialization.Serializable

@Serializable
data class ProgressWidgetAll(val items: List<ProgressWidgetAllItem>)

@Serializable
data class ProgressWidgetAllItem(val title: String, val text: String, val subtext: String)