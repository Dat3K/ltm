package lab5;
import javax.swing.*;
import java.awt.*;

public class Lab5 extends JFrame {
    public Lab5() {
        setTitle("Lab 5");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create and set up the menu bar
        JMenuBar menuBar = new JMenuBar();

        // 1. URLConnection menu
        JMenu urlConnectionMenu = new JMenu("URLConnection");
        urlConnectionMenu.add(new JMenuItem("Opening URLConnections"));
        urlConnectionMenu.add(new JMenuItem("Reading data from a server"));
        urlConnectionMenu.add(new JMenuItem("GetOutputStream"));
        menuBar.add(urlConnectionMenu);

        // 2. Correct Character Set menu
        JMenu characterSetMenu = new JMenu("Correct Character Set");
        characterSetMenu.add(new JMenuItem("EndcodingAwareSourceViewer"));
        characterSetMenu.add(new JMenuItem("HeaderViewer"));
        characterSetMenu.add(new JMenuItem("Get Header Field With String Name"));
        characterSetMenu.add(new JMenuItem("Get All Headers"));
        menuBar.add(characterSetMenu);

        // 3. Configuring The Connection menu
        JMenu configConnectionMenu = new JMenu("Configuring The Connection");
        menuBar.add(configConnectionMenu);

        setJMenuBar(menuBar);

        // Create a label with the student information
        JLabel nameLabel = new JLabel("52100781-Nguyễn Thành Đạt");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Add the label to the center of the frame
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(nameLabel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Lab5 app = new Lab5();
            app.setVisible(true);
        });
    }
}
