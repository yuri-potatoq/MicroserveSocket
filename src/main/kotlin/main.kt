import SendSMTP.MassiveSend
import SendSMTP.model.Body
import com.google.gson.Gson
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.net.InetAddress
import java.net.ServerSocket
import java.io.*
import java.lang.StringBuilder
import java.net.Socket
import java.util.*
import java.util.stream.Collectors
import kotlin.system.measureTimeMillis


class Connections(
    private val server:ServerSocket
){
    private var input: BufferedReader
    private val client: Socket
    private val output: OutputStreamWriter
    private val gson = Gson()

    init {
        client = this.server.accept()

        input = BufferedReader(
            InputStreamReader(client.getInputStream())
        )
        output = OutputStreamWriter(
            client.getOutputStream(), "UTF-8"
        )

        System.out.println(
        """
            Conexão em ${client.inetAddress.hostAddress}
            channel: ${client.channel}
            port: ${client.port}
            Thread: ${Thread.currentThread().name}
        """.trimIndent())
    }

    fun clientClose(){
        this.server.close()
    }

    /* Lê do inputStream */
    fun read() = this.gson.fromJson(input.lines().collect(
            Collectors.joining()
    ), Request::class.java)


    fun write(response: Response){
        /* Escreve no outputStream */
        val outputStream = this.gson.toJson(
            response
        )
    }

    @Serializable
    data class Response(
        val request: String,
        val task: String,
        val state: String
    )

    @Serializable
    data class Request(
        val service: String,
        val task: Int,
        val path: String
    )
}


fun main(args: Array<String>) {

    val server = ServerSocket(
        6060, 50, InetAddress.getByName("localhost")
    )

    while (true) {
        val client = Connections(server)

        Thread {

            val response = client.read()

            when(response.service){
                "massiveSendEmails" -> {

                    val emailQueue: Queue<String> = LinkedList<String>()
                    emailQueue.addAll(scrapyCsv(response.path))

//                    val time = measureTimeMillis {
//                        MassiveSend().massiveSend(3, Body(
//                            "yuri.ylr@outlook.com",
//                            "nao-responder@seatelecom.com.br",
//                            "blabla","Eu")
//                        )
//                    }
                    System.out.println("$response")
                }
            }

        }.start()
    }

}

fun scrapyCsv(path: String): List<String>{
}