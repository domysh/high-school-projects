package graphics

import data.*
import backend.*
import java.awt.*
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.util.*
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.event.ChangeListener
import kotlin.properties.Delegates

class NewDonationPane : JPanel {
    private var bottom: JPanel by Delegates.notNull()
    private var center: PrenotationPane by Delegates.notNull()
    private var top: SearchDonatorPane by Delegates.notNull()
    private var lastDonation: JLabel by Delegates.notNull()
    private var canAccess: JLabel by Delegates.notNull()
    private var canAccessFrom: JLabel by Delegates.notNull()
    private var toSaveLabel: JLabel by Delegates.notNull()
    private var nextPrenotLabel: JLabel by Delegates.notNull()
    private var phoneFinded: JLabel by Delegates.notNull()
    private var lastDonationDate: Date? = null
    private var nextDonationDate: Date? = null
    private var nextPrenotationDate: Date? = null
    private var toSave = false
    private var donatCanDonate = false
    private var canSave = false
    private fun wantSave() {
        MainFrame.choosePane(StaticVals.Generics.WANT_SAVE_FIRST, { graphicSave() }) { MainFrame.setFrame(MenuPane()) }
    }

    private fun setLastDonationDate(d: Date?) {
        lastDonationDate = d
        if (center.getIdDon() != null && center.getIdDon() == -1) {
            lastDonation.text = NOT_PERMITTED
            lastDonationDate = null
        } else if (lastDonationDate != null)
            lastDonation.text = PftFile.fromDateToString(lastDonationDate)
        else
            lastDonation.text = NONE_TEXT
    }

    private fun setNextDonationDate() {
        if (center.getIdDon() != null && center.getIdDon() == -1) {
            nextDonationDate = null
            canAccessFrom.text = NOT_PERMITTED
        } else if (lastDonationDate == null) {
            nextDonationDate = null
            canAccessFrom.text = NONE_TEXT
        } else {
            nextDonationDate = PftFile.sumDayDate(lastDonationDate, PftFile.NEXT_DAY_FORM_DONATION)
            canAccessFrom.text = PftFile.fromDateToString(nextDonationDate)
        }
    }

    private fun setNextPrenotationDate() {
        if (center.getIdDon() != null && center.getIdDon() == -1) {
            nextDonationDate = null
            nextPrenotLabel.text = NOT_PERMITTED
        } else if (center.getIdDon() != null) {
            val don = center.getIdDon()!!
            val res = PftFile.filterFromDate(Date(), PftFile.IDDON_KEY, don)
            var minEle: PrenotationObject? = null
            var compareTo: PrenotationObject?
            for (i in res.indices) {
                compareTo = res[i]
                if (minEle == null) {
                    minEle = compareTo
                    nextPrenotationDate = minEle!!.date
                } else {
                    val a = minEle.date
                    val b = compareTo!!.date
                    if (b.before(a)) {
                        minEle = compareTo
                        nextPrenotationDate = b
                    }
                }
            }
            if (minEle != null) {
                nextPrenotLabel.text = PftFile.fromDateToString(nextPrenotationDate)
            } else {
                nextPrenotationDate = null
                nextPrenotLabel.text = NONE_TEXT
            }
        }
    }

    fun setPhoneNumber() {
        if (center.getIdDon() != null && center.getIdDon() == -1) {
            phoneFinded.text = NOT_PERMITTED
        } else if (center.getIdDon() == null) {
            phoneFinded.text = NONE_TEXT
        } else {
            val find = try{DBManagment.self.getPhone(center.getIdDon()!!)}catch(e:IllegalStateException){null}
            if (find === "" || find == null) {
                phoneFinded.text = NONE_TEXT
            } else {
                phoneFinded.text = find
            }
        }
    }

    fun setCanDonateFlag() {
        if (center.getIdDon() == null || center.date == null) {
            canAccessReset()
            return
        }
        val dataPermitted: Boolean
        val nextPrenotationValidation: Boolean
        val agePermitted: Boolean
        val birth_don: Date = if (center.getIdDon() == -1) {
            if (top.birthDate != null)
                top.birthDate!!
            else {
                canAccessReset()
                return
            }
        } else DBManagment.self.getBirthDate(center.getIdDon()!!)
        agePermitted = PftFile.donatorAgePermitted(birth_don, center.date!!)
        if (center.getIdDon() == -1) {
            donatCanDonate = agePermitted
            canAccess.text = (if (donatCanDonate) "SI" else "NO") + " (${StaticVals.Generics.ONLY_AGE})"
            if (donatCanDonate) {
                canAccess.foreground = Color.GREEN
            } else {
                canAccess.foreground = Color.RED
            }
        } else {
            dataPermitted = PftFile.verifyDistanceLastDonation(center.date!!, center.getIdDon()!!)
            nextPrenotationValidation = PftFile.verifyDistance(center.date!!, center.getIdDon()!!)
            if (dataPermitted && nextPrenotationValidation && agePermitted) {
                donatCanDonate = true
                canAccess.text = "SI"
                canAccess.foreground = Color.GREEN
            } else {
                donatCanDonate = false
                if (!agePermitted) {
                    canAccess.text = StaticVals.Generics.ILLEGAL_AGE
                    canAccess.foreground = Color.RED
                } else if (!nextPrenotationValidation && dataPermitted) {
                    canAccess.text = StaticVals.Generics.DATE_CONFLICT
                    canAccess.foreground = Color.RED
                } else {
                    canAccess.text = "NO"
                    canAccess.foreground = Color.RED
                }
            }
        }
    }

