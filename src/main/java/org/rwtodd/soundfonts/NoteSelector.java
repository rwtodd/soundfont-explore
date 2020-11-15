package org.rwtodd.soundfonts;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * A component that allows the user to play notes with mouse clicks.
 *
 * @author rwtodd
 */
public class NoteSelector extends MouseAdapter implements AutoCloseable {

    private final JComponent panel;
    private final Synthesizer synth;
    private final MidiChannel channel0;
    private Soundbank currentSoundbank;

    public NoteSelector() throws MidiUnavailableException {
        // This panel draws a grid to help you know you can click in it for
        // notes+velocity info
        panel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                final int wd = getWidth();
                final int ht = getHeight();
                final int xinc = Math.max(wd / 64, 4);
                final int yinc = Math.max(ht / 64, 4);
                g.setColor(Color.BLACK);
                for(int x = 0; x < wd; x+=xinc) {
                    g.drawLine(x, 0, x, ht);
                }
                for(int y = 0; y < ht; y+=yinc) {
                    g.drawLine(0, y, wd, y);
                }
            }
        };
        panel.setPreferredSize(new Dimension(128, 128));
        panel.addMouseListener(this);
        synth = MidiSystem.getSynthesizer();
        synth.open();
        channel0 = synth.getChannels()[0];
        currentSoundbank = null;
    }

    JComponent getUserInterface() {
        return panel;
    }

    void setSoundbank(final Soundbank sb) {
        if (currentSoundbank != null) {
            synth.unloadAllInstruments(currentSoundbank);
        }
        currentSoundbank = sb;
        synth.loadAllInstruments(sb);
    }

    void setInstrument(final Instrument i) {
        if (i == null) {
            return;
        }
        channel0.programChange(i.getPatch().getBank(), i.getPatch().getProgram());
    }

    @Override
    public void mousePressed(MouseEvent me) {
        final double y = me.getY();
        final double x = me.getX();

        final double ht = panel.getHeight();
        final double wd = panel.getWidth();

        // range of MIDI is 0 to 127....
        final int midiNote = 127 - (int) (127 * (y / ht));
        // range of Velocity i 0 to 127...
        final int velocity = (int) (127 * (x / wd));

        channel0.noteOn(midiNote, velocity);
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        channel0.allNotesOff();
    }

    @Override
    public void close() {
        synth.close();
    }

}
