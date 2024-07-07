package graphics

import backend.MainFrame
import data.*
import java.awt.*
import javax.swing.*
import kotlin.properties.Delegates

/**
 *
 * @author DomySh
 */
class MenuPane : JPanel() {
    private val newPren = JButton(StaticVals.Generics.Menu.NEW_PRENOTATION)
    private val elenc = JButton(StaticVals.Generics.Menu.LIST)
    private val Stati = JButton(StaticVals.Generics.Menu.STATS)
    private val fileLink = JButton(StaticVals.Generics.Menu.SETTINGS)
    private val loadingText = JLabel("${StaticVals.Generics.LOADING}...", JLabel.CENTER)
    private val loading = JProgressBar()
    private var loader: Thread by Delegates.notNull()
    fun loaderStart() {
        loading.isIndeterminate = true
        fileLink.isEnabled = false
        Stati.isEnabled = false
        elenc.isEnabled = false
        newPren.isEnabled = false
        loader = Thread(Runnable {

            // Load DB
            if (!DBManagment.loaded())
                try {
                    DBManagment.load(AccessDB(StaticVals.Info.DB.TABLE_DONATORI),
                            AccessDB(StaticVals.Info.DB.TABLE_DONAZIONI))
                }catch(e:Exception){}
            if (!PftFile.isLoaded) PftFile.loadData()
            loading.isIndeterminate = false
            val DBLoaded = DBManagment.loaded()
            val PFTLoaded = PftFile.isLoaded
            var notLoaded = StaticVals.Generics.Errors.LOADING_FILE
            // Checking status DB
            if (DBLoaded && PFTLoaded) {
                loadingText.foreground = Color(23, 114, 69)
                loadingText.text = StaticVals.Generics.LOADING_DONE
                loading.maximum = 1
                loading.value = 1
                newPren.isEnabled = true
                elenc.isEnabled = true
                Stati.isEnabled = true
                fileLink.isEnabled = true
            } else {
                loadingText.foreground = Color.RED
                if (!DBLoaded) {
                    notLoaded += " DB"
                }
                if (!PFTLoaded) {
                    notLoaded += " PFT"
                }
                loadingText.text = notLoaded
                fileLink.isEnabled = true
            }
        })
        loader.start()
    }

    companion object {
        const val WIDTH = 580
        const val HEIGHT = 400
        private const val ROWBUTTONPANE = 2
        private const val COLBUTTONPANE = 2
        private const val SPACER = 30
        private val logo_img = SettingsManager.getImageIconByImg(StaticVals.Img.TREE_LOGO)
    }

    init{
        layout = BorderLayout()
        preferredSize = Dimension(WIDTH, HEIGHT)
        add(JLabel("          "), BorderLayout.WEST)
        add(JLabel("          "), BorderLayout.EAST)
        // Creazione con i relativi layout dei pannelli
        var topWrite = JPanel(FlowLayout(FlowLayout.CENTER))

        var centerPane = JPanel(BorderLayout(SPACER,SPACER))

        var buttonsPane = JPanel(GridLayout(ROWBUTTONPANE, COLBUTTONPANE, SPACER, SPACER))
        centerPane.add(buttonsPane,BorderLayout.CENTER)

        var bottomPane = JPanel(BorderLayout(SPACER, SPACER))
        // Top Panel
        topWrite.add(JLabel(logo_img))
        val texttmp = JLabel("   "+StaticVals.Generics.NAME_MAIN_FRAME.toUpperCase())
        texttmp.font = Font("Curier", Font.BOLD, 30)
        topWrite.add(texttmp)
        // Buttons Panel
        buttonsPane.add(newPren)
        buttonsPane.add(elenc)
        buttonsPane.add(Stati)
        buttonsPane.add(fileLink)
        val LoadPane = JPanel(BorderLayout())
        val ttt = JPanel(FlowLayout())
        ttt.add(loading)
        LoadPane.add(loadingText, BorderLayout.NORTH)
        LoadPane.add(ttt, BorderLayout.CENTER)
        centerPane.add(LoadPane,BorderLayout.SOUTH)
        // Bottom Panel
        bottomPane.add(JLabel(StaticVals.Info.VERSION), BorderLayout.WEST)
        bottomPane.add(JLabel("<html><b>By DomySh</b> (<a href=\"mailto:" + StaticVals.Info.Contact.EMAIL + "\">"
                + StaticVals.Info.Contact.EMAIL + "</a>)<html>"), BorderLayout.EAST)
        // Adding Panels
        add(topWrite, BorderLayout.NORTH)
        add(centerPane, BorderLayout.CENTER)
        add(bottomPane, BorderLayout.SOUTH)
        // Set Buttons Functions
        newPren.addActionListener { MainFrame.setFrame(NewDonationPane()) }
        elenc.addActionListener { MainFrame.setFrame(ElencPane()) }
        Stati.addActionListener { MainFrame.setFrame(StatsPane()) }
        fileLink.addActionListener { MainFrame.setFrame(SettingsPane()) }
        // Load DB
        loaderStart()
    }
}