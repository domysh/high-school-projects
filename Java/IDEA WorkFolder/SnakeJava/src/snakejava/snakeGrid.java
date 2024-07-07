package snakejava;

import java.awt.*;
import java.util.Random;
import javax.swing.*;

public class snakeGrid extends JPanel{
	private static final long serialVersionUID = 2L;
	//Identificazione dell'elemento presente nella griglia
    protected enum obj{snake,food,free}
    //Variabili di classi
    protected GridLayout gameMatrix;
    protected JPanel[][] matrix;
    protected obj[][] matrix_obj;
    protected point max;
    protected Color snake_img,food_img,free_img,free_dark_img;
    protected int prefW, prefH,free_freq,I_rows,I_cols;
    protected Color background_color;
    //Costruttore
    snakeGrid(int rows, int cols,Color snake,Color food,Color free,Color free_2,Color background,int free_2_freq) {
        super();
        //Controllo delle variabili in input
        if(rows<10 || cols<10)
            throw new ExceptionInInitializerError("snakeGrid min Value (of cols and rows) is 10!");
        //Assegnazione delle variabili a quelle della classe
        I_rows = rows;I_cols = cols;
        free_freq=free_2_freq;
        background_color = background;
        //Creazione degli oggetti tramite calcoli
        setBackground(background_color);
        prefH = rows * 10;prefW = cols * 10;
        setMinimumSize(new Dimension(prefW, prefH));
        max = new point(rows-1,cols-1);
        gameMatrix = new GridLayout(rows,cols);
        setLayout(gameMatrix);
        matrix_obj = new obj[rows][cols];
        matrix = new JPanel[rows][cols];
        //Assegnazioni delle immagini alle variabili
        snake_img = snake;
        food_img = food;
        free_img = free;
        free_dark_img = free_2;
        //Creazione della matrice
        for (int rw=0;rw<rows;rw++){
            for(int cl=0;cl<cols;cl++){
                JPanel tmp = new JPanel();
                matrix[rw][cl] = tmp;
                setPointDefault(new point(rw,cl));
                add(tmp);
            }
            }
    }
    snakeGrid(int rows,int cols){
        this(rows,cols,Color.BLACK,Color.RED,new Color(157,197,85),new Color(98,185,75),new Color(60,200,255),35);
    }
    snakeGrid(snakeGrid gr){
        this(gr.I_rows,gr.I_cols,gr.snake_img,gr.food_img,gr.free_img,gr.free_dark_img,gr.background_color,gr.free_freq);
    }
    snakeGrid() {
        this(30,30);
    }
    //Controllo del punto
    public boolean isValidPoint(point p){
        return p.getx()<=max.getx() && p.gety()<=max.gety() && p.getx()>=0 && p.gety()>=0;
    }
    //ALtezza schermata preferita
    public int getHpref(){
        return prefH;
    }
    //Larghezza schermata preferita
    public int getWpref(){
        return prefW;
    }
    //Funzione che cambia un punto della matrice secondo i parametri
    public void setPoint(point p,obj o,Color i){
        if (isValidPoint(p)){
        	Dimension std = new Dimension(10, 10);
            matrix_obj[p.getx()][p.gety()]=o;
            matrix[p.getx()][p.gety()].setBackground(i);
            matrix[p.getx()][p.gety()].setPreferredSize(std);
            matrix[p.getx()][p.gety()].setMinimumSize(std);
            matrix[p.getx()][p.gety()].setMaximumSize(std);
            matrix[p.getx()][p.gety()].setSize(std);
            matrix[p.getx()][p.gety()].revalidate();
        }else{
            throw new ExceptionInInitializerError("POINT OUT OF BOUNDS");
        }
    }
    //Assegnazione di un array di punti a dei parametri
    public void setPoint(point[] p,obj o,Color i){
        for(point pos : p)
            this.setPoint(pos,o,i);
    }
    //Settaggio di un punto in snake_point
    public void setPointSnake(point p){
        setPoint(p, obj.snake,snake_img);
    }
    //Settaggio di un punto in free_point
    public void setPointDefault(point p){
        Random rnd = new Random();
        Color sel;
        switch(rnd.nextInt(free_freq)){
            case 0:
                sel = free_dark_img;
                break;
            default:
                sel = free_img;
                break;
        }
        setPoint(p,obj.free,sel);
    }
    //Settaggio di un punto in food_point
    public void setPointFood(point p){
        setPoint(p,obj.food,food_img);
    }
    //Funzione che restituisce il valore presente nella matrice in un punto
    public obj getPointObj(point p){
        if (isValidPoint(p)){
            return matrix_obj[p.getx()][p.gety()];
        }
        return null;
    }
    //punto estremo della matrice
    public point getMax(){
        return max;
    }
    //Metodo get del colore di sfondo
    public Color getBackgroudColor(){
        return background_color;
    }
    
}
