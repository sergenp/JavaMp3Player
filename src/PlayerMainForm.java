import javax.swing.*;
import java.awt.*;
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

    public PlayerMainForm() {
        // add an action listener listening if you clicked the button, and once u clicked the loadMusicButton,
        // call chooseMusicFiles function
        loadMusicButton.addActionListener(e -> chooseMusicFiles());
        // give our JFrame the title of Mp3 Player
        frame = new JFrame("Mp3 Player");
        // set the main content panel to mainPanel
        frame.setContentPane(mainPanel);
        // when you close the JFrame, exit the application
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // gives our frame a default height and width, if you remove this you will get a 1x1 jframe rather than a stretched jframe
        frame.pack();
    }

    public static void main(String[] args) {
        PlayerMainForm playerMainForm = new PlayerMainForm();
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
            listModel.addElement(file.getName());
        }
        // set the model to the model we have created
        list1.setModel(listModel);
    }

}
