import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.plaf.metal.*;
import javax.swing.event.*;
import java.awt.datatransfer.*;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

class editor extends JFrame implements ActionListener, WindowListener, DocumentListener {

    JTextArea t;
    JFrame f;
    boolean saved = true; // Track if changes are saved for close function

    editor() {
        f = new JFrame("Armaan's Text Editor");

        // Attempt to make set look and feel using UIManager & handle exceptions
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            MetalLookAndFeel.setCurrentTheme(new OceanTheme());
        } catch (Exception e) {
            // Write to console if there's an error
            System.console().writer().println("ERROR in UIManager.setLookAndFeel:" + e);
        }

        // Define my JTextArea
        t = new JTextArea();

        // Add document listener to monitor changes in the text area for close function
        t.getDocument().addDocumentListener(this);

        // Define MenuBar with my options
        JMenuBar mb = new JMenuBar();
        JMenu m1 = new JMenu("File");
        JMenuItem mi1 = new JMenuItem("New");
        JMenuItem mi2 = new JMenuItem("Open");
        JMenuItem mi3 = new JMenuItem("Save");
        JMenuItem mi4 = new JMenuItem("Save As");
        JMenuItem mi5 = new JMenuItem("Print");
        JMenuItem mi6 = new JMenuItem("Preferences");

        // Add action listener for preferences so I know when thing's have changed/ user has interacted
        mi1.addActionListener(this);
        mi2.addActionListener(this);
        mi3.addActionListener(this);
        mi4.addActionListener(this);
        mi5.addActionListener(this);
        mi6.addActionListener(this); 

        // Add my menu items to my JMenu
        m1.add(mi1);
        m1.add(mi2);
        m1.add(mi3);
        m1.add(mi4);
        m1.add(mi5);
        m1.add(mi6);

        // 2nd sub menu for editing 
        JMenu m2 = new JMenu("Edit");
        JMenuItem mi7 = new JMenuItem("Cut");
        JMenuItem mi8 = new JMenuItem("Copy");
        JMenuItem mi9 = new JMenuItem("Paste");

        // Add my ActionListeners
        mi7.addActionListener(this);
        mi8.addActionListener(this);
        mi9.addActionListener(this);

        // Add my menu items to my 2nd JMenu for editing
        m2.add(mi7);
        m2.add(mi8);
        m2.add(mi9);

        // Final Top level Menu Option 
        JMenuItem mc = new JMenuItem("Close");
        mc.addActionListener(this);

        // Add the menus which have the submenus to my menu toolbar at the top
        mb.add(m1);
        mb.add(m2);
        mb.add(mc);

