package graphics

import com.toedter.calendar.JDateChooser
import data.DBManagment
import data.PftFile.fromDateToString
import data.StaticVals
import java.awt.*
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.beans.PropertyChangeEvent
import java.lang.IndexOutOfBoundsException
import java.util.*
import javax.swing.*
import kotlin.properties.Delegates

class SearchDonatorPane constructor(noDbPermitted: Boolean ) : JPanel() {
    private var list: Vector<String?>
    private val search: JTextFieldClickIntegrated
    private var name: JTextFieldClickIntegrated? = null
    private var surename: JTextFieldClickIntegrated? = null
    private var telephone: JTextFieldClickIntegrated? = null
    private var birth: JDateChooser by Delegates.notNull()
    private var notInDB: JCheckBox by Delegates.notNull()
    private val donatList = JList<String?>()
    private val donatorLayout = CardLayout()
    private var todo: ((Int?)->Unit)? = null

    fun onSelected(todo: ((Int?)->Unit)?) {
        this.todo = todo
        donatList.addMouseListener(object : MouseListener {
            override fun mouseClicked(e: MouseEvent) {
                if (e.clickCount == 2) {
                    var n = donatList.locationToIndex(e.point)
                    try {
                        n = list[n]!!.split(":".toRegex()).toTypedArray()[0].toInt()
                        if (todo != null) todo(n)
                    } catch (exept: NumberFormatException) {
                    } catch (exept: ArrayIndexOutOfBoundsException) {
                    }
                }
            }

            override fun mousePressed(e: MouseEvent) {}
            override fun mouseReleased(e: MouseEvent) {}
            override fun mouseEntered(e: MouseEvent) {}
            override fun mouseExited(e: MouseEvent) {}
        })
        search.addKeyListener(object:KeyListener{
            override fun keyPressed(e: KeyEvent?) {}
            override fun keyTyped(e: KeyEvent?) {}
            override fun keyReleased(e: KeyEvent?) {
                searchUpdate()
            }
        })
        search.addPropertyChangeListener { searchUpdate() }
    }

    fun searchUpdate() {
        list.clear()
        list = searchInDonatorWithSentence(search.text)
        donatList.setListData(list)
        donatList.revalidate()
    }

    private fun searchInDonatorWithSentence(key: String): Vector<String?> {
        var ele: List<Any?>
        val res = Vector<String?>()
        val keys = key.toUpperCase().split(" ".toRegex()).toTypedArray()
        var findedInRow = true
        var toAdd: String? = null
        // Ciclo sulle righe
        var i = 0
        while (true) {
            try {
                ele = DBManagment.self.getDonatorInfo(i + 1)
            }catch(e:IndexOutOfBoundsException){return res}
            // Ciclo sulle parole chiave
            var select = 0
            while (select < keys.size && findedInRow) {
                val s = keys[select]
                // Ciclo sui dati
                for (ind in 5..13) {
                    if (ele[ind] != null) {
                        val find = when (ind) {
                            9 -> fromDateToString(ele[ind] as Date?)
                            5 -> "" + (i + 1)
                            else -> ele[ind] as String?
                        }
                        if (find!!.toUpperCase().contains(s)) {
                            toAdd = conactenateInformation(ele)
                            findedInRow = true
                            break
                        } else {
                            findedInRow = false
                        }
                    }
                }
                select++
            }
            if (findedInRow) {
                res.add(toAdd)
            } else {
                findedInRow = true
            }
            i++
        }
    }

    fun reset() {
        search.text = ""
        if(name != null && surename != null && telephone != null) {
            name!!.text = ""
            surename!!.text = ""
            birth.date = null
            telephone!!.text = ""
            if (!notInDB.isSelected) notInDB.doClick()
        }
    }

    override fun getName(): String {
        return try {
            name!!.text
        } catch (e: IllegalStateException) {
            ""
        }
    }

    fun getSurename(): String {
        return try {
            surename!!.text
        } catch (e: IllegalStateException) {
            ""
        }
    }

    fun getBirth(): String? {
        return if (birth.date != null) fromDateToString(birth.date) else null
    }

    val birthDate: Date?
        get() = birth.date

    val phone: String
        get() = try {
            telephone!!.text
        } catch (e: NullPointerException) {
            ""
        }

    fun onBirthChange(op:()->Unit) {
        birth.dateEditor.addPropertyChangeListener { d: PropertyChangeEvent ->
            if (d.propertyName !== "graphicsConfiguration" && d.propertyName !== "ancestor") {
                op()
            }
        }
    }

