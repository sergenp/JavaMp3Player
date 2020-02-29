import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.HashMap;

public class PlayerMainForm {
    private JFrame frame;
    private JPanel mainPanel;
    private JButton playButton;
    private JButton pauseButton;
    private JButton nextButton;
    private JList<String> list1;
    private JButton loadMusicButton;
    private HashMap<String, String> musicList = new HashMap<>();
    private BasicPlayer player;

    // TODO
    // add a thread that checks if the player is stopped playing and make it play the next song
    // add a shuffle checkbox, if it gets checked, next song will be a random song other than the next following song
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
                }
                // you guessed right, paused status is 1!
                else if (player.getStatus() == 1) {
                    pauseButton.setText("Pause");
                    player.resume();
                }
            } catch (BasicPlayerException ex) {
                ex.printStackTrace();
            }
        });
        nextButton.addActionListener(e ->
        {
            if (list1.getSelectedIndex() < list1.getModel().getSize()) {
                list1.setSelectedIndex(list1.getSelectedIndex() + 1);
            } else {
                list1.setSelectedIndex(0);
            }
            try {
                pauseButton.setText("Pause");
                playSound();
            } catch (BasicPlayerException ex) {
                ex.printStackTrace();
            }
        });

        list1.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() >= 2 && evt.getButton() == MouseEvent.BUTTON1) {
                    try {
                        playSound();
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
        try {
            // try to set the look of the UI to GTK+ which seems modern
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        PlayerMainForm playerMainForm = new PlayerMainForm();
        playerMainForm.frame.setVisible(true);
    }

    //TODO
    //make it so that selecting music files adds to the current musicList rather than recreating it
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
            listModel.addElement(file.getName());
        }
        // set the model to the model we have created
        list1.setModel(listModel);
        try {
            list1.setSelectedIndex(0);
            playSound();
        } catch (BasicPlayerException e) {
            e.printStackTrace();
        }
        frame.pack();
    }

    public void playSound() throws BasicPlayerException {
        File f = new File(musicList.get(list1.getSelectedValue()));
        player.open(f);
        player.play();
        // TODO
        // create a slider for the adjusting the music's volume
        player.setGain(0.2);
    }

}
