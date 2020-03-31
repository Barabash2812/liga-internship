package ru.liga;

import com.leff.midi.MidiFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;

public class App {

    private static Logger allLogger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws Exception {
        MidiFile midiFile = new MidiFile(new FileInputStream(args[0]));

        switch (Engine.getOperation(args)) {
            case "analyze": {
                Engine.analyze(args, midiFile);
                break;
            }
            case "change": {
                Engine.change(args, midiFile);
                break;
            }
            default:
                allLogger.warn("Wrong format. Input parameters should match one of these patterns: " +
                        "({Path} analyze) or ({Path} change -trans {semitones} -tempo {percentage}).");
        }
    }
}
