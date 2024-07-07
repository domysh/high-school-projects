import javax.swing.*;
import java.awt.*;

public class ProgramFrame extends JFrame {
    public ProgramFrame(){
        super("Jolly Game");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        int WIDTH = 500, HEIGTH = 450;

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x_pos = screenSize.width/2 - WIDTH/2;
        int y_pos = screenSize.height/2 - HEIGTH/2;

        setBounds(x_pos,y_pos,WIDTH,HEIGTH);
        add(new GamePane());
        setVisible(true);
    }
}
