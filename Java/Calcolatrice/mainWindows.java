import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.SystemColor;
import java.awt.Toolkit;
import javax.swing.JSeparator;

public class mainWindows {

	private JFrame frmCalcolatrice;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					mainWindows window = new mainWindows();
					window.frmCalcolatrice.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	//Object Free
	static JLabel ScreenLabel = new JLabel("0.0");
	static JLabel N1Label = new JLabel("0.0");
	static JLabel OperationLabel = new JLabel("");
	static JLabel N2Label = new JLabel("");
	static JLabel UgualeLabel = new JLabel("");
	static JLabel RisultatoLabel = new JLabel("");
	
	/**
	 * Create the application.
	 */
	
	public mainWindows() {
		NumberSystem.ResetAll();
		initialize();
	}
	/**
	 * Initialize the contents of the frame.
	 */
	public void initialize() {
		frmCalcolatrice = new JFrame();
		frmCalcolatrice.setIconImage(Toolkit.getDefaultToolkit().getImage(mainWindows.class.getResource("/img/icon.png")));
		frmCalcolatrice.setTitle("Calcolatrice");
		frmCalcolatrice.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
		frmCalcolatrice.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmCalcolatrice.setBounds(100, 100, 523, 332);
		frmCalcolatrice.getContentPane().setLayout(new BoxLayout(frmCalcolatrice.getContentPane(), BoxLayout.X_AXIS));
		
		JPanel panel = new JPanel();
		panel.setBackground(SystemColor.activeCaption);
		frmCalcolatrice.getContentPane().add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		JPanel Screen = new JPanel();
		Screen.setBackground(Color.DARK_GRAY);
		Screen.setForeground(Color.WHITE);
		panel.add(Screen);
		Screen.setLayout(new GridLayout(0, 1, 0, 0));
		
		ScreenLabel.setHorizontalAlignment(SwingConstants.CENTER);
		ScreenLabel.setForeground(Color.WHITE);
		ScreenLabel.setBackground(Color.WHITE);
		ScreenLabel.setFont(new Font("Oxygen", Font.BOLD, 35));
		Screen.add(ScreenLabel);
		
		JPanel underScreen = new JPanel();
		underScreen.setBackground(SystemColor.controlShadow);
		FlowLayout fl_underScreen = (FlowLayout) underScreen.getLayout();
		fl_underScreen.setAlignment(FlowLayout.LEFT);
		panel.add(underScreen);

		N1Label.setFont(new Font("Oxygen", Font.BOLD, 20));
		N1Label.setHorizontalAlignment(SwingConstants.LEFT);
		underScreen.add(N1Label);
		OperationLabel.setFont(new Font("Oxygen", Font.BOLD, 20));
		
		OperationLabel.setHorizontalAlignment(SwingConstants.LEFT);
		underScreen.add(OperationLabel);
		N2Label.setFont(new Font("Oxygen", Font.BOLD, 20));
		
		N2Label.setHorizontalAlignment(SwingConstants.LEFT);
		underScreen.add(N2Label);
		UgualeLabel.setFont(new Font("Oxygen", Font.BOLD, 20));
		
		UgualeLabel.setHorizontalAlignment(SwingConstants.LEFT);
		underScreen.add(UgualeLabel);
		RisultatoLabel.setFont(new Font("Oxygen", Font.BOLD, 20));
		
		RisultatoLabel.setHorizontalAlignment(SwingConstants.LEFT);
		underScreen.add(RisultatoLabel);
		
		JSeparator separator = new JSeparator();
		separator.setBackground(SystemColor.activeCaption);
		panel.add(separator);
		
		JPanel Tastiera = new JPanel();
		Tastiera.setBackground(SystemColor.activeCaption);
		panel.add(Tastiera);
		Tastiera.setLayout(new BoxLayout(Tastiera, BoxLayout.X_AXIS));
		
		JPanel C147 = new JPanel();
		C147.setBackground(SystemColor.activeCaption);
		Tastiera.add(C147);
		C147.setLayout(new BoxLayout(C147, BoxLayout.Y_AXIS));
		
		JButton btn1 = new JButton("1");
		btn1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NumberSystem.UpdateNumber(1);
			}
		});
		
		JButton btnC = new JButton("C");
		C147.add(btnC);
		btnC.setBackground(new Color(192, 192, 192));
		btnC.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NumberSystem.ResetAll();
			}
		});
		btn1.setBackground(new Color(204, 204, 204));
		C147.add(btn1);
		
		JButton btn4 = new JButton("4");
		btn4.setBackground(new Color(204, 204, 204));
		btn4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NumberSystem.UpdateNumber(4);
			}
		});
		C147.add(btn4);
		
		JButton btn7 = new JButton("7");
		btn7.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NumberSystem.UpdateNumber(7);
			}
		});
		btn7.setBackground(new Color(204, 204, 204));
		C147.add(btn7);
		
		JPanel P258 = new JPanel();
		P258.setBackground(SystemColor.activeCaption);
		Tastiera.add(P258);
		P258.setLayout(new BoxLayout(P258, BoxLayout.Y_AXIS));
		
		JButton btnP = new JButton("+");
		btnP.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NumberSystem.SetOperation(1);
			}
		});
		btnP.setBackground(new Color(204, 204, 204));
		P258.add(btnP);
		
		JButton btn2 = new JButton("2");
		btn2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NumberSystem.UpdateNumber(2);
			}
		});
		btn2.setBackground(new Color(204, 204, 204));
		P258.add(btn2);
		
		JButton btn5 = new JButton("5");
		btn5.setBackground(new Color(204, 204, 204));
		btn5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NumberSystem.UpdateNumber(5);
			}
		});
		P258.add(btn5);
		
		JButton btn8 = new JButton("8");
		btn8.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NumberSystem.UpdateNumber(8);
			}
		});
		btn8.setBackground(new Color(204, 204, 204));
		P258.add(btn8);
		
		JPanel M369 = new JPanel();
		M369.setBackground(SystemColor.activeCaption);
		Tastiera.add(M369);
		M369.setLayout(new BoxLayout(M369, BoxLayout.Y_AXIS));
		
		JButton btnM = new JButton(" -");
		btnM.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NumberSystem.SetOperation(2);
			}
		});
		btnM.setBackground(new Color(204, 204, 204));
		M369.add(btnM);
		
		JButton btn3 = new JButton("3");
		btn3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NumberSystem.UpdateNumber(3);
			}
		});
		btn3.setBackground(new Color(204, 204, 204));
		M369.add(btn3);
		
		JButton btn6 = new JButton("6");
		btn6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NumberSystem.UpdateNumber(6);
			}
		});
		btn6.setBackground(new Color(204, 204, 204));
		M369.add(btn6);
		
		JButton btn9 = new JButton("9");
		btn9.setBackground(new Color(204, 204, 204));
		btn9.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NumberSystem.UpdateNumber(9);
			}
		});
		M369.add(btn9);
		
		JPanel SDM0 = new JPanel();
		SDM0.setBackground(SystemColor.activeCaption);
		Tastiera.add(SDM0);
		SDM0.setLayout(new BoxLayout(SDM0, BoxLayout.Y_AXIS));
		
		JButton btnD = new JButton(" /");
		btnD.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NumberSystem.SetOperation(4);
			}
		});
		
		JButton btnS = new JButton("R");
		SDM0.add(btnS);
		btnS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NumberSystem.RadiceQ();
			}
		});
		btnS.setBackground(new Color(204, 204, 204));
		btnD.setBackground(new Color(204, 204, 204));
		SDM0.add(btnD);
		
		JButton btnM_1 = new JButton("x");
		btnM_1.setBackground(new Color(204, 204, 204));
		btnM_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				NumberSystem.SetOperation(3);
			}
		});
		SDM0.add(btnM_1);
		
		JButton btn0 = new JButton("0");
		btn0.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NumberSystem.UpdateNumber(0);
			}
		});
		btn0.setBackground(new Color(204, 204, 204));
		SDM0.add(btn0);
		
		JPanel UPo = new JPanel();
		UPo.setBackground(SystemColor.activeCaption);
		Tastiera.add(UPo);
		UPo.setLayout(new BoxLayout(UPo, BoxLayout.Y_AXIS));
		
		JButton btnU = new JButton(" = ");
		btnU.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NumberSystem.EgualOperation();
			}
		});
		UPo.add(btnU);
		btnU.setHorizontalAlignment(SwingConstants.RIGHT);
		btnU.setBackground(new Color(204, 204, 204));
		
		JButton btnPo = new JButton("  .  ");
		btnPo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainClass.ClickedPoint=1;
			}
		});
		btnPo.setBackground(new Color(204, 204, 204));
		UPo.add(btnPo);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setOrientation(SwingConstants.VERTICAL);
		separator_1.setBackground(SystemColor.activeCaption);
		panel.add(separator_1);
	}
}
