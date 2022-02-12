package aaron.wordle.solver;

import aaron.wordle.game.PositionResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WordleSolverTest {

    @Test
    public void testNextGuessWhenCorrectNextGuessIsAtFirstIndex() {
        WordleSolver solver = new WordleSolver(Arrays.asList("cat", "cad", "car", "fox"));
        Assertions.assertEquals("cat", solver.nextGuess());
    }

    @Test
    public void testNextGuessWhenCorrectNextGuessIsNotAtFirstIndex() {
        WordleSolver solver = new WordleSolver(Arrays.asList("fox", "cad", "car", "cat"));
        Assertions.assertEquals("cad", solver.nextGuess());
    }

    @Test
    public void testNextGuessWithOneWordInDictionary() {
        WordleSolver solver = new WordleSolver(Collections.singletonList("axe"));
        Assertions.assertEquals("axe", solver.nextGuess());
    }

    @Test
    public void testNextGuessWithMultipleOfSameWordInDictionary() {
        WordleSolver solver = new WordleSolver(Arrays.asList("a", "a", "a", "b", "c"));
        Assertions.assertEquals("a", solver.nextGuess());
    }

    @Test
    public void testNextGuessWithMultipleOfSameWordInDictionaryAndCorrectNextGuessIsNotAtFirstIndex() {
        WordleSolver solver = new WordleSolver(Arrays.asList("de", "de", "de", "ab", "ac", "fg"));
        Assertions.assertEquals("ab", solver.nextGuess());
    }

    @Test
    public void testNextGuessWithMultipleOfSameWordInDictionaryAndCorrectNextGuessIsNotAtFirstIndexAndCorrectNextGuessHasDuplicates() {
        WordleSolver solver = new WordleSolver(Arrays.asList("de", "ab", "ab", "ab", "ac", "fg"));
        Assertions.assertEquals("ab", solver.nextGuess());
    }

    @Test
    public void testUpdateFromGuess() {
        WordleSolver solver = new WordleSolver(Arrays.asList("de", "ab", "ab", "ab", "ac", "fg"));
        solver.updateFromGuess("ab", Arrays.asList(PositionResponse.IN_POSITION, PositionResponse.NOT_IN_WORD));
        Assertions.assertEquals(Collections.singletonList("ac"), solver.getAnswerDictionary());
    }

    @Test
    public void testWithDifferentAnswerAndWordMatchDictionaries() {
        List<String> wordMatchDictionary = Arrays.asList("ab", "cd", "ef", "be");
        List<String> answerDictionary = Arrays.asList("bd", "cf");
        WordleSolver solver = new WordleSolver(wordMatchDictionary, answerDictionary);
        solver.updateFromGuess("ab", Arrays.asList(PositionResponse.NOT_IN_WORD, PositionResponse.IN_WORD_NOT_POSITION));
        Assertions.assertEquals(Collections.singletonList("bd"), solver.getAnswerDictionary());
        Assertions.assertEquals("bd", solver.nextGuess());
    }

    @Test
    public void testTwoAnswersThatHaveSameWorstCaseButOneIsNotAnAnswerAndTheOtherIsAnAnswer() {
        List<String> wordMatchDictionary = Arrays.asList("ab");
        List<String> answerDictionary = Arrays.asList("cd", "ae");
        WordleSolver solver = new WordleSolver(wordMatchDictionary, answerDictionary);
        Assertions.assertEquals("cd", solver.nextGuess());
    }

}
