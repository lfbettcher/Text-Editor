package editor;

import javafx.util.Pair;

import javax.swing.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchWorker extends SwingWorker<String, Object> {
    JTextArea textArea;
    String text;
    ArrayList<Pair<Integer, Integer>> matchIndexes;
    String searchText;
    boolean isCheckedRegEx;

    public SearchWorker(JTextArea textArea, ArrayList<Pair<Integer, Integer>> matchIndexes,
                        String searchText, boolean isCheckedRegEx) {
        this.textArea = textArea;
        this.text = textArea.getText();
        this.matchIndexes = matchIndexes;
        this.searchText = searchText;
        this.isCheckedRegEx = isCheckedRegEx;
    }

    @Override
    protected String doInBackground() throws Exception {
        if (this.isCheckedRegEx) searchRegEx();
        else searchText();
        return null;
    }

    @Override
    protected void done() {
        try {
            firstMatch();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void searchRegEx() {
        this.matchIndexes = new ArrayList<>();
        Pattern pattern = Pattern.compile(this.searchText);
        Matcher matcher = pattern.matcher(this.text);
        while (matcher.find()) {
            this.matchIndexes.add(new Pair<>(matcher.start(), matcher.end()));
        }
    }

    private void searchText() {
        if (this.text.contains(searchText)) {
            int index = this.text.indexOf(searchText);
            while (index >= 0) {
                this.matchIndexes.add(new Pair<>(index, index + searchText.length()));
                index = this.text.indexOf(searchText, ++index);
            }
        }
    }

    private void firstMatch() {
        if (matchIndexes.size() < 1) return;
        int start = matchIndexes.get(0).getKey();
        int end = matchIndexes.get(0).getValue();
        textArea.setCaretPosition(end);
        textArea.select(start, end);
        textArea.grabFocus();
    }
}