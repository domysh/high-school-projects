package snakejava;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.imageio.ImageIO;


public class Snake_Frame extends JFrame{
	private static final long serialVersionUID = 1L;
	//Settings del frame
    protected final String NAME = "Snake";
    protected final Font FONT = new Font("Arial", Font.PLAIN, 20);
    //Variabili locali del frame
    protected Rectangle FrameBounds;
    protected JPanel startPane;
    protected snakeGrid gamePane;
    protected Snake sn;
    protected int HEIGHTFRAME;
    protected final int WIDTHFRAME = 420;
    protected int widthScreen = Toolkit.getDefaultToolkit().getScreenSize().width;
    protected int heightScreen = Toolkit.getDefaultToolkit().getScreenSize().height;
    //COSTRUCTOR del frame
    Snake_Frame(){
        super();
        
        if(System.getProperty("os.name").toUpperCase().contains("WIN")){ //Scelgo la dimesione in altezza in base all'os
            HEIGHTFRAME = 260;
        }else{
            HEIGHTFRAME = 250;
        }
        
        //Creazione della schermata iniziale
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setVisible(true);
        setFocusable(true);
        setAutoRequestFocus(true);
        setBounds((widthScreen/2)-(WIDTHFRAME/2),(heightScreen/2)-(HEIGHTFRAME/2),WIDTHFRAME,HEIGHTFRAME);
        setTitle(NAME);
        setFont(FONT);
        setResizable(false);
        Image icon,intestazione;
        // Memorizzazione delle immagini
        try{
            icon = ImageIO.read(ClassLoader.getSystemResource("img/icon.png"));
            intestazione = ImageIO.read(ClassLoader.getSystemResource("img/intestazione.png"));
        }catch(IOException e){
            icon = null;
            intestazione = null;
        }
        setIconImage(icon);

        startPane = new JPanel(new BorderLayout());
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(startPane,BorderLayout.CENTER);
        startPane.setVisible(true);
        startPane.add(new JSeparator(SwingConstants.HORIZONTAL),BorderLayout.CENTER); 
       
        JLabel img_init = new JLabel(new ImageIcon(intestazione));
        startPane.add(img_init,BorderLayout.NORTH);
        
        JPanel bottons = new JPanel();
        startPane.add(bottons,BorderLayout.SOUTH);
        //Pulsante di avvio del gioco
        /*
        JButton start = new JButton("Create Game");
        bottons.add(start,BorderLayout.SOUTH);
            /*
                QUESTO PULSANTE DOVREBBE DARE LA POSSIBILTA'
                DI FAR CREARE LIVELLI PERSONALIZZATI DALL'UTENTE INCLUSA LA GARFICA
                (CREANDO DEI TEMPLATE DI GRAFICA GIA FATTI E DANDO LA POSSIBILITA'
                DI INSERIRE DELLE IMMAGINI 10X10PX COME COLORE...)
                LE CLASSI PERMETTONO GIA LA PERSONALIZZAZIONE DI QUESTI PARAMETRI BASTA CREARE
                UN AMBIENTE CHE PERMETTA ALL'UTENTE DI INSERIRE QUESTI CON APPOSITI PULSANTI ETC...
            \*\/
        });
        
        
        
*/
        JButton easy = new JButton("EASY");
        bottons.add(easy,BorderLayout.SOUTH);
        easy.addActionListener((ActionEvent e) -> {
            snakeGrid tmp = new snakeGrid(50,50);
            gameStart(new Snake(tmp,80,10));
        });
        JButton normal = new JButton("NORMAL");
        bottons.add(normal,BorderLayout.SOUTH);
        normal.addActionListener((ActionEvent e) -> {
            snakeGrid tmp = new snakeGrid(50,50);
            gameStart(new Snake(tmp,55,30));
        });
        JButton hard = new JButton("HARD");
        bottons.add(hard,BorderLayout.SOUTH);
        hard.addActionListener((ActionEvent e) -> {
            snakeGrid tmp = new snakeGrid(70,70);
            gameStart(new Snake(tmp,45,80));
        });
        //Pulsante per entrare nella schermata degli scores
        JButton score = new JButton("Scores");
        bottons.add(score,BorderLayout.SOUTH);
        score.addActionListener((ActionEvent e) -> {
            scoreScreenGen();
        });

        setVisible(true);
    }
    //Funzione che crea la schermata di gioco
    public void gameStart(Snake s){
        if(sn != null){
            sn.pause=true;
            removeKeyListener(sn.getKeyAdpGame());
            sn = null;}
        sn= s;
        gamePane=sn.grid;
        getContentPane().setBackground(gamePane.getBackgroudColor());
        add(gamePane,BorderLayout.CENTER);
        startPane.setVisible(false);
        gamePane.setVisible(true);
        setVisible(true);
        Rectangle gameBounds = new Rectangle((widthScreen/2)-(gamePane.prefW/2),(heightScreen/2)-(gamePane.prefH/2),gamePane.prefW,gamePane.prefH);
        setBounds(gameBounds);
        setMaximumSize(new Dimension(gameBounds.width,gameBounds.height));
        if(System.getProperty("os.name").toUpperCase().contains("WIN"))
            setResizable(false);
        addKeyListener(sn.getKeyAdpGame());
        sn.startGame();
    }
    //Funzione che crea la schermata degli score
    public void scoreScreenGen(){
        //Variabili di lavoro
        String data[][] = scoreManager.getScores();
        JPanel pscores = new JPanel(new GridLayout(data.length,1));
        JScrollPane scores = new JScrollPane(pscores);
        //creazione lista di pane contenenti i dati estratti dall'xml
        for (int i =data.length-1;i>=0;i--){
            String sco[] = data[i];
            JPanel pane_single_score = new JPanel(new GridLayout(1,2));
            JPanel sc = new JPanel(new GridLayout(sco.length+1,1));
            JLabel tmp_JL=new JLabel("SCORE N."+i);
            if(sco[3].contentEquals(sco[4]))
                tmp_JL.setForeground(Color.GREEN);
            else
                tmp_JL.setForeground(Color.RED);
            sc.add(tmp_JL);
            int i_tmp = 0;
            for(String ele : sco){
                String introd="???:";
                switch(i_tmp){
                    case 0:
                        introd = "DATA: ";
                        break;
                    case 1:
                        introd = "ORA: ";
                        break;
                    case 2:
                        introd = "TEMPO DI GIOCO: ";
                        break;
                    case 3:
                        introd = "LUNGEZZA VITTORIA: ";
                        break;
                    case 4:
                        introd = "LUNGHEZZA RAGGIUNTA: ";
                        break;
                    case 5:
                        introd = "VELOCITA' DELLO SNAKE: ";
                        break;
                }
                JLabel insert_in = new JLabel("   "+introd+ele);
                sc.add(insert_in);
                i_tmp++;
            }
            
            sc.setVisible(true);
            pane_single_score.add(sc);
            JPanel btn_del = new JPanel(new FlowLayout());
            JButton delete_this_score = new JButton("DELETE");
            int i_ref = i+1;
            delete_this_score.addActionListener((ActionEvent ev)->{
                scoreManager.deleteScore(i_ref);
                SnakeJava.reloadScore();
            });
            btn_del.add(delete_this_score);
            pane_single_score.add(btn_del);
            pscores.add(pane_single_score);
        }
        //schermata perla creazione dei pulsanti sotto
        startPane.setVisible(false);
        scores.setVisible(true);
        add(scores,BorderLayout.CENTER);
        JPanel under_btn = new JPanel(new FlowLayout());
        
        JButton chiudi = new JButton("CHIUDI");
        chiudi.addActionListener((ActionEvent btn_chiudi) ->{
            SnakeJava.recreateFrame();
        });
            
        JButton Delete_all = new JButton("CANCELLA TUTTO");
        Delete_all.addActionListener((ActionEvent btn_canc)->{
            scoreManager.deleteAllScores();
            SnakeJava.reloadScore();
        });
            
        under_btn.add(Delete_all);
        under_btn.add(chiudi);
            
        add(under_btn, BorderLayout.SOUTH);
            
    }
}
