import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class BeatCreator {
    Box leftBox = new Box(BoxLayout.Y_AXIS);
    Box rightBox = new Box(BoxLayout.Y_AXIS);
    JPanel centerPanel;
    ArrayList<JLabel> labelList = new ArrayList<JLabel>();
    ArrayList<JCheckBox> checkboxList = new ArrayList<JCheckBox>();
    JFrame frame = new JFrame("Cyber BeatBox");
    Sequencer sequencer;
    Sequence sequence;
    Track track;
    int[] instrument = {35, 42, 46, 38, 49, 39, 50, 60, 70, 35, 64, 56, 58, 47, 67, 63};
    String[] instrumentNames = {"Bass Drum",
            "Closed Hi-Har",
            "Open Hi-Har",
            "Acoustic Snare",
            "Crash Cymbal",
            "Hand Clap",
            "High Tom",
            "Hi Bongo",
            "Maracas",
            "Whistle",
            "Low Conga",
            "Cowbell",
            "Vibraslap",
            "Low-mind Tom",
            "High Agogo",
            "Open Hi Conga"};

    public static void main(String[] args) {
        new BeatCreator().setUpGUI();
    }

    public void setUpGUI() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BorderLayout layout = new BorderLayout();
        JPanel background = new JPanel(layout);
        background.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setUpLeftBox();
        setUpRightBox();
        setupCentPanel();
        background.add(BorderLayout.EAST, rightBox);
        background.add(BorderLayout.WEST, leftBox);
        background.add(BorderLayout.CENTER, centerPanel);
        frame.getContentPane().add(background);
        setupMidi();
        frame.setBounds(50, 50, 300, 300);
        frame.pack();
        frame.setVisible(true);


    }

    public void setUpLeftBox() {
        for (String inst : instrumentNames) {
            JLabel c = new JLabel(inst);
            leftBox.add(c);
            labelList.add(c);
        }
    }

    public void setUpRightBox() {
        JButton startButton = new JButton("Start");
        startButton.addActionListener(new StartLisener());
        JButton stopButton = new JButton("Stop");
        stopButton.addActionListener(new StopLisener());
        JButton tempoUpButton = new JButton("Tempo Up");
        tempoUpButton.addActionListener(new TempoUpLisener());
        JButton tempoDownButton = new JButton("Tempo Down");
        tempoDownButton.addActionListener(new TempoDownLisener());
        JButton randomSelect = new JButton("Generate Random");
        randomSelect.addActionListener(new RandomLisener());
        JButton clearButton = new JButton("Clear Selected");
        clearButton.addActionListener(new clearLisener());
        rightBox.add(startButton);
        rightBox.add(stopButton);
        rightBox.add(tempoUpButton);
        rightBox.add(tempoDownButton);
        rightBox.add(randomSelect);
        rightBox.add(clearButton);
    }

    public class clearLisener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            for (JCheckBox c : checkboxList) {
                c.setSelected(false);
            }
            sequencer.stop();
        }
    }

    public class RandomLisener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            sequencer.stop();
            for (JCheckBox c : checkboxList) {
                if (Math.random() < 0.2) {
                    c.setSelected(true);
                } else {
                    c.setSelected(false);
                }
            }
            buildTrackAndStart();
        }
    }

    public void setupCentPanel() {
        GridLayout grid = new GridLayout(16, 16);
        grid.setVgap(1);
        grid.setHgap(2);
        centerPanel = new JPanel(grid);
        for (int i = 0; i < 256; i++) {
            JCheckBox c = new JCheckBox();
            c.setSelected(false);
            checkboxList.add(c);
            centerPanel.add(c);
        }
    }

    public class StartLisener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            buildTrackAndStart();
        }
    }


    public class StopLisener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            sequencer.stop();
        }
    }

    public class TempoUpLisener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float) (tempoFactor * 1.03));
        }
    }


    public class TempoDownLisener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float) (tempoFactor * 0.97));
        }
    }

    public void setupMidi() {
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequence = new Sequence(Sequence.PPQ, 4);
            track = sequence.createTrack();
            sequencer.setTempoInBPM(120);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void buildTrackAndStart() {
        int[] trackList = null;
        sequence.deleteTrack(track);
        track = sequence.createTrack();
        for (int i = 0; i < 16; i++) {
            trackList = new int[16];

            int key = instrument[i];

            for (int j = 0; j < 16; j++) {
                if (checkboxList.get(j + 16 * i).isSelected()) {
                    trackList[j] = key;
                } else {
                    trackList[j] = 0;
                }
            }
            makeTracks(trackList);
            track.add(makeEvent(176, 1, 127, 0, 16));
        }
        track.add(makeEvent(192, 9, 1, 0, 15));
        try {
            sequencer.setSequence(sequence);
            sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
            sequencer.start();
            sequencer.setTempoInBPM(120);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void makeTracks(int[] list) {
        for (int i = 0; i < 16; i++) {
            int key = list[i];
            if (key != 0) {
                track.add(makeEvent(144, 9, key, 100, i));
                track.add(makeEvent(128, 9, key, 100, i + 1));
            }
        }
    }

    public MidiEvent makeEvent(int comd, int chan, int a1, int a2, int tick) {
        MidiEvent event = null;
        try {
            ShortMessage a = new ShortMessage();
            a.setMessage(comd, chan, a1, a2);
            event = new MidiEvent(a, tick);
        } catch (Exception e) {
        }
        return event;
    }
}
