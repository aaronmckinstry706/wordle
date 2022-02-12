package aaron.wordle.solver;

import aaron.wordle.game.PositionResponse;

import java.util.*;

public class WordConstraints {

    private final int wordLength;
    private final int[] characterToMinCountInWord;
    private final Map<Character, Integer> characterToMaxCountInWord;
    private final boolean[][] positionToAllowedCharacters;

    private WordConstraints() {
        throw new IllegalStateException();
    }

    private WordConstraints(WordConstraints wordConstraints) {
        wordLength = wordConstraints.wordLength;
        characterToMinCountInWord = Arrays.copyOf(wordConstraints.characterToMinCountInWord, wordConstraints.characterToMinCountInWord.length);
        characterToMaxCountInWord = new HashMap<>(wordConstraints.characterToMaxCountInWord);
        positionToAllowedCharacters = new boolean[wordConstraints.wordLength][];
        for (int position = 0; position < positionToAllowedCharacters.length; position++) {
            positionToAllowedCharacters[position] = Arrays.copyOf(wordConstraints.positionToAllowedCharacters[position], wordConstraints.positionToAllowedCharacters[position].length);
        }
    }

    public WordConstraints(int wordLength) {
        this.characterToMinCountInWord = new int[26];
        this.characterToMaxCountInWord = new HashMap<>();
        this.wordLength = wordLength;
        this.positionToAllowedCharacters = new boolean[wordLength][];
        for (int position = 0; position < positionToAllowedCharacters.length; ++position) {
            this.positionToAllowedCharacters[position] = new boolean[26];
            for (int allowedCharacter = 0; allowedCharacter < 26; ++allowedCharacter) {
                this.positionToAllowedCharacters[position][allowedCharacter] = true;
            }
        }
    }

    public WordConstraints updateFromGuess(String guess, List<PositionResponse> response) {
        WordConstraints constraints = new WordConstraints(this);

        if (!guessResponseIsValid(guess, response)) {
            return null;
        }

        Map<Character, Integer> charToNumResponsesIndicatingCharInWord = new HashMap<>();
        Map<Character, Integer> charToNumResponsesIndicatingCharNotInWord = new HashMap<>();
        for (int guessPosition = 0; guessPosition < constraints.wordLength; ++guessPosition) {
            char guessCharacter = guess.charAt(guessPosition);
            int guessCharOffset = guessCharacter - 'a';
            PositionResponse responseAtPosition = response.get(guessPosition);
            if (responseAtPosition == PositionResponse.IN_WORD_NOT_POSITION) {
                constraints.characterToMinCountInWord[guessCharOffset] = Math.max(constraints.characterToMinCountInWord[guessCharOffset], 1);
                constraints.positionToAllowedCharacters[guessPosition][guessCharOffset] = false;
                charToNumResponsesIndicatingCharInWord.put(guessCharacter, charToNumResponsesIndicatingCharInWord.getOrDefault(guessCharacter, 0) + 1);
            } else if (responseAtPosition == PositionResponse.IN_POSITION) {
                constraints.characterToMinCountInWord[guessCharOffset] = Math.max(constraints.characterToMinCountInWord[guessCharOffset], 1);
                charToNumResponsesIndicatingCharInWord.put(guessCharacter, charToNumResponsesIndicatingCharInWord.getOrDefault(guessCharacter, 0) + 1);
                for (char notAllowedOffset = 0; notAllowedOffset < 26; notAllowedOffset++) {
                    if (notAllowedOffset == guessCharOffset) continue;
                    constraints.positionToAllowedCharacters[guessPosition][notAllowedOffset] = false;
                }
            } else if (responseAtPosition == PositionResponse.NOT_IN_WORD) {
                charToNumResponsesIndicatingCharNotInWord.put(guessCharacter, charToNumResponsesIndicatingCharNotInWord.getOrDefault(guessCharacter, 0) + 1);
                constraints.positionToAllowedCharacters[guessPosition][guessCharOffset] = false;
            } else {
                throw new IllegalStateException("response.get(" + guessPosition + ") is " + responseAtPosition + ", which is not a recognized response!");
            }
        }

        for (int guessPosition = 0; guessPosition < constraints.wordLength; ++guessPosition) {
            char guessChar = guess.charAt(guessPosition);
            int guessCharOffset = guessChar - 'a';
            int inWordCount = charToNumResponsesIndicatingCharInWord.getOrDefault(guessChar, 0);
            int notInWordCount = charToNumResponsesIndicatingCharNotInWord.getOrDefault(guessChar, 0);
            if (inWordCount == 0 && notInWordCount > 0) {
                for (int answerPosition = 0; answerPosition < constraints.wordLength; answerPosition++) {
                    constraints.positionToAllowedCharacters[answerPosition][guessCharOffset] = false;
                }
            } else if (inWordCount > 0 && notInWordCount == 0) {
                constraints.characterToMinCountInWord[guessCharOffset] = Math.max(constraints.characterToMinCountInWord[guessCharOffset], inWordCount);
            } else if (inWordCount > 0 && notInWordCount > 0) {
                constraints.characterToMinCountInWord[guessCharOffset] = Math.max(constraints.characterToMinCountInWord[guessCharOffset], inWordCount);
                constraints.characterToMaxCountInWord.put(guessChar, Math.min(constraints.characterToMaxCountInWord.getOrDefault(guessChar, Integer.MAX_VALUE), inWordCount));
            }
        }

        return constraints;
    }

    public boolean wordFitsConstraints(String word) {
        if (word.length() != wordLength) {
            throw new IllegalArgumentException("word = " + word + " should have length wordLength = " + wordLength + ".");
        }

        int[] characterToCount = new int[26];
        for (int wordIndex = 0; wordIndex < word.length(); wordIndex++) {
            char c = word.charAt(wordIndex);
            int cOffset = c - 'a';
            if (!positionToAllowedCharacters[wordIndex][cOffset]) {
                return false;
            }
            characterToCount[cOffset]++;
        }

        for (int charOffset = 0; charOffset < 26; charOffset++) {
            if (characterToCount[charOffset] < characterToMinCountInWord[charOffset]) {
                return false;
            }
        }
        for (Character c : characterToMaxCountInWord.keySet()) {
            int cOffset = c - 'a';
            if (characterToCount[cOffset] > characterToMaxCountInWord.get(c)) {
                return false;
            }
        }

        return true;
    }

    private static boolean guessResponseIsValid(String guess, List<PositionResponse> guessResponse) {
        if (guessResponse.size() != guess.length()) {
            throw new IllegalArgumentException("guess and guessResponse do not match in length!");
        }

        for (int guessIndex = guessResponse.size() - 1; guessIndex >= 0; guessIndex--) {
            char guessCharAtHigherIndex = guess.charAt(guessIndex);
            PositionResponse responseAtHigherIndex = guessResponse.get(guessIndex);
            for (int lowerIndex = guessIndex - 1; lowerIndex >= 0; lowerIndex--) {
                char guessCharAtLowerIndex = guess.charAt(lowerIndex);
                PositionResponse responseAtLowerIndex = guessResponse.get(lowerIndex);
                if (guessCharAtHigherIndex == guessCharAtLowerIndex && responseAtHigherIndex == PositionResponse.IN_WORD_NOT_POSITION && responseAtLowerIndex == PositionResponse.NOT_IN_WORD) {
                    return false;
                }
            }
        }
        return true;
    }

    public int getWordLength() {
        return wordLength;
    }

}
