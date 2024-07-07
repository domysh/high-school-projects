import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

public class GamePane extends JPanel {

    JButton play, show;
    JLabel tries, status;
    GameTable table;
    boolean grid_listener_on;
    ImageIcon jolly_icon;

    final int MAX_TRIES = MainClass.TENTATIVI_MASSIMI;
    int tries_done = 0;
    int jolly_founded = 0;

    final boolean FAIL_ON_SHOW = MainClass.PERDI_QUANDO_CLICCHI_MOSTRA; // Se questa variabile è true quando clicchi mostra perdi in automatico
    final boolean SHOW_ON_FAIL = MainClass.MOSTRA_JOLLY_QUANDO_PERDI;


    public void startGame(){

        table.genJolly();
        tries_done = 0;
        table.resetGrid();
        tries.setText(Integer.toString(MAX_TRIES-tries_done));
        for(int i=0;i<table.labelGrid.length;i++){
            table.labelGrid[i].setOpaque(false);
            table.labelGrid[i].setBackground(Color.GRAY);
        }
        status.setForeground(Color.BLACK);
        status.setText("Jolly Game!");
        grid_listener_on = true;
        jolly_founded = 0;

    }

    public GamePane(){
        super(new BorderLayout());
        if ( MAX_TRIES < 0 ){
            throw new IllegalArgumentException("Invalid const!");
        }
        {
            JPanel buttons_row = new JPanel();
            buttons_row.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
            play = new JButton("GIOCA");
            show = new JButton("MOSTRA");
            tries = new JLabel("");
            buttons_row.add(play);
            buttons_row.add(new JLabel("Tentativi rimasti: "));
            buttons_row.add(tries);
            buttons_row.add(show);

            add(buttons_row,BorderLayout.NORTH);
        }
        {
            table = new GameTable();
            JPanel tmp_panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            tmp_panel.add(table);
            add(tmp_panel, BorderLayout.CENTER);
        }
        {
            JPanel tmp_panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            status = new JLabel("Jolly Game!");
            tmp_panel.add(status);
            status.setFont(new Font(
                    status.getFont().getName(),
                    Font.BOLD, 20
            ));

            add(tmp_panel,BorderLayout.SOUTH);
        }
        for(int i=0;i<table.labelGrid.length;i++){
            final int val = i;
            table.labelGrid[i].addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent mouseEvent) {}
                @Override
                public void mousePressed(MouseEvent mouseEvent) {click_event(val);}
                @Override
                public void mouseReleased(MouseEvent mouseEvent) {}
                @Override
                public void mouseEntered(MouseEvent mouseEvent) {}
                @Override
                public void mouseExited(MouseEvent mouseEvent) {}
            });
        }

        try{
            BufferedImage image = ImageIO.read(getClass().getResourceAsStream("/jolly.png"));
            Image dimg = image.getScaledInstance(90, 90,
                    Image.SCALE_SMOOTH);
            jolly_icon = new ImageIcon(dimg);
        }
        catch(Exception e){System.exit(1);}

        startGame();
        play.addActionListener((l)->{
            startGame();
        });

        show.addActionListener((l)->{
            show_jolly();
        });



    }

    public void show_jolly(boolean stop_fail){
        if(FAIL_ON_SHOW && !stop_fail){fail_game();} // Avoid ricursion
        for(int i=0;i<table.jollyStatus.length;i++){
            if(!table.jollyStatus[i]) continue;
            table.labelGrid[i].setOpaque(true);
            if(FAIL_ON_SHOW) {
                if (table.labelGrid[i].getText().equals("")) {
                    table.labelGrid[i].setBackground(Color.GREEN);
                } else {
                    table.labelGrid[i].setBackground(Color.RED);
                }
            }else{
                table.labelGrid[i].setBackground(Color.BLUE);
            }
        }
    }
    public void show_jolly(){
        show_jolly(false);
    }

    public void fail_game(){
        grid_listener_on = false;
        status.setText("Hai Perso D:");
        status.setForeground(Color.RED);
        if(SHOW_ON_FAIL) show_jolly(true);
    }

    public void win_game(){
        grid_listener_on = false;
        status.setText("Hai Vinto :D");
        status.setForeground(new Color(23,114,69));
    }

    public void click_event(int sel){
        //System.out.println("Clicked: "+sel);
        if(!grid_listener_on)return;
        if(table.jollyStatus[sel]){
            if(table.labelGrid[sel].getText().equals("")) return;
            table.labelGrid[sel].setText("");
            table.labelGrid[sel].setIcon(jolly_icon);
            jolly_founded++;
        }else{
            if(table.labelGrid[sel].getText().equals("?")){
                table.labelGrid[sel].setText("X");
            }else{
                return;
            }
        }
        tries_done++;
        tries.setText(Integer.toString(MAX_TRIES-tries_done));
        if(jolly_founded == table.JOLLY_TO_GEN){
            win_game();
            return;
        }
        if(tries_done==MAX_TRIES || MAX_TRIES-tries_done<table.JOLLY_TO_GEN-jolly_founded){
            //Si aggiunge alla perdita il caso in cui le mosse che hai a disposizione non sono sufficienti
            fail_game();
            return;
        }
    }
}
