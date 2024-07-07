package graphics

import javax.swing.JTextField
import javax.swing.text.Document

/*
 * Questa classe vuole evitare di dimenticarsi di aggiungere il ContextMenuMouseListener
 * Quindi "sovrascrive la classe JTextField in modo da utilizzare questa"
 */
class JTextFieldClickIntegrated : JTextField {
    constructor() : super() {
        addMouseListener(ContextMenuMouseListener())
    }

    constructor(n: Int) : super(n) {
        addMouseListener(ContextMenuMouseListener())
    }

    constructor(s: String?) : super(s) {
        addMouseListener(ContextMenuMouseListener())
    }

    constructor(s: String?, n: Int) : super(s, n) {
        addMouseListener(ContextMenuMouseListener())
    }

    constructor(d: Document?, s: String?, n: Int) : super(d, s, n) {
        addMouseListener(ContextMenuMouseListener())
    }
}