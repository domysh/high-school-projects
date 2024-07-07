public class NumberSystem {
	public static void ResetAll() {
		mainClass.display=0.0;
		mainClass.noInteger=0.0;
		mainClass.YesInteger=0;
		mainClass.controlUnderScreen=1;
		mainClass.ClickedPoint=0;
		mainClass.ControlNoIntegerPart=0;
		mainClass.N1=0;
		mainClass.N2=0;
		mainClass.Op=0;
		mainWindows.N1Label.setText("0.0");
		mainWindows.N2Label.setText("");
		mainWindows.OperationLabel.setText("");
		mainWindows.RisultatoLabel.setText("");
		mainWindows.ScreenLabel.setText("0.0");
		mainWindows.UgualeLabel.setText("");
	}
	public static void UpdateNumber(int n) {
		//Creazione Numero
		switch(mainClass.ClickedPoint) {
		case 0://Creazione parte Intera:
			mainClass.YesInteger*=10;
			mainClass.YesInteger+=n;
			mainClass.display=mainClass.YesInteger;
			break;
		case 1://Creazione parte decimale:
			double tmp;
			tmp=n;
			mainClass.ControlNoIntegerPart++;
			for(int i=0;i<mainClass.ControlNoIntegerPart;i++) {
				tmp/=10;
			}
			mainClass.noInteger+=tmp;
			if(mainClass.ControlNoIntegerPart<=3)
			mainClass.display=mainClass.YesInteger+mainClass.noInteger;
			break;
			default:
				ResetAll();
				break;
		}
		//Selezione N1/N2
		switch(mainClass.controlUnderScreen) {
		case 1:
			mainClass.N1=mainClass.display;
			mainWindows.ScreenLabel.setText(Double.toString(mainClass.display));
			mainWindows.N1Label.setText(Double.toString(mainClass.N1));
			break;
		case 2:
			mainClass.N2=mainClass.display;
			mainWindows.ScreenLabel.setText(Double.toString(mainClass.display));
			mainWindows.N2Label.setText(Double.toString(mainClass.N2));
			break;
			default:
				ResetAll();
				break;
		}
	}
	public static void SetOperation(int op) {
		mainClass.Op=(byte)op;//ADD=1;SOTR=2;MOLT=3;DIV=4;
		mainClass.controlUnderScreen=2;
		mainClass.display=0.0;
		mainClass.ClickedPoint=0;
		mainClass.ControlNoIntegerPart=0;
		mainClass.YesInteger=0;
		mainClass.noInteger=0.0;
		switch (op) {
		case 1:
			mainWindows.OperationLabel.setText("+");
			break;
		case 2:
			mainWindows.OperationLabel.setText("-");
			break;
		case 3:
			mainWindows.OperationLabel.setText("x");
			break;
		case 4:
			mainWindows.OperationLabel.setText("/");
			break;
			default:
				ResetAll();
				break;
		}
	}
	public static void EgualOperation(){
		mainClass.N2=mainClass.display;
		mainWindows.UgualeLabel.setText("=");
		switch(mainClass.Op) {
		case 1:
			mainClass.display=mainClass.N1+mainClass.N2;
			break;
		case 2:
			mainClass.display=mainClass.N1-mainClass.N2;
			break;
		case 3:
			mainClass.display=mainClass.N1*mainClass.N2;
			break;
		case 4:
			mainClass.display=mainClass.N1/mainClass.N2;
			break;
			default:
				ResetAll();
				break;
		}
		mainWindows.RisultatoLabel.setText(Double.toString(mainClass.display));
		mainWindows.ScreenLabel.setText(Double.toString(mainClass.display));
	}
	public static void RadiceQ() {
		mainClass.N2=mainClass.display;
		mainClass.N1=Math.sqrt(mainClass.display);
		mainClass.noInteger=0.0;
		mainClass.YesInteger=0;
		mainClass.controlUnderScreen=1;
		mainClass.ClickedPoint=0;
		mainClass.ControlNoIntegerPart=0;
		mainClass.Op=0;
		mainWindows.N1Label.setText("sqrt(");
		mainWindows.N2Label.setText(")");
		mainWindows.OperationLabel.setText(Double.toString(mainClass.N2));
		mainWindows.RisultatoLabel.setText("");
		mainWindows.ScreenLabel.setText(Double.toString(mainClass.N1));
		mainWindows.UgualeLabel.setText("");
		
	}
}


