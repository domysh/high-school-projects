package graphics;
import backend.CodiceFiscale;
import backend.Comuni;
import other.svar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FormInputPane extends JPanel {

    JTextField cognome, nome, luogo_di_nascita,provincia;
    JComboBox sesso;
    ComboDataChooser data_di_nascita;
    JButton submit;
    JLabel result;

    final String[] choose_sesso = {"M","F"};

    public FormInputPane() {
        super();
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        final int HC = 30; // Height components
        final int VGAP_ROW = 0;
        {
            JPanel row_cognome = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, VGAP_ROW));
            row_cognome.setBackground(Color.WHITE);
            {
                JLabel indicazione_inp = new JLabel("<html><h3><b>COGNOME</b></h3></html>");
                indicazione_inp.setForeground(svar.back_color);
                indicazione_inp.setSize(90, HC);
                indicazione_inp.setMinimumSize(new Dimension(90, HC));
                row_cognome.add(indicazione_inp);
            }
            {
                cognome = new JTextField();
                cognome.setPreferredSize(new Dimension(410, HC));
                row_cognome.add(cognome);
            }
            add(row_cognome);
        }
        {
            JPanel row_nome_sesso = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, VGAP_ROW));
            row_nome_sesso.setBackground(Color.WHITE);
            {
                JLabel indicazione_inp = new JLabel("<html><h3><b>NOME</b></h3></html>");
                indicazione_inp.setMinimumSize(new Dimension(90, HC));
                indicazione_inp.setForeground(svar.back_color);
                indicazione_inp.setSize(90, HC);
                row_nome_sesso.add(indicazione_inp);
            }
            {
                nome = new JTextField();
                nome.setPreferredSize(new Dimension(310, HC));
                row_nome_sesso.add(nome);
            }
            {
                JLabel indicazione_inp = new JLabel("<html><h3><b>SESSO</b></h3></html>");
                indicazione_inp.setMinimumSize(new Dimension(90, HC));
                indicazione_inp.setForeground(svar.back_color);
                indicazione_inp.setSize(90, HC);

                row_nome_sesso.add(indicazione_inp);
            }
            {
                sesso = new JComboBox(choose_sesso);
                sesso.setBackground(Color.WHITE);
                row_nome_sesso.add(sesso);
            }
            add(row_nome_sesso);
        }
        {
            JPanel row_nascita = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, VGAP_ROW));
            row_nascita.setBackground(Color.WHITE);
            {
                JLabel indicazione_inp = new JLabel("<html><h3><b>LUOGO DI NASCITA</b></h3></html>");
                indicazione_inp.setForeground(svar.back_color);
                indicazione_inp.setSize(90, HC);
                indicazione_inp.setMinimumSize(new Dimension(90, HC));
                row_nascita.add(indicazione_inp);
            }
            {
                luogo_di_nascita = new JTextField();
                luogo_di_nascita.setPreferredSize(new Dimension(350, HC));
                row_nascita.add(luogo_di_nascita);
            }
            add(row_nascita);
        }
        {
            JPanel row_provincia_nascita = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, VGAP_ROW));
            row_provincia_nascita.setBackground(Color.WHITE);
            {
                JLabel indicazione_inp = new JLabel("<html><h3><b>PROVINCIA<br>(SIGLA)</b></h3></html>");
                indicazione_inp.setForeground(svar.back_color);
                indicazione_inp.setSize(90, HC);
                indicazione_inp.setMinimumSize(new Dimension(90, HC));
                row_provincia_nascita.add(indicazione_inp);
            }
            {
                provincia = new JTextField();
                provincia.setPreferredSize(new Dimension(140, HC));
                row_provincia_nascita.add(provincia);
            }
            {
                JLabel indicazione_inp = new JLabel("<html><h3><b>DATA DI<br>NASCITA</b></h3></html>");
                indicazione_inp.setForeground(svar.back_color);
                indicazione_inp.setSize(90, HC);
                indicazione_inp.setMinimumSize(new Dimension(90, HC));
                row_provincia_nascita.add(indicazione_inp);
            }
            {
                data_di_nascita = new ComboDataChooser();
                data_di_nascita.setBackground(Color.WHITE);
                data_di_nascita.giorno.setBackground(Color.WHITE);
                data_di_nascita.mese.setBackground(Color.WHITE);
                data_di_nascita.anno.setBackground(Color.WHITE);
                row_provincia_nascita.add(data_di_nascita);
            }
            add(row_provincia_nascita);
        }
        {
            submit = new JButton("Calcola il Codice Fiscale");
            MarginPane submit_button = new MarginPane(submit, 30, 20);
            submit_button.setBackground(Color.WHITE);
            submit.setBackground(svar.back_color);
            submit.setForeground(Color.WHITE);
            submit.setPreferredSize(new Dimension(500, HC));
            add(submit_button);
        }
        {
            JPanel row_result = new JPanel(new FlowLayout(FlowLayout.CENTER, 0,0));
            row_result.setBackground(Color.WHITE);
            result = new JLabel("Programmed by DomySh", SwingConstants.CENTER); //Initial value
            result.setForeground(svar.back_color);
            result.setSize(90, HC);
            result.setMinimumSize(new Dimension(90, HC));
            row_result.add(result);
            add(row_result);
        }
        //Listener
        {
             submit.addActionListener((l)->{
                 if(cognome.getText().equalsIgnoreCase("")){
                     result.setForeground(Color.RED);
                     result.setText("Inserire un cognome!");
                     return;
                 }
                 if(nome.getText().equalsIgnoreCase("")) {
                     result.setForeground(Color.RED);
                     result.setText("Inserire un nome!");
                     return;
                 }
                 String city = luogo_di_nascita.getText();
                 String prov = provincia.getText();
                 if(svar.isAlphaS(city) && city.length()!= 0) {
                     if(prov.length() != 0 && Comuni.provinceExist(prov)) {
                         try {
                             Comuni.getBelfiore(city,prov);
                         }catch(Exception e) {
                             result.setForeground(Color.RED);
                             result.setText("Inserire un luogo di nascita valido!");
                             return;
                         }
                     }else{
                         result.setForeground(Color.RED);
                         result.setText("Inserire una sigla di provincia valida!");
                         return;
                     }

                 }else{
                     result.setForeground(Color.RED);
                     result.setText("Inserire un luogo di nascita valido!");
                     return;
                 }
                 try {
                     boolean sex = ((String)sesso.getSelectedItem()).equalsIgnoreCase("f");
                     int[] date_cf = new int[]{
                             Integer.parseInt( (String) data_di_nascita.giorno.getSelectedItem()),
                             Integer.parseInt( (String) data_di_nascita.mese.getSelectedItem()),
                             Integer.parseInt( (String) data_di_nascita.anno.getSelectedItem())
                     };
                     String cf = CodiceFiscale.calc(nome.getText(), cognome.getText(), sex, date_cf, city, prov);
                     result.setForeground(svar.back_color);
                     result.setText("CODICE FISCALE GENERATO: "+cf+"");
                 }catch(Exception e){
                     result.setForeground(Color.RED);
                     result.setText("Errore nella generazione del codice fiscale");
                 }
             });
        }

    }

}
