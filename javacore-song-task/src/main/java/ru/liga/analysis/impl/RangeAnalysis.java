package ru.liga.analysis.impl;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.analysis.Analyzer;
import ru.liga.analysis.util.AnalysisUtil;
import ru.liga.exception.MidiTrackNotFoundException;
import ru.liga.songtask.domain.Note;

import java.util.Comparator;
import java.util.List;

public class RangeAnalysis implements Analyzer {

    private static Logger allLogger = LoggerFactory.getLogger(RangeAnalysis.class);
    private MidiFile midiFile;
    private MidiTrack voiceTrack;
    private Note minNote;
    private Note maxNote;
    private Integer range;

    public RangeAnalysis(MidiFile midiFile) {
        this.midiFile = midiFile;
    }

    @Override
    public void perform() {
        allLogger.info("Range analysis started");
        Long start = System.nanoTime();

        try {
            voiceTrack = AnalysisUtil.getVoiceTrack(midiFile);
        } catch (MidiTrackNotFoundException e) {
            allLogger.error("Voice track not found \n Class Name: {} Method Name: {} Line Number: {}", e.getStackTrace()[0].getClassName(), e.getStackTrace()[0].getMethodName(), e.getStackTrace()[0].getLineNumber());
            return;
        }

        List<Note> trackNotes = AnalysisUtil.eventsToNotes(voiceTrack.getEvents());
        minNote = trackNotes.stream().min(Comparator.comparing(Note::sign)).get();
        maxNote = trackNotes.stream().max(Comparator.comparing(Note::sign)).get();
        range = maxNote.sign().ordinal() - minNote.sign().ordinal();

        Long end = System.nanoTime();
        allLogger.info("Range analysis completed. Elapsed time: {}ms", (end - start) / 1000000);
    }

    @Override
    public String report() {
        return "Анализ диапазона: \n" +
                "верхняя: " + maxNote.sign().fullName() + "\n" +
                "нижняя: " + minNote.sign().fullName() + "\n" +
                "диапазон: " + range + "\n";
    }

    public MidiTrack getVoiceTrack() {
        return voiceTrack;
    }

    public Note getMinNote() {
        return minNote;
    }

    public Note getMaxNote() {
        return maxNote;
    }

    public Integer getRange() {
        return range;
    }
}
