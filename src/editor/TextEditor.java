package editor;

import javafx.util.Pair;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class TextEditor extends JFrame {

    final int WIDTH = 640;
    final int HEIGHT = 400;
    final int ICON_SIZE = 32;

    final String OPEN_ICON = "icons/open_256.png";
    final String SAVE_ICON = "icons/save_256.png";
    final String SEARCH_ICON = "icons/search_256.png";
    final String PREV_ICON = "icons/left-arrow_256.png";
    final String NEXT_ICON = "icons/right-arrow_256.png";

    protected boolean isCheckedRegEx;
    protected ArrayList<Pair<Integer, Integer>> matchIndexes;
    protected int curIndex;

    private JTextArea textArea;
    private JTextField searchTextField;
    private JCheckBox useRegExCheckbox;
    private JMenuItem useRegExMenuItem;
    private JFileChooser fileChooser;

    public TextEditor() {
        super("Text Editor");
        this.fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        fileChooser.setName("FileChooser");
        add(fileChooser);
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        add(actionPanel(), BorderLayout.NORTH);
        add(scrollTextArea(), BorderLayout.CENTER);
        menuBar();
        setVisible(true);
    }

    private JScrollPane scrollTextArea() {
        // TextArea
        textArea = new JTextArea();
        textArea.setName("TextArea");
        //textArea.setLineWrap(true);
        //textArea.setWrapStyleWord(true);

        // Scrollable TextArea
        JScrollPane scrollTextArea = new JScrollPane(textArea);
        scrollTextArea.setName("ScrollPane");
        scrollTextArea.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollTextArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        return scrollTextArea;
    }

    private JPanel actionPanel() {
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton openButton = new JButton(scaleIcon(OPEN_ICON, ICON_SIZE));
        openButton.setName("OpenButton");
        openButton.addActionListener(event -> openFile());

        JButton saveButton = new JButton(scaleIcon(SAVE_ICON, ICON_SIZE));
        saveButton.setName("SaveButton");
        saveButton.addActionListener(event -> saveFile());

        searchTextField = new JTextField();
        searchTextField.setName("SearchField");
        searchTextField.setPreferredSize(new Dimension(200, ICON_SIZE));

        JButton startSearchButton = new JButton(scaleIcon(SEARCH_ICON, ICON_SIZE));
        startSearchButton.setName("StartSearchButton");
        startSearchButton.addActionListener(startSearchAction);

        JButton prevMatchButton = new JButton(scaleIcon(PREV_ICON, ICON_SIZE));
        prevMatchButton.setName("PreviousMatchButton");
        prevMatchButton.addActionListener(prevMatchAction);

        JButton nextMatchButton = new JButton(scaleIcon(NEXT_ICON, ICON_SIZE));
        nextMatchButton.setName("NextMatchButton");
        nextMatchButton.addActionListener(nextMatchAction);

        useRegExCheckbox = new JCheckBox("Use RegEx");
        useRegExCheckbox.setName("UseRegExCheckbox");
        useRegExCheckbox.addActionListener(regExCheckboxAction);

        actionPanel.add(openButton);
        actionPanel.add(saveButton);
        actionPanel.add(searchTextField);
        actionPanel.add(startSearchButton);
        actionPanel.add(prevMatchButton);
        actionPanel.add(nextMatchButton);
        actionPanel.add(useRegExCheckbox);

        return actionPanel;
    }

    private void menuBar() {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        menuBar.add(createFileMenu());
        menuBar.add(createSearchMenu());
    }

    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("File");
        fileMenu.setName("MenuFile");

        // Open
        JMenuItem openMenuItem = new JMenuItem("Open");
        openMenuItem.setName("MenuOpen");
        openMenuItem.addActionListener(event -> openFile());

        // Save
        JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.setName("MenuSave");
        saveMenuItem.addActionListener(event -> saveFile());

        // Exit
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setName("MenuExit");
        exitMenuItem.addActionListener(event -> dispose()); // System.exit(0));

        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);

        return fileMenu;
    }

    private JMenu createSearchMenu() {
        JMenu searchMenu = new JMenu("Search");
        searchMenu.setName("MenuSearch");

        // Start Search
        JMenuItem startSearchMenuItem = new JMenuItem("Start Search");
        startSearchMenuItem.setName("MenuStartSearch");
        startSearchMenuItem.addActionListener(startSearchAction);

        // Previous Match
        JMenuItem previousMatchMenuItem = new JMenuItem("Find Previous");
        previousMatchMenuItem.setName("MenuPreviousMatch");
        previousMatchMenuItem.addActionListener(prevMatchAction);

        // Next Match
        JMenuItem nextMatchMenuItem = new JMenuItem("Find Next");
        nextMatchMenuItem.setName("MenuNextMatch");
        nextMatchMenuItem.addActionListener(nextMatchAction);

        // Use RegEx
        useRegExMenuItem = new JMenuItem("Use Regular Expressions");
        useRegExMenuItem.setName("MenuUseRegExp");
        useRegExMenuItem.addActionListener(regExCheckboxAction);

        searchMenu.add(startSearchMenuItem);
        searchMenu.add(previousMatchMenuItem);
        searchMenu.add(nextMatchMenuItem);
        searchMenu.addSeparator();
        searchMenu.add(useRegExMenuItem);

        return searchMenu;
    }

    // Search Actions for Menu and Buttons
    ActionListener startSearchAction = actionEvent -> startSearch();
    ActionListener prevMatchAction = actionEvent -> {curIndex--; showMatchText();};
    ActionListener nextMatchAction = actionEvent -> {curIndex++; showMatchText();};
    ActionListener regExCheckboxAction = actionEvent -> {
        isCheckedRegEx = !isCheckedRegEx;
        useRegExCheckbox.setSelected(isCheckedRegEx);
        useRegExMenuItem.setSelected(isCheckedRegEx);
    };

    private void startSearch() {
        this.matchIndexes = new ArrayList<>();
        this.curIndex = 0;
        String searchText = searchTextField.getText();
        new SearchWorker(this.textArea, this.matchIndexes, searchText, this.isCheckedRegEx).execute();
    }

    private void showMatchText() {
        if (matchIndexes.size() < 1) return;

        // Wrap beginning and end
        if (curIndex < 0) curIndex = matchIndexes.size() - 1;
        else if (curIndex >= matchIndexes.size()) curIndex = 0;

        // Highlight matching text
        int start = matchIndexes.get(curIndex).getKey();
        int end = matchIndexes.get(curIndex).getValue();
        textArea.setCaretPosition(end);
        textArea.select(start, end);
        textArea.grabFocus();
    }

    private ImageIcon scaleIcon(String path, int size) {
        ImageIcon icon = new ImageIcon(path);
        return new ImageIcon(icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH));
    }

    private void openFile() {
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                textArea.setText(new String(Files.readAllBytes(selectedFile.toPath())));
            } catch (IOException e) {
                textArea.setText("");
                e.printStackTrace();
            }
        }
    }

    private void saveFile() {
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try (FileWriter writer = new FileWriter(selectedFile)) {
                writer.write(textArea.getText());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}