package graphics;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame(String s){
        super(s);
        int WIDTH = 600, HEIGTH = 450;

        //Put window in the center of the screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x_pos = screenSize.width/2 - WIDTH/2;
        int y_pos = screenSize.height/2 - HEIGTH/2;

        setBounds(x_pos,y_pos,WIDTH,HEIGTH);

        JPanel p = new JPanel(new BorderLayout());
        add(p);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        p.add(new HeaderPanel(),BorderLayout.NORTH);
        p.add(new MarginPane(new FormInputPane(),30,30),BorderLayout.CENTER);
    }

}
