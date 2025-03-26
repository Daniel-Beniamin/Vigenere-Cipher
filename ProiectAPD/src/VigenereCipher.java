import java.io.*;
import java.nio.file.*;
import java.util.Random;

public class VigenereCipher {
    private static final int NUM_INPUTS = 10;
    private static final String INPUT_DIR = "Inputs";
    private static final String OUTPUT_DIR = "Outputs";
    private static final String RESULTS_DIR = "Results";
    private static final String RESULTS_FILE = RESULTS_DIR + "/results.txt";
    private static final String KEY = "SECURITY";
    private static final long BASE_SIZE = 50;
    private static final Random RANDOM = new Random();

    public static void main(String[] args) {
        createDirectories();
        generateInputs();
        processInputs();
    }

    private static void createDirectories() {
        if (!Files.exists(Paths.get(INPUT_DIR))) {
            new File(INPUT_DIR).mkdirs();
        }
        if (!Files.exists(Paths.get(OUTPUT_DIR))) {
            new File(OUTPUT_DIR).mkdirs();
        }
        if (!Files.exists(Paths.get(RESULTS_DIR))) {
            new File(RESULTS_DIR).mkdirs();
        }
    }

    private static void generateInputs() {
        for (int i = 0; i < NUM_INPUTS; i++) {
            long size = Math.min(BASE_SIZE * (long) Math.pow(10, i), 30_000_000_000L);
            String fileName = INPUT_DIR + "/in" + i + ".txt";

            if (!Files.exists(Paths.get(fileName))) {
                try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileName))) {
                    for (int j = 0; j < size; j++) {
                        char c = (char) ('A' + RANDOM.nextInt(26));
                        writer.write(c);
                    }
                } catch (IOException e) {
                    System.err.println("Error generating input : " + e.getMessage());
                }
            }
        }
    }

    private static void processInputs() {
        try (BufferedWriter resultsWriter = new BufferedWriter(new FileWriter(RESULTS_FILE))) {
            for (int i = 0; i < NUM_INPUTS; i++) {
                String inputFile = INPUT_DIR + "/in" + i + ".txt";
                String outputFile = OUTPUT_DIR + "/out" + i + ".txt";

                long startTime = System.currentTimeMillis();
                encryptFileToOutput(inputFile, outputFile, KEY);
                long endTime = System.currentTimeMillis();

                long duration = endTime - startTime;
                resultsWriter.write("Test #" + i + ": " + duration + " ms\n");
                System.out.println("Test #" + i + " completed in " + duration + " ms");
            }
        } catch (IOException e) {
            System.err.println("Error writing results : " + e.getMessage());
        }
    }

    private static void encryptFileToOutput(String inputFilePath, String outputFilePath, String key) {
        int keyLength = key.length();
        int keyIndex = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {

            int ch;
            while ((ch = reader.read()) != -1) {
                if (ch >= 'A' && ch <= 'Z') {
                    char keyChar = key.charAt(Math.floorMod(keyIndex, keyLength));
                    char encryptedChar = (char) ('A' + (ch - 'A' + keyChar - 'A') % 26);
                    writer.write(encryptedChar);
                    keyIndex++;
                } else {
                    writer.write(ch);
                }
            }
        } catch (IOException e) {
            System.err.println("Encryption error : " + e.getMessage());
        }
    }
}
