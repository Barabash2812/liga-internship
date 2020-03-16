package ru.liga;

import com.leff.midi.MidiFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.analysis.impl.DurationAnalysis;
import ru.liga.analysis.impl.QuantityAnalysis;
import ru.liga.analysis.impl.RangeAnalysis;
import ru.liga.change.impl.TempoChange;
import ru.liga.change.impl.TransposeChange;

import java.io.FileInputStream;

public class App {

    private static Logger allLogger = LoggerFactory.getLogger(App.class);
    private static Logger analysisLogger = LoggerFactory.getLogger("analyze");
    private static Logger consoleLogger = LoggerFactory.getLogger("console");

    public static void main(String[] args) throws Exception {
        String name = getName(args);
        String path = getPath(args);
        String operation = getOperation(args);
        MidiFile midiFile = new MidiFile(new FileInputStream(args[0]));

        if (args.length == 2) {
            if (operation.equals("analyze")) {
                allLogger.info("File: {}.mid Operation: {}", name, operation);
                String report = fullAnalysis(midiFile);
                analysisLogger.info(report);
                consoleLogger.info(report);
            } else {
                allLogger.warn("Wrong operation! Please choose: analyze, change.");
            }
        }

        if (args.length == 6) {
            if (operation.equals("change")) {
                if (args[2].equals("-trans") && args[4].equals("-tempo")) {
                    allLogger.info("File: {}.mid Operation: {} Transpose: {} Tempo: {}", name, operation, args[3], args[5]);
                    transposeAndTempoChange(midiFile, Integer.parseInt(args[3]), Integer.parseInt(args[5]), name, path);
                } else {
                    allLogger.warn("Wrong change operation parameters!");
                }
            } else {
                allLogger.warn("Wrong operation! Please choose: analyze, change.");
            }
        }
    }

    private static void transposeAndTempoChange(MidiFile midiFile, int semitones, int percentage, String name, String path) {
        allLogger.info("Transpose and tempo change operations started");
        Long start = System.nanoTime();

        TransposeChange transposeChange = new TransposeChange(midiFile, semitones, name, path);
        transposeChange.change();
        TempoChange tempoChange = new TempoChange(midiFile, percentage, transposeChange.getNewName(), path);
        tempoChange.change();
        tempoChange.save();

        Long end = System.nanoTime();
        allLogger.info("Transpose and tempo change operations completed. Elapsed time: {}ms", (end - start) / 1000000);
    }

    public static String fullAnalysis(MidiFile midiFile) {
        allLogger.info("Full analysis started");
        Long start = System.nanoTime();

        DurationAnalysis durationAnalysis = new DurationAnalysis(midiFile);
        QuantityAnalysis quantityAnalysis = new QuantityAnalysis(midiFile);
        RangeAnalysis rangeAnalysis = new RangeAnalysis(midiFile);
        durationAnalysis.perform();
        quantityAnalysis.perform();
        rangeAnalysis.perform();

        Long end = System.nanoTime();
        allLogger.info("Full analysis completed. Report saved in file: midi-analysis. Elapsed time: {}ms", (end - start) / 1000000);
        return "\n" + rangeAnalysis.report() + "\n" + durationAnalysis.report() + "\n" + quantityAnalysis.report();
    }

    public static String getName(String[] args) {
        return args[0].substring(args[0].lastIndexOf("\\") + 1, args[0].lastIndexOf("."));
    }

    public static String getPath(String[] args) {
        return args[0].replaceAll("/", "\\").substring(0, args[0].lastIndexOf("\\"));
    }

    public static String getOperation(String[] args) {
        return args[1];
    }
}
