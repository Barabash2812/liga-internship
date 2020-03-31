package ru.liga.analysis.util;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.meta.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.exception.MidiTrackNotFoundException;
import ru.liga.songtask.domain.Note;
import ru.liga.songtask.domain.NoteSign;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class AnalysisUtil {

    private static Logger allLogger = LoggerFactory.getLogger(AnalysisUtil.class);

    public static MidiTrack getVoiceTrack(MidiFile midiFile) throws MidiTrackNotFoundException {

        allLogger.info("Voice track search started");

        Long start = System.nanoTime();

        MidiTrack result = null;
        MidiTrack textTrack;
        List<MidiTrack> trackList = midiFile.getTracks();

        textTrack = trackList.stream()
                .filter(track -> track.getEvents().stream()
                        .filter(event -> event instanceof Text).count() == track.getEvents().size() - 1)
                .findFirst()
                .orElseThrow(() -> new MidiTrackNotFoundException("Text track not found"));

        List<MidiEvent> textEventsList = new ArrayList<>(textTrack.getEvents());
        textEventsList.sort(Comparator.naturalOrder());
        textEventsList.remove(0);

        for (MidiTrack track : trackList) {
            List<Note> notedTrackList = eventsToNotes(track.getEvents());
            if (notedTrackList.size() == textEventsList.size()) {
                boolean isVoiceTrack = true;
                for (int i = 0; i < textEventsList.size(); i++) {
                    if (!(notedTrackList.get(i).startTick() == textEventsList.get(i).getTick())) {
                        isVoiceTrack = false;
                    }
                }
                if (isVoiceTrack) {
                    result = track;
                    break;
                }
            }
        }

        Long end = System.nanoTime();

        if (result != null) {
            allLogger.info("Search completed. Voice track number found: {}. Elapsed time: {}ms", midiFile.getTracks().indexOf(result) + 1, (end - start) / 1000000);
        } else {
            allLogger.info("Search completed. Voice track not found. Elapsed time: {}ms", (end - start) / 1000000);
        }
        return result;
    }

    public static List<Note> eventsToNotes(TreeSet<MidiEvent> events) {
        List<Note> vbNotes = new ArrayList<>();
        Queue<NoteOn> noteOnQueue = new LinkedBlockingQueue<>();
        for (MidiEvent event : events) {
            if (event instanceof NoteOn || event instanceof NoteOff) {
                if (isEndMarkerNote(event)) {
                    NoteSign noteSign = NoteSign.fromMidiNumber(extractNoteValue(event));
                    if (noteSign != NoteSign.NULL_VALUE) {
                        NoteOn noteOn = noteOnQueue.poll();
                        if (noteOn != null) {
                            long start = noteOn.getTick();
                            long end = event.getTick();
                            vbNotes.add(
                                    new Note(noteSign, start, end - start));
                        }
                    }
                } else {
                    if (event instanceof NoteOn)
                        noteOnQueue.offer((NoteOn) event);
                }
            }
        }
        return vbNotes;
    }

    private static Integer extractNoteValue(MidiEvent event) {
        if (event instanceof NoteOff) {
            return ((NoteOff) event).getNoteValue();
        } else if (event instanceof NoteOn) {
            return ((NoteOn) event).getNoteValue();
        } else {
            return null;
        }
    }

    private static boolean isEndMarkerNote(MidiEvent event) {
        if (event instanceof NoteOff) {
            return true;
        } else if (event instanceof NoteOn) {
            return ((NoteOn) event).getVelocity() == 0;
        } else {
            return false;
        }
    }
}
