import javax.swing.*;
import java.awt.*;

public class PlayerMainForm {
    private JFrame frame;
    private JPanel mainPanel;
    private JButton playButton;
    private JButton pauseButton;
    private JButton nextButton;
    private JList<String> list1;
    private JButton loadMusicButton;

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
            listModel.addElement(file.getAbsolutePath());
        }
        // TODO @aren
        // make it so we don't add every element to the listmodel, rather we add the name of the file,
        // and when we click on the name of it, we get the absolute path to the file
        // hint: make a Hashmap<String, String>, first parameter will be the name of the music, second parameter will be the path to it
        // and our model will only show the "keys" of the hashmap, no need to implement the click functionality yet

        // set the model to the model we have created
        list1.setModel(listModel);
    }

}