        // Initialise my text editor with some defined variables
        f.setJMenuBar(mb);
        f.add(t);
        f.setSize(500, 500);
        f.addWindowListener(this); // Add window listener
        f.setVisible(true);
    }

    // Handle the actionPerformed
    public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();

        if (s.equals("Cut")) {
            t.cut();
        } else if (s.equals("Copy")) {
            t.copy();
        } else if (s.equals("Paste")) {
            pasteFromClipboard();
        } else if (s.equals("Save")) {
            saveFile();
        } else if (s.equals("Save As")) {
            saveFile();
        } else if (s.equals("Print")) {
            printFile();
        } else if (s.equals("Open")) {
            openFile();
        } else if (s.equals("New")) {
            t.setText("");
        } else if (s.equals("Close")) {
            closeWindow();
        } else if (s.equals("Preferences")) { // Handle preferences menu item
            showPreferencesDialog();
        }
    }

    public void pasteFromClipboard() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents(this);
        if (contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                String pastedText = (String) contents.getTransferData(DataFlavor.stringFlavor);
                int caretPosition = t.getCaretPosition();
                t.insert(pastedText, caretPosition);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(f, "Error pasting from clipboard.");
            }
        }
    }

    // Save File
    public void saveFile() {
        // use JFileChooser
        JFileChooser j = new JFileChooser(".");
        int r = j.showSaveDialog(null);
        if (r == JFileChooser.APPROVE_OPTION) {
            File fi = new File(j.getSelectedFile().getAbsolutePath());
            try {
                FileWriter wr = new FileWriter(fi, false);
                BufferedWriter w = new BufferedWriter(wr);
                w.write(t.getText());
                w.flush();
                w.close();
                saved = true; // Mark changes as saved
            } catch (Exception evt) {
                JOptionPane.showMessageDialog(f, evt.getMessage());
            }
        }
    }

    // print Using func print()
    public void printFile() {
        try {
            t.print();
        } catch (Exception evt) {
            JOptionPane.showMessageDialog(f, evt.getMessage());
        }
    }

    // Use the JFileChooser to open files
    public void openFile() {
        JFileChooser j = new JFileChooser(".");
        int r = j.showOpenDialog(null);
        if (r == JFileChooser.APPROVE_OPTION) {
            File fi = new File(j.getSelectedFile().getAbsolutePath());

            // use try catch to ensure br is closed out as there was an error without it saying not closed
            try (FileReader fr = new FileReader(fi);
                BufferedReader br = new BufferedReader(fr)) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                t.setText(sb.toString());
            } catch (IOException evt) {
                JOptionPane.showMessageDialog(f, evt.getMessage());
            }
        }
    }

    public void closeWindow() {
        if (!saved) { // Check if changes are not saved I need to check with the user
            // use the JOptionPane.YES_NO_CANCEL_OPTION built in to decide the options
            int option = JOptionPane.showConfirmDialog(f, "Do you want to save the changes before closing?", "Save Changes", JOptionPane.YES_NO_CANCEL_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                saveFile(); 
            } else if (option == JOptionPane.CANCEL_OPTION) {
                return; // Cancel closing the window
            }
        }
        f.dispose(); // Close the window
    }

    // DocumentListener methods to make sure I know if the user has changed anything since last saved so we don't lose any of their work
    public void insertUpdate(DocumentEvent e) {
        saved = false; // Set changes to unsaved when content is inserted
    }
    public void removeUpdate(DocumentEvent e) {
        saved = false; // Set changes to unsaved when content is removed
    }
    public void changedUpdate(DocumentEvent e) {
        saved = false; // Set changes to unsaved when content is changed
    }

    // Define my Window listener methods
    public void windowOpened(WindowEvent e) {}
    public void windowClosing(WindowEvent e) {
        closeWindow(); // Close window event
    }
    public void windowClosed(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}

    public static void main(String args[]) {
        editor e = new editor();
    }

    // Method to show preferences dialog
       private void showPreferencesDialog() {
        // Create a new preferences dialog
        JDialog preferencesDialog = new JDialog(f, "Preferences", true);
        preferencesDialog.setSize(300, 200);
        preferencesDialog.setLayout(new GridLayout(2, 2));

        // Add components to the preferences dialog
        JLabel textSizeLabel = new JLabel("Text Size:");
        JSpinner textSizeSpinner = new JSpinner(new SpinnerNumberModel(12, 8, 72, 1));
        JLabel textColorLabel = new JLabel("Text Color:");
        JButton chooseColorButton = new JButton("Choose Color");

        // Add action listener to the color chooser button
        chooseColorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color textColor = JColorChooser.showDialog(preferencesDialog, "Choose Text Color", t.getForeground());
                if (textColor != null) {
                    t.setForeground(textColor);
                }
            }
        });

        // Add action listener to the spinner to change text size
        textSizeSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int textSize = (int) textSizeSpinner.getValue();
                Font currentFont = t.getFont();
                Font newFont = currentFont.deriveFont((float) textSize);
                t.setFont(newFont);
            }
        });

        // Add components to the preferences dialog
        preferencesDialog.add(textSizeLabel);
        preferencesDialog.add(textSizeSpinner);
        preferencesDialog.add(textColorLabel);
        preferencesDialog.add(chooseColorButton);

        // Display the preferences dialog
        preferencesDialog.setVisible(true);
    }

}
