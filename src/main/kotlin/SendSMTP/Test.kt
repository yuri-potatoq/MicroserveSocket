package SendSMTP

import org.omg.CORBA.Object
import java.util.Observable
import java.util.Observer
import SendSMTP.model.ResponseFields
import SendSMTP.db.DBConnection

class Target(
    var count: Int
): Observable(){

    fun onChange(){
        count--;

        setChanged()
        notifyObservers(count)
    }
}



fun main(){
//    val db = DBConnection(
//        database ="mydb",
//        port ="5432",
//        realm ="",
//        user = "postgres"
//    ).pgInsert()


}