    fun canAccessReset() {
        canAccess.text = NONE_TEXT
        canAccess.foreground = Color.BLACK
    }

    fun setSave(what: Boolean) {
        toSave = what
        if (toSave) {
            toSaveLabel.text = StaticVals.Generics.TO_SAVE
            toSaveLabel.foreground = Color.RED
        } else {
            toSaveLabel.text = StaticVals.Generics.SAVED
            toSaveLabel.foreground = Color.GREEN
        }
    }

    fun graphicSave() {
        if (saveDates()) {
            resetPane()
            setSave(false)
        } else if (!canSave) {
            MainFrame.noticePane(StaticVals.Generics.COMPLETE_PARAMETHERS)
        } else {
            MainFrame.noticePane(StaticVals.Generics.DATE_NOT_PERMITTED)
        }
    }

    fun saveDates(): Boolean {
        canSave = (center.getIdDon() != null && center.pRID !== NONE_TEXT && center.dateYear != -1
                && center.dateMonth != -1 && center.dateDay != -1)
        if (canSave && center.getIdDon() == -1) {
            canSave = (top.name !== "" && top.getSurename() !== "" && top.phone !== "" && top.getBirth() != null)
        }
        if (canSave && donatCanDonate) {
            val birth = if(top.birthDate == null) Date(0) else top.birthDate!!
            PftFile.addNewDataMap(PrenotationObject(center.getIdDon()!!, center.getAccess(), center.getDonated(), center.getNote(),
                    center.dateDay, center.dateMonth, center.dateYear, center.hFrom,
                    center.hTo, PftFile.getInternalFromPRID(center.pRID)!!, top.name,
                    top.getSurename(), birth, top.phone))
            return true
        }
        return false
    }

    private fun resetPane() {
        top.reset()
        center.reset()
        nextPrenotLabel.text = NONE_TEXT
        lastDonation.text = NONE_TEXT
        canAccessReset()
        canAccessFrom.text = NONE_TEXT
        toSaveLabel.text = NONE_TEXT
        toSaveLabel.foreground = Color.black
    }

    private companion object {
        val WIDTH = StaticVals.Dimensions.NewDonation.width
        val HEIGHT = StaticVals.Dimensions.NewDonation.height
        val NONE_TEXT = StaticVals.Generics.NONE_TEXT
        val NOT_PERMITTED = StaticVals.Generics.IMPOSSIBLE
    }

