package snakejava;

public class point {
    //Classe che definisce un punto
    private int xpos;
    private int ypos;
    point(int x,int y){
        xpos=x;
        ypos=y;
    }
    point(){
        this(0,0);
    }
    point(point p){
        this(p.getx(),p.gety());
    }
    public int getx(){
        return xpos;
    }
    public int gety(){
        return ypos;
    }
    public void set(int x,int y){
        xpos=x;
        ypos=y;
    }
    public void setx(int x){
        xpos=x;
    }
    public void sety(int y){
        ypos=y;
    }
}
