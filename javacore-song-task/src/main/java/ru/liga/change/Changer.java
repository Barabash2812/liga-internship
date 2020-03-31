package ru.liga.change;

import com.leff.midi.MidiFile;
import lombok.Getter;

import java.util.ArrayList;

@Getter
public abstract class Changer {

    protected MidiFile midiFile;
    protected String newName;
    protected int param;
    protected ArrayList<Object> result = new ArrayList<>();

    public Changer(MidiFile midiFile, int param) {
        this.midiFile = midiFile;
        this.param = param;
    }

    public abstract MidiFile change();
}
