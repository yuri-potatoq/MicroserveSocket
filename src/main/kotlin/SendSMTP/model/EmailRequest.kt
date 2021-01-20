package SendSMTP.model

import retrofit2.http.Body

data class Body(
    var to: String,
    var from: String,
    var body: String,
    var subject: String
)