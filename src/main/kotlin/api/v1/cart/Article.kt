package api.v1.cart

import kotlinx.serialization.Serializable

@Serializable
data class Article(val id: Long, val quantity: Int = 1, val form_type: String = "product", val utf8: String = "✓")