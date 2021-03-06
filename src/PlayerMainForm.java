import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PlayerMainForm {
    private JFrame frame;
    private JPanel mainPanel;
    private JButton playButton;
    private JButton pauseButton;
    private JButton nextButton;
    private JList<String> list1;
    private JButton loadMusicButton;
    private JCheckBox shuffleCheckBox;
    private JSlider volumeSlider;
    private JSlider songDurationSlider;
    private JLabel secsLabel;
    private JLabel minsLabel;
    private HashMap<String, String> musicList = new HashMap<>();
    private BasicPlayer player;
    private double currentSongDuration;
    private int interruptedAtDuration;

    private Thread soundDurationCheck;


    public PlayerMainForm() {
        player = new BasicPlayer();
        // add an action listener listening if you clicked the button, and once u clicked the loadMusicButton,
        // call chooseMusicFiles function
        loadMusicButton.addActionListener(e -> chooseMusicFiles());
        playButton.addActionListener(e -> {
            try {
                playSound();
                // just in case we paused a song and decided to play another
                pauseButton.setText("Pause");
            } catch (BasicPlayerException ex) {
                ex.printStackTrace();
            }
        });
        pauseButton.addActionListener(e -> {
            try {
                // status 0 is playing status, smfh use enums for god's sake
                // so if playing, stop it, make the button string resume
                if (player.getStatus() == 0) {
                    pauseButton.setText("Resume");
                    player.pause();
                    soundDurationCheck.interrupt();
                }
                // you guessed right, paused status is 1!
                else if (player.getStatus() == 1) {
                    pauseButton.setText("Pause");
                    player.resume();
                    soundDurationCheck = soundDurationSliderThread(interruptedAtDuration);
                    soundDurationCheck.start();
                }
            } catch (BasicPlayerException ex) {
                ex.printStackTrace();
            }
        });
        nextButton.addActionListener(e -> playNextSong());
        volumeSlider.addChangeListener(e -> {
            try {
                player.setGain((float) volumeSlider.getValue() / 100);
            } catch (BasicPlayerException ex) {
                ex.printStackTrace();
            }
        });
        list1.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() >= 2 && evt.getButton() == MouseEvent.BUTTON1) {
                    try {
                        playSound();
                        pauseButton.setText("Pause");
                    } catch (BasicPlayerException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        // TODO
        // add a font select menu to chance font
        list1.setFont(new Font("Arial", Font.PLAIN, 16));
        // give our JFrame the title of Mp3 Player
        frame = new JFrame("Mp3 Player");
        // set the main content panel to mainPanel
        frame.setContentPane(mainPanel);
        // when you close the JFrame, exit the application
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        // center the frame
        frame.setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        PlayerMainForm playerMainForm = new PlayerMainForm();
        try {
            // try to set the look of the UI to GTK+ which seems modern
            UIManager.setLookAndFeel(UIManager.getLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        // remove the thick value from the slider
        UIManager.put("Slider.paintValue", false);
        playerMainForm.frame.setVisible(true);
    }

    public void chooseMusicFiles() {

        // create a select file dialog
        FileDialog dialog = new FileDialog(new Frame(), "Select Music File(s) to add", FileDialog.LOAD);
        dialog.setMultipleMode(true);
        // make the selected file can only be in mp3 or wav format
        //dialog.setFile("*.mp3;*.wav");
        dialog.setFilenameFilter((dir, name) -> name.contains(".mp3") || name.contains(".wav"));
        // show up the dialog
        dialog.setVisible(true);
        // create a listModel for binding the JList(list1) to
        DefaultListModel<String> listModel = new DefaultListModel<>();
        // add every file path to the listModel
        for (var file : dialog.getFiles()) {
            musicList.put(file.getName(), file.getAbsolutePath());
        }

        for (var musicName : musicList.keySet()) {
            listModel.addElement(musicName);
        }
        // set the model to the model we have created
        list1.setModel(listModel);
        list1.setSelectedIndex(0);
        frame.pack();
    }

    public void setDuration(double duration) {
        int secs = 0;
        int mins = 0;
        if (duration != 0) {
            secs = (int) duration % 60;
            mins = (int) duration / 60;
        }
        secsLabel.setText(String.valueOf(String.format("%02d", secs)));
        minsLabel.setText(String.valueOf(mins));
    }

    public void playSound() throws BasicPlayerException {
        File f = new File(musicList.get(list1.getSelectedValue()));
        try {
            currentSongDuration = getDurationOfSound(f);
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
        setDuration(currentSongDuration);
        if (soundDurationCheck != null) {
            soundDurationCheck.interrupt();
        }
        soundDurationCheck = soundDurationSliderThread(0);
        soundDurationCheck.start();
        songDurationSlider.setMaximum((int) currentSongDuration);
        player.open(f);
        player.play();
        player.setGain((float) volumeSlider.getValue() / 100);
        createCheckerThread().start();
    }

    public void playNextSong() {
        int nextSongIndex = 0;
        // if the checkbox enabled get a random song id from the list
        if (shuffleCheckBox.isSelected()) {
            nextSongIndex = new Random().nextInt(list1.getModel().getSize());
        } else {
            System.out.println(list1.getSelectedIndex());
            if (list1.getSelectedIndex() + 1 < list1.getModel().getSize()) {
                nextSongIndex = list1.getSelectedIndex() + 1;
            }
        }
        list1.setSelectedIndex(nextSongIndex);
        try {
            pauseButton.setText("Pause");
            playSound();
        } catch (BasicPlayerException ex) {
            ex.printStackTrace();
        }
    }

    // Thread for checking if the song is stopped playing
    public Thread createCheckerThread() {
        return new Thread(() -> {
            try {
                while (true) {
                    // check if the player is stopped playing
                    if (player.getStatus() == 2) {
                        // if it did play the next song
                        this.playNextSong();
                        break;
                    }
                    Thread.sleep(2000);
                }
            } catch (InterruptedException ignored) {

            }
        });
    }

    public Thread soundDurationSliderThread(int currentTime) {
        return new Thread(() -> {
            int atTime = currentTime;
            while (currentSongDuration > 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    interruptedAtDuration = atTime;
                    break;
                }
                atTime += 1;
                currentSongDuration -= 1;
                songDurationSlider.setValue(atTime);
                setDuration(currentSongDuration);
            }
        });
    }

    private double getDurationOfSound(File file) throws UnsupportedAudioFileException, IOException {
        AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(file);
        System.out.println(fileFormat.getFormat().toString());
        // if the given file is a mp3 file
        if (fileFormat.getFormat().toString().startsWith("MPEG1L3")) {
            Map<?, ?> properties = fileFormat.properties();
            String key = "duration";
            Long microseconds = (Long) properties.get(key);
            // convert microseconds to seconds
            return ((int) (microseconds / 1000000));
        }
        // if the given file is a .wav file
        else if (fileFormat.getFormat().toString().startsWith("PCM")) {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
            AudioFormat format = audioInputStream.getFormat();
            long frames = audioInputStream.getFrameLength();
            return (frames + 0.0) / format.getFrameRate();
        } else {
            throw new UnsupportedAudioFileException();
        }
    }
}
