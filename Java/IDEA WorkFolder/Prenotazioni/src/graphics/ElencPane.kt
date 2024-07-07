package graphics

import backend.MainFrame
import com.toedter.calendar.JDateChooser
import data.PftFile
import data.PrenotationObject
import data.SettingsManager
import data.StaticVals
import java.awt.*
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.util.*
import javax.swing.*

class ElencPane : JPanel() {
    private val donSearch: SearchDonatorPane
    private val donSearchState: JCheckBox
    private val searchPRIDState: JCheckBox
    private val todayDateState: JCheckBox
    private val filterInH: JCheckBox
    private val idDonSelected: JLabel
    private val dataSearch: JDateChooser
    private val searchPRID: JTextFieldClickIntegrated
    private val prenotationPanel: JScrollPane
    private val prenotList: JList<String?>
    private var list: Vector<String>
    private val hFrom: JSpinner
    private val hTo: JSpinner
    private fun modifyPaneActive(PRID: String) {
        MainFrame.setFrame(ModifyPane(PftFile.serachWithPRID(PRID)[0], this))
    }

    private fun setState(s: Boolean, g: JCheckBox) {
        if (s != g.isSelected) {
            g.doClick()
        }
    }

    private fun searchUpdate() {
        list.clear()
        // Generazione della lista
        val withDon = (donSearchState.isSelected && idDonSelected.text !== NONE_TEXT
                && idDonSelected.text != null)
        val withPRID = searchPRIDState.isSelected
        val withToday = todayDateState.isSelected
        val withDate = dataSearch.isEnabled
        val withHour = filterInH.isSelected
        if (withPRID) {
            val writedPRID = searchPRID.text
            if (writedPRID != null && writedPRID !== "") {
                if (PftFile.pridExist(writedPRID)) {
                    val elements = PftFile.serachWithPRID(writedPRID)
                    for (ele in elements) list.add(listEleString(ele))
                } else {
                    list.add("PRID: $writedPRID ${StaticVals.Generics.NOT_VALID}")
                }
            }
        } else {
            var res: Array<PrenotationObject>? = null

            if (withDon) res = PftFile.filterTotal(PftFile.IDDON_KEY, idDonSelected.text.toInt())

            if (withToday) {

                res = if (res != null) PftFile.filterFromDateInResults(Date(), res).requireNoNulls()
                      else PftFile.listerFromDate(Date()).requireNoNulls()

            } else if (withDate) {

                if (res != null) {

                    if(dataSearch.date!=null) res = PftFile.filterWithDateInResults(dataSearch.date, res)

                } else {

                    res = if(dataSearch.date==null) PftFile.listerTotal()
                          else PftFile.listerInDate(dataSearch.date)
                }

            }
            if (withHour) {
                if (res != null) {
                    res = PftFile.filterResults(PftFile.ENDH_KEY, hTo.value, res)
                    res = PftFile.filterResults(PftFile.INITH_KEY, hFrom.value, res)
                } else {
                    res = PftFile.filterResults(PftFile.ENDH_KEY, hTo.value,
                            PftFile.filterTotal(PftFile.INITH_KEY, hFrom.value))
                }
            }
            if (res != null) {
                PftFile.listPrenotationSorter(res)
                list = fromPFTToVectorString(res)
            }

        }
        // Set Graphical List
        try {
            if (list.size == 0) {
                list.add(StaticVals.Generics.NO_PRENOTATION)
            } // Se Vuoto
            setList(list)
        } catch (e: ArrayIndexOutOfBoundsException) {}
        prenotList.revalidate()
    }

    fun fromPFTToVectorString(ele: Array<PrenotationObject>): Vector<String> {
        val res = Vector<String>()
        for (o in ele) {
            res.add(listEleString(o))
        }
        return res
    }

    private fun setList(v: Vector<String>) {
        val res = arrayOfNulls<String>(v.size)
        for (i in v.indices) res[i] = v[i]
        prenotList.setListData(res)
    }

    private fun listEleString(obj: PrenotationObject): String {
        var res = ""
        res += obj.iDDonator.toString() + ": "
        res += "<b>"
        res += obj.name.toUpperCase() + " " + obj.surename.toUpperCase()
        res += "</b>"
        var spacer = ""
        for (i in 0 until DISTANCE - (res.length - 7)) spacer += "&nbsp;"
        res += " $spacer"
        res += ("Nato:" + PftFile.fromDateToString(obj.birth) + " | Tel:<b>"
                + obj.phone + "</b>")
        res += "<br/>"
        val secondPart = (obj.prid + " -> <b>" + obj.day + "-" + obj.month + "-"
                + obj.year + "</b> | Dalle " + obj.hInit + " alle " + obj.hEnd
                + " |")
        spacer = ""
        for (i in 0 until DISTANCE - (secondPart.length - 7)) {
            spacer += "&nbsp;"
        }
        res += secondPart + " " + spacer + "Accesso: "
        res += "<b>"
        res += if (obj.access) {
            "SI"
        } else {
            "NO"
        }
        res += "</b>"
        res += " | Donato: "
        res += "<b>"
        res += if (obj.done) {
            "SI"
        } else {
            "NO"
        }
        res += "</b>"
        return "<html>$res</html>"
    }


