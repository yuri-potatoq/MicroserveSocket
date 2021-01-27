package SendSMTP

import Connections
import Counter
import SendSMTP.db.DBConnection
import SendSMTP.model.Body
import SendSMTP.model.ResponseFields
import SendSMTP.service.Locaweb
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*


class MassiveSend(
    val socketClient: Connections,
    val target: Counter
){
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.smtplw.com.br/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val call = retrofit
        .create(Locaweb::class.java)

    private val db = DBConnection(
        database ="mydb",
        port ="5432",
        realm ="",
        user = "postgres"
    )
//    lateinit var manager: Observer

    private fun requirer(body: Body) {

        call.emailResponse(body).enqueue(object: Callback<ResponseFields>{
            override fun onResponse(
                call: Call<ResponseFields>,
                response: Response<ResponseFields>
            ){
                if (response.isSuccessful) {

                    System.out.println("Response OK")

                    response.body()?.let {
                        /** Escreve na stream, uma instância ResponseFields */
                        socketClient.writeData(it)

                        /** Decrementa o número de tarefas */
                        target.onChange()

                        /** Insere os dados na DB */
                        db.pgInsert(it)
                    }

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

    fun massiveSend(
        emails: Queue<String>,
        body: String
    ) {
        for (item in emails){
            requirer(Body(
                item,
                "nao-responder@seatelecom.com.br",
                body,
                "Eu"
            ))
        }
    }


}
