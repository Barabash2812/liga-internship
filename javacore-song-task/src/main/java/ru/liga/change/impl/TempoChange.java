package ru.liga.change.impl;

import com.leff.midi.MidiFile;
import com.leff.midi.event.meta.Tempo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.change.Changer;
import ru.liga.exception.MidiEventNotFoundException;

import java.io.File;
import java.io.IOException;

public class TempoChange implements Changer {

    static Logger allLogger = LoggerFactory.getLogger(TempoChange.class);
    private MidiFile midiFile;
    private int percentage;
    private String path;
    private String newName;
    private Tempo tempo;

    public TempoChange(MidiFile midiFile, int percentage, String name, String path) {
        this.midiFile = midiFile;
        this.percentage = percentage;
        this.path = path;
        this.newName = setNewName(name, percentage);
    }

    @Override
    public void change() {
        allLogger.info("Track tempo change started");
        Long start = System.nanoTime();

        try {
            tempo = (Tempo) midiFile.getTracks().get(0).getEvents().stream()
                    .filter(value -> value instanceof Tempo)
                    .findAny()
                    .orElseThrow(() -> new MidiEventNotFoundException("Tempo event not found"));
        } catch (MidiEventNotFoundException e) {
            allLogger.error("Tempo event not found \n Class Name: {} Method Name: {} Line Number: {}", e.getStackTrace()[0].getClassName(), e.getStackTrace()[0].getMethodName(), e.getStackTrace()[0].getLineNumber());
            return;
        }

        tempo.setBpm(tempo.getBpm() * (1 + percentage / 100f));

        Long end = System.nanoTime();
        allLogger.info("Track tempo change completed. Elapsed time: {}ms", (end - start) / 1000000);
    }

    @Override
    public void save() {
        File outMidiFile = new File( path + "\\" + newName + ".mid");
        try {
            midiFile.writeToFile(outMidiFile);
        }
        catch (IOException e) {
            allLogger.error("Some problems with file \n Class Name: {} Method Name: {} Line Number: {}", e.getStackTrace()[0].getClassName(), e.getStackTrace()[0].getMethodName(), e.getStackTrace()[0].getLineNumber());
            return;
        }
        allLogger.info("Changed MIDI-file saved with name: {}.mid", newName);
    }

    private String setNewName(String name, int percentage) {
        return name + "-tempo" + percentage;
    }

    public Tempo getTempo() { return tempo; }

    public String getNewName() {
        return newName;
    }
}
