package aaron.wordle.solver;

import aaron.wordle.game.PositionResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

public class WordResponsesTest {

    @Test
    public void testWithLengthOne() {
        Iterator<List<PositionResponse>> responses = WordResponses.withLength(1);
        for (int i = 0; i < PositionResponse.values().length; ++i) {
            Assertions.assertTrue(responses.hasNext());
            List<PositionResponse> potentialResponse = responses.next();
            Assertions.assertEquals(Collections.singletonList(PositionResponse.values()[i]), potentialResponse);
        }
        Assertions.assertFalse(responses.hasNext());
    }

    @Test
    public void testWithLengthTwo() {
        Iterator<List<PositionResponse>> responses = WordResponses.withLength(2);
        List<List<PositionResponse>> actualResponses = new ArrayList<>();
        while (responses.hasNext()) {
            actualResponses.add(responses.next());
        }

        List<List<PositionResponse>> expectedResponses = Arrays.asList(
                Arrays.asList(PositionResponse.values()[0], PositionResponse.values()[0]),
                Arrays.asList(PositionResponse.values()[1], PositionResponse.values()[0]),
                Arrays.asList(PositionResponse.values()[2], PositionResponse.values()[0]),
                Arrays.asList(PositionResponse.values()[0], PositionResponse.values()[1]),
                Arrays.asList(PositionResponse.values()[1], PositionResponse.values()[1]),
                Arrays.asList(PositionResponse.values()[2], PositionResponse.values()[1]),
                Arrays.asList(PositionResponse.values()[0], PositionResponse.values()[2]),
                Arrays.asList(PositionResponse.values()[1], PositionResponse.values()[2]),
                Arrays.asList(PositionResponse.values()[2], PositionResponse.values()[2])
        );
        Assertions.assertEquals(expectedResponses, actualResponses);
    }

    @Test
    public void testWithLengthThree() {
        Iterator<List<PositionResponse>> responses = WordResponses.withLength(3);
        List<List<PositionResponse>> actualResponses = new ArrayList<>();
        while (responses.hasNext()) {
            actualResponses.add(responses.next());
        }

        List<List<PositionResponse>> expectedResponses = Arrays.asList(
                Arrays.asList(PositionResponse.values()[0], PositionResponse.values()[0], PositionResponse.values()[0]),
                Arrays.asList(PositionResponse.values()[1], PositionResponse.values()[0], PositionResponse.values()[0]),
                Arrays.asList(PositionResponse.values()[2], PositionResponse.values()[0], PositionResponse.values()[0]),
                Arrays.asList(PositionResponse.values()[0], PositionResponse.values()[1], PositionResponse.values()[0]),
                Arrays.asList(PositionResponse.values()[1], PositionResponse.values()[1], PositionResponse.values()[0]),
                Arrays.asList(PositionResponse.values()[2], PositionResponse.values()[1], PositionResponse.values()[0]),
                Arrays.asList(PositionResponse.values()[0], PositionResponse.values()[2], PositionResponse.values()[0]),
                Arrays.asList(PositionResponse.values()[1], PositionResponse.values()[2], PositionResponse.values()[0]),
                Arrays.asList(PositionResponse.values()[2], PositionResponse.values()[2], PositionResponse.values()[0]),
                Arrays.asList(PositionResponse.values()[0], PositionResponse.values()[0], PositionResponse.values()[1]),
                Arrays.asList(PositionResponse.values()[1], PositionResponse.values()[0], PositionResponse.values()[1]),
                Arrays.asList(PositionResponse.values()[2], PositionResponse.values()[0], PositionResponse.values()[1]),
                Arrays.asList(PositionResponse.values()[0], PositionResponse.values()[1], PositionResponse.values()[1]),
                Arrays.asList(PositionResponse.values()[1], PositionResponse.values()[1], PositionResponse.values()[1]),
                Arrays.asList(PositionResponse.values()[2], PositionResponse.values()[1], PositionResponse.values()[1]),
                Arrays.asList(PositionResponse.values()[0], PositionResponse.values()[2], PositionResponse.values()[1]),
                Arrays.asList(PositionResponse.values()[1], PositionResponse.values()[2], PositionResponse.values()[1]),
                Arrays.asList(PositionResponse.values()[2], PositionResponse.values()[2], PositionResponse.values()[1]),
                Arrays.asList(PositionResponse.values()[0], PositionResponse.values()[0], PositionResponse.values()[2]),
                Arrays.asList(PositionResponse.values()[1], PositionResponse.values()[0], PositionResponse.values()[2]),
                Arrays.asList(PositionResponse.values()[2], PositionResponse.values()[0], PositionResponse.values()[2]),
                Arrays.asList(PositionResponse.values()[0], PositionResponse.values()[1], PositionResponse.values()[2]),
                Arrays.asList(PositionResponse.values()[1], PositionResponse.values()[1], PositionResponse.values()[2]),
                Arrays.asList(PositionResponse.values()[2], PositionResponse.values()[1], PositionResponse.values()[2]),
                Arrays.asList(PositionResponse.values()[0], PositionResponse.values()[2], PositionResponse.values()[2]),
                Arrays.asList(PositionResponse.values()[1], PositionResponse.values()[2], PositionResponse.values()[2]),
                Arrays.asList(PositionResponse.values()[2], PositionResponse.values()[2], PositionResponse.values()[2])
        );
        Assertions.assertEquals(expectedResponses, actualResponses);
    }

}
