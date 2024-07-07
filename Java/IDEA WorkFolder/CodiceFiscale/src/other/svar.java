package other;

import java.awt.*;

//svar = static variables
public final class svar {
    private svar(){}
    public static Color back_color = new Color(14,166,3);

    public static boolean isAlpha(String s) {
        for(int i=0;i<s.length();i++) {
            if(!Character.isLetter(s.charAt(i))) return false;
        }
        return true;
    }
    public static boolean isAlphaS(String s) {
        s = s.replace(" ", "");
        return isAlpha(s);
    }

}
