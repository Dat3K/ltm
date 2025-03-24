import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Add imports for lab classes in their new locations
import lab1.Lab1;
import lab2.Lab2;
import lab34.Lab34;
import lab5.Lab5;
import lab6.Lab6;
import lab7.Lab7;
import lab8.Lab8;
import lab9.Lab9;
import lab10.Lab10;

public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("52100781_NguyenThanhDat - Exercise Launcher");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Add a centered title label at the top
        JLabel titleLabel = new JLabel("52100781_NguyenThanhDat - Exercise Launcher", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 2, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton lab1Button = new JButton("Lab 1");
        JButton lab2Button = new JButton("Lab 2");
        JButton lab3Button = new JButton("Lab 3");
        JButton lab4Button = new JButton("Lab 4");
        JButton lab5Button = new JButton("Lab 5");
        JButton lab6Button = new JButton("Lab 6");
        JButton lab7Button = new JButton("Lab 7");
        JButton lab8Button = new JButton("Lab 8");
        JButton lab9Button = new JButton("Lab 9");
        JButton lab10Button = new JButton("Lab 10");

        // Add action listeners to buttons
        lab1Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open Lab 1
                new Lab1().setVisible(true);
            }
        });

        lab2Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open Lab 2
                new Lab2().setVisible(true);
            }
        });

        lab3Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open Lab 3
                new Lab34().setVisible(true);
            }
        });

        lab5Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open Lab 5
                new Lab5().setVisible(true);
            }
        });

        lab6Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open Lab 6
                new Lab6().setVisible(true);
            }
        });

        lab7Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open Lab 7
                new Lab7().setVisible(true);
            }
        });

        lab8Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open Lab 8
                new Lab8().setVisible(true);
            }
        });

        lab9Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open Lab 9
                new Lab9().setVisible(true);
            }
        });

        lab10Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open Lab 10
                new Lab10().setVisible(true);
            }
        });

        buttonPanel.add(lab1Button);
        buttonPanel.add(lab2Button);
        buttonPanel.add(lab3Button);
        buttonPanel.add(lab4Button);
        buttonPanel.add(lab5Button);
        buttonPanel.add(lab6Button);
        buttonPanel.add(lab7Button);
        buttonPanel.add(lab8Button);
        buttonPanel.add(lab9Button);
        buttonPanel.add(lab10Button);

        add(buttonPanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }
}
