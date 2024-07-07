package backend;
import java.io.FileReader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Comuni {
	private Comuni() {}
	
	private static JSONArray db = null;
	public static final String FILE_NAME = "comuni.json";
	
	public static boolean loader() {
		try {
			db = new JSONArray(new JSONTokener(new FileReader(FILE_NAME)));
		}catch(Exception e) {return false;}
		return true;
	}
	
	public static String getBelfiore(String citta,String provincia) {
		if(db == null) if(!loader()) throw new IllegalArgumentException("File Not Found!");
		int pos = -1;
		for (int i=0;i<db.length();i++) {
			JSONObject ele = db.getJSONObject(i);
			String prov = ele.getString("provincia");
			if(prov.equalsIgnoreCase(provincia)) {
				pos = i;break;
			}
		}
		if (pos==-1)throw new IllegalArgumentException("Provincia not Found");
		JSONArray comuni = db.getJSONObject(pos).getJSONArray("paesi");
		for(int i =0;i<comuni.length();i++) {
			if(comuni.getJSONObject(i).getString("comune").replace(" ", "").equalsIgnoreCase(citta.replace(" ", ""))) {
				return comuni.getJSONObject(i).getString("belfiore").toUpperCase();
			}
		}
		throw new IllegalArgumentException("Comune not Found");
	}
	
	public static boolean provinceExist(String provincia) {
		if(db == null) if(!loader()) throw new IllegalArgumentException("File Not Found!");
		int pos = -1;
		for (int i=0;i<db.length();i++) {
			JSONObject ele = db.getJSONObject(i);
			String prov = ele.getString("provincia");
			if(prov.equalsIgnoreCase(provincia)) {
				pos = i;break;
			}
		}
		if (pos==-1)return false;
		return true;
	}
	
}
