package snakejava;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.util.Arrays;
import javax.swing.JFrame;
import javax.swing.JTextArea;

public class SnakeJava {
    public static Snake_Frame MainFrame;
    public static void main(String[] args) {
        
        //Create Folder
        scoreManager.dirCreator();
        //Create Frame
        MainFrame = new Snake_Frame();
       
    }
    
    public static Rectangle getFrameBound(){
        return MainFrame.getBounds();
    }
    //Funzione che ricreai il Main Frame
    public static void recreateFrame(){
        deleteMainFrame();
        MainFrame = new Snake_Frame();
    }
    // Funzione che ricarica il gioco con i parametri precedenti
    public static void reloadGame(){
        if (MainFrame.sn.isInPause() || MainFrame.sn.isStopped()){
            MainFrame.gamePane.setVisible(false);
            MainFrame.gameStart(new Snake(MainFrame.sn));
        }
    }
    //Funzione che aggiorna la pagina dello score
    public static void reloadScore(){
        recreateFrame();
        MainFrame.scoreScreenGen();
    }
    
    private static void deleteMainFrame(){
        MainFrame.setVisible(false);
        MainFrame.removeAll();
        MainFrame = null;
        System.gc();
    }
    //Funzione che crea una schermata che visualizza un errore (Nel caso questo sia in un blocco catch e in realta non venga gestito)
    // (La grafica è brutta .... bisognerebbe migliorarla)
    public static void createJError(String msg,Exception e,int exit_code){
            JFrame error = new JFrame();
            JTextArea title = new JTextArea(msg);
            title.setForeground(Color.red);
            title.setFont(new Font("Arial",Font.BOLD,12));
            JTextArea message = new JTextArea("EVENTUALI MESSAGGI:\n\n"+e.getMessage()+"\n\nSTACK TRACE:\n\n"+Arrays.toString(e.getStackTrace())+"\n\nCODE ERROR:"+exit_code);
            title.setKeymap(null);
            message.setKeymap(null);
            error.getContentPane().setLayout(new GridLayout(2,1));
            error.add(title);
            error.add(message);            
            error.setBounds(0,0,300,300);
            error.setAlwaysOnTop(true);
            error.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            error.setVisible(true);
    }

}
