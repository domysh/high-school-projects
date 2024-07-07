package backend

import data.SettingsManager
import data.StaticVals
import graphics.MenuPane
import mdlaf.MaterialLookAndFeel
import java.awt.*
import java.awt.event.WindowEvent
import java.awt.event.WindowListener
import java.net.URI
import javax.swing.*
import kotlin.system.exitProcess

object MainFrame {
    val NAMEFRAME = StaticVals.Generics.NAME_MAIN_FRAME
    val DEFAULT_STYLE = MaterialLookAndFeel()
    private val logo_img = SettingsManager.getImageByImg(StaticVals.Img.LOGO)
    private var MainPanel: JPanel = JPanel()
    private var frame: JFrame = JFrame()
    private var started = false
    private var stopCalled = false


    fun start(panel: JPanel = MenuPane()) {
        if (started) return
        setLookAndFeel()
        started = true
        frame.defaultCloseOperation = JFrame.DO_NOTHING_ON_CLOSE
        frame.addWindowListener(object : WindowListener {
            override fun windowOpened(arg0: WindowEvent) {}
            override fun windowIconified(arg0: WindowEvent) {}
            override fun windowDeiconified(arg0: WindowEvent) {}
            override fun windowDeactivated(arg0: WindowEvent) {}
            override fun windowClosing(arg0: WindowEvent) {
                wantClose()
            }

            override fun windowClosed(arg0: WindowEvent) {}
            override fun windowActivated(arg0: WindowEvent) {}
        })
        setBounds(panel.preferredSize.width, panel.preferredSize.height,true)
        frame.layout = BorderLayout()
        //frame.isResizable = false
        frame.title = NAMEFRAME
        frame.iconImage = logo_img
        setFrame(panel)
        setVisible(true)
    }

    fun setLookAndFeel(l: Any?) {
        if (l == null) return
        try {
            if (l is String) {
                UIManager.setLookAndFeel(l as String?)
            } else if (l is LookAndFeel) {
                UIManager.setLookAndFeel(l as LookAndFeel?)
            }
            frame.validate()
        } catch (e: Exception) {
            createJError(StaticVals.Generics.Errors.GRAPHICS_STYLE, e)
        }
    }

    fun setLookAndFeel() {
        setLookAndFeel(DEFAULT_STYLE)
    }

    private fun wantClose() {
        if (stopCalled) return
        stopCalled = true
        choosePane(StaticVals.Generics.WANT_EXIT, {
            stopCalled = false
            try {
                BugLogger.Logger.getInstance().closeErrorOut()
            } catch (e: Exception) {
            } // In caso di eliminazione del file questo porta a una non chiusura del programma
            exitProcess(0)
        }, { stopCalled = false })
    }

    val screenW: Int
        get() = Toolkit.getDefaultToolkit().screenSize.width

    val screenH: Int
        get() = Toolkit.getDefaultToolkit().screenSize.height

    fun setBounds(r: Rectangle) {
        if (!started) return
        frame.minimumSize = Dimension(r.width,r.height)
        frame.bounds = r
    }

    fun setBounds() {
        setBounds(StaticVals.Dimensions.MainFrame.width, StaticVals.Dimensions.MainFrame.height)
    }

    fun setBounds(w: Int, h: Int,center:Boolean = false) {
        if (center) {
            setBounds(getCentralScreen(w, h))
        }else{
            setBounds(Rectangle(frame.x,frame.y,w,h))
        }
    }


    fun getCentralScreen(w: Int, h: Int): Rectangle {
        return getCentralRelative(0, 0, screenW, screenH, w, h)
    }

    fun getCentral(w: Int, h: Int): Rectangle {
        return if (!started) getCentralScreen(w,h) else getCentralRelative(frame.x, frame.y, frame.width, frame.height, w, h)
    }

    fun getCentralRelative(X: Int, Y: Int, W: Int, H: Int, w: Int, h: Int): Rectangle {
        val x = W / 2 - w / 2 + X
        val y = H / 2 - h / 2 + Y
        return Rectangle(x, y, w, h)
    }

    fun getCentralRelative(r: Rectangle, w: Int, h: Int): Rectangle {
        return getCentralRelative(r.getX().toInt(), r.getY().toInt(), r.getWidth().toInt(), r.getHeight().toInt(), w, h)
    }

    fun setVisible(a: Boolean) {
        if (!started) return
        try {
            frame.isVisible = a
        } catch (e: Exception) {
        }
    }

    fun updatePane() {
        if (!started) return
        MainPanel.revalidate()
        MainPanel.repaint()
    }

    val pane: JPanel
        get() {
            if (!started) start(MenuPane())
            return MainPanel
        }

