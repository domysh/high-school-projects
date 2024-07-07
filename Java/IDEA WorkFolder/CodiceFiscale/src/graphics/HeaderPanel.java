package graphics;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import other.svar;


public class HeaderPanel extends JPanel {
    public HeaderPanel(){
        super(new BorderLayout());
        try{
            BufferedImage image = ImageIO.read(getClass().getResourceAsStream("/img/ri-head.png"));
            Image dimg = image.getScaledInstance(90, 90,
                    Image.SCALE_SMOOTH);
            add(new MarginPane(new JLabel(new ImageIcon(dimg)),20,10,svar.back_color),BorderLayout.WEST);
        }
        catch(Exception e){System.exit(1);}
        setBackground(svar.back_color);
        JLabel l = new JLabel("<html><h2>Codice Fiscale</h2><h4>Calcola il tuo codice fiscale</h4></html>",JLabel.SOUTH_EAST);
        l.setForeground(Color.WHITE);

        add(new MarginPane(l,30,10,svar.back_color),BorderLayout.EAST);

    }
}
