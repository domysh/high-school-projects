package graphics

import backend.MainFrame
import data.StaticVals
import java.awt.Dimension
import javax.swing.JButton
import javax.swing.JPanel

/**
 *
 * @author DomySh
 */
class StatsPane : JPanel() {
    companion object {
        const val WIDTH = 580
        const val HEIGHT = 400
    }

    init {
        preferredSize = Dimension(WIDTH, HEIGHT)
        val home = JButton(StaticVals.Generics.HOME)
        add(home)
        home.addActionListener { MainFrame.setFrame(MenuPane()) }
    }
}