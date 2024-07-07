package backend

import data.StaticVals
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class BugLogger private constructor(private var PATHERROUT: String, private var errOut: PrintStream){

    object Logger{
        private var me : BugLogger? = null
        fun getInstance() : BugLogger{
            if(me == null){
                me = BugLogger()
            }
            return me as BugLogger
        }
    }

    private constructor(): this("", PrintStream(System.err)){
        createFolder()
        PATHERROUT = getPathFile(StaticVals.Path.LogFolder.FILE_PREFIX_ERROUT)
        try {
            errOut = PrintStream(PATHERROUT)
        } catch (e: FileNotFoundException) {
        }
        System.setErr(errOut)
        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                closeErrorOut()
            }
        })

    }

    fun closeErrorOut() {
        errOut.close()
        val sel = File(PATHERROUT)
        if (getStringFile(sel) == "") sel.deleteOnExit()
    }

    private fun getStringFile(f: File): String? {
        return try {
            val read = BufferedReader(FileReader(f))
            var data = ""
            while (true) {
                val line = read.readLine() ?: break
                data += "\n${line}\n\n"
            }
            read.close()
            data
        } catch (e: IOException) {
            null
        }
    }

    fun createFolder() {
        File(StaticVals.Path.LogFolder.LOG_DIR).mkdirs()
    }

    private fun getPathFile(init: String): String {
        var res = init
        val d = Date()
        res += SimpleDateFormat("ddMMyyyyHHmmss").format(d)
        val files = File(StaticVals.Path.LogFolder.LOG_DIR).list() ?: throw java.lang.Exception("Problem while Reading files!")
        for (i in files.indices) files[i] = files[i].toLowerCase().replace(StaticVals.Path.LogFolder.FILE_EXTENTION, "")
        var ipotetic = res
        var finded = true
        var i = 0
        while (finded) {
            if (i != 0) {
                ipotetic = res + "_" + i
            }
            finded = false
            for (s in files) {
                if (s.equals(ipotetic, true)) {
                    finded = true
                    break
                }
            }
            i++
        }
        res = ipotetic
        res += StaticVals.Path.LogFolder.FILE_EXTENTION
        return StaticVals.joinPath(StaticVals.Path.LogFolder.LOG_DIR, res)
    }

    private val pathFile: String
        get() = getPathFile(StaticVals.Path.LogFolder.FILE_PREFIX_LOG)

    fun createLog(msg: String, e: Exception = java.lang.Exception()) {
        createFolder()
        val filePath = pathFile
        try {
            val f = File(filePath)
            val fw = FileWriter(f)
            fw.append("Throwed Exception:$e")
            fw.append("\nMessage:$msg")
            fw.append("\nStack Trace:\n")
            val trace = StringWriter()
            e.printStackTrace(PrintWriter(trace))
            fw.append(trace.toString())
            fw.close()
        } catch (err: Exception) {
            err.printStackTrace()
        }
    }
}