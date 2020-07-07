package editor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TextEditor extends JFrame {

    public TextEditor() {

        // JFrame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 300);
        setTitle("Text Editor");

        // TextArea
        JTextArea textArea = new JTextArea(100,200);
        textArea.setName("TextArea");
//        textArea.setLineWrap(true);
//        textArea.setWrapStyleWord(true);

        // JScrollPane
        JScrollPane scrollTextArea = new JScrollPane(textArea);
        scrollTextArea.setName("ScrollPane");
        add(scrollTextArea, BorderLayout.CENTER);

        // FilenameField
        JTextField filenameField = new JTextField(10);
        filenameField.setName("FilenameField");

        // SaveButton
        JButton saveButton = new JButton("Save");
        saveButton.setName("SaveButton");
        saveButton.addActionListener(event -> saveFile(textArea, filenameField.getText()));

        // LoadButton
        JButton loadButton = new JButton("Load");
        loadButton.setName("LoadButton");
        loadButton.addActionListener(event -> loadFile(textArea, filenameField.getText()));

        // Add filename field and buttons to top panel
        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.GREEN);
        topPanel.add(filenameField, FlowLayout.LEFT);
        topPanel.add(saveButton);
        topPanel.add(loadButton, FlowLayout.RIGHT);
        add(topPanel, BorderLayout.NORTH);

        // Menu
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        JMenu fileMenu = new JMenu("File");
        fileMenu.setName("MenuFile");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(fileMenu);

        /*
        JMenu newMenuItem = new JMenu("New");
        JMenuItem textFileMenuItem = new JMenuItem("Text File");
        JMenuItem imgFileMenuItem = new JMenuItem("Image File");
        JMenuItem folderMenuItem = new JMenuItem("Folder");
        newMenuItem.add(textFileMenuItem);
        newMenuItem.add(imgFileMenuItem);
        newMenuItem.add(folderMenuItem);

         */

        JMenuItem loadMenuItem = new JMenuItem("Load");
        loadMenuItem.setName("MenuLoad");
        loadMenuItem.addActionListener(event -> loadFile(textArea, filenameField.getText()));

        JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.setName("MenuSave");
        saveMenuItem.addActionListener(event -> saveFile(textArea, filenameField.getText()));

        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setName("MenuExit");
        exitMenuItem.addActionListener(event -> System.exit(0));

        fileMenu.add(loadMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);

        setVisible(true);
    }

    public static void loadFile(JTextArea textArea, String filename) {
        try {
            textArea.setText(Files.readString(Paths.get(filename)));
//                String text = new String(Files.readAllBytes(Paths.get(filenameField.getText())));
//                textArea.setText(text);
        } catch (IOException e) {
            e.printStackTrace();
            textArea.setText("");
        }
    }

    public static void saveFile(JTextArea textArea, String filename) {
        try {
            FileWriter writer = new FileWriter(filename);
            writer.write(textArea.getText());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}