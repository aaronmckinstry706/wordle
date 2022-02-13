package aaron.wordle.game;

import aaron.wordle.solver.WordleSolver;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WordleRunner {

    public static void main(String[] args) {
        if ("manual".equals(args[0])) {
            new WordleRunner(args[1], args[2]).runAgainstManualInputWordleResponses();
        }
        else {
            new WordleRunner(args[1], args[2]).findAverageSolveTime();
        }
    }

    private static List<String> readDictionaryFromFile(String pathToDictionary) {
        List<String> dictionary = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(pathToDictionary))) {
            stream.forEach(line -> dictionary.add(line.trim().toLowerCase()));
        } catch (IOException e) {
            throw new RuntimeException("Error reading file.", e);
        }
        return dictionary;
    }

    List<String> wordMatchDictionary;
    List<String> answerDictionary;
    WordleSolver solver;
    Console console;

    public WordleRunner(String pathToWordMatchDictionary, String pathToAnswerDictionary) {
        wordMatchDictionary = readDictionaryFromFile(pathToWordMatchDictionary);
        answerDictionary = readDictionaryFromFile(pathToAnswerDictionary);
        solver = new WordleSolver(wordMatchDictionary, answerDictionary);
        console = System.console();
    }

    private void runAgainstManualInputWordleResponses() {
        console.printf("Hello! I will guess the next answer based on the dictionary you provided, " +
                "and you will provide the answer in the following format: `_ _ _ _ _`, " +
                "where _ can be 'gray', 'yellow', or 'green' (quotes excluded). " +
                "For example, the input `gray yellow yellow gray green` is valid. " +
                "The number of colors on each line should match the length of the words in the dictionary you provided. " +
                "Hopefully this can be used to efficiently solve the Wordle game at `https://www.nytimes.com/games/wordle/index.html`. \n");

        int guessCount = 0;
        List<PositionResponse> positionResponses;
        do {
            String nextGuess = solver.nextGuess();
            console.printf("I guess '" + nextGuess + "'. What is the response from Wordle?\n");
            positionResponses = getResponseFromLine(System.console().readLine());
            while (positionResponses == null) {
                console.printf("Invalid input! Please enter a valid reply.\n");
                positionResponses = getResponseFromLine(System.console().readLine());
            }
            solver.updateFromGuess(nextGuess, positionResponses);
            guessCount++;
        } while (!positionResponses.stream().allMatch(PositionResponse.IN_POSITION::equals));

        console.printf("It took me " + guessCount + " tries to guess the word!\n");
    }

    private void findAverageSolveTime() {
        Random random = new Random(System.currentTimeMillis());
        List<String> answerDictionaryCopy = new ArrayList<>(answerDictionary);
        Collections.shuffle(answerDictionaryCopy, random);
        int totalGuesses = 0;
        int totalGames = 0;
        for (String answer : answerDictionaryCopy) {
            WordleGame game = new WordleGame(answer);
            WordleSolver solver = new WordleSolver(wordMatchDictionary, answerDictionary);
            int numGuesses = 0;
            String nextGuess;
            do {
                nextGuess = solver.nextGuess();
                solver.updateFromGuess(nextGuess, game.guessWord(nextGuess));
                numGuesses++;
            } while (!nextGuess.equals(answer));
            totalGames++;
            totalGuesses += numGuesses;
            console.printf("With " + totalGames + " games the average number of guesses is " + (int)((double) totalGuesses / totalGames) + ".\n");
        }
    }

    private List<PositionResponse> getResponseFromLine(String line) {
        line = line.trim().toLowerCase().replaceAll("[^a-z]+", " ");
        List<PositionResponse> response = Arrays.stream(line.split(" "))
                .map(PositionResponse::fromColor)
                .map(color -> color.orElse(null))
                .collect(Collectors.toList());
        return response.stream().anyMatch(Objects::isNull) || response.size() != solver.getWordLength() ? null : response;
    }

}
