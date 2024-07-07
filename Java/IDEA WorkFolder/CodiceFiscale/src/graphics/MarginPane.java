package graphics;
import javax.swing.*;
import java.awt.*;

public class MarginPane extends JPanel {

    public MarginPane(JComponent c,int padx,int pady,Color bg) {
        super(new FlowLayout(FlowLayout.CENTER,padx,pady));
        setBackground(bg);
        add(c);
    }

    public MarginPane(JComponent c,int padx,int pady){
        this(c,padx,pady,Color.WHITE);
    }
}
