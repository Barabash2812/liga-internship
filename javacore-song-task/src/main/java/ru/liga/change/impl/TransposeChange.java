package ru.liga.change.impl;

import com.leff.midi.MidiFile;
import com.leff.midi.event.NoteOn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.change.Changer;

import java.io.File;
import java.io.IOException;

public class TransposeChange implements Changer {

    static Logger allLogger = LoggerFactory.getLogger(TransposeChange.class);
    private MidiFile midiFile;
    private int semitones;
    private String path;
    private String newName;

    public TransposeChange(MidiFile midiFile, int semitones, String name, String path) {
        this.midiFile = midiFile;
        this.semitones = semitones;
        this.path = path;
        this.newName = setNewName(name, semitones);
    }

    @Override
    public void change() {
        allLogger.info("Track transpose change started");
        Long start = System.nanoTime();

        midiFile.getTracks().forEach(track ->
                track.getEvents().stream().filter(event -> event instanceof NoteOn)
                        .map(noteEvent -> (NoteOn) noteEvent)
                        .forEach(note -> note.setNoteValue(note.getNoteValue() + semitones))
        );

        Long end = System.nanoTime();
        allLogger.info("Track transpose change completed. Elapsed time: {}ms", (end - start) / 1000000);
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

    private String setNewName(String name, int semitones) {
        return name + "-trans" + semitones;
    }

    public String getNewName() {
        return newName;
    }

    public MidiFile getMidiFile() {
        return midiFile;
    }
}
