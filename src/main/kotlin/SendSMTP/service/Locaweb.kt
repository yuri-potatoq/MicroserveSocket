package SendSMTP.service

import SendSMTP.model.ResponseFields
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


interface Locaweb {

    @Headers("x-auth-token: cb27e7a778a682ac6b7c482bf3d3e720")
    @POST("messages")
    fun emailResponse(
        @Body
        body: SendSMTP.model.Body

    ): Call<ResponseFields>
}