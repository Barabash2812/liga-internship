package ru.liga;

import com.leff.midi.MidiFile;
import com.leff.midi.event.NoteOn;
import org.junit.Before;
import org.junit.Test;
import ru.liga.analysis.impl.DurationAnalysis;
import ru.liga.analysis.impl.QuantityAnalysis;
import ru.liga.analysis.impl.RangeAnalysis;
import ru.liga.change.impl.TempoChange;
import ru.liga.change.impl.TransposeChange;
import ru.liga.songtask.domain.NoteSign;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class AppTest {
    MidiFile midiFile;
    DurationAnalysis durationAnalysis;
    QuantityAnalysis quantityAnalysis;
    RangeAnalysis rangeAnalysis;
    TransposeChange transposeChange;
    TempoChange tempoChange;

    @Before
    public void setup() throws Exception {
        midiFile = new MidiFile(getFile("Wrecking Ball.mid"));
        durationAnalysis = new DurationAnalysis(midiFile);
        durationAnalysis.perform();
        quantityAnalysis = new QuantityAnalysis(midiFile);
        quantityAnalysis.perform();
        rangeAnalysis = new RangeAnalysis(midiFile);
        rangeAnalysis.perform();
        transposeChange = new TransposeChange(midiFile, 2, "Wrecking Ball","C:\\Users\\ilyat\\HW2\\liga-internship\\javacore-song-task\\src\\main\\resources");
        transposeChange.change();
        tempoChange = new TempoChange(midiFile, 20, "Wrecking Ball","C:\\Users\\ilyat\\HW2\\liga-internship\\javacore-song-task\\src\\main\\resources");
        tempoChange.change();
    }

    @Test
    public void RightDurationAnalysisFirstAndLast() {
        assertThat(durationAnalysis.getDurations().get(124)).isEqualTo(9);
        assertThat(durationAnalysis.getDurations().get(745)).isEqualTo(16);
    }

    @Test
    public void RightRangeAnalysis() {
        assertThat(rangeAnalysis.getMaxNote().sign().fullName()).isEqualTo("F3");
        assertThat(rangeAnalysis.getMinNote().sign().fullName()).isEqualTo("A#4");
        assertThat(rangeAnalysis.getRange()).isEqualTo(17);
    }

    @Test
    public void RightQuantityAnalysisFirstAndLast() {
        assertThat(quantityAnalysis.getSigns().get(NoteSign.F_3.ordinal())).isEqualTo(5);
        assertThat(quantityAnalysis.getSigns().get(NoteSign.A_SHARP_4.ordinal())).isEqualTo(79);
    }

    @Test
    public void RightTempoChange() {
        assertThat(tempoChange.getTempo().getBpm()).isEqualTo(144);
    }

    @Test
    public void RightTransposeChange() {
        assertThat(((NoteOn)transposeChange.getMidiFile().getTracks().get(9).getEvents().last()).getNoteValue()).isEqualTo(72);
    }

    private InputStream getFile(String fileName) {
        InputStream result;
        ClassLoader classLoader = getClass().getClassLoader();
        result = classLoader.getResourceAsStream(fileName);
        return result;
    }
}

