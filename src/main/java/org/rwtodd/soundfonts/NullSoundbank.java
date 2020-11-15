package org.rwtodd.soundfonts;

import javax.sound.midi.Instrument;
import javax.sound.midi.Patch;
import javax.sound.midi.Soundbank;
import javax.sound.midi.SoundbankResource;

/**
 * A soundbank with no instruments.  An example of the Null design pattern, so that
 * there aren't special cases for when a Soundbank is not loaded.
 * @author rwtodd
 */
public enum NullSoundbank implements Soundbank {
    INSTANCE;
    
    @Override
    public String getName() {
        return "N/A";
    }

    @Override
    public String getVersion() {
        return "N/A";
    }

    @Override
    public String getVendor() {
        return "N/A";
    }

    @Override
    public String getDescription() {
        return "No soundbank is loaded.";
    }

    @Override
    public SoundbankResource[] getResources() {
        return new SoundbankResource[0];
    }

    @Override
    public Instrument[] getInstruments() {
        return new Instrument[0];
    }

    @Override
    public Instrument getInstrument(Patch patch) {
        return null;
    }
}
