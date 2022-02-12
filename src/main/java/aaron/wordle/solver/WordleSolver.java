package aaron.wordle.solver;

import aaron.wordle.game.PositionResponse;

import java.util.*;
import java.util.stream.Collectors;

public class WordleSolver {

    public List<String> remainingWords;
    WordConstraints wordConstraints;

    public WordleSolver(List<String> dictionary) {
        this.remainingWords = new ArrayList<>();
        Set<String> wordsInDictionary = new HashSet<>();
        for (String word : dictionary) {
            if (!wordsInDictionary.contains(word)) {
                this.remainingWords.add(word);
                wordsInDictionary.add(word);
            }
        }
        if (remainingWords.isEmpty()) {
            throw new IllegalArgumentException("dictionary must be non-empty!");
        }
        int wordLength = dictionary.get(0).length();
        for (String word : dictionary) {
            if (word.length() != wordLength) {
                throw new IllegalArgumentException("dictionary words must be same length!");
            }
            if (!word.matches("[a-z]+")) {
                throw new IllegalArgumentException("dictionary words must match regex `[a-z]+`!");
            }
        }
        this.wordConstraints = new WordConstraints(wordLength);
    }

    public String nextGuess() {
        int minMaxNumRemainingGuesses = Integer.MAX_VALUE;
        String wordWithMinMaxNumRemainingGuesses = null;
        long startTimeMillis = System.currentTimeMillis();
        int numProcessed = 0;
        for (String word : remainingWords) {
            int maxNumRemainingGuesses = Integer.MIN_VALUE;
            Iterator<List<PositionResponse>> wordResponseIterator = WordResponses.withLength(wordConstraints.getWordLength());
            while (wordResponseIterator.hasNext()) {
                List<PositionResponse> potentialResponse = wordResponseIterator.next();
                WordConstraints wordConstraintsAfterGuessAndResponse = wordConstraints.updateFromGuess(word, potentialResponse);
                if (wordConstraintsAfterGuessAndResponse == null) continue;
                int numRemainingGuesses = (int) remainingWords.stream().filter(wordConstraintsAfterGuessAndResponse::wordFitsConstraints).count();
                if (numRemainingGuesses > maxNumRemainingGuesses) {
                    maxNumRemainingGuesses = numRemainingGuesses;
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
                double remainingTime = timeToProcessOneGuess * (remainingWords.size() - numProcessed);
                System.console().printf("Estimated remaining time to guess: " + remainingTime + " seconds.\n");
            }
        }
        return wordWithMinMaxNumRemainingGuesses;
    }

    public void updateFromGuess(String guess, List<PositionResponse> response) {
        wordConstraints = wordConstraints.updateFromGuess(guess, response);
        remainingWords = remainingWords.stream().filter(wordConstraints::wordFitsConstraints).collect(Collectors.toList());
    }

    public List<String> getRemainingWords() {
        return new ArrayList<>(remainingWords);
    }

    public int getWordLength() {
        return wordConstraints.getWordLength();
    }

}
