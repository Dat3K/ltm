package lab6;

import javax.swing.*;
import java.awt.*;

/**
 * Base panel class that all other panels will extend
 */
public class BasePanel extends JPanel {
    protected JTextArea resultArea;
    
    public BasePanel() {
        setLayout(new BorderLayout());
    }
    
    /**
     * Shows a message dialog with the given message
     */
    protected void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
    
    /**
     * Creates a standard text area for results
     */
    protected JTextArea createResultArea() {
        resultArea = new JTextArea(20, 50);
        resultArea.setEditable(false);
        return resultArea;
    }
}
