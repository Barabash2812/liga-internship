package ru.liga.change.impl;

import com.leff.midi.MidiFile;
import com.leff.midi.event.meta.Tempo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.change.Changer;
import ru.liga.exception.MidiEventNotFoundException;

public class TempoChange extends Changer {

    static Logger allLogger = LoggerFactory.getLogger(TempoChange.class);

    public TempoChange(MidiFile midiFile, String name, int param) {
        super(midiFile, param);
        this.newName = setNewName(name, param);
    }

    @Override
    public MidiFile change() {
        allLogger.info("Track tempo change started");
        Long start = System.nanoTime();

        Tempo tempo;
        try {
            tempo = (Tempo) midiFile.getTracks().get(0).getEvents().stream()
                    .filter(value -> value instanceof Tempo)
                    .findAny()
                    .orElseThrow(() -> new MidiEventNotFoundException("Tempo event not found"));
        } catch (MidiEventNotFoundException e) {
            allLogger.error("Tempo event not found \n Class Name: {} Method Name: {} Line Number: {}", e.getStackTrace()[0].getClassName(), e.getStackTrace()[0].getMethodName(), e.getStackTrace()[0].getLineNumber());
            return null;
        }

        tempo.setBpm(tempo.getBpm() * (1 + param / 100f));
        result.add(tempo);

        Long end = System.nanoTime();
        allLogger.info("Track tempo change completed. Elapsed time: {}ms", (end - start) / 1000000);

        return midiFile;
    }

    private String setNewName(String name, int param) {
        return name + "-tempo" + param;
    }
}
