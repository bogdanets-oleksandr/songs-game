package main.java;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {

        int totalScore = 0;
        try {
            totalScore += playRoundOne(false);
            totalScore += playRoundTwo(false);
            totalScore += playRoundThree(false);

            System.out.println("You have scored " + totalScore + " points");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static ArrayList<String> getWordsRound1() {
        ArrayList<String> result = new ArrayList<>(6);
        Random random = new Random();
        Path path = Path.of("C:\\JMC17\\LyricsGame\\src\\main\\resources\\family_business\\%d.txt".formatted(random.nextInt(1, 12)));
//        Path path = Path.of("C:\\JMC17\\LyricsGame\\src\\main\\resources\\messy.txt");
        try(Scanner scanner = new Scanner(new FileReader(path.toFile()))) {
            scanner.useDelimiter("\\s{4,}");
            String title = scanner.next();
             var verses = scanner.tokens()
                     .map(s -> s.replaceAll("\\p{Punct}", ""))
                            .collect(Collectors.groupingBy(String::toLowerCase, Collectors.counting()));

             String chorus = null;
             for (var v : verses.entrySet()) {
                 if (v.getValue() > 1) {
                     chorus = v.getKey();
                 } else {
                     String firstLine = v.getKey().substring(0, v.getKey().indexOf("\n"));
                     v.setValue((long) firstLine.split(" ").length);
                 }
             }
             if (chorus == null) {
                 for (var out : verses.entrySet()) {
                     String firstTen = out.getKey().substring(0, 10);
                     for (var in : verses.entrySet()) {
                         if (in.equals(out)) continue;
                         if (in.getKey().substring(0,10).equals(firstTen)) {
                             chorus = in.getKey();
                         }
                     }
                 }
             }
            StringTokenizer tokens = new StringTokenizer(chorus);
            int start = random.nextInt(tokens.countTokens() - 5);
            for (int i = 0; i < start; i++) {
                tokens.nextToken();
            }
            while (result.size() < 5) {
                result.add(tokens.nextToken());
            }
            result.add(title.trim());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
    private static ArrayList<String> getWordsRound2() {
        ArrayList<String> result = new ArrayList<>(6);
        Random random = new Random();
        Path path = Path.of("C:\\JMC17\\LyricsGame\\src\\main\\resources\\family_business\\%d.txt".formatted(random.nextInt(1, 12)));
//        Path path = Path.of("C:\\JMC17\\LyricsGame\\src\\main\\resources\\messy.txt");
        try(Scanner scanner = new Scanner(new FileReader(path.toFile()))) {
            String title = scanner.nextLine();
            result.addAll(scanner.tokens()
                    .map(s -> s.replaceAll("\\p{Punct}", ""))
                    .collect(Collectors.groupingBy(String::toLowerCase, Collectors.counting()))
                    .entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .map(Map.Entry::getKey)
                    .filter(s-> s.length() > 3)
                    .limit(5)
                    .toList());
            result.add(title.trim());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private static ArrayList<String> getWordsRound3() {
        ArrayList<String> result = new ArrayList<>(9);
        Random random = new Random();
//        Path path = Path.of("C:\\JMC17\\LyricsGame\\src\\main\\resources\\messy.txt");
        Path path = Path.of("C:\\JMC17\\LyricsGame\\src\\main\\resources\\family_business\\%d.txt".
                formatted(random.nextInt(1, 12)));
        try {
            String reader = new String(Files.readAllBytes(path));
            String title = reader.substring(0, reader.indexOf("\n"));
            reader = reader.replaceAll("\\([^)]*\\)", "");
            reader = reader.replaceAll("\\p{Punct}", "");
            StringTokenizer tokens = new StringTokenizer(reader);
            int start = random.nextInt(tokens.countTokens() - 8);
            for (int i = 0; i < start; i++) {
                tokens.nextToken();
            }
            while (result.size() < 8) {
                result.add(tokens.nextToken().toLowerCase());
            }
            result.add(title);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
    private static int playRoundOne(boolean firstTime) throws InterruptedException {

        if (firstTime) printRules(1);
        ArrayList<String> answers = getWordsRound1();
        String songName = answers.remove(5);
        int score = 0;
        Scanner scanner = new Scanner(System.in);
        Thread.sleep(5000);
        ArrayList<String> currentSession = initializeList();

        while (scanner.hasNextLine()) {
            if (scanner.hasNextInt()) {
                int open = scanner.nextInt() - 1;
                scanner.nextLine();
                if (open >= 0 && open < 5) {
                    currentSession.set(open, answers.get(open));
                    printInfo(currentSession);
                } else {
                    System.out.println("Bad data");
                }
            } else {
                String userAnswers = scanner.nextLine();
                System.out.println("your answer is " + userAnswers);
                int i = 0;
                for (String word : userAnswers.split(" ")) {
                    if (word.equals(answers.get(i)) && !currentSession.get(i).equals(word)) {
                        score++;
                    }
                    i++;
                }
                break;
            }
        }

        if (!currentSession.containsAll(answers)) {
            printInfo(answers);
        }

        System.out.println("The song was: '" + songName + "'");
        System.out.println("Your score in this round is " + score);
        return score;
    }

    private static int playRoundTwo(boolean firstTime) throws InterruptedException {

        if (firstTime) printRules(2);
        ArrayList<String> answers = getWordsRound2();
        String songName = answers.remove(5);
        int score = 0;
        int failedAttempts = 0;
        Scanner scanner = new Scanner(System.in);

        Thread.sleep(5000);
        ArrayList<String> currentSession = initializeList();
        System.out.println("The song is " + songName);

        while (failedAttempts < 3 && score < 10) {
            String userAnswer = scanner.nextLine().trim();
            if (answers.contains(userAnswer) && !currentSession.contains(userAnswer)) {
                int index = answers.indexOf(userAnswer);
                currentSession.set(index, userAnswer);
                score += 2;
                printInfo(currentSession);
            } else if (answers.contains(userAnswer)) {
                System.out.println("You have already guessed it");
            } else {
                printInfo(currentSession);
                failedAttempts++;
            }
        }

        if (!currentSession.containsAll(answers)) {
            printInfo(answers);
        }
        System.out.println("Your score in this round is " + score);
        return score;
    }

    private static int playRoundThree(boolean firstTime) throws InterruptedException {

        if (firstTime) printRules(3);
        ArrayList<String> answers = getWordsRound3();
        String songName = answers.remove(8);
        String start = answers.remove(0);
        for (int i = 0; i < 2; i++) {
            start = String.join(" ", start, answers.remove(0));
        }


        System.out.println(start);
        int score = 0;
        boolean secondAttempt = false;
        Scanner scanner = new Scanner(System.in);
        Thread.sleep(5000);
        ArrayList<String> currentSession = initializeList();

        while (true) {
            var userAnswers = scanner.nextLine().split(" ");
            if (userAnswers.length != 5) {
                System.out.println("You need to enter 5 words");
            } else {
                for (int i = 0; i < userAnswers.length; i++) {
                    String guess = userAnswers[i];
                    if (guess.equals(answers.get(i)) && !currentSession.get(i).equals(guess)) {
                        currentSession.set(i, guess);
                        score = score + (secondAttempt ? 1 : 3);
                    }
                }
                if (secondAttempt || score == 15) {
                    printInfo(answers);
                    break;
                } else {
                    secondAttempt = true;
                    printInfo(currentSession);
                }
            }
        }

        System.out.println("The song was: " + songName);
        System.out.println("Your score in this round is " + score);
        return score;
    }
    private static ArrayList<String> initializeList() {
        ArrayList<String> result = new ArrayList<>(5);
        for (int i = 1; i < 6; i++) {
            result.add("______" + i + "_____");
        }
        printInfo(result);
        return result;
    }

    private static void printInfo(ArrayList<String> list) {

        list.forEach(s -> System.out.print(s + " "));
        System.out.println();
    }

    private static void printRules(int round) {
        String rules = switch (round) {
            case 1 -> """
                    There are five words hidden from a refrain.
                    You can enter a number to open a word of in that position.
                    If you know what all five words are you can enter them separated by space.
                    For each hidden word you guess you get 2 points""";
            case 2 -> """
                    You are given a name of a song.
                    You need to guess 5 most popular words of that song.
                    You have 3 strikes and each word you guess correctly gives you 2 points.""";
            case 3 -> """
                    You are given three words of a song.
                    You need to write the next 5 words of that song.
                    First time you enter your answer, you are told which words you got correctly
                    and you get 3 points for each.
                    Second time you enter your answer you get 1 point for a correct word.
                    Always enter 5 words and the order matters""";
            default -> "";
        };
        System.out.println(rules);
    }
}
