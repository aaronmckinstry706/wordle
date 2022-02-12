package aaron.wordle.solver;

import aaron.wordle.game.PositionResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WordConstraintsTest {

    @Test
    public void testWordConstraintsAfterCorrectGuess() {
        WordConstraints wordConstraints = new WordConstraints(4);
        wordConstraints = wordConstraints.updateFromGuess("babe", Arrays.asList(PositionResponse.IN_POSITION, PositionResponse.IN_POSITION, PositionResponse.IN_POSITION, PositionResponse.IN_POSITION));
        List<String> allowedWords = Stream.of("babf", "babb", "baba", "bafe", "baae", "bfbe", "bbbe", "fabe", "eabe", "babe").filter(wordConstraints::wordFitsConstraints).collect(Collectors.toList());
        Assertions.assertEquals(Arrays.asList("babe"), allowedWords);
    }

    @Test
    public void testWordConstraintsAfterPartiallyCorrectGuess() {
        WordConstraints wordConstraints = new WordConstraints(2);
        wordConstraints = wordConstraints.updateFromGuess("ab", Arrays.asList(PositionResponse.IN_POSITION, PositionResponse.NOT_IN_WORD));
        Assertions.assertTrue(wordConstraints.wordFitsConstraints("ac"));
        Assertions.assertFalse(wordConstraints.wordFitsConstraints("dc"));
    }

    @Test
    public void testWordConstraintsAfterNoneInPositionAndSomeInWord() {
        WordConstraints wordConstraints = new WordConstraints(3);
        wordConstraints = wordConstraints.updateFromGuess("abc", Arrays.asList(PositionResponse.NOT_IN_WORD, PositionResponse.IN_WORD_NOT_POSITION, PositionResponse.NOT_IN_WORD));
        Assertions.assertFalse(wordConstraints.wordFitsConstraints("dbe"));
        Assertions.assertFalse(wordConstraints.wordFitsConstraints("bbe"));
        Assertions.assertTrue(wordConstraints.wordFitsConstraints("bde"));
        Assertions.assertTrue(wordConstraints.wordFitsConstraints("bdb"));
        Assertions.assertTrue(wordConstraints.wordFitsConstraints("deb"));
    }

    @Test
    public void testWordConstraintsAfterMultipleGuesses() {
        WordConstraints wordConstraints = new WordConstraints(3);

        wordConstraints = wordConstraints.updateFromGuess("abb", Arrays.asList(PositionResponse.NOT_IN_WORD, PositionResponse.IN_WORD_NOT_POSITION, PositionResponse.NOT_IN_WORD));
        Assertions.assertTrue(wordConstraints.wordFitsConstraints("bcd"));
        Assertions.assertFalse(wordConstraints.wordFitsConstraints("ccb"));
        Assertions.assertFalse(wordConstraints.wordFitsConstraints("bbc"));
        Assertions.assertFalse(wordConstraints.wordFitsConstraints("bca"));

        wordConstraints = wordConstraints.updateFromGuess("cde", Arrays.asList(PositionResponse.IN_WORD_NOT_POSITION, PositionResponse.IN_WORD_NOT_POSITION, PositionResponse.NOT_IN_WORD));
        Assertions.assertTrue(wordConstraints.wordFitsConstraints("bcd"));
        Assertions.assertFalse(wordConstraints.wordFitsConstraints("bdc"));
        Assertions.assertFalse(wordConstraints.wordFitsConstraints("cbd"));
        Assertions.assertFalse(wordConstraints.wordFitsConstraints("cdb"));
        Assertions.assertFalse(wordConstraints.wordFitsConstraints("dcb"));
        Assertions.assertFalse(wordConstraints.wordFitsConstraints("dbc"));
    }

    @Test
    public void testWordConstraintsAfterConflictingResponsesWithInPosition() {
        WordConstraints wordConstraints = new WordConstraints(1);
        wordConstraints = wordConstraints
                .updateFromGuess("a", Collections.singletonList(PositionResponse.IN_POSITION))
                .updateFromGuess("b", Collections.singletonList(PositionResponse.IN_POSITION));
        Assertions.assertFalse(wordConstraints.wordFitsConstraints("a"));
        Assertions.assertFalse(wordConstraints.wordFitsConstraints("b"));
    }

    @Test
    public void testWordConstraintsAfterConflictingResponsesWithLetterCounts() {
        WordConstraints wordConstraints = new WordConstraints(5);
        wordConstraints = wordConstraints
                .updateFromGuess("aaabb", Arrays.asList(PositionResponse.IN_WORD_NOT_POSITION, PositionResponse.IN_WORD_NOT_POSITION, PositionResponse.NOT_IN_WORD, PositionResponse.NOT_IN_WORD, PositionResponse.NOT_IN_WORD))
                .updateFromGuess("aaabb", Arrays.asList(PositionResponse.IN_WORD_NOT_POSITION, PositionResponse.NOT_IN_WORD, PositionResponse.NOT_IN_WORD, PositionResponse.NOT_IN_WORD, PositionResponse.NOT_IN_WORD));
        Assertions.assertFalse(wordConstraints.wordFitsConstraints("cccaa"));
        Assertions.assertFalse(wordConstraints.wordFitsConstraints("cccca"));
        Assertions.assertFalse(wordConstraints.wordFitsConstraints("ccccc"));
    }

}