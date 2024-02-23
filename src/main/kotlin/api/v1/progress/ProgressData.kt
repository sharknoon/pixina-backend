package api.v1.progress

import kotlinx.serialization.Serializable

@Serializable
data class ProgressData(
    val finished: Set<Int> = setOf(),
    val inProgress: Set<Int> = setOf(),
    val reserved: Set<Int> = setOf(),
    val availableInStock: Set<Int> = setOf(),
    val availableOutOfStock: Set<Int> = setOf(),
)