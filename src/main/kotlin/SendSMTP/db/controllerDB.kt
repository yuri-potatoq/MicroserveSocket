package SendSMTP.db


import SendSMTP.model.ResponseFields
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
    private val tableName: String = "send"
    private val addr: String = System.getenv("MASSIVE_EMAIL_DB_ADDR")

    init {
        try {
            Class.forName("org.postgresql.Driver")
            this.conn = DriverManager.getConnection(
                "jdbc:postgresql://$addr:$port/$database?user=postgres"//&password=admin"
            )
            
            conn.createStatement().executeUpdate("""
                CREATE TABLE IF NOT EXISTS $tableName( 
                    id VARCHAR(12) NOT NULL, 
                    target VARCHAR(35) NOT NULL, 
                    subject VARCHAR(45) NOT NULL, 
                    description VARCHAR(45), 
                    title VARCHAR(30), 
                    status VARCHAR(45) NOT NULL, 
                    launch TIMESTAMP, 
                    PRIMARY KEY (id)
                );""".trimIndent()
            )


//            this.conn ?: throw Throwable()

        } catch(timeout: TimeoutCancellationException){
            println("Conexão com o banco de dados não efetuada!")
        }
    }

    fun pgInsert(responseObject: ResponseFields){
        val listData = arrayOf("",
            responseObject.data.id,
            responseObject.data.attributes.to,
            responseObject.data.attributes.subject,
            responseObject.data.links.self,
            "Envio de emails Sea Telecom",
            responseObject.data.attributes.status
        )

        val query = conn.prepareStatement(
        """
            INSERT INTO $tableName values(?, ?, ?, ?, ?, ? , current_timestamp(0));
        """.trimIndent())

        for (index in 1..6) {
            query.setString(index, listData[index].toString())
        }

        query.addBatch()
        query.executeBatch()

    }

    fun pgSelect(query: String): ResultSet {
        val statement = this.conn.createStatement()!!
        return statement.executeQuery(query)

    }

}


