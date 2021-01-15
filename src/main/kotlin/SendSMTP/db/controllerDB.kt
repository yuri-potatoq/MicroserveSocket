package SendSMTP.db


import kotlinx.coroutines.TimeoutCancellationException
import java.sql.DriverManager
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import kotlin.random.Random


class DBConnection(
    val database: String,
    val port: String,
    val realm: String,
    val user: String
){
    private lateinit var conn: Connection
    init {
        try {
            Class.forName("org.postgresql.Driver")
            this.conn = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/mydb"
            )
//            this.conn ?: throw Throwable()

        } catch(timeout: TimeoutCancellationException){
            println("Conexão com o banco de dados não efetuada!")
        }
    }

    fun pgInsert(statement: PreparedStatement): IntArray? {
        return statement.executeBatch()
    }

    fun pgSelect(query: String): ResultSet {
        val statement = this.conn.createStatement()!!
        return statement.executeQuery(query)

    }

    fun takeStatement(query: String): PreparedStatement? {
        return this.conn.prepareStatement(query)
    }

}

data class Persons(
    val id: Int,
    val nome: String
)

fun init(){
    val db = DBConnection(
        database ="mydb",
        port ="5432",
        realm ="",
        user = "postgres"
    )

    val rand = (100..1000).random()
    println(rand)
    val stm = db.takeStatement("insert into persons(id, nome) values(?, ?) ")
    stm?.let {
        stm.setInt(1, 772)
        stm.setString(2,"Doe")
        stm.addBatch()

        stm.clearParameters()
        stm.setInt(1, 773)
        stm.setString(2,"Smith")
        stm.addBatch()
        val insert = db.pgInsert(stm)?.iterator()

        val result = db.pgSelect("SELECT * FROM persons")
        var registers: MutableList<Persons> = mutableListOf(Persons(1, ""))

        result.let {
            while(result.next()){
                registers.add(Persons(
                    id = it.getInt("id"),
                    nome = it.getString("nome")
                ))
            }
        }

        registers.map {
            println(it)
        }

        insert?.forEach {
            println(it)
        }

        println("result $result")

    }

}