package graphics

import com.toedter.calendar.JDateChooser
import data.PftFile.genPRID
import data.PftFile.getDay
import data.PftFile.getMonth
import data.PftFile.getMonthFromPRID
import data.PftFile.getYear
import data.PftFile.getYearFromPRID
import java.awt.*
import java.awt.event.FocusListener
import java.beans.PropertyChangeEvent
import java.lang.IllegalStateException
import java.util.*
import javax.swing.*
import javax.swing.event.ChangeListener
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import kotlin.properties.Delegates

class PrenotationPane : JPanel() {
    private var idDon: JLabel by Delegates.notNull()
    private var prid: JLabel by Delegates.notNull()
    private var note: JTextFieldClickIntegrated by Delegates.notNull()
    private var access: JCheckBox by Delegates.notNull()
    private var donated: JCheckBox by Delegates.notNull()
    private var hFromPik: JSpinner by Delegates.notNull()
    private var hToPik: JSpinner by Delegates.notNull()
    private var dataPicker: JDateChooser by Delegates.notNull()
    private var actualPRID: String? = null
    var updatePRID = {

        // PRID control
        val year = dateYear
        val month = dateMonth
        val YearIsSame = (year == getYearFromPRID(actualPRID))
        val MonthIsSame = (month == getMonthFromPRID(actualPRID))
        if (year == -1 || month == -1) {
            actualPRID = ""
            prid.text = NONE_TEXT
        } else if (!YearIsSame || !MonthIsSame) {
            actualPRID = genPRID(month, year)
            prid.text = actualPRID
        }
    }

    var date: Date?
        get() = dataPicker.date
        set(d) {
            dataPicker.date = d
        }

    val dateYear: Int
        get(){
            return try {
                getYear(dataPicker.date)
            }catch(e:IllegalStateException){
                -1
            }
        }

    val dateMonth: Int
        get(){
            return try {
                getMonth(dataPicker.date)
            }catch(e:IllegalStateException){
                -1
            }
        }

    val dateDay: Int
        get(){
            return try {
                getDay(dataPicker.date)
            }catch(e:IllegalStateException){
                -1
            }
        }

    fun setIdDon(iddon: Int) {
        idDon.text = iddon.toString()
    }

    fun getIdDon(): Int? {
        return if (idDon.text === NONE_TEXT) {
            null
        } else Integer.valueOf(idDon.text)
    }

    fun onHFromChange(a: ChangeListener?) {
        hFromPik.addChangeListener(a)
    }

    var hFrom: Int
        get() = hFromPik.value as Int
        set(v) {
            hFromPik.value = v
        }

    fun onHToChange(a: ChangeListener?) {
        hToPik.addChangeListener(a)
    }

    var hTo: Int
        get() = hToPik.value as Int
        set(v) {
            hToPik.value = v
        }

    fun onHChange(l: ChangeListener?) {
        onHFromChange(l)
        onHToChange(l)
    }

    fun onNoteFocus(f: FocusListener?) {
        note.addFocusListener(f)
    }

    fun getNote(): String {
        return note.text
    }

    fun setNote(a: String?) {
        note.text = a
    }

    fun onDocumentNote(l: DocumentListener?) {
        note.document.addDocumentListener(l)
    }

    fun onNoteChange(go:() -> Unit) {
        note.document.addDocumentListener(object : DocumentListener {
            override fun removeUpdate(e: DocumentEvent) {
                go()
            }

            override fun insertUpdate(e: DocumentEvent) {
                go()
            }

            override fun changedUpdate(e: DocumentEvent) {
                go()
            }
        })
    }

    fun onDataPickerChange(go:() -> Unit) {
        dataPicker.dateEditor.addPropertyChangeListener { d: PropertyChangeEvent ->
            if (d.propertyName !== "graphicsConfiguration" && d.propertyName !== "ancestor") {
                go()
            }
        }
    }

    var pRID: String?
        get() {
            updatePRID()
            return prid.text
        }
        set(a) {
            updatePRID()
            prid.text = a
            actualPRID = a
        }

    fun getAccess(): Boolean {
        return access.isSelected
    }

    fun setAccess(b: Boolean) {
        access.isSelected = b
    }

    fun onAccessChange(go:() -> Unit) {
        access.addChangeListener { go() }
    }

    fun getDonated(): Boolean {
        return donated.isSelected
    }

    fun setDonated(b: Boolean) {
        donated.isSelected = b
    }

