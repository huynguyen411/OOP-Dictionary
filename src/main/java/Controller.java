import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import org.json.JSONArray;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class Controller {
    @FXML
    private StackPane stackPane;
    @FXML
    private BorderPane borderPane;
    @FXML
    private TextField searchField;

    @FXML
    private TextField addWordField, addWordMeaningField, deleteField, translateField, meaningField;

    @FXML
    private TextArea consoleOutput;
    @FXML
    private ListView<String> listView;

    @FXML
    private Button searchButton, exitAddSceneButton, addButton;

    @FXML
    private AnchorPane subSceneAdder, subSceneDelete, subSceneTranslate;

    private ArrayList<String> wordsList = new ArrayList<>();
    private ArrayList<String> favouriteList = new ArrayList<>();
    ObservableList<String> list;

    public void initialize() {
        try {
            getDataFromDatabase();
            normalizeList();
            getRecommendWords();
            subSceneAdder.setVisible(false);
            subSceneDelete.setVisible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void normalizeList() {
        Set<String> set = new HashSet<>(wordsList);
        wordsList.clear();
        wordsList.addAll(set);
        wordsList.replaceAll(String::toLowerCase);
        Collections.sort(wordsList);
    }
    @FXML
    public void searchWord (ActionEvent event) {
        buttonClicked();
    }

    private void getDataFromDatabase() {
        String url = "jdbc:mysql://localhost:3306/entries";
        String user = "root";
        String password = "thukhoatt2k1";
        try {
            Connection myConnect = DriverManager.getConnection(url, user, password);
            Statement myStatement = myConnect.createStatement();
            String sql ="SELECT * FROM entries.entries";
            ResultSet resultSet = myStatement.executeQuery(sql);

            while (resultSet.next()) {
                wordsList.add(resultSet.getString("word"));
            }


            myConnect.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void buttonClicked() {
        String url = "jdbc:mysql://localhost:3306/entries";
        String user = "root";
        String password = "thukhoatt2k1";
        String wordToFind = String.valueOf(listView.getSelectionModel().getSelectedItems());
        StringBuilder output = new StringBuilder();
        try {
            Connection myConnect = DriverManager.getConnection(url, user, password);
            Statement myStatement = myConnect.createStatement();
            String sql ="SELECT * FROM entries.entries WHERE word LIKE '"
                    + wordToFind.substring(1, wordToFind.length() - 1) + "'";
            ResultSet resultSet = myStatement.executeQuery(sql);
            while (resultSet.next()) {
                output.append(resultSet.getString("definition"));
                output.append("\n");
            }
            consoleOutput.setText(String.valueOf(output));
            myConnect.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void getRecommendWords() {
        ArrayList<String> wordList = new ArrayList<>();
        searchField.textProperty().addListener((observable, oldText, newText) -> {
            wordList.clear();
            for (String word: wordsList) {
                if (word.toLowerCase().indexOf(newText) == 0){
                    wordList.add(word);
                }
            }
            list = FXCollections.observableList(wordList);
            listView.setItems(list);
        });
    }

    @FXML
    public void speech(ActionEvent event) {
        String word = searchField.getText();
        try {
            URL url = new URL("https://translate.google.com/translate_tts?ie=UTF-8&tl=en&client=tw-ob&q=" + word);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            InputStream audio = new BufferedInputStream(conn.getInputStream());
            new Player(audio).play();
        } catch (IOException | JavaLayerException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void addWords(ActionEvent event) {
        subSceneAdder.setVisible(true);
        borderPane.setVisible(false);
        subSceneDelete.setVisible(false);
    }

    @FXML
    //add your word and then click "exit" button to return the homeScene
    public void addNewWord(ActionEvent event) {
        String word, meaning;
        word = addWordField.getText();
        System.out.println(word);
        meaning = addWordMeaningField.getText();
        wordsList.add(word);
        System.out.println(wordsList.size());
        subSceneAdder.setVisible(false);
        subSceneDelete.setVisible(false);
        borderPane.setVisible(true);
    }

    @FXML
    public void changeToDeleteScene(ActionEvent event) {
        borderPane.setVisible(false);
        subSceneAdder.setVisible(false);
        subSceneDelete.setVisible(true);
    }

    @FXML
    //delete word and return to mainScene
    public void deleteWord(ActionEvent event) {
        String wordToDelete = deleteField.getText();
        wordsList.removeIf(word -> word.equalsIgnoreCase(wordToDelete));

        borderPane.setVisible(true);
        subSceneAdder.setVisible(false);
        subSceneDelete.setVisible(false);
    }

    @FXML
    public void addToFavourite(ActionEvent event) {
        String favoriteWord = String.valueOf(listView.getSelectionModel().getSelectedItems());
        favouriteList.add(favoriteWord.substring(1, favoriteWord.length() - 1));
    }
    @FXML
    public void viewFavourite(ActionEvent event) {
        list = FXCollections.observableList(favouriteList);
        listView.setItems(list);
    }

    @FXML
    public void openTranslateSentenceScene(ActionEvent event) {
        subSceneTranslate.setVisible(true);
        borderPane.setVisible(false);
    }

    @FXML
    public void exitTranslateSentenceScene(ActionEvent event) {
        borderPane.setVisible(true);
        subSceneTranslate.setVisible(false);
    }

    private String callUrlAndParseResult(String langFrom, String langTo, String word) throws Exception {
        String url = "https://translate.googleapis.com/translate_a/single?"+
                "client=gtx&"+
                "sl=" + langFrom +
                "&tl=" + langTo +
                "&dt=t&q=" + URLEncoder.encode(word, "UTF-8");

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestProperty("User-Agent", "Mozilla/5.0");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // return response.toString();
        return parseResult(response.toString());
    }

    private String parseResult(String inputJson) throws Exception {
        JSONArray jsonArray = new JSONArray(inputJson);
        JSONArray jsonArray2 = (JSONArray) jsonArray.get(0);
        JSONArray jsonArray3 = (JSONArray) jsonArray2.get(0);

        return jsonArray3.get(0).toString();
    }

    @FXML
    public void translateSentence(ActionEvent event) {
        String sentence = translateField.getText();
        try {
            meaningField.setText(callUrlAndParseResult("en", "vi", sentence));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
