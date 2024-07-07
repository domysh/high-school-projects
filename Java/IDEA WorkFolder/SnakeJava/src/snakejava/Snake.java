package snakejava;

import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;


public class Snake{
    //enumerazione delle direzioni perse dallo snake
    public enum direction{up,down,right,left}
    //Variabili Della Classe snake
    protected direction direct,I_d,tmp_direct;
    protected snakeGrid grid,I_gridS;
    protected direction[] Hdirect;
    protected point posSnake,lastposSnake,I_init; 
    protected long init_time,time=0;
    protected int win_value,lenSnake=0,look_Hdirect = 0,timeOfThread,hmenu,wmenu;
    protected boolean stop_game = false,thereIsFood = false,pause = false,win = false;
    protected Thread SLoop;
    protected JFrame menu;
    protected JButton riprendi = new JButton("Riprendi"),chiudi = new JButton("Chiudi"),ricomincia = new JButton("Ricomincia");
    
    //Funzione runnable Utile al thread per far muovere e interagire lo snake (LOOP)
    Runnable threadOBJ = () -> {
        while(!stop_game){
            if (pause){
                return;
            }
            try{
                Thread.sleep(timeOfThread);
            }catch(InterruptedException e){
                return;
            }
            direct=tmp_direct;
            if (!thereIsFood)
                foodGenerator();
            move();
        }
        time += System.currentTimeMillis()-init_time;
        if (win){ // Codice eseguito in caso di vittoria
            menuCreate("VITTORIA","HAI VINTO!",descriptionString(), ricomincia, chiudi);
            menu.setFocusable(true);
            menu.setVisible(true);
        }else{ // Codice eseguito in caso di perdita 
            menuCreate("SCONFITTA","HAI PERSO!",descriptionString(), ricomincia, chiudi);
            menu.setFocusable(true);
            menu.setVisible(true);
        }
        scoreManager.saveScore(time, win_value, lenSnake, timeOfThread);
    };
    //Listener da tastiera per l'imput utente
    protected final KeyAdapter key_control = new KeyAdapter(){
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyTyped(e);
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_ESCAPE || key == KeyEvent.VK_ENTER){
                    if(isStopped()){
                        ricomincia.doClick();
                    }else{
                        pauseGame();
                    }
                }else if(!isStopped() && !isInPause()){
                switch(key){
                    case KeyEvent.VK_UP:setDirection(direction.up);    break;
                    case KeyEvent.VK_DOWN:setDirection(direction.down);  break;
                    case KeyEvent.VK_LEFT:setDirection(direction.left);  break;
                    case KeyEvent.VK_RIGHT:setDirection(direction.right); break;
                }
                }
            }    
        };
    // Costruttore dell'oggetto
    Snake(snakeGrid gridS,point init,direction d,int fast_time,int win_len){
        //Controllo dei parametri
        if(win_len<=2)
            throw new ExceptionInInitializerError("Snake class: win_len must be >2");
        //Salvataggio delle variabili iniziali 
        I_gridS = new snakeGrid(gridS);
        I_init = new point(init);
        I_d = d;
        //assegnazione delle variabili ricevute come parametri in quelle dell oggetto
        tmp_direct = d;
        timeOfThread = fast_time;
        direct = d;
        win_value=win_len;
        grid = gridS;
        //Array di salvataggio delle direzione dello snake
        Hdirect = new direction[win_len];
        //Creazione dello snake (Lungo inizialmente 2) sulla snakeGrid secondo i parametri in input
        Hdirect[0] = d;look_Hdirect++;
        grid.setPointSnake(init);lenSnake++;
        lastposSnake=init;
        posSnake = new point(init);
        direction_switch(direct, posSnake);
        grid.setPointSnake(posSnake);lenSnake++;
        //Creazione delle dimensioni delle schemate menu in base al sistema operativo
        wmenu=350;hmenu=130;
        if (System.getProperty("os.name").toUpperCase().contains("WIN")){
            hmenu+=10;
            wmenu+=20;
        }
        //Aggiunta dei listener ai pusanti nel menu
        riprendi.addActionListener((ActionEvent e) -> {
            pauseGame();
        });
        chiudi.addActionListener((ActionEvent e) -> {
            menu.setVisible(false);
            SnakeJava.recreateFrame();
        });
        ricomincia.addActionListener((ActionEvent e) -> {
            menu.setVisible(false);
            SnakeJava.reloadGame();
        });
    }
    //Altri costruttori con alcuni parametri dati in default
    Snake(snakeGrid gridS,int fast_time,int win_len){
        this(gridS,new point(),direction.right,fast_time,win_len);}
    Snake(snakeGrid gridS,int fast_time) {
        this(gridS,fast_time,(int)((gridS.getMax().getx()*gridS.getMax().gety())/15));}
    Snake(snakeGrid gridS){
        this(gridS,100);}
    //Costruttore Utile alla ricostruzione della schermata iniziale
    Snake(Snake s){
        this(s.I_gridS, s.I_init, s.I_d, s.timeOfThread, s.win_value);}
    
    //Funzioni per la restituzione di valori utili all'esterno della classe (Metodi GET)
    public boolean isStopped(){
        return stop_game;
    }
    public boolean isInPause(){
        return pause;
    }
    //Funzione per mettere in pausa e far ricominciare il gioco
    public void pauseGame(){
        if (pause){
            init_time = System.currentTimeMillis();
            createLoopThread();
            pause = false;
            SLoop.start();
            menu.setVisible(false);
            menu.removeKeyListener(key_control);
            menu.setFocusable(false);
            menu = null;
            
        }else{
            time += System.currentTimeMillis()-init_time;
            menuCreate("PAUSA", "GIOCO IN PAUSA",descriptionString(),riprendi,ricomincia, chiudi,wmenu,hmenu);
            menu.setFocusable(true);
            SLoop = null;
            pause = true;
            menu.setVisible(true);
        }
    }
    //Funzione utile a far partire lo snake per la prima volta
    public void startGame(){
        init_time = System.currentTimeMillis();
        createLoopThread();
        pause = false;
        SLoop.start();
    }
    //Ri-Costruttore del Thread di interazione dello snake
    protected void createLoopThread(){
    if(SLoop == null)
        SLoop = new Thread(threadOBJ);
    }
    //Funzione che permette il movimento dello snake in una direzione definita dall'utente
    protected void move(){      
        direction_switch(direct, posSnake);
        if(grid.isValidPoint(posSnake)){
            snakeGrid.obj afterpoint = grid.getPointObj(posSnake);
            switch(afterpoint){
                case snake: stop_game = true; break;
                case food: eat(); break;
                case free:{
                    grid.setPointSnake(posSnake);
                    grid.setPointDefault(lastposSnake);
                    Hdirect[look_Hdirect] = direct;
                    look_Hdirect++;
                    resetValue();
                    direction_switch(Hdirect[look_Hdirect], lastposSnake);
                    grid.revalidate();
 
                }
            }

        }else{
            stop_game=true;
        }
    }
    //Funzione per aumentare di 1 casella lo snake
    protected void eat(){
        grid.setPointSnake(posSnake);
        lenSnake++;
        for(int i =lenSnake-1;i>=look_Hdirect;i--){
            int isecond = i-1;
            isecond=resetValue(isecond);
            Hdirect[i] = Hdirect[isecond];
        }
        Hdirect[look_Hdirect]=direct;
        look_Hdirect++;
        resetValue();
        thereIsFood=false;
        if (lenSnake== win_value){
            win = true;
            stop_game = true;
        }
    }
    // Funzione per permettere il settaggio della direzione da parte dell'utente
    protected void setDirection(direction d){
        switch(d){
            case up:
                if (direct != direction.down)  tmp_direct = d; break;
            case down:
                if (direct != direction.up)    tmp_direct = d; break;
            case right:
                if (direct != direction.left)  tmp_direct = d; break;
            case left:
                if (direct != direction.right) tmp_direct = d; break;
        }
    }
    //unzione che permette di calcolare la prossima posizione di un punto in base alla direzione
    protected void direction_switch(direction d,point p){
        switch(d){
            case up:
                p.setx(p.getx()-1); break;
            case down:
                p.setx(p.getx()+1); break;
            case right:
                p.sety(p.gety()+1); break;
            case left:
                p.sety(p.gety()-1); break;
        }
    }
    //Funzione che genera il cibo randomicamente nella griglia (Evitando lo snake)
    protected void foodGenerator(){
        Random rnd = new Random();
        boolean notfree = true;
        point maxp = grid.getMax();
        point randomFound = null;
        while(notfree){
            randomFound = new point(rnd.nextInt(maxp.getx()+1),
                                    rnd.nextInt(maxp.gety()+1));
            if(grid.getPointObj(randomFound)==snakeGrid.obj.free)
                notfree=false;
        }
        grid.setPointFood(randomFound);
        thereIsFood = true;
    }
    // Funzione che permette l'overload dei puntatori all'array di direzioni
    //          (Per far si che il puntatore all'array non vada su posizioni ancora non definite)--> ArrayNullPointerExeption
    protected int resetValue(int i){
        if(i>=lenSnake)
            return i-=lenSnake;
        else if(i<0){
            return i+=lenSnake;
        }
        return i;
    }
    //Overload delle funzione percedente sulla variabile look_Hdirect
    protected void resetValue(){
        look_Hdirect=resetValue(look_Hdirect);
    }
    // Funzione che crea il menu di pausa,vincita,perdita in base ai parametri dati
    protected void menuCreate(String name,String title,String description,JButton button1,JButton button2,JButton button3,int WH,int HT){
            menu = new JFrame(name);
            menu.setBounds(SnakeJava.getFrameBound().x+(SnakeJava.getFrameBound().width/6),SnakeJava.getFrameBound().y+(SnakeJava.getFrameBound().height/4),WH,HT);
            menu.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            menu.setResizable(false);
            menu.setAlwaysOnTop(true);
            menu.getContentPane().setLayout(new BorderLayout());
            JLabel WPausa = new JLabel();
            WPausa.setFont(new Font("Arial",Font.BOLD,28));
            WPausa.setText(title);
            JPanel title_pane = new JPanel(new FlowLayout());
            title_pane.add(WPausa);
            menu.getContentPane().add(title_pane,BorderLayout.NORTH);
            if (description !=null){
                JPanel tmp = new JPanel(new FlowLayout());
                tmp.add(new JLabel(description));
                menu.getContentPane().add(tmp,BorderLayout.CENTER);}
            JPanel buttons = new JPanel(new FlowLayout());
            if (button1 != null)
                buttons.add(button1);
            if (button2 != null)
                buttons.add(button2);
            if (button3 != null)
                buttons.add(button3);
            menu.getContentPane().add(buttons,BorderLayout.SOUTH);
            menu.addKeyListener(key_control);
    }
    protected void menuCreate(String name,String title,String description,JButton button1,JButton button2){menuCreate(name, title,description, button1, button2, null,wmenu,hmenu);}
    //Funzione che genera la stringa di descrizione nel menu
    protected String descriptionString(){
        String time_s="",res;
        int seconds = (int)(time/1000);
        int minutes = seconds/60;
        seconds %= 60;
        if(minutes<10)
            time_s+="0";
        time_s+=minutes+":";
        if(seconds<10)
            time_s+="0";
        time_s +=seconds;
        res = "Tempo: "+time_s+" Snake:"+lenSnake;
        if(!win)
            res+=" Vittoria:"+win_value;
        return res;
        
    }
    //Funzione che restituisce il key listener predefiniti dalla classe
    public KeyAdapter getKeyAdpGame(){return key_control;}
}