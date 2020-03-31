package ru.liga.analysis.impl;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.analysis.Analyzer;
import ru.liga.analysis.util.AnalysisUtil;
import ru.liga.exception.MidiTrackNotFoundException;
import ru.liga.songtask.domain.Note;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class RangeAnalysis extends Analyzer {

    private static Logger allLogger = LoggerFactory.getLogger(RangeAnalysis.class);

    public RangeAnalysis(MidiFile midiFile) {
        super(midiFile);
    }

    @Override
    public void perform() {
        allLogger.info("Range analysis started");
        Long start = System.nanoTime();

        MidiTrack voiceTrack;
        try {
            voiceTrack = AnalysisUtil.getVoiceTrack(midiFile);
        } catch (MidiTrackNotFoundException e) {
            allLogger.error("Voice track not found \n Class Name: {} Method Name: {} Line Number: {}", e.getStackTrace()[0].getClassName(), e.getStackTrace()[0].getMethodName(), e.getStackTrace()[0].getLineNumber());
            return;
        }

        List<Note> trackNotes = AnalysisUtil.eventsToNotes(voiceTrack.getEvents());
        Note minNote = trackNotes.stream().min(Comparator.comparing(Note::sign)).get();
        Note maxNote = trackNotes.stream().max(Comparator.comparing(Note::sign)).get();
        Integer range = maxNote.sign().ordinal() - minNote.sign().ordinal();

        Long end = System.nanoTime();

        result.addAll(Arrays.asList(minNote, maxNote, range));
        report = report();

        allLogger.info("Range analysis completed. Elapsed time: {}ms", (end - start) / 1000000);
    }

    public String report() {
        return "Анализ диапазона: \n" +
                "верхняя: " + ((Note) result.get(0)).sign().fullName() + "\n" +
                "нижняя: " + ((Note) result.get(1)).sign().fullName() + "\n" +
                "диапазон: " + (Integer) result.get(2) + "\n";
    }
}
