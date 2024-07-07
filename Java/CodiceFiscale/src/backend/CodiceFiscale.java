package backend;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CodiceFiscale {
	private CodiceFiscale() {}
	
	public static boolean isVocale(char let) {
		let = toUpper(let);
		return (let == 'A' || let == 'E' || let == 'I' || let == 'O' || let == 'U');
		
	}
	
	public static char toUpper(char let) {
		return let = (""+let).toUpperCase().charAt(0);
	}
	
	
	public static boolean isConsonante(char let) {return !isVocale(let) && Character.isLetter(let);}
	
	public static String[] separateConsEVoc(String a) {
		if(a == null) throw new IllegalArgumentException("Null Value");
		String voc = "",cons = "";
		for(int i=0;i<a.length();i++) {
			if (isVocale(a.charAt(i))) {
				voc+=a.charAt(i);
			}else if(isConsonante(a.charAt(i))){
				cons+=a.charAt(i);
			}
		}
		String res[] = new String[2];
		res[0] = voc;res[1] = cons;
		return res;
	}
	
	public static String calcSurename(String surename) {
		surename = surename.toUpperCase();
		String res = "";
		String[] sep = separateConsEVoc(surename);
		String cons = sep[1],voc = sep[0];
		if(surename.length()<3) {
			res = cons+voc;
			for(int i=1;i<4-surename.length();i++) {
				res+='X';
			}
		}else if(cons.length()<3) {
			res = cons;
			for(int i=0;i<voc.length();i++) {
				res+=voc.charAt(i);
				if(res.length() == 3) {
					break;
				}
			}
			
		}else {
			for(int i=0;i<3;i++) {
				res+= cons.charAt(i);
			}
		}
		return res;
	}
	
	public static String calcName(String name) {
		name = name.toUpperCase();
		String res = "";
		String[] sep = separateConsEVoc(name);
		String cons = sep[1],voc = sep[0];
		if(name.length()<3) {
			res = cons+voc;
			for(int i=1;i<4-name.length();i++) {
				res+='X';
			}
		}else if(cons.length()<3) {
			res = cons;
			for(int i=0;i<voc.length();i++) {
				res+=voc.charAt(i);
				if(res.length() == 3) {
					break;
				}
			}
		}else if(cons.length() == 3){
			res = cons;
		}else {
			res=cons.charAt(0)+""+cons.charAt(2)+""+cons.charAt(3);
		}
		
		return res;
	}
	
	public static int[] dateToNum(String d) {
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(new SimpleDateFormat("dd/MM/yyyy").parse(d));
			int[] res = new int[3];
			res[0] = cal.get(Calendar.DAY_OF_MONTH);
			res[1] = cal.get(Calendar.MONTH)+1;
			res[2] = cal.get(Calendar.YEAR);
			return res;
		}catch(Exception e) {
			throw new IllegalArgumentException("Not Valid Date!");
		}

	}
	
	public static String getYearCode(int y) {
		String res = y+"";
		if (res.length() == 4) {
			return ""+res.charAt(res.length()-2)+res.charAt(res.length()-1);
		}
		return null;
	}
	
	public static char getMonth(int m) {
		switch(m) {
			case 1:return  'A';
			case 2:return  'B';
			case 3:return  'C';
			case 4:return  'D';
			case 5:return  'E';
			case 6:return  'H';
			case 7:return  'L';
			case 8:return  'M';
			case 9:return  'P';
			case 10:return 'R';
			case 11:return 'S';
			case 12:return 'T';
			default:return '\0';
		}
	}
	
	public static boolean femaleOrMale(char s) {
		s = toUpper(s);
		if (s == 'F') {
			return true;
		}else if(s == 'M'){
			return false;
		}else {
			throw new IllegalArgumentException("Not Valid char");
		}
	}
	
	public static String dayPart(int day,boolean sex) {
		if (sex) {
			day+=40;
		}
		if(day>0 && day<10) {
			return "0"+day;
		}
		return ""+day;
	}
	
	public static String getSexAndBirth(boolean sex,int[] birth) {
		int d = birth[0];int m = birth[1];int y = birth[2];
		String res = "";
		res += getYearCode(y);
		res += getMonth(m);
		res += dayPart(d, sex);
		return res;
	}
	
	public static int ConvertDispPos(char l) {
		l = toUpper(l);
		switch(l) {
			case '0':return 1;case '1':return 0;case '2':return 5;case '3':return 7;
			case '4':return 9;case '5':return 13;case '6':return 15;case '7':return 17;
			case '8':return 19;case '9':return 21;case 'A':return 1;case 'B':return 0;
			case 'C':return 5;case 'D':return 7;case 'E':return 9;case 'F':return 13;
			case 'G':return 15;case 'H':return 17;case 'I':return 19;case 'J':return 21;
			case 'K':return 2;case 'L':return 4;case 'M':return 18;case 'N':return 20;
			case 'O':return 11;case 'P':return 3;case 'Q':return 6;case 'R':return 8;
			case 'S':return 12;case 'T':return 14;case 'U':return 16;case 'V':return 10;
			case 'W':return 22;case 'X':return 25;case 'Y':return 24;case 'Z':return 23;
			default:throw new IllegalArgumentException("Not valid char");
		}
	}
	public static int ConvertParPos(char l) {
		l = toUpper(l);
		if(l >= '0' && l <= '9') {
			return Integer.parseInt(l+"");
		}else {
			return ((int)(l)-(int)('A'));
		}
	}
	
	public static char fromNumToChar(int n) {
		return (char)(n+(int)('A'));
	}
	
	public static char chkDigit(String firstCode) {
		int resNum = 0;
		boolean swi = false;
		for(int i=0;i<firstCode.length();i++) {
			if(swi) {
				resNum += ConvertParPos(firstCode.charAt(i));
			}else {
				resNum += ConvertDispPos(firstCode.charAt(i));
			}
			swi ^= true;
		}
		return fromNumToChar(resNum%26);
	}
	
	public static String calc(String name,String surename,boolean sex,int[] birth,String city,String province) {
		String firstPart = 
				CodiceFiscale.calcSurename(surename)+
				CodiceFiscale.calcName(name)+
				CodiceFiscale.getSexAndBirth(sex, birth)+
				Comuni.getBelfiore(city,province);
		return firstPart+CodiceFiscale.chkDigit(firstPart);
	}


}
