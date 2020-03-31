package ru.liga.analysis;

import com.leff.midi.MidiFile;
import lombok.Getter;

import java.util.ArrayList;

@Getter
public abstract class Analyzer {

    protected MidiFile midiFile;
    protected String report;
    protected ArrayList<Object> result = new ArrayList<>();

    public Analyzer(MidiFile midiFile) {
        this.midiFile = midiFile;
    }

    public abstract void perform();

    public abstract String report();
}
