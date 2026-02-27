import java.io.*;
import java.nio.file.*;

/**
 * Represents and validates a DNA sequence.
 * Provides utilities for loading DNA from various sources.
 */
public class DNASequence {
    private final String sequence;
    private final String source;

    /**
     * Constructs a DNASequence from a string.
     *
     * @param sequence The DNA sequence
     * @param source   Description of the sequence source
     * @throws IllegalArgumentException if the sequence is invalid
     */
    public DNASequence(String sequence, String source) {
        if (sequence == null || sequence.isEmpty()) {
            throw new IllegalArgumentException("DNA sequence cannot be null or empty");
        }
        
        String cleaned = cleanSequence(sequence);
        validateSequence(cleaned);
        
        this.sequence = cleaned.toUpperCase();
        this.source = source;
    }

    /**
     * Loads a DNA sequence from a file.
     *
     * @param filePath Path to the file containing the DNA sequence
     * @return DNASequence object
     * @throws IOException if the file cannot be read
     */
    public static DNASequence fromFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        
        if (!Files.exists(path)) {
            throw new FileNotFoundException("File not found: " + filePath);
        }
        
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Skip FASTA header lines
                if (!line.startsWith(">") && !line.startsWith(";")) {
                    content.append(line.trim());
                }
            }
        }
        
        return new DNASequence(content.toString(), "File: " + filePath);
    }

    /**
     * Creates a DNASequence from a string.
     *
     * @param sequence The DNA sequence string
     * @return DNASequence object
     */
    public static DNASequence fromString(String sequence) {
        return new DNASequence(sequence, "Direct input");
    }

    /**
     * Cleans the sequence by removing whitespace and non-nucleotide characters.
     *
     * @param sequence The raw sequence
     * @return Cleaned sequence
     */
    private String cleanSequence(String sequence) {
        // Remove whitespace, numbers, and common formatting characters
        return sequence.replaceAll("[\\s\\d\\-_]", "");
    }

    /**
     * Validates that the sequence contains only valid nucleotides (A, T, G, C).
     *
     * @param sequence The sequence to validate
     * @throws IllegalArgumentException if the sequence contains invalid characters
     */
    private void validateSequence(String sequence) {
        for (int i = 0; i < sequence.length(); i++) {
            char nucleotide = Character.toUpperCase(sequence.charAt(i));
            if (nucleotide != 'A' && nucleotide != 'T' && nucleotide != 'G' && nucleotide != 'C') {
                throw new IllegalArgumentException(
                    String.format("Invalid nucleotide '%c' at position %d. Only A, T, G, C are allowed.",
                        sequence.charAt(i), i));
            }
        }
    }

    /**
     * Gets the DNA sequence.
     *
     * @return The DNA sequence string
     */
    public String getSequence() {
        return sequence;
    }

    /**
     * Gets the source description.
     *
     * @return The source description
     */
    public String getSource() {
        return source;
    }

    /**
     * Gets the length of the sequence.
     *
     * @return The sequence length
     */
    public int getLength() {
        return sequence.length();
    }

    /**
     * Saves the DNA sequence to a file in FASTA format.
     *
     * @param filePath Path where the file should be saved
     * @param header   FASTA header description
     * @throws IOException if the file cannot be written
     */
    public void saveToFile(String filePath, String header) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath))) {
            writer.write(">" + header);
            writer.newLine();
            
            // Write sequence in lines of 80 characters (FASTA standard)
            int lineLength = 80;
            for (int i = 0; i < sequence.length(); i += lineLength) {
                int endIndex = Math.min(i + lineLength, sequence.length());
                writer.write(sequence.substring(i, endIndex));
                writer.newLine();
            }
        }
    }

    @Override
    public String toString() {
        if (sequence.length() <= 100) {
            return sequence;
        }
        return sequence.substring(0, 50) + "..." + sequence.substring(sequence.length() - 47) +
               String.format(" (%,d bp)", sequence.length());
    }

    /**
     * Returns a preview of the sequence.
     *
     * @param length Number of characters to show from start and end
     * @return Preview string
     */
    public String preview(int length) {
        if (sequence.length() <= length * 2) {
            return sequence;
        }
        return sequence.substring(0, length) + "..." + sequence.substring(sequence.length() - length);
    }
}
