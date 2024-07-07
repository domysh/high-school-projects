package graphics

import backend.MainFrame
import data.*
import java.awt.*
import java.util.*
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.event.ChangeListener

/*
 * IMPLEMENTARE I CONTROLLI, AVVISI. -> ATTIVARE LE FUNZIONALITA DEI PULANTI
 * IMPLEMENTARE FUNZIONE DI GESTIONE FILE SU MODIFICA PRENOTAZIONE.
 * IMPLEMENTARE FUNZIONE DI GESTIONE FILE SULLA CANCELLAZIONE DELLA PRENOTAZIONE!
 */
class ModifyPane(private val prenotationSelected: PrenotationObject, last: JPanel) : JPanel(BorderLayout(STDSPACE, STDSPACE)) {
    private var center: PrenotationPane
    private var save: JButton
    private var back: JButton
    private var delete: JButton
    private var modLab: JLabel
    private var canAccessLab: JLabel
    private var oldPane: JPanel
    private var changed = false
    private var PRIDchanged = false
    private var canAccess = false
    private var IPOTETIC_LIST_PRT: Array<PrenotationObject?> = arrayOf()

    // OLD DATE SAVE
    private var OLD_PRID: String = ""
    private var OLD_NOTE: String = ""
    private var OLD_DATE: Date = Date(0)
    private var DON_BIRTH: Date = Date(0)
    private var OLD_DONE: Boolean
    private var OLD_ACCESS: Boolean
    private var OLD_HINIT: Int
    private var OLD_HEND: Int
    fun updateModified() {
        try {
            if (PftFile.getPRID(center.date!!, PftFile.getInternalFromPRID(OLD_PRID)!!) == OLD_PRID) center.pRID = OLD_PRID
            PRIDchanged = !center.pRID.equals(OLD_PRID, ignoreCase = true)
            changed = (center.getDonated() != OLD_DONE || center.getAccess() != OLD_ACCESS
                    || center.getNote() != OLD_NOTE
                    || !center.date.toString().contentEquals(OLD_DATE.toString())
                    || center.hFrom != OLD_HINIT || center.hTo != OLD_HEND)
        } catch (e: NullPointerException) {
            return
        } catch (e: NumberFormatException) {
            return
        }
        when {
            PRIDchanged -> {
                modLab.text = "SI (PRID)"
                modLab.foreground = Color.GREEN
            }
            changed -> {
                modLab.text = "SI"
                modLab.foreground = Color.GREEN
            }
            else -> {
                modLab.text = "NO"
                modLab.foreground = Color.RED
            }
        }
        if (center.date !== OLD_DATE) verifyConditions()
    }

    fun resetCanAccess() {
        canAccessLab.text = NONE_TEXT
        canAccessLab.foreground = Color.BLACK
    }

    fun verifyConditions() {
        val id = center.getIdDon()
        val agePermitted = PftFile.donatorAgePermitted(DON_BIRTH, center.date!!)
        if (id == -1) {
            canAccess = agePermitted
            canAccessLab.text = (if (canAccess) "SI" else "NO") + " (${StaticVals.Generics.ONLY_AGE})"
            if (canAccess) {
                canAccessLab.foreground = Color.GREEN
            } else {
                canAccessLab.foreground = Color.RED
            }
            return
        }
        canAccess = PftFile.dataPermitted(center.date!!, id!!, IPOTETIC_LIST_PRT.requireNoNulls()) && agePermitted
        if (!agePermitted) {
            canAccessLab.text = StaticVals.Generics.ILLEGAL_AGE
            canAccessLab.foreground = Color.RED
        } else if (canAccess) {
            canAccessLab.text = "SI"
            canAccessLab.foreground = Color.GREEN
        } else { // PftFile.
            canAccessLab.text = "NO"
            canAccessLab.foreground = Color.RED
        }
    }

    companion object {
        private val person_img = SettingsManager.getImageIconByImg(StaticVals.Img.PERSON)
        private val blood_img = SettingsManager.getImageIconByImg(StaticVals.Img.BLOOD_ICON)
        private val save_img = SettingsManager.getImageIconByImg(StaticVals.Img.SAVE_FLOPPY)
        private val delete_img = SettingsManager.getImageIconByImg(StaticVals.Img.DELETE_CROSS)
        private val back_img = SettingsManager.getImageIconByImg(StaticVals.Img.GO_BACK)

        // -----------------------------------------
        private const val WIDTH = 620
        private const val HEIGHT = 550
        private const val SPACER = "       "
        private const val STDSPACE = 12
        private const val SEPARE = "&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;"
        private val NONE_TEXT = StaticVals.Generics.NONE_TEXT
        private val NOT_PERMITTED = StaticVals.Generics.IMPOSSIBLE
    }