    companion object {
        private const val DISTANCE: Byte = 50
        private const val NONE_TEXT = "None"
        private const val LIST_ID = "0"
        private const val NODB_ID = "1"
        private fun conactenateInformation(o: List<Any?>): String {
            val ele0: String = if (o[5] != null) {
                (o[5] as Double?)!!.toInt().toString()
            } else {
                NONE_TEXT
            }
            val ele1: String = if (o[6] != null) {
                (o[6] as String?)!!.toUpperCase()
            } else {
                NONE_TEXT
            }
            val ele2: String = if (o[7] != null) {
                (o[7] as String?)!!.toUpperCase()
            } else {
                NONE_TEXT
            }
            val ele3: String = if (o[9] != null) {
                fromDateToString(o[9] as Date?)
            } else {
                NONE_TEXT
            }
            val ele4: String = if (o[8] != null) {
                (o[8] as String?)!!.toUpperCase()
            } else {
                NONE_TEXT
            }

            val lnSentenceFirst = ele0.length + 2 + ele1.length + 1 + ele2.length
            var spacer = ""
            for (i in 0 until DISTANCE - lnSentenceFirst) {
                spacer += " "
            }
            return ele0 + ": " + ele1 + " " + ele2 + " " + spacer + "Nato il: " + ele3 + " a: " + ele4
        }
    }

    init {
        layout = BorderLayout()
        list = Vector()
        search = JTextFieldClickIntegrated()
        notInDB = JCheckBox()
        list.add(StaticVals.Generics.SEARCH_WRITING_DOWN)
        notInDB.isSelected = true
        donatList.setListData(list)
        donatList.layoutOrientation = JList.VERTICAL
        donatList.selectionMode = ListSelectionModel.SINGLE_SELECTION
        donatList.font = Font(Font.MONOSPACED, Font.PLAIN, 12)
        val scrollList = JScrollPane(donatList)
        val donatorPane = JPanel(donatorLayout)
        donatorPane.add(scrollList, LIST_ID)
        if (noDbPermitted) {
            var serviceLayout = JPanel(GridLayout(4, 1))
            var serviceLayout2 = JPanel()
            var lay = GroupLayout(serviceLayout2)
            lay.autoCreateGaps = true
            lay.autoCreateContainerGaps = true
            serviceLayout2.layout = lay
            var serviceLabel = JLabel("Nome:")
            name = JTextFieldClickIntegrated()
            lay.setHorizontalGroup(lay.createSequentialGroup().addComponent(serviceLabel).addComponent(name))
            lay.setVerticalGroup(lay.createParallelGroup().addComponent(serviceLabel).addComponent(name))
            serviceLayout.add(serviceLayout2)
            serviceLayout2 = JPanel(FlowLayout())
            lay = GroupLayout(serviceLayout2)
            lay.autoCreateGaps = true
            lay.autoCreateContainerGaps = true
            serviceLayout2.layout = lay
            serviceLabel = JLabel("Cognome:")
            surename = JTextFieldClickIntegrated()
            lay.setHorizontalGroup(lay.createSequentialGroup().addComponent(serviceLabel).addComponent(surename))
            lay.setVerticalGroup(lay.createParallelGroup().addComponent(serviceLabel).addComponent(surename))
            serviceLayout.add(serviceLayout2)
            serviceLayout2 = JPanel(FlowLayout())
            lay = GroupLayout(serviceLayout2)
            lay.autoCreateGaps = true
            lay.autoCreateContainerGaps = true
            serviceLayout2.layout = lay
            serviceLabel = JLabel("Data di Nascita:")
            birth = JDateChooser()
            birth.font = Font(Font.MONOSPACED, Font.PLAIN, 10)
            birth.preferredSize = Dimension(120, 25)
            birth.dateFormatString = data.SettingsManager.dataFormat
            lay.setHorizontalGroup(lay.createSequentialGroup().addComponent(serviceLabel).addComponent(birth))
            lay.setVerticalGroup(lay.createParallelGroup().addComponent(serviceLabel).addComponent(birth))
            serviceLayout.add(serviceLayout2)
            serviceLayout2 = JPanel()
            lay = GroupLayout(serviceLayout2)
            lay.autoCreateGaps = true
            lay.autoCreateContainerGaps = true
            serviceLayout2.layout = lay
            serviceLabel = JLabel("Telefono:")
            telephone = JTextFieldClickIntegrated()
            lay.setHorizontalGroup(lay.createSequentialGroup().addComponent(serviceLabel).addComponent(telephone))
            lay.setVerticalGroup(lay.createParallelGroup().addComponent(serviceLabel).addComponent(telephone))
            serviceLayout.add(serviceLayout2)
            donatorPane.add(serviceLayout, NODB_ID)
            serviceLayout = JPanel()
            lay = GroupLayout(serviceLayout)
            serviceLayout.layout = lay
            lay.setHorizontalGroup(
                    lay.createSequentialGroup().addComponent(search).addGap(5).addComponent(notInDB).addGap(5))
            lay.setVerticalGroup(lay.createParallelGroup().addComponent(search).addComponent(notInDB))
            add(serviceLayout, BorderLayout.SOUTH)
        } else {
            add(search, BorderLayout.SOUTH)
        }
        onSelected(todo)
        add(donatorPane, BorderLayout.CENTER)
        // Listener
        if (noDbPermitted) {
            notInDB.addActionListener{
                if (notInDB.isSelected) {
                    search.isEnabled = true
                    donatorLayout.show(donatorPane, LIST_ID)
                    if (todo != null) todo!!(null)
                } else {
                    search.isEnabled = false
                    donatorLayout.show(donatorPane, NODB_ID)
                    if (todo != null) todo!!(-1)
                }
            }
        } else {
            notInDB.isEnabled = false
        }
    }
}