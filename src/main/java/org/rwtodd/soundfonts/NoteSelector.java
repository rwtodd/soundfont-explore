package org.rwtodd.soundfonts;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Closeable;
import java.io.IOException;
import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;
import javax.swing.JPanel;

/**
 * A component that allows the user to play notes with mouse clicks.
 *
 * @author rwtodd
 */
public class NoteSelector extends MouseAdapter implements AutoCloseable {

    private final JPanel panel;
    private int playingNote;
    private Instrument loadedInstrument;
    private final Synthesizer synth;
    
    public NoteSelector() throws MidiUnavailableException {
        panel = new JPanel();
        panel.setPreferredSize(new Dimension(40,200));
        panel.addMouseListener(this);
        playingNote = -1;
        loadedInstrument = null;
        synth = MidiSystem.getSynthesizer();
        synth.open();
    }

    JPanel getUserInterface() {
        return panel;
    }

    void setInstrument(Instrument i) {
        if(i == null) return;
        try {
            if(loadedInstrument != null) {
                synth.unloadInstrument(loadedInstrument);
            }
            synth.loadInstrument(i);
            loadedInstrument = i;
            synth.getReceiver().send(  
                    new ShortMessage(ShortMessage.PROGRAM_CHANGE, 0, i.getPatch().getProgram(), i.getPatch().getBank()),
                    -1);
        } catch (InvalidMidiDataException|MidiUnavailableException ex) {
            /* do what, exactly??? */
            System.err.println(ex);
        }
    }

    @Override
    public void mousePressed(MouseEvent me) {
        final double y = me.getY();
        final double ht = panel.getHeight();
        playingNote = 127 - (int) (127 * (y / ht));
        final int velocity = 90;

        try {
            // range of MIDI is 0 to 127....
            final var msg = new ShortMessage(ShortMessage.NOTE_ON, 0, playingNote, velocity);
            synth.getReceiver().send(msg, -1);
        } catch (InvalidMidiDataException | MidiUnavailableException ex) {
            /* for now, just ignore it... maybe need to do more... */
            System.err.println(ex);
        }
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        if (playingNote != -1) {
            try {
                // range of MIDI is 0 to 127....
                final var msg = new ShortMessage(ShortMessage.NOTE_OFF, 0, playingNote, 0);
                synth.getReceiver().send(msg, -1);
            } catch (InvalidMidiDataException | MidiUnavailableException ex) {
                /* for now, just ignore it... maybe need to do more... */
                System.err.println(ex);
            } finally {
                playingNote = -1;
            }
        }
    }

    @Override
    public void close() {
        synth.close();
    }

}
