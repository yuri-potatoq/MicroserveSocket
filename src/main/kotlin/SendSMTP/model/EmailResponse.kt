package SendSMTP.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable


data class ResponseFields(
    val data: Data
)

data class Data(
    val attributes: Attributes,
    val id: Int,
    val links: Links
)

data class Attributes(
    val bcc: Any,
    var body: String,
    val cc: Any,
    val from: String,
    val headers: Headers,
    val response: Any,
    val status: String,
    val subject: String,
    val to: String
)


data class Links(
    val self: String
)


data class Headers(
    @SerializedName("Content-Type")
    val Content_Type: String
)
