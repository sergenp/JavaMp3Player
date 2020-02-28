import javax.swing.*;

public class PlayerMainForm {
    private JPanel panel1;
    private JButton playButton;
    private JButton pauseButton;
    private JButton nextButton;
    private JList list1;

    public static void main(String[] args) {
        JFrame frame = new JFrame("PlayerMainForm");
        frame.setContentPane(new PlayerMainForm().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
