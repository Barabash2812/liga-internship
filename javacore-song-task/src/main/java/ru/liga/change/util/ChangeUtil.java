package ru.liga.change.util;

import com.leff.midi.MidiFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class ChangeUtil {

    private static Logger allLogger = LoggerFactory.getLogger(ChangeUtil.class);

    public static void save(MidiFile midiFile, String path, String newName) {
        File outMidiFile = new File(path + "\\" + newName + ".mid");
        try {
            midiFile.writeToFile(outMidiFile);
        } catch (IOException e) {
            allLogger.error("Some problems with file \n Class Name: {} Method Name: {} Line Number: {}", e.getStackTrace()[0].getClassName(), e.getStackTrace()[0].getMethodName(), e.getStackTrace()[0].getLineNumber());
            return;
        }
        allLogger.info("Changed MIDI-file saved with name: {}.mid", newName);
    }
}