    constructor() {
        preferredSize = Dimension(WIDTH, HEIGHT)
        top = SearchDonatorPane(true)
        center = PrenotationPane()
        bottom = JPanel(GridLayout(1, 3, 20, 20))
        layout = BorderLayout(20, 20)
        add(top, BorderLayout.NORTH)
        add(center, BorderLayout.CENTER)
        add(bottom, BorderLayout.SOUTH)
        add(JLabel("         "), BorderLayout.WEST)
        add(JLabel("         "), BorderLayout.EAST)

        // Top pane

        // Select element on search
        top.onSelected { index: Int? ->
            setSave(true)
            if (index != null) center.setIdDon(index) else center.resetIdDon()
            try {
                setLastDonationDate(PftFile.getLastDonation(index!!))
            } catch (err: NullPointerException) {
                lastDonation.text = NONE_TEXT
            }
            // RESET DATI
            nextDonationDate = null
            canAccessFrom.text = NONE_TEXT
            nextPrenotationDate = null
            nextPrenotLabel.text = NONE_TEXT
            canAccess.text = NONE_TEXT
            canAccess.foreground = Color.BLACK
            donatCanDonate = false
            // SET DATI
            setNextDonationDate()
            setNextPrenotationDate()
            setPhoneNumber()
            setCanDonateFlag()
        }

        // Center Pane
        center.onDataPickerChange {
            setSave(true)
            setCanDonateFlag()
        }

        // Bottom Pane
        var servicelayout: JPanel
        var servicelayout2: JPanel
        var servicelabel: JLabel
        val SMALL_FONT = Font(Font.SANS_SERIF, Font.PLAIN, 10)
        val SMALL_FONT_BOLD = Font(Font.SANS_SERIF, Font.BOLD, 10)
        val ALIGN_LABEL = JLabel.LEFT
        // BUTTONS
        servicelayout = JPanel(GridLayout(2, 1))
        bottom.add(servicelayout)
        val save = JButton(StaticVals.Generics.SAVE)
        val home = JButton(StaticVals.Generics.HOME)
        servicelayout.add(save)
        servicelayout.add(home)

        // OTHER INFO
        servicelayout = JPanel(GridLayout(3, 1))
        bottom.add(servicelayout)
        // FIRST
        servicelayout2 = JPanel(GridLayout(2, 1))
        servicelayout.add(servicelayout2)
        servicelabel = JLabel(StaticVals.Generics.SAVE_STATE, ALIGN_LABEL)
        servicelabel.font = SMALL_FONT_BOLD
        servicelayout2.add(servicelabel)
        toSaveLabel = JLabel(NONE_TEXT, ALIGN_LABEL)
        toSaveLabel.font = SMALL_FONT
        servicelayout2.add(toSaveLabel)
        // SECOND
        servicelayout2 = JPanel(GridLayout(2, 1))
        servicelayout.add(servicelayout2)
        servicelabel = JLabel(StaticVals.Generics.NEXT_PRENOTATION, ALIGN_LABEL)
        servicelabel.font = SMALL_FONT_BOLD
        servicelayout2.add(servicelabel)
        nextPrenotLabel = JLabel(NONE_TEXT, ALIGN_LABEL)
        nextPrenotLabel.font = SMALL_FONT
        servicelayout2.add(nextPrenotLabel)
        // THIRD
        servicelayout2 = JPanel(GridLayout(2, 1))
        servicelayout.add(servicelayout2)
        servicelabel = JLabel(StaticVals.Generics.SAVED_PHONE, ALIGN_LABEL)
        servicelabel.font = SMALL_FONT_BOLD
        servicelayout2.add(servicelabel)
        phoneFinded = JLabel(NONE_TEXT, ALIGN_LABEL)
        phoneFinded.font = SMALL_FONT
        servicelayout2.add(phoneFinded)

        // INFORMATIONS
        servicelayout = JPanel(GridLayout(3, 1))
        bottom.add(servicelayout)
        // FIRST PART
        servicelayout2 = JPanel(GridLayout(2, 1))
        servicelayout.add(servicelayout2)
        servicelabel = JLabel(StaticVals.Generics.LAST_DONATION, ALIGN_LABEL)
        servicelabel.font = SMALL_FONT_BOLD
        servicelayout2.add(servicelabel)
        lastDonation = JLabel(NONE_TEXT, ALIGN_LABEL)
        lastDonation.font = SMALL_FONT
        servicelayout2.add(lastDonation)
        // SECOND PART
        servicelayout2 = JPanel(GridLayout(2, 1))
        servicelayout.add(servicelayout2)
        servicelabel = JLabel(StaticVals.Generics.AGREE, ALIGN_LABEL)
        servicelabel.font = SMALL_FONT_BOLD
        servicelayout2.add(servicelabel)
        canAccess = JLabel(NONE_TEXT, ALIGN_LABEL)
        canAccess.font = SMALL_FONT
        servicelayout2.add(canAccess)
        // THIRD PART
        servicelayout2 = JPanel(GridLayout(2, 1))
        servicelayout.add(servicelayout2)
        servicelabel = JLabel(StaticVals.Generics.FROM, ALIGN_LABEL)
        servicelabel.font = SMALL_FONT_BOLD
        servicelayout2.add(servicelabel)
        canAccessFrom = JLabel(NONE_TEXT, ALIGN_LABEL)
        canAccessFrom.font = SMALL_FONT
        servicelayout2.add(canAccessFrom)

        // Attivazione dei vari Listener
        center.onHFromChange(ChangeListener { setSave(true) })
        center.onHToChange(ChangeListener { setSave(true) })

        // Pulsanti
        home.addActionListener { if (toSave) wantSave() else MainFrame.setFrame(MenuPane()) }
        save.addActionListener { graphicSave() }
        center.onNoteFocus(object : FocusListener {
            override fun focusLost(e: FocusEvent) {}
            override fun focusGained(e: FocusEvent) {
                setSave(true)
            }
        })
        top.onBirthChange { setCanDonateFlag() }
    }
}