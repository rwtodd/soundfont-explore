package org.rwtodd.soundfonts;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Closeable;
import java.io.IOException;
import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Soundbank;
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
    private final MidiChannel channel0;
    private Soundbank currentSoundbank;

    public NoteSelector() throws MidiUnavailableException {
        panel = new JPanel();
        panel.setPreferredSize(new Dimension(40, 200));
        panel.addMouseListener(this);
        playingNote = -1;
        loadedInstrument = null;
        synth = MidiSystem.getSynthesizer();
        synth.open();
        channel0 = synth.getChannels()[0];
        currentSoundbank = null;
    }

    JPanel getUserInterface() {
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
        final double ht = panel.getHeight();
        // range of MIDI is 0 to 127....
        playingNote = 127 - (int) (127 * (y / ht));
        final int velocity = 90;

        channel0.noteOn(playingNote, velocity);
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        if (playingNote != -1) {
            channel0.noteOff(playingNote);
            playingNote = -1;
        }
    }

    @Override
    public void close() {
        synth.close();
    }

}