    init {
        preferredSize = Dimension(WIDTH, HEIGHT)
        oldPane = last
        // Taking Information about the prenotation
        val ID = prenotationSelected.iDDonator
        OLD_PRID = prenotationSelected.prid
        OLD_NOTE = prenotationSelected.note
        OLD_DATE = prenotationSelected.dateNoH
        OLD_DONE = prenotationSelected.done
        OLD_ACCESS = prenotationSelected.access
        OLD_HINIT = prenotationSelected.hInit
        OLD_HEND = prenotationSelected.hEnd
        DON_BIRTH = prenotationSelected.birth
        // CREATE STRINGS
        val name = prenotationSelected.name + " " + prenotationSelected.surename
        val phone = prenotationSelected.phone
        val birth = PftFile.fromDateToString(DON_BIRTH)
        val idDon = ID.toString()
        var liveplace = ""
        if (ID == -1) {
            liveplace += "<u><i>"
            liveplace += NOT_PERMITTED
            liveplace += "</i></u>"
        } else {
            liveplace += try {
                DBManagment.self.getLiveCity(ID).toUpperCase()
            } catch (e: NullPointerException) {
                NONE_TEXT
            }
            liveplace += " <b>in:</b> "
            liveplace += try {
                DBManagment.self.getLiveStreet(ID).toUpperCase()
            } catch (e: NullPointerException) {
                NONE_TEXT
            }
            liveplace += "."
        }
        // Taking Information of the prenotation
        val PRID = OLD_PRID
        var note = OLD_NOTE
        val date = PftFile.fromDateToString(OLD_DATE)
        val access = if (OLD_ACCESS) "SI" else "NO"
        val hPR = "$OLD_HINIT:00 -> $OLD_HEND:00"
        val done = if (OLD_DONE) "SI" else "NO"
        if (note == "") {
            note = "<html><u><i>${StaticVals.Generics.NO_NOTE}</i></u></html>"
        }

        // CREATE IPOTETIC LIST OF PRENOTATION
        if (ID != -1) {
            val elenc = PftFile.filterTotal(PftFile.IDDON_KEY, ID)
            var ind = 0
            while (ind < elenc.size) {
                if (elenc[ind].dateNoH.toString() == OLD_DATE.toString()) break
                ind++
            }
            IPOTETIC_LIST_PRT = arrayOfNulls(elenc.size - 1)
            var countVet2 = 0
            for (i in IPOTETIC_LIST_PRT.indices) {
                if (countVet2 == ind) countVet2++
                IPOTETIC_LIST_PRT[i] = elenc[countVet2]
                countVet2++
            }
        } else IPOTETIC_LIST_PRT = arrayOf()
        var servicelayout = JPanel(BorderLayout())
        var servicelayout2 = JPanel(BorderLayout(STDSPACE, STDSPACE))
        var servicelayout3 = JPanel(BorderLayout())
        val servicelayout4 = JPanel(BorderLayout())
        add(servicelayout, BorderLayout.NORTH)
        // Donator Info
        servicelayout.add(servicelayout2, BorderLayout.NORTH)
        // TITLE
        servicelayout2.add(servicelayout3, BorderLayout.NORTH)
        servicelayout3.layout = FlowLayout(FlowLayout.CENTER)
        servicelayout3.add(JLabel(person_img))
        servicelayout3.add(
                JLabel("<html><b>&nbsp;&nbsp;Informazioni donatore" + SEPARE + "ID: </b>" + idDon + "</html>"))
        // INFORMATIONS
        servicelayout3 = JPanel(GridLayout(4, 1))
        servicelayout2.add(servicelayout3, BorderLayout.CENTER)
        servicelayout3.add(JLabel("<html><b>Nome: </b>$name</html>"))
        servicelayout3.add(JLabel("<html><b>Nato il: </b>$birth</html>"))
        servicelayout3.add(JLabel("<html><b>Telefono: </b>$phone</html>"))
        servicelayout3.add(JLabel("<html><b>Vive a: </b>$liveplace</html>"))
        // SPACER
        servicelayout2.add(JLabel(SPACER), BorderLayout.WEST)
        servicelayout2.add(JLabel(SPACER), BorderLayout.EAST)
        servicelayout2 = JPanel(BorderLayout(STDSPACE, STDSPACE))
        servicelayout3 = JPanel(BorderLayout())

        // Prenotation Info
        servicelayout.add(servicelayout2, BorderLayout.SOUTH)
        // TITLE
        servicelayout2.add(servicelayout3, BorderLayout.NORTH)
        servicelayout3.layout = FlowLayout(FlowLayout.CENTER)
        servicelayout3.add(JLabel(blood_img))
        servicelayout3.add(JLabel(
                "<html><b>&nbsp;&nbsp;Informazioni prenotazione" + SEPARE + "PRID: </b>" + PRID + "</html>"))
        // INFORMATIONS
        servicelayout3 = JPanel(GridLayout(3, 1))
        servicelayout2.add(servicelayout3, BorderLayout.CENTER)
        servicelayout3.add(JLabel("<html><b>Data: </b>$date$SEPARE$hPR</html>"))
        servicelayout3.add(JLabel("<html><b>Note: </b>$note</html>"))
        servicelayout3.add(servicelayout4)
        servicelayout4.layout = FlowLayout(FlowLayout.CENTER, 0, 0)
        servicelayout4
                .add(JLabel("<html><b>Accesso: </b>$access$SEPARE<b>Donato: </b>$done</html>"))
        // SPACER
        servicelayout2.add(JLabel(SPACER), BorderLayout.WEST)
        servicelayout2.add(JLabel(SPACER), BorderLayout.EAST)

        // MAIN SPACER
        add(JLabel(SPACER), BorderLayout.WEST)
        add(JLabel(SPACER), BorderLayout.EAST)

        // Center Panel
        center = PrenotationPane()
        add(center, BorderLayout.CENTER)
        // Set This dates
        center.setIdDon(ID)
        center.setAccess(OLD_ACCESS)
        center.setDonated (OLD_DONE)
        center.setNote(OLD_NOTE)
        center.hFrom = OLD_HINIT
        center.hTo = OLD_HEND
        center.date = OLD_DATE
        center.pRID = OLD_PRID
        servicelayout = JPanel(GridLayout(1, 2))
        servicelayout2 = JPanel(GridLayout(1, 3))
        // servicelayout4 = new JPanel(new BorderLayout());

        // Bottom Panel
        add(servicelayout, BorderLayout.SOUTH)
        servicelayout.add(servicelayout2)
        save = JButton(save_img)
        //save.background = Color.WHITE;
        back = JButton(back_img)
        //back.background = Color.WHITE;
        delete = JButton(delete_img)
        //delete.background = Color.WHITE;
        // SET BUTTONS
        servicelayout3 = JPanel(FlowLayout(FlowLayout.CENTER, 0, 0))
        servicelayout2.add(servicelayout3)
        servicelayout3.add(save)
        servicelayout3 = JPanel(FlowLayout(FlowLayout.CENTER, 0, 0))
        servicelayout2.add(servicelayout3)
        servicelayout3.add(delete)
        servicelayout3 = JPanel(FlowLayout(FlowLayout.CENTER, 0, 0))
        servicelayout2.add(servicelayout3)
        servicelayout3.add(back)
        // servicelayout2.add(save);servicelayout2.add(delete);servicelayout2.add(back);
        servicelayout3 = JPanel(FlowLayout(FlowLayout.LEFT, 0, 0))
        servicelayout2 = JPanel(GridLayout(2, 1))
        servicelayout.add(servicelayout2)
        // First Service Info
        servicelayout2.add(servicelayout3)
        servicelayout3.add(JLabel("<html><b>Modificato: </b></html>"))
        modLab = JLabel(NONE_TEXT)
        servicelayout3.add(modLab)
        // Second Service Info
        servicelayout3 = JPanel(FlowLayout(FlowLayout.LEFT, 0, 0))
        servicelayout2.add(servicelayout3)
        servicelayout3.add(JLabel("<html><b>Idoneo: </b></html>"))
        canAccessLab = JLabel(NONE_TEXT)
        servicelayout3.add(canAccessLab)

        // Buttons
        save.addActionListener {
            if ((changed || PRIDchanged) && canAccess) {
                var name_: String? = null
                var surename_: String? = null
                var birth_: String? = null
                var phone_: String? = null
                if (prenotationSelected.iDDonator == -1) {
                    name_ = prenotationSelected.name
                    surename_ = prenotationSelected.surename
                    birth_ = PftFile.fromDateToString(prenotationSelected.birth)
                    phone_ = prenotationSelected.phone
                }
                if (PftFile.modifyPR(prenotationSelected,
                                PrenotationObject(center.getIdDon()!!, center.getAccess(), center.getDonated(), center.getNote(),
                                        center.dateDay, center.dateMonth, center.dateYear, center.hFrom,
                                        center.hTo, PftFile.getInternalFromPRID(center.pRID)!!, name_ , surename_ ,
                                        PftFile.fromStringToDate(birth_) , phone_ ))) {
                    MainFrame.noticePane(StaticVals.Generics.PRENOTATION_MODIFIED)
                } else {
                    MainFrame.createJError(StaticVals.Generics.Errors.PRENOTATION_WRITE)
                }
                MainFrame.setFrame(oldPane)
            } else if (!canAccess) {
                MainFrame.noticePane(StaticVals.Generics.DATE_NOT_PERMITTED)
            } else {
                MainFrame.noticePane(StaticVals.Generics.PRENOTATION_NOT_MODIFIED)
            }
        }
        back.addActionListener {
            if (changed || PRIDchanged) {
                MainFrame.choosePane(StaticVals.Generics.SURE_TO_IGNORE, { MainFrame.setFrame(oldPane) }) {}
            } else {
                MainFrame.setFrame(oldPane)
            }
        }
        delete.addActionListener {
            MainFrame.choosePane(StaticVals.Generics.SURE_TO_DELETE_PRENOTATION, {
                PftFile.deletePR(prenotationSelected)
                MainFrame.setFrame(oldPane)
            }) {}
        }

        // CENTRAL PANE
        center.onDataPickerChange { updateModified() }
        center.onHChange (ChangeListener { updateModified() })
        center.onNoteChange { updateModified() }
        center.onAccessChange { updateModified() }
        center.onDonatedChange { updateModified() }

        // Update State
        updateModified()
        verifyConditions()
        revalidate()
    }
}