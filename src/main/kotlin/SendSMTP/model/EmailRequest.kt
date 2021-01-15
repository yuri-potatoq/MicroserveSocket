package SendSMTP.model

import retrofit2.http.Body

data class Body(
    val to: String,
    val from: String,
    val body: String,
    val subject: String
)