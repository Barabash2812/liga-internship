package ru.liga;

import com.leff.midi.MidiFile;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.meta.Tempo;
import org.junit.Before;
import org.junit.Test;
import ru.liga.analysis.Analyzer;
import ru.liga.analysis.impl.DurationAnalysis;
import ru.liga.analysis.impl.QuantityAnalysis;
import ru.liga.analysis.impl.RangeAnalysis;
import ru.liga.analysis.util.AnalysisUtil;
import ru.liga.change.Changer;
import ru.liga.change.impl.TempoChange;
import ru.liga.change.impl.TransposeChange;
import ru.liga.exception.MidiTrackNotFoundException;
import ru.liga.songtask.domain.Note;
import ru.liga.songtask.domain.NoteSign;

import java.io.InputStream;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class AppTest {

    MidiFile midiFile;
    Analyzer durationAnalysis;
    Analyzer quantityAnalysis;
    Analyzer rangeAnalysis;
    Changer transposeChange;
    Changer tempoChange;

    @Before
    public void setup() throws Exception {
        midiFile = new MidiFile(getFile("Wrecking Ball.mid"));
        durationAnalysis = new DurationAnalysis(midiFile);
        durationAnalysis.perform();
        quantityAnalysis = new QuantityAnalysis(midiFile);
        quantityAnalysis.perform();
        rangeAnalysis = new RangeAnalysis(midiFile);
        rangeAnalysis.perform();
        transposeChange = new TransposeChange(midiFile, "Wrecking Ball", 2);
        transposeChange.change();
        tempoChange = new TempoChange(midiFile, "Wrecking Ball", 20);
        tempoChange.change();
    }

    @Test
    public void rightDurationAnalysisFirstAndLast() {
        assertThat(((Map<Integer, Integer>) durationAnalysis.getResult().get(0)).get(124)).isEqualTo(9);
        assertThat(((Map<Integer, Integer>) durationAnalysis.getResult().get(0)).get(745)).isEqualTo(16);
    }

    @Test
    public void rightRangeAnalysis() {
        assertThat(((Note) rangeAnalysis.getResult().get(0)).sign().fullName()).isEqualTo("A#4");
        assertThat(((Note) rangeAnalysis.getResult().get(1)).sign().fullName()).isEqualTo("F3");
        assertThat(rangeAnalysis.getResult().get(2)).isEqualTo(17);
    }

    @Test
    public void rightQuantityAnalysisFirstAndLast() {
        assertThat(((Map<Integer, Integer>) quantityAnalysis.getResult().get(0)).get(NoteSign.F_3.ordinal())).isEqualTo(5);
        assertThat(((Map<Integer, Integer>) quantityAnalysis.getResult().get(0)).get(NoteSign.A_SHARP_4.ordinal())).isEqualTo(79);
    }

    @Test
    public void rightTempoChange() {
        assertThat(((Tempo) tempoChange.getResult().get(0)).getBpm()).isEqualTo(144);
    }

    @Test
    public void rightTransposeChange() throws MidiTrackNotFoundException {
        assertThat(((NoteOn) AnalysisUtil.getVoiceTrack(transposeChange.getMidiFile()).getEvents().last()).getNoteValue()).isEqualTo(72);
    }

    private InputStream getFile(String fileName) {
        InputStream result;
        ClassLoader classLoader = getClass().getClassLoader();
        result = classLoader.getResourceAsStream(fileName);
        return result;
    }
}

