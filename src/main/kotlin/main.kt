package SendSMTP


import SendSMTP.model.ResponseFields
import com.google.gson.Gson
import kotlinx.serialization.Serializable
import java.net.InetAddress
import java.net.ServerSocket
import java.io.*
import java.lang.StringBuilder
import java.net.Socket
import java.util.Queue
import java.util.LinkedList
import java.util.Observer
import java.util.Observable


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
        client.close()
    }

    /* Lê do inputStream */
    fun read(): Request {

//        val resp = gson.fromJson(
//            input.lines().collect(Collectors.joining()),
//            Request::class.java
//        )
        val resp = StringBuilder()

        while(true){
            val ch = input.read()
            if (ch == -1) break;
            if (ch == 125) {
                resp.append((125).toChar())
                break
            }
            resp.append(ch.toChar())
        }
        println("gotcha")
        return gson.fromJson(resp.toString(), Request::class.java)
    }

    fun write(response: String){
        /* Escreve no outputStream */
        output.write(this.gson.toJson(
            response
        ))
        output.flush()
    }

    fun writeData(response: ResponseFields){
        /* Escreve no outputStream */
        response.data.attributes.body = "default"
        output.write(gson.toJson(response))
        output.flush()
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
        val template: String,
        val emails: String
    )

    @Serializable
    data class Test(
        val sevice: String,
        val task: String,
        val templat: String,
        val email: String
    )
}


class Counter(
    var amount: Int
): Observable(){

    fun onChange(){
        amount--;

        setChanged()
        notifyObservers(amount)
    }
}

class ManagerCounter(
    val client: Connections
): Observer{

    override fun update(p0: Observable?, p1: Any?) {
        p1?.let{
            println(it)
           if (it == 0) {
               println("FECHOU!")
               client.clientClose()
           }
        }
    }
}

fun main(args: Array<String>){

    val server = ServerSocket(
        6060, 50, InetAddress.getByName("localhost")
    )

    while (true) {
        val client = Connections(server)

        val response = client.read()

        /** Multiplexação dos serviços à serem executados. */
        when(response.service){
            "massiveSendEmails" -> {
                    val emailQueue: Queue<String> = LinkedList<String>()

                    emailQueue.addAll(scrapyCsv(response.emails))

                    val template = takeTemplate(response.template)

                    var target = Counter(emailQueue.size)
                    target.addObserver(ManagerCounter(client))

                    MassiveSend(
                        client, target
                    ).massiveSend(
                        emailQueue,
                        template
                    )

                    println("$response")
                    println("${template}")
            }
        }
    }

}

/**
 * Função que enfileira os emails recebidos por csv
 * em uma Queue à ser decrementada.
 */

fun scrapyCsv(path: String): List<String>{
    val list = mutableListOf<String>()

    val buff = BufferedReader(FileReader(File(path))).use {
        try {
            while (it.ready()){
                list.add(it.readLine())
            }

        }catch (e: IOException) {
            println("Falha ao ler CSV")
        }
    }

    return list
}

/**
 * Função na qual faz o build de uma string
 * contendo o template do path submetido.
 */
fun takeTemplate(path: String): String{
    val buffString  = StringBuilder()

    val buff = BufferedReader(FileReader(File(path))).use {
        while (it.ready()) {
            buffString.append(it.readLine())
        }
    }

    return buffString.toString()
}