    private companion object {
        const val DISTANCE = 48
        const val WIDTH = 650
        const val HEIGHT = 460
        const val WIDTHBB = 650
        const val HEIGHTBB = 680
        const val NONE_TEXT = "None"
        fun pridStringValidator(inputText : String):String {
            val job = inputText.toUpperCase()
            var res = ""
            analysisLoop@for (i in job.indices){
                when(i){
                    in 0..3->{
                        if (job[i] in '0'..'9') res+= job[i]
                        else break@analysisLoop
                    }
                    in 4..7->{
                        if(job[i] in 'A'..'Z') res+= job[i]
                        else break@analysisLoop
                    }
                    8 -> {
                        if (job[i] in '0'..'2')res+= job[i]
                        else break@analysisLoop
                    }
                    9 -> {
                        if (job[i] in '0'..'9') res+= job[i]
                        else break@analysisLoop
                    }
                    else -> break@analysisLoop
                }
            }
            return res
        }

    }

    // Inserire ricerca per fasce orarie
    init {
        layout = BorderLayout()
        preferredSize = Dimension(WIDTH, HEIGHT)
        // SELECT TO SEE SEARCH FOR DONATORS
        val setDonatorSearch = JPanel(BorderLayout())
        add(setDonatorSearch, BorderLayout.NORTH)
        donSearch = SearchDonatorPane(false)
        donSearch.isVisible = false
        setDonatorSearch.add(donSearch, BorderLayout.NORTH)
        donSearchState = JCheckBox()
        var servicelayout = JPanel(FlowLayout())
        setDonatorSearch.add(servicelayout, BorderLayout.SOUTH)
        servicelayout.add(donSearchState)
        servicelayout.add(JLabel("Ricerca per donatore"))
        servicelayout.add(JLabel("| ID:"))
        idDonSelected = JLabel(NONE_TEXT)
        servicelayout.add(idDonSelected)

        // RESULT PANEL
        prenotList = JList()
        list = Vector()
        prenotationPanel = JScrollPane(prenotList)
        setList(list)
        prenotList.layoutOrientation = JList.VERTICAL
        prenotList.selectionMode = ListSelectionModel.SINGLE_SELECTION
        prenotList.font = Font(Font.MONOSPACED, Font.PLAIN, 12)
        add(prenotationPanel, BorderLayout.CENTER)

        // Chooser Panel
        val chooseSettings = JPanel(FlowLayout(FlowLayout.CENTER, 40, 10))
        add(chooseSettings, BorderLayout.SOUTH)
        // PULSANTE HOME
        servicelayout = JPanel(GridLayout(2, 1, 10, 10))
        val home = JButton("HOME")
        servicelayout.add(home)
        chooseSettings.add(servicelayout)
        // TERZA RIGA
        var servicelayout2 = JPanel(FlowLayout(FlowLayout.CENTER, 10, 0))
        servicelayout.add(servicelayout2)
        servicelayout2.add(JLabel("Ora:"))
        filterInH = JCheckBox()
        servicelayout2.add(filterInH)
        hFrom = JSpinner(SpinnerNumberModel(0, 0, 24, 1))
        servicelayout2.add(hFrom, BorderLayout.SOUTH)
        hTo = JSpinner(SpinnerNumberModel(0, 0, 24, 1))
        servicelayout2.add(hTo, BorderLayout.SOUTH)

        // IMPOSTAZIONI RICERCA
        servicelayout = JPanel(GridLayout(2, 1, 10, 10))
        chooseSettings.add(servicelayout)
        servicelayout2 = JPanel(FlowLayout())
        servicelayout.add(servicelayout2)
        // PRIMA RIGA
        searchPRIDState = JCheckBox()
        searchPRID = JTextFieldClickIntegrated(12)
        searchPRID.isEnabled = false
        servicelayout2.add(JLabel("PRID:"))
        servicelayout2.add(searchPRIDState)
        servicelayout2.add(searchPRID)

        // SECONDA RIGA
        servicelayout2 = JPanel(FlowLayout(FlowLayout.CENTER, 10, 0))
        servicelayout.add(servicelayout2)
        servicelayout2.add(JLabel("Da Oggi:"))
        todayDateState = JCheckBox()
        servicelayout2.add(todayDateState)
        dataSearch = JDateChooser()
        dataSearch.font = Font(Font.MONOSPACED, Font.PLAIN, 10)
        dataSearch.preferredSize = Dimension(120, 25)
        dataSearch.dateFormatString = SettingsManager.dataFormat
        servicelayout2.add(dataSearch)
        hFrom.isEnabled = false
        hTo.isEnabled = false
        hFrom.addChangeListener {
            val `val` = hFrom.value as Int
            val ref = hTo.value as Int
            if (`val` > ref - 1) {
                if (`val` < 24) {
                    hTo.value = `val` + 1
                } else {
                    hTo.value = `val`
                }
            }
            searchUpdate()
        }
        hTo.addChangeListener {
            val `val` = hTo.value as Int
            val ref = hFrom.value as Int
            if (`val` < ref) {
                hFrom.value = `val`
                hTo.value = `val`
            }
            searchUpdate()
        }

        // SET LISTENER
        home.addActionListener { MainFrame.setFrame(MenuPane()) }
        donSearchState.addActionListener {
            val active = donSearchState.isSelected
            donSearch.isVisible = active
            if (active) {
                setState(false, searchPRIDState)
                preferredSize = Dimension(WIDTHBB, HEIGHTBB)
                MainFrame.setBounds(WIDTHBB, HEIGHTBB)
            } else {
                idDonSelected.text = NONE_TEXT
                preferredSize = Dimension(WIDTH, HEIGHT)
                MainFrame.setBounds(WIDTH, HEIGHT)
                donSearch.reset()
                searchUpdate()
            }
        }
        searchPRIDState.addActionListener {
            val active = searchPRIDState.isSelected
            if (active) {
                setState(false, todayDateState)
                setState(false, donSearchState)
                setState(false, filterInH)
                dataSearch.date = null
            } else {
                searchPRID.text = ""
            }
            dataSearch.isEnabled = !active
            searchPRID.isEnabled = active
            searchUpdate()
        }
        todayDateState.addActionListener {
            val sel = todayDateState.isSelected
            dataSearch.isEnabled = !sel
            if (sel) {
                setState(false, searchPRIDState)
                dataSearch.date = null
            }
            searchUpdate()
        }
        dataSearch.dateEditor.addPropertyChangeListener{ act ->
            if (act.propertyName == "date") {
                searchUpdate()
            }
        }
        todayDateState.addActionListener {
            val sel = todayDateState.isSelected
            dataSearch.isEnabled = !sel
            if (sel) {
                setState(false, searchPRIDState)
                dataSearch.date = null
            }
            searchUpdate()
        }
        filterInH.addActionListener {
            val sel = filterInH.isSelected
            hTo.isEnabled = sel
            hFrom.isEnabled = sel
            if (sel) {
                setState(false, searchPRIDState)
            } else {
                hTo.value = 0
                hFrom.value = 0
            }
            searchUpdate()
        }
        donSearch.onSelected { id: Int? ->
            if (id != null) idDonSelected.text = id.toString() else idDonSelected.text = NONE_TEXT
            searchUpdate()
        }

        val pridSearchUpdate: () -> Unit = {
            searchPRID.text = pridStringValidator(searchPRID.text)
            searchUpdate()
        }

        searchPRID.addKeyListener(object: KeyListener {
            override fun keyPressed(e: KeyEvent?) {}
            override fun keyTyped(e: KeyEvent?) {}
            override fun keyReleased(e: KeyEvent) {
                if (!e.isActionKey && !e.isAltDown && !e.isAltGraphDown && !e.isControlDown && !e.isMetaDown) {pridSearchUpdate()}
            }})
        searchPRID.addPropertyChangeListener {pridSearchUpdate()}

        prenotList.addMouseListener(object : MouseListener {
            override fun mouseClicked(e: MouseEvent) {
                if (e.clickCount == 2) {
                    val n = prenotList.locationToIndex(e.point)
                    try {
                        val PRID = list[n].split("<br/>".toRegex()).toTypedArray()[1].split(" ".toRegex()).toTypedArray()[0]
                        modifyPaneActive(PRID)
                    } catch (err: IndexOutOfBoundsException) {
                    }
                }
            }

            override fun mousePressed(e: MouseEvent) {}
            override fun mouseReleased(e: MouseEvent) {}
            override fun mouseEntered(e: MouseEvent) {}
            override fun mouseExited(e: MouseEvent) {}
        })
    }
}