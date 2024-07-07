package graphics

import backend.MainFrame
import data.SettingsManager
import data.StaticVals
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GridLayout
import java.text.SimpleDateFormat
import java.util.*
import javax.swing.*
import javax.swing.filechooser.FileFilter
import javax.swing.filechooser.FileNameExtensionFilter
import kotlin.properties.Delegates

class SettingsPane : JPanel {
    private var dBLink : JButton by Delegates.notNull()
    private var pFratesLink: JButton by Delegates.notNull()
    private var homeBtn: JButton by Delegates.notNull()
    private  var dataFormatChooser :JComboBox<String> by Delegates.notNull()
    //private var dataFormatSelector:JTe by Delegates.notNull()

    companion object {
        const val WIDTH = 580
        const val HEIGHT = 400
        private const val SPACER = 15
    }

    private fun summonFileChooser(filter:FileFilter? = null):String?{
        val filechoose = JFileChooser(SettingsManager.pathToFratesP)
        if (filter != null) {
            filechoose.addChoosableFileFilter(filter)
            filechoose.fileFilter = filter
        }
        filechoose.showOpenDialog(this@SettingsPane)
        return try {
             filechoose.selectedFile.absolutePath
        } catch (e: NullPointerException) {null}
        catch (e: IllegalStateException) {null}
    }

    constructor() {
        layout = BorderLayout()
        preferredSize = Dimension(WIDTH, HEIGHT)
        var servicelayout = JPanel(GridLayout(1, 3))
        add(servicelayout, BorderLayout.SOUTH)
        dBLink = JButton("Seleziona il DB")
        pFratesLink = JButton("Seleziona il .pft")
        homeBtn = JButton("Home")
        servicelayout.add(dBLink)
        servicelayout.add(pFratesLink)
        servicelayout.add(homeBtn)
        dBLink.addActionListener {
            val res = summonFileChooser(FileNameExtensionFilter("Database Access Frates", "mdb"))
            if (res != null) SettingsManager.pathAccessDB = res

        }
        pFratesLink.addActionListener {
            val res = summonFileChooser(FileNameExtensionFilter("Registro delle prenotazioni (.pft)", "pft"))
            if (res != null) SettingsManager.pathToFratesP = res
        }
        homeBtn.addActionListener { MainFrame.setFrame(MenuPane()) }
        servicelayout = JPanel(GridLayout(1, 2))
        add(servicelayout, BorderLayout.NORTH)
        var servicelayout2 = JPanel()
        servicelayout2.layout = BoxLayout(servicelayout2,BoxLayout.PAGE_AXIS)
        servicelayout.add(servicelayout2)
        servicelayout2.add(JLabel(StaticVals.Generics.DATE_FORMAT_INPUT,JLabel.CENTER))
        dataFormatChooser  = JComboBox(Array(StaticVals.Settings.DATA_FORMATS.size)
        {i -> SimpleDateFormat(StaticVals.Settings.DATA_FORMATS[i]).format(Date())+"  -  Formato: (${StaticVals.Settings.DATA_FORMATS[i]})" })
        dataFormatChooser.selectedIndex = SettingsManager.dataFormatInt
        servicelayout2.add(dataFormatChooser)
        dataFormatChooser.addActionListener {
            SettingsManager.dataFormatInt =dataFormatChooser.selectedIndex
        }

        isVisible = true
    }
}