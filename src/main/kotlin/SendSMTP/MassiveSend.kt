package SendSMTP

import SendSMTP.model.Body
import SendSMTP.model.ResponseFields
import SendSMTP.service.Locaweb
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.system.measureTimeMillis


class MassiveSend(
){
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.smtplw.com.br/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val call = retrofit
        .create(Locaweb::class.java)

    private fun requirer(body: Body) {
        // lateinit var item: ResponseFields

        call.emailResponse(body).enqueue(object: Callback<ResponseFields>{
            override fun onResponse(
                call: Call<ResponseFields>,
                response: Response<ResponseFields>
            ){
                if (response.isSuccessful) {
                    System.out.println("Response OK")

                } else {
                    System.out.println("Response ERROR")
                }
            }

            override fun onFailure(
                call: Call<ResponseFields>,
                t: Throwable
            ) {
                System.out.println("Require ERROR")
            }

        })

    }

    fun massiveSend(tasksNumber: Int, body: Body) {
        for (item in 1..tasksNumber){
            requirer(body)
        }
    }
}

fun main() {

    val time = measureTimeMillis {
        MassiveSend().massiveSend(3,Body(
            "yuri.ylr@outlook.com",
            "nao-responder@seatelecom.com.br",
            "blabla","Eu"))
    }

    System.out.println("tempo exec: $time")

}