    fun onDonatedChange(go:() -> Unit) {
        donated.addChangeListener { go() }
    }

    fun resetIdDon() {
        idDon.text = NONE_TEXT
    }

    fun resetAccess() {
        access.isSelected = false
    }

    fun resetDonated() {
        donated.isSelected = false
    }

    fun resetNote() {
        note.text = ""
    }

    fun resetPRID() {
        prid.text = NONE_TEXT
        prid.foreground = Color.black
    }

    fun resetDataPicker() {
        dataPicker.calendar = null
    }

    fun resetHFrom() {
        hFromPik.value = 0
    }

    fun resetHTo() {
        hToPik.value = 0
    }

    fun reset() {
        resetIdDon()
        resetAccess()
        resetDonated()
        resetNote()
        resetPRID()
        resetDataPicker()
        resetHFrom()
        resetHTo()
    }

    companion object {
        private const val NONE_TEXT = "None"
    }

    init {
        layout = GridLayout(2, 4, 20, 20)
        var servicelayout = JPanel(BorderLayout())
        add(servicelayout)
        servicelayout.add(JLabel("ID"), BorderLayout.CENTER)
        idDon = JLabel(NONE_TEXT)
        servicelayout.add(idDon, BorderLayout.SOUTH)
        servicelayout = JPanel(BorderLayout())
        add(servicelayout)
        servicelayout.add(JLabel("Accesso"), BorderLayout.CENTER)
        access = JCheckBox()
        servicelayout.add(access, BorderLayout.SOUTH)
        servicelayout = JPanel(BorderLayout())
        add(servicelayout)
        servicelayout.add(JLabel("Donato"), BorderLayout.CENTER)
        donated = JCheckBox()
        servicelayout.add(donated, BorderLayout.SOUTH)
        servicelayout = JPanel(BorderLayout())
        add(servicelayout)
        servicelayout.add(JLabel("Note"), BorderLayout.CENTER)
        note = JTextFieldClickIntegrated()
        servicelayout.add(note, BorderLayout.SOUTH)
        servicelayout = JPanel(BorderLayout())
        add(servicelayout)
        servicelayout.add(JLabel("PRID"), BorderLayout.CENTER)
        prid = JLabel(NONE_TEXT)
        prid.font = Font("Arial", Font.BOLD, 12)
        servicelayout.add(prid, BorderLayout.SOUTH)
        servicelayout = JPanel(BorderLayout())
        add(servicelayout)
        servicelayout.add(JLabel("Data"), BorderLayout.CENTER)
        dataPicker = JDateChooser()
        dataPicker.font = Font(Font.MONOSPACED, Font.PLAIN, 10)
        dataPicker.preferredSize = Dimension(120, 25)
        dataPicker.dateFormatString = data.SettingsManager.dataFormat
        servicelayout.add(dataPicker, BorderLayout.SOUTH)
        servicelayout = JPanel(BorderLayout())
        add(servicelayout)
        servicelayout.add(JLabel("Dalle ore"), BorderLayout.CENTER)
        hFromPik = JSpinner(SpinnerNumberModel(0, 0, 24, 1))
        servicelayout.add(hFromPik, BorderLayout.SOUTH)
        servicelayout = JPanel(BorderLayout())
        add(servicelayout)
        servicelayout.add(JLabel("alle"), BorderLayout.CENTER)
        hToPik = JSpinner(SpinnerNumberModel(0, 0, 24, 1))
        servicelayout.add(hToPik, BorderLayout.SOUTH)
        hFromPik.addChangeListener {
            val `val` = hFromPik.value as Int
            val ref = hToPik.value as Int
            if (`val` > ref - 1) {
                if (`val` < 24) {
                    hToPik.value = `val` + 1
                } else {
                    hToPik.value = `val`
                }
            }
        }
        hToPik.addChangeListener {
            // setSave(true);
            val `val` = hToPik.value as Int
            val ref = hFromPik.value as Int
            if (`val` < ref) {
                hFromPik.value = `val`
                hToPik.value = `val`
            }
        }

        // Listener to checkbox
        access.addChangeListener { if (!access.isSelected) donated.isSelected = false }
        donated.addChangeListener { if (donated.isSelected) access.isSelected = true }
        dataPicker.dateEditor.addPropertyChangeListener {act ->
            if (act.propertyName == "date") {
                // To Save Setter
                updatePRID()
            }
        }
    }
}