public class Dictionary {
    private int numberOfWord;
    public Word[] dictionaryList;

    public void setNumberOfWord(int numberOfWord) {
        this.numberOfWord = numberOfWord;
    }

    public int getNumberOfWord() {
        return numberOfWord;
    }

    public Dictionary(int numberOfWord) {
        dictionaryList = new Word[numberOfWord];
    }
}
