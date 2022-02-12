package aaron.wordle.game;

import java.util.Arrays;
import java.util.Optional;

public enum PositionResponse {
    NOT_IN_WORD("gray"), IN_WORD_NOT_POSITION("yellow"), IN_POSITION("green");

    private final String color;

    PositionResponse(String color) {
        this.color = color;
    }

    public static Optional<PositionResponse> fromColor(String color) {
        return Arrays.stream(PositionResponse.values()).filter(positionResponse -> positionResponse.color.equals(color)).findFirst();
    }
}
