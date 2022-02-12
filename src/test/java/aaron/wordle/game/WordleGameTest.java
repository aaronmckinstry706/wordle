package aaron.wordle.game;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class WordleGameTest {

    @Test
    public void testWordleGameReturnsAllCorrect() {
        WordleGame game = new WordleGame("ab");
        Assertions.assertEquals(Arrays.asList(PositionResponse.IN_POSITION, PositionResponse.IN_POSITION), game.guessWord("ab"));
    }

    @Test
    public void testWordleGameReturnsAllIncorrect() {
        WordleGame game = new WordleGame("ab");
        Assertions.assertEquals(Arrays.asList(PositionResponse.NOT_IN_WORD, PositionResponse.NOT_IN_WORD), game.guessWord("de"));
    }

    @Test
    public void testWordleGameWhenCorrectLetterIsAheadOfIncorrectLetterAndTwoLettersAreIdentical() {
        WordleGame game = new WordleGame("bbbab");
        Assertions.assertEquals(
                Arrays.asList(PositionResponse.NOT_IN_WORD, PositionResponse.NOT_IN_WORD, PositionResponse.NOT_IN_WORD, PositionResponse.IN_POSITION, PositionResponse.NOT_IN_WORD),
                game.guessWord("aaaaa"));
    }

    @Test
    public void testWordleGameWithTwoCorrectAndOneIncorrectInstanceOfSameLetter() {
        WordleGame game = new WordleGame("bbaab");
        Assertions.assertEquals(
                Arrays.asList(PositionResponse.IN_WORD_NOT_POSITION, PositionResponse.NOT_IN_WORD, PositionResponse.NOT_IN_WORD, PositionResponse.IN_POSITION, PositionResponse.NOT_IN_WORD),
                game.guessWord("aadaa"));
    }

}
