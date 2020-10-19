import java.io.*;
import java.util.Scanner;

public class DictionaryManagement extends DictionaryCommandline{
    public Dictionary dictionary;
    int numberOfWord;
    public DictionaryManagement() {
        dictionary = new Dictionary(numberOfWord);
    }
    public void insertFromCommandline() {
        Scanner sc = new Scanner(System.in);

        String wordTarget = "";
        String wordExplain = "";
        String _numberOfWord;

        _numberOfWord = sc.nextLine();
        numberOfWord = Integer.parseInt(_numberOfWord);

        dictionary = new Dictionary(numberOfWord);
        dictionary.setNumberOfWord(numberOfWord);

        for (int i = 0; i < numberOfWord; i++) {
            Word word = new Word();
            wordTarget = sc.nextLine();
            wordExplain = sc.nextLine();
            word.setWord_target(wordTarget);
            word.setWord_explain(wordExplain);
            dictionary.dictionaryList[i] = word;
        }
    }

    public void insertFromFile()  {
        File f = new File("dictionaries.txt");
        Scanner sc;
        String _numberOfWord;
        int countLine = 0;

        try {
            sc = new Scanner(f);
            String[] line;
            _numberOfWord = sc.nextLine();
            numberOfWord = Integer.parseInt(_numberOfWord);

            dictionary = new Dictionary(numberOfWord);
            dictionary.setNumberOfWord(numberOfWord);
            while (sc.hasNextLine()) {
                Word word = new Word();
                line = sc.nextLine().split("\\s+");
                word.setWord_target(line[0]);
                word.setWord_explain(line[1]);
                dictionary.dictionaryList[countLine] = word;
                countLine++;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void dictionaryExportToFile() {
        File f = new File("Dictionary.txt");
        try {
            FileWriter fw = new FileWriter(f);
            fw.write(numberOfWord + "\n");
            for (int i = 0; i < numberOfWord; i++) {
                fw.write(dictionary.dictionaryList[i].getWord_target() + "\t"
                    + dictionary.dictionaryList[i].getWord_explain() + "\n");
            }
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dictionarySearcher() {
        Scanner sc = new Scanner(System.in);
        String wordToFind = sc.nextLine();
        for (int i = 0; i < dictionary.getNumberOfWord(); i++) {
            if (wordToFind.equals(dictionary.dictionaryList[i].getWord_target().
                    substring(0, wordToFind.length()))){
                System.out.println(dictionary.dictionaryList[i].getWord_target());
            }
        }
    }

    @Override
    public void showAllWords() {
        System.out.println("No    | English     | Vietnamese");
        for (int i = 0; i < dictionary.getNumberOfWord(); i++) {
            System.out.println(i + "      | " + dictionary.dictionaryList[i].getWord_target()
                    + "          | " + dictionary.dictionaryList[i].getWord_explain());
        }
    }
    @Override
    public void dictionaryBasic(){
        insertFromCommandline();
        showAllWords();
    }
    @Override
    public void dictionaryAdvanced(){
        insertFromFile();
        showAllWords();
        dictionarySearcher();

    }



}
