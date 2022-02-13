package aaron.wordle.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WordleGame {

    private String answer;
    private int[] charOffsetToCountInAnswer;

    public WordleGame(String answer) {
        this.answer = answer;
        charOffsetToCountInAnswer = new int[26];
        for (int position = 0; position < answer.length(); ++position) {
            charOffsetToCountInAnswer[answer.charAt(position) - 'a']++;
        }
    }

    public List<PositionResponse> guessWord(String word) {
        if (word == null) {
            throw new NullPointerException("word");
        }
        if (word.length() != answer.length()) {
            throw new IllegalArgumentException("word must have same length as answer!");
        }

        List<PositionResponse> response = new ArrayList<>(answer.length());
        int[] charOffsetToCountInWord = new int[26];
        for (int position = 0; position < answer.length(); position++) {
            response.add(null);
            if (answer.charAt(position) == word.charAt(position)) {
                response.set(position, PositionResponse.IN_POSITION);
                charOffsetToCountInWord[word.charAt(position) - 'a']++;
            }
        }
        for (int position = 0; position < word.length(); ++position) {
            int wordCharOffset = word.charAt(position) - 'a';
            if (answer.charAt(position) != word.charAt(position)) {
                if (charOffsetToCountInAnswer[wordCharOffset] > 0) {
                    if (charOffsetToCountInWord[wordCharOffset] < charOffsetToCountInAnswer[wordCharOffset]) {
                        response.set(position, PositionResponse.IN_WORD_NOT_POSITION);
                    } else {
                        response.set(position, PositionResponse.NOT_IN_WORD);
                    }
                    charOffsetToCountInWord[wordCharOffset]++;
                }
                else {
                    response.set(position, PositionResponse.NOT_IN_WORD);
                }
            }
        }

        return response;
    }

}
