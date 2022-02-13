package aaron.wordle.solver;

import aaron.wordle.game.PositionResponse;

import java.util.*;
import java.util.stream.Collectors;

public class WordleSolver {

    public List<String> wordMatchDictionary;
    public List<String> answerDictionary;
    WordConstraints wordConstraints;

    public WordleSolver(List<String> dictionary) {
        this(dictionary, dictionary);
    }

    public WordleSolver(List<String> wordMatchDictionary, List<String> answerDictionary) {
        ensureDictionaryHasWords(wordMatchDictionary);
        ensureDictionaryHasWords(answerDictionary);
        int wordLength = wordMatchDictionary.get(0).length();
        ensureDictionaryWordsHaveCorrectLength(wordMatchDictionary, wordLength);
        ensureDictionaryWordsHaveCorrectLength(answerDictionary, wordLength);

        this.wordMatchDictionary = new ArrayList<>();
        Set<String> wordsInMatchDictionary = new HashSet<>();
        // Add answer dictionary first so that potential answers are preferred over match words, all else being equal.
        for (String word : answerDictionary) {
            if (!wordsInMatchDictionary.contains(word)) {
                this.wordMatchDictionary.add(word);
                wordsInMatchDictionary.add(word);
            }
        }
        for (String word : wordMatchDictionary) {
            if (!wordsInMatchDictionary.contains(word)) {
                this.wordMatchDictionary.add(word);
                wordsInMatchDictionary.add(word);
            }
        }

        this.answerDictionary = new ArrayList<>();
        Set<String> wordsInAnswerDictionary = new HashSet<>();
        for (String word : answerDictionary) {
            if (!wordsInAnswerDictionary.contains(word)) {
                this.answerDictionary.add(word);
                wordsInAnswerDictionary.add(word);
            }
        }

        this.wordConstraints = new WordConstraints(wordLength);
    }

    private static void ensureDictionaryHasWords(List<String> dictionary) {
        if (dictionary == null) throw new NullPointerException("dictionary");
        if (dictionary.isEmpty()) throw new IllegalArgumentException("dictionary must be non-empty!");
    }

    private static void ensureDictionaryWordsHaveCorrectLength(List<String> dictionary, int wordLength) {
        for (String word : dictionary) {
            if (word.length() != wordLength) {
                throw new IllegalArgumentException("wordMatchDictionary words must be same length!");
            }
            if (!word.matches("[a-z]+")) {
                throw new IllegalArgumentException("wordMatchDictionary words must match regex `[a-z]+`!");
            }
        }
    }

    public String nextGuess() {
        if (answerDictionary.size() == 1) {
            return answerDictionary.get(0);
        }
        int minMaxNumRemainingGuesses = Integer.MAX_VALUE;
        String wordWithMinMaxNumRemainingGuesses = null;
        long startTimeMillis = System.currentTimeMillis();
        int numProcessed = 0;
        for (String word : wordMatchDictionary) {
            int maxNumRemainingGuesses = Integer.MIN_VALUE;
            Iterator<List<PositionResponse>> wordResponseIterator = WordResponses.withLength(wordConstraints.getWordLength());
            while (wordResponseIterator.hasNext()) {
                List<PositionResponse> potentialResponse = wordResponseIterator.next();
                WordConstraints wordConstraintsAfterGuessAndResponse = wordConstraints.updateFromGuess(word, potentialResponse);
                if (wordConstraintsAfterGuessAndResponse == null) continue;
                int numRemainingGuesses = 0;
                for (String remainingGuess : answerDictionary) {
                    if (wordConstraintsAfterGuessAndResponse.wordFitsConstraints(remainingGuess)) {
                        numRemainingGuesses++;
                    }
                }
                if (numRemainingGuesses > maxNumRemainingGuesses) {
                    maxNumRemainingGuesses = numRemainingGuesses;
                }
                if (maxNumRemainingGuesses > minMaxNumRemainingGuesses) {
                    break;
                }
            }
            if (maxNumRemainingGuesses < minMaxNumRemainingGuesses && maxNumRemainingGuesses > 0) {
                wordWithMinMaxNumRemainingGuesses = word;
                minMaxNumRemainingGuesses = maxNumRemainingGuesses;
            }
            numProcessed++;
            if (numProcessed % 100 == 0) {
                long endTimeMillis = System.currentTimeMillis();
                double timeSpent = ((double) (endTimeMillis - startTimeMillis) / 1000);
                double timeToProcessOneGuess = timeSpent/numProcessed;
                double remainingTime = timeToProcessOneGuess * (wordMatchDictionary.size() - numProcessed);
                System.console().printf("Estimated remaining time to guess: " + remainingTime + " seconds.\n");
            }
        }
        return wordWithMinMaxNumRemainingGuesses;
    }

    public void updateFromGuess(String guess, List<PositionResponse> response) {
        wordConstraints = wordConstraints.updateFromGuess(guess, response);
        answerDictionary = answerDictionary.stream().filter(wordConstraints::wordFitsConstraints).collect(Collectors.toList());
    }

    public List<String> getAnswerDictionary() {
        return new ArrayList<>(answerDictionary);
    }

    public int getWordLength() {
        return wordConstraints.getWordLength();
    }

}
