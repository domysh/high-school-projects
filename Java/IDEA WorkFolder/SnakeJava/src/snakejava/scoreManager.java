package snakejava;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.time.LocalDateTime;


public class scoreManager {
    //VARIABILI - COSTANTI
    private final static String SEP = System.getProperty("file.separator"),NAMEXML = "ScoreData.xml",NAMETMPXML = "tmpData.xml";
    public final static String DIR_ROOT = System.getProperty("user.home")+SEP+".SnakeJava"+SEP;
    protected final static String XMLFILE = DIR_ROOT+NAMEXML;
    protected final static String XMLTMPFILE = DIR_ROOT+NAMETMPXML;
    protected final static String VERSION = "0.1";
    protected final static int SCORE_LIMIT = 100;
    //Funzione che salva lo score nel file
    public static void saveScore(long time,int winLen,int snakeLen,int snakeVelocity){
        dirCreator();
        cript();
        //Controllo dell'eventuale superamento del limite
        if(countGame()==SCORE_LIMIT){
            deleteScore(0);
        }
        //Scrittura del fine XML sul file TMP
        try{
            try (FileWriter wr = new FileWriter(XMLTMPFILE)) {
                Scanner sc = new Scanner(new File(XMLFILE));
                while(true){
                    try{
                        wr.write(sc.nextLine()+"\n");
                    }catch(NoSuchElementException e){
                        sc.close();
                        break;
                    }
                }
                wr.write("<game>\n"
                       + "<dateDay>"+getDate()+";\n"
                            + "<dateTime>"+getTime()+";\n"
                            + "<time>"+TimetoString(time)+";\n"
                            + "<winLen>"+winLen+";\n"
                            + "<snakeLen>"+snakeLen+";\n"
                            + "<snakeFast>"+snakeVelocity+";\n"
                            + "</game>\n");
                wr.close();
            }
        }catch(IOException ex){}
        changeFileTMP();
        cript();
    }
    //Eliminazione di uno score con un determinato id temporaneo
    public static void deleteScore(int idNum){
        cript();
        Scanner fileData; 
        if(idNum <=0)
            return;
        try{
            fileData = new Scanner(new File(XMLFILE));
        }catch(FileNotFoundException e){
            fileData=null;
            SnakeJava.createJError("ERRORE NELLA MODIFICA DELLO SCORE", e, 2);
        }
        
        String line;
        int from_line=0,to_line=0,point,numofTag=0,atLine=0;
        boolean inTagGame = false,succes=true;
        //Individuazione della posizione del tag game da eliminare
        while(true){
            try{
                line =fileData.nextLine();
                atLine++;
            }catch(NoSuchElementException e){
                fileData.close();
                fileData = null;
                succes = false;
                line = null;
                SnakeJava.createJError("Search TAG: idNum non trovato del file "+XMLFILE, e, 2);
            }
            if(line !=null){
            if(inTagGame){
                point = line.indexOf("</game>");
                if(point !=-1){
                    if(numofTag == idNum){
                        to_line = atLine;
                        fileData.close();
                        fileData=null;
                        try{
                            fileData = new Scanner(new File(XMLFILE));
                        }catch(FileNotFoundException e){
                            SnakeJava.createJError("ERRORE NELLA MODIFICA DELLO SCORE"+XMLFILE, e, 2);
                        }
                        
                        break;
                    }
                    inTagGame=false;
                }
                
            }else{
                if(line.contains("<game>")){
                    inTagGame=true;
                    numofTag++;
                    if(numofTag == idNum){
                        from_line=atLine;
                    }
                }
            }
        }else break;
        //Creazione del nuovo file con i dati eliminati
        }
        if(succes && fileData!=null){
            FileWriter FileWr;
            try{
                FileWr = new FileWriter(XMLTMPFILE);
                atLine=0;
                while(true){
                try{
                    line =fileData.nextLine();
                    atLine++;
                }catch(NoSuchElementException e){
                    fileData.close();
                    FileWr.close();
                    changeFileTMP();
                    break;
                }
                if(!(atLine >= from_line && atLine<=to_line)){
                    FileWr.write(line+'\n');
                    FileWr.flush();
                }
                    
                }
                FileWr.close();
            }catch(IOException e){
                SnakeJava.createJError("ERRORE NELLA MODIFICA DELLO SCORE"+XMLFILE, e, 2);
            }
        }
        cript();
    }
    //Eliminazione del file
    public static void deleteAllScores(){
        File xml = new File(XMLFILE);
        if(xml.exists())
            xml.delete();
    }
    //Funzione che restituisce un array bidimensionale con i dati degli score
    public static String[][] getScores(){
        String line, dateDay="", dateTime="",time="",winLen="",snakeLen="",snakeFast="";
        String[][] res = new String[countGame()][6];
        cript();
        Scanner fileData;
        boolean inTagGame = false;
        
        try{
            fileData = new Scanner(new File(XMLFILE));
        }catch(FileNotFoundException e){
            fileData= null;
            SnakeJava.createJError("ERRORE NEL OTTENERE I DATI DI SCORE"+XMLFILE, e, 2);
        }
        int array_point = 0;
        while(true){
            try{
                line =fileData.nextLine();
            }catch(NoSuchElementException e){
                fileData.close();
                cript();
                return res;
            }
            if(inTagGame){
                int point;
                point = line.indexOf("<dateDay>");
                if(point !=-1){
                    point+=9;
                    dateDay = memorise(line, point, ';');
                }
                point = line.indexOf("<dateTime>");
                if(point !=-1){
                    point+=10;
                    dateTime = memorise(line, point, ';');
                }
                point = line.indexOf("<time>");
                if(point !=-1){
                    point+=6;
                    time = memorise(line, point, ';');
                }       
                point = line.indexOf("<winLen>");
                if(point !=-1){
                    point+=8;
                    winLen = memorise(line, point, ';');
                }
                point = line.indexOf("<snakeLen>");
                if(point !=-1){
                    point+=10;
                    snakeLen = memorise(line, point, ';');
                }
                point = line.indexOf("<snakeFast>");
                if(point !=-1){
                    point+=11;
                    snakeFast = memorise(line, point, ';');
                }
                point = line.indexOf("</game>");
                if(point !=-1){
                    res[array_point][0]=dateDay;
                    res[array_point][1]=dateTime;
                    res[array_point][2]=time;
                    res[array_point][3]=winLen;
                    res[array_point][4]=snakeLen;
                    res[array_point][5]=snakeFast;
                    dateDay = "";dateTime="";time = "";winLen = "";snakeLen="";snakeFast="";
                    array_point++;
                    inTagGame=false;
                }
                
            }else{
                if(line.contains("<game>"))
                    inTagGame=true;
            }
        }
    }
    // Funzione che restituisce la versione dell'xml e controlla che sia supportata
    // (ATTUALMENTE NON UTILIZZATA)
    @SuppressWarnings("unused")
	private static String xmlVersion(){
        cript();
        Scanner fileData;
        
        try{
        fileData = new Scanner(new File(XMLFILE));
        }catch(FileNotFoundException e){
            fileData= null;
            SnakeJava.createJError("ERRORE NELL OTTENERE LA VERSIONE DELL'XML"+XMLFILE, e, 2);
        }
        //cerca dell'intestazione
        String find,version="";
        int p_find,line=0;
        while(true){
            try{
                find =fileData.nextLine();
                line++;
            }catch(NoSuchElementException e){
                break;
            }
            p_find=find.indexOf("<?xml");
            if(p_find != -1){
                p_find = find.lastIndexOf("version=");
                if (p_find != -1)
                    if(find.charAt(p_find+8) == '"'){
                        p_find+=9;
                        version = memorise(find, p_find, '"');
                        break;
                    }
            }
        }
        if(!version.contentEquals(VERSION)){
            throw new ExceptionInInitializerError("NOT COMPATIBLE VERSION OF XML!");
        }
        fileData.close();
        cript();
        return version;
    }
    //Estrae il contenuto di un Tag
    private static String memorise(String find,int p_find,char endCh){
        String res="";
        while(true){
            char tmp = find.charAt(p_find);
            p_find++;
            if(tmp == endCh)
                break;
            else
                res+=tmp;
        }
        return res;
    }
    //Conta i tag <game> presenti e ne restituisce il numero
    public static int countGame(){
     Scanner fileData;           
        try{
            dirCreator();
            cript();
            fileData = new Scanner(new File(XMLFILE));
        }catch(FileNotFoundException e){
            fileData= null;
            SnakeJava.createJError("ERRORE NELL ANALIZZARE LO SCORE"+XMLFILE, e, 2);
        }
     
        String line;
        int res=0,point;
        boolean inTagGame = false;
        while(true){
            try{
                line =fileData.nextLine();
            }catch(NoSuchElementException e){
                fileData.close();
                cript();
                return res;
            }
            if(inTagGame){
                point = line.indexOf("</game>");
                if(point !=-1){
                    res++;
                    inTagGame=false;
                }
                
            }else{
                if(line.contains("<game>"))
                    inTagGame=true;
            }
        }
        
    }
    //Funzione che restituisce la data di oggi in formato stringa
    public static String getDate(){
        LocalDateTime t = LocalDateTime.now();
        int day,mounth,year;
        String res="";
        day = t.getDayOfMonth();mounth=t.getMonthValue();year=t.getYear();
        if(day<10)
            res+="0";
        res+=day+"/";
        if(mounth<10)
            res+="0";
        res+=mounth+"/";
        if(year<10)
            res+="0";
        return res+year;
    }
    //Funzione che restituisce l'ora attuale in formato stringa
    public static String getTime(){
        LocalDateTime t = LocalDateTime.now();
        int h,m;
        String res="";
        h=t.getHour();
        m=t.getMinute();
        if(h<10)
            res+="0";
        res+=h+":";
        if(m<10)
            res+="0";
        return res+m;
    }
    //Converte il tempo di gioco in formato stringa
    public static String TimetoString(long time){
        String time_s="";
        int seconds = (int)(time/1000);
        int minutes = seconds/60;
        seconds %= 60;
        if(minutes<10)
            time_s+="0";
        time_s+=minutes+":";
        if(seconds<10)
            time_s+="0";
        time_s +=seconds;
        return time_s;
    }
    //Sostituisce il file degli score con quello temporaneo
    protected static void changeFileTMP(){
        new File(XMLFILE).delete();
        new File(XMLTMPFILE).renameTo(new File(XMLFILE));
        
    }
    //Crea la directory .SnakeJava dove salvare i vile + il file TXT README per eventuali informazioni sulla cartella
    public static void dirCreator(){
        new File(scoreManager.DIR_ROOT).mkdirs();
            try{
                FileWriter fw = new FileWriter(scoreManager.DIR_ROOT+"README.txt");
                fw.write("I FILE IN QUESTA CARTELLA SONO GENERATI DAL GIOCO JAVASNAKE...\n"
                        + "SE QUESTA CARTELLA VERRA' ELIMINATA, VERRA' RICREATA AL SUCCESSIVO "
                        + "AVVIO DEL GIOCO.\nATTENZIONE! QUESTA CARTELLA CONTIENE DATI UTILI AL GIOCO COME LO SCORE!\n\n@DomySh");
                fw.close();
                if(!new File(scoreManager.XMLFILE).exists()){
                    fw = new FileWriter(scoreManager.XMLFILE);fw.write("<?xml version=\""+VERSION+"\"?>");fw.close();
                    cript();
                }
            }catch(IOException e){
                SnakeJava.createJError("ERRORE NEL CREARE IL FILE:"+scoreManager.DIR_ROOT, e, 3);
            }
            
    }
    private static void cript(){
        FileReader file;
        try{
            file = new FileReader(new File(XMLFILE));
        }catch(FileNotFoundException E){
            dirCreator();
            try{file = new FileReader(new File(XMLFILE));}catch(FileNotFoundException err){SnakeJava.createJError("FILE NON TROVATO", E, 6);file=null;System.exit(1);}
        }
        FileWriter fw;
        try{
            fw = new FileWriter(new File(XMLTMPFILE));
        }catch(IOException eccz){
            SnakeJava.createJError("ERROR TO WRITE TMP FILE", eccz, 20);
            System.exit(10);
            fw = null;
        }
        char tmp_by;
        try{
            while(true){
                tmp_by = (char)file.read();
                tmp_by ^= '^';
                if(tmp_by ==65441 || tmp_by == 65535) break;
                fw.write(tmp_by);
            }
        }catch(IOException e){e.printStackTrace();}
        
        try{
        fw.close();
        file.close();

        }catch(IOException e){SnakeJava.createJError("ERRORE NEL CHIUDERE IL FILE", e, 56);}
        changeFileTMP();
        
    }
}

