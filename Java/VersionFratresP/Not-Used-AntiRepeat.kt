package backend

class AntiRepeat(private val exe: () -> Unit, private val WAIT: Int) {
    private var calls = 0
    private var STOP = 0

    @Volatile
    var isStopped = true

    private var antiRepeter: Thread? = null

    private fun threadCreator() {
        antiRepeter = Thread( Runnable {
            var OLD = calls
            val STATE = STOP
            loop@while (true) {
                if (STOP != STATE) {
                    isStopped = true
                    break@loop
                }
                try {
                    Thread.sleep(WAIT.toLong(), 0)
                } catch (e: InterruptedException) {
                }
                if (OLD == calls) {
                    calls = 0
                    exe()
                    threadCreator()
                    isStopped = true
                    break@loop
                } else {
                    OLD++
                }
            }
        })
    }

    fun call() {
        try {
            isStopped = false
            calls++
            if (calls == 1) antiRepeter!!.start()
        } catch (e: IllegalThreadStateException) {
            forcedStop()
            threadCreator()
        }
    }

    fun forcedStop() {
        STOP++
        calls = 0
    }

    init {
        threadCreator()
    }
}