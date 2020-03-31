package ru.liga.analysis.impl;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.analysis.Analyzer;
import ru.liga.analysis.util.AnalysisUtil;
import ru.liga.exception.MidiTrackNotFoundException;
import ru.liga.songtask.domain.Note;
import ru.liga.songtask.domain.NoteSign;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class QuantityAnalysis extends Analyzer {

    private static Logger allLogger = LoggerFactory.getLogger(QuantityAnalysis.class);

    public QuantityAnalysis(MidiFile midiFile) {
        super(midiFile);
    }

    public void perform() {
        allLogger.info("Quantity analysis started");
        Long start = System.nanoTime();

        Map<Integer, Integer> signs = new TreeMap<>(Comparator.reverseOrder());

        MidiTrack voiceTrack;
        try {
            voiceTrack = AnalysisUtil.getVoiceTrack(midiFile);
        } catch (MidiTrackNotFoundException e) {
            allLogger.error("Voice track not found \n Class Name: {} Method Name: {} Line Number: {}", e.getStackTrace()[0].getClassName(), e.getStackTrace()[0].getMethodName(), e.getStackTrace()[0].getLineNumber());
            return;
        }

        AnalysisUtil.eventsToNotes(voiceTrack.getEvents()).stream()
                .map(Note::sign)
                .map(Enum::ordinal)
                .map(signOrdinal -> {
                    int count = 0;
                    if (signs.containsKey(signOrdinal)) {
                        count = signs.get(signOrdinal);
                    }
                    return new Pair<>(signOrdinal, ++count);
                }).forEach(keyValue -> signs.put(keyValue.getKey(), keyValue.getValue()));

        Long end = System.nanoTime();

        result.add(signs);
        report = report();

        allLogger.info("Quantity analysis completed. Elapsed time: {}ms", (end - start) / 1000000);
    }

    public String report() {
        StringBuilder result = new StringBuilder("Анализ нот по количеству:\n");
        Map<Integer, Integer> signs = (Map<Integer, Integer>) this.result.get(0);
        for (Map.Entry<Integer, Integer> entry : signs.entrySet()) {
            result.append((NoteSign.values()[entry.getKey()]).fullName()).append(": ").append(entry.getValue()).append("\n");
        }
        return result.toString();
    }
}
