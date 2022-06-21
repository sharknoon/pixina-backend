package api.v1.progress

import kotlinx.serialization.Serializable

@Serializable
data class ProgressData(
    val finished: Int = 1,
    val inProgress: Int = 2,
    val reserved: Int = 3,
    val available: Int = 4
)
