package ru.liga;

import com.leff.midi.MidiFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.analysis.Analyzer;
import ru.liga.analysis.impl.DurationAnalysis;
import ru.liga.analysis.impl.QuantityAnalysis;
import ru.liga.analysis.impl.RangeAnalysis;
import ru.liga.change.Changer;
import ru.liga.change.impl.TempoChange;
import ru.liga.change.impl.TransposeChange;
import ru.liga.change.util.ChangeUtil;

import java.util.Arrays;

public class Engine {

    private static Logger allLogger = LoggerFactory.getLogger(Engine.class);
    private static Logger analysisLogger = LoggerFactory.getLogger("analyze");
    private static Logger consoleLogger = LoggerFactory.getLogger("console");

    public static void analyze(String[] args, MidiFile midiFile) {
        allLogger.info("File: {}.mid Operation: analyze", getName(args));
        String report = fullAnalysis(midiFile);
        analysisLogger.info(report);
        consoleLogger.info(report);
    }

    public static void change(String[] args, MidiFile midiFile) {
        allLogger.info("File: {}.mid Operation: change Transpose: {} Tempo: {}", getName(args), args[3], args[5]);
        transposeAndTempoChange(midiFile, Integer.parseInt(args[3]), Integer.parseInt(args[5]), getName(args), getPath(args));
    }

    public static String getOperation(String[] args) {
        String input = Arrays.stream(args).reduce((s1, s2) -> s1 + " " + s2).orElse("wrong");
        String analyzePattern = ".+ analyze";
        String changePattern = ".+ change -trans \\d+ -tempo \\d+";
        if (input.matches(analyzePattern)) return "analyze";
        if (input.matches(changePattern)) return "change";
        return input;
    }

    private static void transposeAndTempoChange(MidiFile midiFile, int semitones, int percentage, String name, String path) {
        allLogger.info("Transpose and tempo change operations started");
        Long start = System.nanoTime();

        Changer transposeChange = new TransposeChange(midiFile, name, semitones);
        MidiFile newMidiFile = transposeChange.change();
        Changer tempoChange = new TempoChange(newMidiFile, transposeChange.getNewName(), percentage);
        ChangeUtil.save(tempoChange.change(), path, tempoChange.getNewName());

        Long end = System.nanoTime();
        allLogger.info("Transpose and tempo change operations completed. Elapsed time: {}ms", (end - start) / 1000000);
    }

    private static String fullAnalysis(MidiFile midiFile) {
        allLogger.info("Full analysis started");
        Long start = System.nanoTime();

        Analyzer durationAnalysis = new DurationAnalysis(midiFile);
        Analyzer quantityAnalysis = new QuantityAnalysis(midiFile);
        Analyzer rangeAnalysis = new RangeAnalysis(midiFile);
        durationAnalysis.perform();
        quantityAnalysis.perform();
        rangeAnalysis.perform();

        Long end = System.nanoTime();
        allLogger.info("Full analysis completed. Report saved in file: midi-analysis. Elapsed time: {}ms", (end - start) / 1000000);
        return "\n" + rangeAnalysis.report() + "\n" + durationAnalysis.report() + "\n" + quantityAnalysis.report();
    }

    private static String getName(String[] args) {
        return args[0].substring(args[0].lastIndexOf("\\") + 1, args[0].lastIndexOf("."));
    }

    private static String getPath(String[] args) {
        return args[0].replaceAll("/", "\\").substring(0, args[0].lastIndexOf("\\"));
    }
}