    fun setFrame(panel: JPanel) {
        if (!started) start(MenuPane())
        frame.contentPane.removeAll()
        try {
            frame.contentPane.add(panel, BorderLayout.CENTER)
        } catch (e: IndexOutOfBoundsException) {}
        MainPanel = panel
        setBounds(panel.preferredSize.width, panel.preferredSize.height)
        updatePane()
    }

    fun setFocusable(a: Boolean) {
        if (!started) return
        frame.isEnabled = a
    }


    fun noticePane(phrase: String, okPhrase: String = "OK", op: () -> Unit) {
        val finestra = JFrame()
        finestra.defaultCloseOperation = JFrame.DO_NOTHING_ON_CLOSE
        finestra.contentPane.layout = BorderLayout()
        finestra.isResizable = false
        val message = JLabel("<html><center>$phrase</center></html>", JLabel.CENTER)
        message.font = Font("Arial", Font.BOLD, 15)
        val ok = JButton("<html>$okPhrase</html>")
        val buttonPane = JPanel(GridLayout(1, 1))
        buttonPane.add(ok)
        setFocusable(false)
        ok.addActionListener {
            setFocusable(true)
            finestra.isVisible = false
            op()
        }
        finestra.contentPane.add(buttonPane, BorderLayout.SOUTH)
        finestra.contentPane.add(message, BorderLayout.CENTER)
        finestra.isAlwaysOnTop = true
        finestra.bounds = getCentral(StaticVals.Dimensions.ServiceFrame.width, StaticVals.Dimensions.ServiceFrame.height)
        finestra.isVisible = true
    }

    fun noticePane(phrase: String, op:  () -> Unit) {
        noticePane(phrase, "OK", op)
    }
    fun noticePane(phrase: String) {
        noticePane(phrase, "OK") {}
    }

    fun choosePane(phrase: String, trueCh: String, falseCh: String,
                   yes_:  () -> Unit, no_: () -> Unit ) {
        val finestra = JFrame()
        finestra.defaultCloseOperation = JFrame.DO_NOTHING_ON_CLOSE
        finestra.contentPane.layout = BorderLayout()
        finestra.isResizable = false
        val message = JLabel("<html><center>$phrase</center></html>", JLabel.CENTER)
        message.font = Font("Arial", Font.BOLD, 15)
        val yes = JButton("<html>$trueCh</html>")
        val no = JButton("<html>$falseCh</html>")
        val buttonPane = JPanel(GridLayout(1, 2))
        buttonPane.add(yes)
        buttonPane.add(no)
        setFocusable(false)
        yes.addActionListener {
            setFocusable(true)
            finestra.isVisible = false
            yes_()
        }
        no.addActionListener {
            setFocusable(true)
            finestra.isVisible = false
            no_()
        }
        finestra.contentPane.add(buttonPane, BorderLayout.SOUTH)
        finestra.contentPane.add(message, BorderLayout.CENTER)
        finestra.isAlwaysOnTop = true
        finestra.bounds = getCentral(StaticVals.Dimensions.ServiceFrame.width, StaticVals.Dimensions.ServiceFrame.height)
        finestra.isVisible = true
    }

    fun choosePane(phrase: String, yes:  () -> Unit, no:  () -> Unit) {
        choosePane(phrase, "Si", "No", yes, no)
    }

    fun createJError(msg: String, e: Exception = java.lang.Exception()) {
        val error = JFrame()
        try {
            setFocusable(false)
            frame.isVisible = false
        } catch (err: Exception) {
        }
        error.isResizable = false
        error.bounds = getCentralScreen(StaticVals.Dimensions.ErrorFrame.width, StaticVals.Dimensions.ErrorFrame.height)
        error.isAlwaysOnTop = true
        error.title = StaticVals.Generics.Errors.ERROR_TITLE
        val pane = JPanel(BorderLayout())
        error.contentPane = pane
        pane.add(JLabel("<html><h2>${StaticVals.Generics.Errors.SUB_TITLE_ERROR}</h2></html>"), BorderLayout.NORTH)
        pane.add(JLabel("<html><b>" + msg + "</b><br>${StaticVals.Generics.Errors.CONTACT_PREFIX}<u>${StaticVals.Info.Contact.BUGEMAIL}</u></html>"),
                BorderLayout.CENTER)
        val btn = JButton("OK")
        btn.addActionListener {
            try {
                Desktop.getDesktop().mail(URI.create("mailto:" + StaticVals.Info.Contact.BUGEMAIL + "?subject=BUG%20PRENOTAZIONI%20FRATRES"))
            } catch (e1: Exception) {
            }
            exitProcess(1)
        }
        pane.add(btn, BorderLayout.SOUTH)
        BugLogger.Logger.getInstance().createLog(msg, e)
        error.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        error.isVisible = true
    }
}