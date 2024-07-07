import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;

public class GameTable extends JPanel{

    boolean jollyStatus[];
    JLabel labelGrid[];
    final int JOLLY_TO_GEN = MainClass.JOLLY_DA_GENERARE;

    public void genJolly(){
        for(int i=0;i<jollyStatus.length;i++){
            jollyStatus[i] = false;
        }
        int fulled = 0;
        Random rnd = new Random();
        while(fulled<JOLLY_TO_GEN){
            int generated = Math.abs(rnd.nextInt()%9);
            if (!jollyStatus[generated]){
                jollyStatus[generated] = true;
                fulled+=1;
            }
        }
    }

    public GameTable(){
        super(new GridLayout(3,3));
        if (JOLLY_TO_GEN > 9 || JOLLY_TO_GEN < 0 ){
            //Anche se non ha seno si permette di inserire gli estremi 0 e 9
            throw new IllegalArgumentException("Invalid const!");
        }
        jollyStatus = new boolean[9];
        labelGrid = new JLabel[9];

        for(int i=0;i<labelGrid.length;i++) {
            labelGrid[i] = new JLabel("",SwingConstants.CENTER);
            labelGrid[i].setSize(100,100);
            labelGrid[i].setPreferredSize(new Dimension(100,100));
            labelGrid[i].setFont(new Font(
                    labelGrid[i].getFont().getName(),
                    Font.BOLD, 100
            ));

            labelGrid[i].setBackground(new Color(30,30,30));
            add(labelGrid[i]);
        }
    }

    void resetGrid(){
        for(int i=0;i<labelGrid.length;i++) {
            labelGrid[i].setText("?");
            labelGrid[i].setIcon(null);
        }
    }

}
