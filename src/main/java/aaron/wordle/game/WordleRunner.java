package aaron.wordle.game;

import aaron.wordle.solver.WordleSolver;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WordleRunner {

    List<String> dictionary;
    WordleSolver solver;
    Console console;

    public WordleRunner(String pathToDictionary) {
        dictionary = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(pathToDictionary))) {
            stream.forEach(line -> dictionary.add(line.trim().toLowerCase()));
        } catch (IOException e) {
            throw new RuntimeException("Error reading file.", e);
        }
        solver = new WordleSolver(dictionary);
        console = System.console();
    }

    public static void main(String[] args) {
        new WordleRunner(args[0]).run();
    }

    private void run() {
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
            console.printf("I guess '" + nextGuess + "'. What is the response from Wordle?");
            positionResponses = getResponseFromLine(System.console().readLine());
            while (positionResponses == null) {
                console.printf("Invalid input! Please enter a valid reply.\n");
                positionResponses = getResponseFromLine(System.console().readLine());
            }
            guessCount++;
        } while (!positionResponses.stream().allMatch(PositionResponse.IN_POSITION::equals));

        console.printf("It took me " + guessCount + " tries to guess the word!");
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
