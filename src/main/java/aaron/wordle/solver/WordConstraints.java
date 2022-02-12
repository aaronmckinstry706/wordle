package aaron.wordle.solver;

import aaron.wordle.game.PositionResponse;

import java.util.*;

public class WordConstraints {

    private final int wordLength;
    private final Map<Character, Integer> characterToMinCountInWord;
    private final Map<Character, Integer> characterToMaxCountInWord;
    private final Map<Integer, Set<Character>> positionToAllowedCharacters;

    private WordConstraints() {
        throw new IllegalStateException();
    }

    private WordConstraints(WordConstraints wordConstraints) {
        wordLength = wordConstraints.wordLength;
        characterToMinCountInWord = new HashMap<>(wordConstraints.characterToMinCountInWord);
        characterToMaxCountInWord = new HashMap<>(wordConstraints.characterToMaxCountInWord);
        positionToAllowedCharacters = new HashMap<>();
        for (int position : wordConstraints.positionToAllowedCharacters.keySet()) {
            positionToAllowedCharacters.put(position, new HashSet<>(wordConstraints.positionToAllowedCharacters.get(position)));
        }
    }

    public WordConstraints(int wordLength) {
        this.characterToMinCountInWord = new HashMap<>();
        this.characterToMaxCountInWord = new HashMap<>();
        this.positionToAllowedCharacters = new HashMap<>();
        this.wordLength = wordLength;
        for (int i = 0; i < wordLength; ++i) {
            Set<Character> alphabet = new HashSet<>();
            for (char c = 'a'; c <= 'z'; c++) {
                alphabet.add(c);
            }
            positionToAllowedCharacters.put(i, alphabet);
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
            PositionResponse responseAtPosition = response.get(guessPosition);
            if (responseAtPosition == PositionResponse.IN_WORD_NOT_POSITION) {
                constraints.characterToMinCountInWord.put(guessCharacter, Math.max(constraints.characterToMinCountInWord.getOrDefault(guessCharacter, 0), 1));
                constraints.positionToAllowedCharacters.get(guessPosition).remove(guessCharacter);
                charToNumResponsesIndicatingCharInWord.put(guessCharacter, charToNumResponsesIndicatingCharInWord.getOrDefault(guessCharacter, 0) + 1);
            } else if (responseAtPosition == PositionResponse.IN_POSITION) {
                constraints.characterToMinCountInWord.put(guessCharacter, Math.max(constraints.characterToMinCountInWord.getOrDefault(guessCharacter, 0), 1));
                charToNumResponsesIndicatingCharInWord.put(guessCharacter, charToNumResponsesIndicatingCharInWord.getOrDefault(guessCharacter, 0) + 1);
                for (char notAllowedChar = 'a'; notAllowedChar <= 'z'; notAllowedChar++) {
                    if (notAllowedChar == guessCharacter) continue;
                    constraints.positionToAllowedCharacters.get(guessPosition).remove(notAllowedChar);
                }
            } else if (responseAtPosition == PositionResponse.NOT_IN_WORD) {
                charToNumResponsesIndicatingCharNotInWord.put(guessCharacter, charToNumResponsesIndicatingCharNotInWord.getOrDefault(guessCharacter, 0) + 1);
                constraints.positionToAllowedCharacters.get(guessPosition).remove(guessCharacter);
            } else {
                throw new IllegalStateException("response.get(" + guessPosition + ") is " + responseAtPosition + ", which is not a recognized response!");
            }
        }

        for (int guessPosition = 0; guessPosition < constraints.wordLength; ++guessPosition) {
            char guessChar = guess.charAt(guessPosition);
            int inWordCount = charToNumResponsesIndicatingCharInWord.getOrDefault(guessChar, 0);
            int notInWordCount = charToNumResponsesIndicatingCharNotInWord.getOrDefault(guessChar, 0);
            if (inWordCount == 0 && notInWordCount > 0) {
                for (int answerPosition = 0; answerPosition < constraints.wordLength; answerPosition++) {
                    constraints.positionToAllowedCharacters.get(answerPosition).remove(guessChar);
                }
            } else if (inWordCount > 0 && notInWordCount == 0) {
                constraints.characterToMinCountInWord.put(guessChar, Math.max(constraints.characterToMinCountInWord.getOrDefault(guessChar, 0), inWordCount));
            } else if (inWordCount > 0 && notInWordCount > 0) {
                constraints.characterToMinCountInWord.put(guessChar, Math.max(constraints.characterToMinCountInWord.get(guessChar), inWordCount));
                constraints.characterToMaxCountInWord.put(guessChar, Math.min(constraints.characterToMaxCountInWord.getOrDefault(guessChar, Integer.MAX_VALUE), inWordCount));
            }
        }

        return constraints;
    }

    public boolean wordFitsConstraints(String word) {
        if (word.length() != wordLength) {
            throw new IllegalArgumentException("word = " + word + " should have length wordLength = " + wordLength + ".");
        }

        Map<Character, Integer> characterToCount = new HashMap<>();
        for (int wordIndex = 0; wordIndex < word.length(); wordIndex++) {
            char c = word.charAt(wordIndex);
            if (!positionToAllowedCharacters.get(wordIndex).contains(c)) {
                return false;
            }
            characterToCount.put(c, characterToCount.getOrDefault(c, 0) + 1);
        }

        for (Character c : characterToMinCountInWord.keySet()) {
            if (characterToCount.getOrDefault(c, 0) < characterToMinCountInWord.get(c)) {
                return false;
            }
        }
        for (Character c : characterToMaxCountInWord.keySet()) {
            if (characterToCount.getOrDefault(c, 0) > characterToMaxCountInWord.get(c)) {
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
