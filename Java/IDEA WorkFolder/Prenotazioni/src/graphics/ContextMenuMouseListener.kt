package graphics

import data.StaticVals
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.event.ActionEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.AbstractAction
import javax.swing.Action
import javax.swing.JPopupMenu
import javax.swing.text.JTextComponent

class ContextMenuMouseListener : MouseAdapter() {
    private val popup = JPopupMenu()
    private val cutAction: Action
    private val copyAction: Action
    private val pasteAction: Action
    private val undoAction: AbstractAction
    private val selectAllAction: Action
    private var textComponent: JTextComponent? = null
    private var savedString = ""
    private var lastActionSelected: Actions? = null

    private enum class Actions {
        UNDO, CUT, COPY, PASTE, SELECT_ALL
    }

    override fun mouseClicked(e: MouseEvent) {
        if ((e.modifiersEx and MouseEvent.META_DOWN_MASK) == MouseEvent.META_DOWN_MASK)  {
            if (e.source !is JTextComponent) {
                return
            }
            textComponent = e.source as JTextComponent
            textComponent!!.requestFocus()
            val enabled = textComponent!!.isEnabled
            val editable = textComponent!!.isEditable
            val nonempty = !(textComponent!!.text == null || textComponent!!.text == "")
            val marked = textComponent!!.selectedText != null
            val pasteAvailable = Toolkit.getDefaultToolkit().systemClipboard.getContents(null).isDataFlavorSupported(DataFlavor.stringFlavor)
            undoAction.isEnabled = enabled && editable && (lastActionSelected == Actions.CUT || lastActionSelected == Actions.PASTE)
            cutAction.isEnabled = enabled && editable && marked
            copyAction.isEnabled = enabled && marked
            pasteAction.isEnabled = enabled && editable && pasteAvailable
            selectAllAction.isEnabled = enabled && nonempty
            var nx = e.x
            if (nx > 500) {
                nx -= popup.size.width
            }
            popup.show(e.component, nx, e.y - popup.size.height)
        }
    }

    init {
        undoAction = object : AbstractAction(StaticVals.Generics.ActionMouse.UNDO) {
            override fun actionPerformed(ae: ActionEvent) {
                textComponent!!.text = ""
                textComponent!!.replaceSelection(savedString)
                lastActionSelected = Actions.UNDO
            }
        }
        popup.add(undoAction)
        popup.addSeparator()
        cutAction = object : AbstractAction(StaticVals.Generics.ActionMouse.CUT) {
            override fun actionPerformed(ae: ActionEvent) {
                lastActionSelected = Actions.CUT
                savedString = textComponent!!.text
                textComponent!!.cut()
            }
        }
        popup.add(cutAction)
        copyAction = object : AbstractAction(StaticVals.Generics.ActionMouse.COPY) {
            override fun actionPerformed(ae: ActionEvent) {
                lastActionSelected = Actions.COPY
                textComponent!!.copy()
            }
        }
        popup.add(copyAction)
        pasteAction = object : AbstractAction(StaticVals.Generics.ActionMouse.PASTE) {
            override fun actionPerformed(ae: ActionEvent) {
                lastActionSelected = Actions.PASTE
                savedString = textComponent!!.text
                textComponent!!.paste()
            }
        }
        popup.add(pasteAction)
        popup.addSeparator()
        selectAllAction = object : AbstractAction(StaticVals.Generics.ActionMouse.SELECT_ALL) {
            override fun actionPerformed(ae: ActionEvent) {
                lastActionSelected = Actions.SELECT_ALL
                textComponent!!.selectAll()
            }
        }
        popup.add(selectAllAction)
    }
}