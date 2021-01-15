package SendSMTP

import SendSMTP.model.Body
import SendSMTP.model.ResponseFields
import SendSMTP.service.Locaweb
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MassiveSend(
    val body: Body,
    val retrofit: Retrofit
){

    private val call = retrofit
        .create(Locaweb::class.java)
        .emailResponse(body)

    private fun requirer(call: Call<ResponseFields>): ResponseFields {
        lateinit var item: ResponseFields

        call.enqueue(object: Callback<ResponseFields>{
            override fun onResponse(
                call: Call<ResponseFields>,
                response: Response<ResponseFields>
            ){
                response?.body()?.let {
                    System.out.println("Response $it")
                    item = it
                }
            }

            override fun onFailure(
                call: Call<ResponseFields>,
                t: Throwable
            ) {
                TODO("Not yet implemented")
            }

        })



        return item
    }

    fun massiveSend(tasksNumber: Int) {
        for (item in 1..tasksNumber){
            requirer(call)
        }
    }
}

fun main() {

    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.smtplw.com.br/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val call = MassiveSend(Body(
        "yuri.ylr@outlook.com",
        "nao-responder@seatelecom.com.br",
        "teste",
        "test"
    ), retrofit).massiveSend(3)

}