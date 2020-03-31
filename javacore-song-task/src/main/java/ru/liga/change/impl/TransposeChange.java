package ru.liga.change.impl;

import com.leff.midi.MidiFile;
import com.leff.midi.event.NoteOn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.change.Changer;

public class TransposeChange extends Changer {

    static Logger allLogger = LoggerFactory.getLogger(TransposeChange.class);

    public TransposeChange(MidiFile midiFile, String name, int param) {
        super(midiFile, param);
        this.newName = setNewName(name, param);
    }

    @Override
    public MidiFile change() {
        allLogger.info("Track transpose change started");
        Long start = System.nanoTime();

        midiFile.getTracks().forEach(track ->
                track.getEvents().stream().filter(event -> event instanceof NoteOn)
                        .map(noteEvent -> (NoteOn) noteEvent)
                        .forEach(note -> note.setNoteValue(note.getNoteValue() + param))
        );

        Long end = System.nanoTime();
        allLogger.info("Track transpose change completed. Elapsed time: {}ms", (end - start) / 1000000);

        return midiFile;
    }

    private String setNewName(String name, int semitones) {
        return name + "-trans" + semitones;
    }
}
