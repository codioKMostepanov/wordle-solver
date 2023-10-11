import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;


public class WordleSolver {
    private static List<String> ignoreWords = new ArrayList<>();
    private static List<String> board = new ArrayList<>();

    private static List<String> mustHave(List<String> bigList, String must) {
        List<String> newList = new ArrayList<>();
        for (String word : bigList) {
            boolean flag = true;
            for (char ch : must.toCharArray()) {
                if (word.indexOf(ch) == -1) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                newList.add(word);
            }
        }
        return newList;
    }

    private static List<String> remover(List<String> bigList, String ignore) {
        List<String> newList = new ArrayList<>();
        for (String word : bigList) {
            boolean flag = true;
            for (char ch : ignore.toCharArray()) {
                if (word.indexOf(ch) != -1) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                newList.add(word);
            }
        }
        return newList;
    }

    private static List<String> matcher(List<String> bigList, List<String> target) {
        List<String> results = new ArrayList<>();
        
        for (String word : bigList) {
            boolean isMatch = true;
            for (int i = 0; i < 5; i++) {
                if (!target.get(i).equals("_") && !target.get(i).equals(String.valueOf(word.charAt(i)))) {
                    isMatch = false;
                    break;
                }
            }
            if (isMatch) {
                results.add(word);
            }
        }

        return results;
    }

    private static List<String> letterWords() {
        List<String> rows = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("wordle-solver/words.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                rows.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rows;
    }

    private static void initializeBoard() {
        int i = 0;
        while(i < 5) {
            i = userEntry(i);
        }
        display(board);
    }

    private static int userEntry(int location) {
        String initialState = board.get(location);
        board.set(location, "=");
        String prompt = "Your current cell is marked with a '='.\n * Enter a letter if you know it belongs in that cell.\n * Enter a space if you want to skip to the next cell.\n * Enter '1' to move one cell back and make changes.\n * Enter '0' to exit the board editing process.";
        System.out.println(prompt);
        String userInput = getInput(board.toString());

        if (userInput.equals(" ") || userInput.isEmpty()) {
            board.set(location, initialState);
            return location + 1;
        } else if (userInput.equals("0")) {
            board.set(location, initialState);
            return 5;
        } else if (userInput.equals("1")) {
            board.set(location, "_");
            return Math.max(location - 1, 0);
        } else {
            board.set(location, userInput);
            return location + 1;
        }
    }

    private static String getInput(String prompt) {
        System.out.print(prompt + " ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }
    
    private static void display(List<String> currentBoard) {
        System.out.println(currentBoard);
    }

    private static void resetBoard() {
        board.clear();
        for (int i = 0; i < 5; i++) {
            board.add("_");
        }
    }

    private static void play() {
        resetBoard();
        initializeBoard();
        
        while (true) {
            List<String> rows = letterWords();
            
            String ignore = getInput("Type in the letters to ignore (if any): ");
            ignoreWords = new ArrayList<>(Arrays.asList(ignore.split(",")));
            
            if (!ignoreWords.isEmpty() && !ignoreWords.get(0).equals("")) {
                rows = remover(rows, ignoreWords.get(0));
            }

            List<String> results = matcher(rows, board);
            
            String must = getInput("Type in the letters to include but not sure of location: ");
            results = mustHave(results, must);

            System.out.println("Here are the results of the matches:");
            if (results.size() <= 10) {
                System.out.println(results);
            } else {
                System.out.println(results.subList(0, 10));
            }

            String nextMove = getInput("Do you want to continue (c), reset (r), or exit (e)? ");
            
            if (nextMove.equals("e")) {
                break;
            } else if (nextMove.equals("r")) {
                resetBoard();
                initializeBoard();
            } else if (nextMove.equals("c")) {
                initializeBoard();
            }
        }
    }

    public static void main(String[] args) {
      play();
    }
}