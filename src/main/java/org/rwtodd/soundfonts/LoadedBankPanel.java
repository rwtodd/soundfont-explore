package org.rwtodd.soundfonts;

import java.awt.BorderLayout;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Instrument;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

/**
 * A loaded sound bank, which knows how to present itself to the user.
 *
 * @author rwtodd
 */
public class LoadedBankPanel {

    private JPanel panel;
    private PropertyChangeSupport propertySupport;

    private Soundbank sb;
    private Instrument[] instruments;

    private final JLabel description;
    private final JTable instTable;
    private final JScrollPane scrollPane;
    private final InstrumentTableModel instTableModel;
    
    public LoadedBankPanel() {
        panel = new JPanel(new BorderLayout());
        propertySupport = new PropertyChangeSupport(this);

        description = new JLabel();
        panel.add(description, BorderLayout.NORTH);

        sb = NullSoundbank.INSTANCE;
        instruments = sb.getInstruments();
        instTableModel = new InstrumentTableModel();
        instTable = new JTable(instTableModel);
        instTable.setAutoCreateRowSorter(true);
        instTable.setFillsViewportHeight(true);
        scrollPane = new JScrollPane(instTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // set up listening for selected instrument changes...
        instTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        instTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    propertySupport.firePropertyChange("SelectedInstrument", null, getSelectedInstrument());
                }
            }

        });

        setSoundbank(sb);
    }

    /**
     * Get the UI that this component builds.
     *
     * @return the UI.
     */
    JPanel getUserInterface() {
        return panel;
    }

    public void addPropertyChangeListener(final PropertyChangeListener l) {
        propertySupport.addPropertyChangeListener(l);
    }

    // *** property Soundbank ***
    public Soundbank getSoundbank() {
        return sb;
    }

    public void setSoundbank(final Soundbank sb) {
        final var oldsb = this.sb;
        this.sb = sb;
        instruments = sb.getInstruments();

        description.setText(String.format("""
           <html>Name: <b>%s</b><br>
           Description: <b>%s</b>
           </html>
           """,
                sb.getName(),
                sb.getDescription()
        ));
        instTableModel.fireTableStructureChanged();
        propertySupport.firePropertyChange("Soundbank", oldsb, sb);
        propertySupport.firePropertyChange("SelectedInstrument", null, null);
    }

    public Instrument getSelectedInstrument() {
        final var selected = instTable.getSelectedRow();
        return (selected == -1)
                ? null
                : instruments[instTable.convertRowIndexToModel(selected)];
    }

    private class InstrumentTableModel extends AbstractTableModel {
        
        @Override
        public Class getColumnClass(int col) {
            return switch (col) {
                case 0,1 -> Integer.class;
                case 2 -> String.class;
                default -> null;
            };
        }
        
        @Override
        public String getColumnName(int col) {
            return switch (col) {
                case 0 ->
                    "Bank";
                case 1 ->
                    "Program";
                case 2 ->
                    "Name";
                default ->
                    "";
            };
        }

        @Override
        public int getRowCount() {
            return instruments.length;
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public Object getValueAt(int row, int col) {
            if ((row >= instruments.length) || (col >= 3)) {
                return null;
            }
            return switch (col) {
                case 0 ->
                    instruments[row].getPatch().getBank();
                case 1 ->
                    instruments[row].getPatch().getProgram();
                case 2 ->
                    instruments[row].getName();
                default ->
                    null;
            };
        }

    }
}
