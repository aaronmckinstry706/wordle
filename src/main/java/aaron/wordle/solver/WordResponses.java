package aaron.wordle.solver;

import aaron.wordle.game.PositionResponse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WordResponses implements Iterator<List<PositionResponse>> {

    int wordLength;
    int responseNumber;
    int endOfResponseNumbers;

    private WordResponses(int wordLength) {
        this.wordLength = wordLength;
        this.responseNumber = 0;
        this.endOfResponseNumbers = 1;
        for (int position = 0; position < wordLength; position++) this.endOfResponseNumbers *= PositionResponse.values().length;
    }

    public static WordResponses withLength(int wordLength) {
        return new WordResponses(wordLength);
    }

    @Override
    public boolean hasNext() {
        return responseNumber < endOfResponseNumbers;
    }

    @Override
    public List<PositionResponse> next() {
        if (hasNext()) {
            return responseNumberToPositionResponses(responseNumber++, wordLength);
        }
        else {
            throw new IllegalStateException("next() was called on iterator when hasNext() is false!");
        }
    }

    private static List<PositionResponse> responseNumberToPositionResponses(int responseNumber, int wordLength) {
        List<PositionResponse> response = new ArrayList<>(wordLength);
        while (response.size() < wordLength) response.add(PositionResponse.values()[0]);
        int place = 1;
        int base = PositionResponse.values().length;
        for (int position = 0; position < response.size(); position++, place *= base) {
            int digit = (responseNumber / place) % base;
            response.set(position, PositionResponse.values()[digit]);
        }
        return response;
    }
}
