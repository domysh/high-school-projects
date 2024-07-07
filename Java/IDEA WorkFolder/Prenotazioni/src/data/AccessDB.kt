package data

import java.lang.IndexOutOfBoundsException
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.util.*

class AccessDB(MainTable:String){

    private var table = listOf<List<Any?>>()

    private fun queryDB(db:Connection, SQLCode: String):ResultSet{
        return db.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)
                .executeQuery(SQLCode)
    }

    private fun sqlStringify( s:String):String{
        return "`"+s.replace("\u0000", "")
            .replace("'", "\\'")
            .replace("\\","\\\\")
            .replace("`","\\'").
            replace("\"","\\")+"`"
    }

    init{
        Class.forName("net.ucanaccess.jdbc.UcanaccessDriver")
        val db = DriverManager.getConnection("jdbc:ucanaccess://${SettingsManager.pathAccessDB};", "", "")
        val res = queryDB(db, "SELECT * FROM ${sqlStringify(MainTable)}")

        table = fromSetToList(res).toList()
        db.close()
    }

    private fun fromSetToList(set: ResultSet): List<List<Any?>> {
        val res = Vector<List<Any?>>()
        var i = 1
        while (true) {
            try {
                val ele = getRow(set, i)
                res.add(ele)
            }catch (e:IndexOutOfBoundsException){
                return res.toTypedArray().toList()
            }
            i++
        }
    }


    private fun getRow(res: ResultSet, row: Int): List<Any?> {
            res.beforeFirst()
            for (i in 0 until row) {
                if (!res.next()) {
                    throw IndexOutOfBoundsException()
                }
            }
            val numCol = res.metaData.columnCount
            val result = arrayOfNulls<Any>(numCol)
            for (i in 1..numCol) {
                result[i - 1] = res.getObject(i)
            }
            return result.toList()
    }




    private fun getElement(set: ResultSet, row: Int, col: Int): Any? {
        set.beforeFirst()
        for (i in 0 until row) {
            if (!set.next()) {
                throw ArrayIndexOutOfBoundsException()
            }
        }
        return set.getObject(col)
    }





    fun getRow(row: Int): List<Any?> {
        return table[row]

    }

    val noRows: Int get() = table.size




}



