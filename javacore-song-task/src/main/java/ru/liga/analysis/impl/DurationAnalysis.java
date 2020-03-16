package ru.liga.analysis.impl;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.meta.Tempo;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.analysis.Analyzer;
import ru.liga.analysis.util.AnalysisUtil;
import ru.liga.exception.MidiEventNotFoundException;
import ru.liga.exception.MidiTrackNotFoundException;
import ru.liga.songtask.domain.Note;
import ru.liga.songtask.util.SongUtils;

import java.util.Map;
import java.util.TreeMap;

public class DurationAnalysis implements Analyzer {

    private static Logger allLogger = LoggerFactory.getLogger(DurationAnalysis.class);
    private MidiFile midiFile;
    private MidiTrack voiceTrack;
    private Map<Integer, Integer> durations = new TreeMap<>();
    private Tempo tempo;

    public DurationAnalysis(MidiFile midiFile) {
        this.midiFile = midiFile;
    }

    @Override
    public void perform() {
        allLogger.info("Duration analysis started");
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

        try {
            voiceTrack = AnalysisUtil.getVoiceTrack(midiFile);
        } catch (MidiTrackNotFoundException e) {
            allLogger.error("Voice track not found \n Class Name: {} Method Name: {} Line Number: {}", e.getStackTrace()[0].getClassName(), e.getStackTrace()[0].getMethodName(), e.getStackTrace()[0].getLineNumber());
            return;
        }

        AnalysisUtil.eventsToNotes(voiceTrack.getEvents()).stream()
                .map(Note::durationTicks)
                .map(durationTicks -> SongUtils.tickToMs(tempo.getBpm(), midiFile.getResolution(), durationTicks))
                .map(durationMs -> {
                    int count = 0;
                    if (durations.containsKey(durationMs)) {
                        count = durations.get(durationMs);
                    }
                    return new Pair<>(durationMs, ++count);
                }).forEach(keyValue -> durations.put(keyValue.getKey(), keyValue.getValue()));

        Long end = System.nanoTime();
        allLogger.info("Duration analysis completed. Elapsed time: {}ms", (end - start) / 1000000);
    }

    @Override
    public String report() {
        StringBuilder result = new StringBuilder("Анализ длительности нот (мс):\n");
        for (Map.Entry<Integer, Integer> entry : durations.entrySet()) {
            result.append(entry.getKey()).append("мс: ").append(entry.getValue()).append("\n");
        }
        return result.toString();
    }

    public MidiTrack getVoiceTrack() {
        return voiceTrack;
    }

    public Map<Integer, Integer> getDurations() {
        return durations;
    }

    public Tempo getTempo() {
        return tempo;
    }
}
