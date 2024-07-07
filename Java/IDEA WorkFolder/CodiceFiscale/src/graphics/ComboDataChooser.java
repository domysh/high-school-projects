package graphics;
import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ComboDataChooser extends JPanel {

    String[] giorni_list, mesi_list, anni_list;

    public JComboBox giorno,mese,anno;

    public ComboDataChooser(){
        super(new FlowLayout(FlowLayout.CENTER));

        giorni_list = new String[31];
        for (int i=1;i<=31;i++) giorni_list[i-1] = Integer.toString(i);
        mesi_list = new String[12];
        for (int i=1;i<=12;i++) mesi_list[i-1] = Integer.toString(i);

        int this_year = new GregorianCalendar().get(Calendar.YEAR);
        anni_list = new String[this_year-1900+1];
        for (int i=1900;i<=this_year;i++) anni_list[i-1900] = Integer.toString(i);

        giorno = new JComboBox(giorni_list);
        add(giorno);

        mese = new JComboBox(mesi_list);
        add(mese);

        anno = new JComboBox(anni_list);
        add(anno);

    }
}
