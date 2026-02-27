/**
 * Represents a gene found in a DNA sequence.
 * A gene consists of a start codon (ATG), a sequence of nucleotides, and a stop codon.
 */
public class Gene {
    private final int startIndex;
    private final int stopIndex;
    private final String startCodon;
    private final String stopCodon;
    private final String sequence;
    private final int length;

    /**
     * Constructs a Gene object.
     *
     * @param startIndex  The index of the start codon in the DNA sequence
     * @param stopIndex   The index of the stop codon in the DNA sequence
     * @param startCodon  The start codon (typically "ATG")
     * @param stopCodon   The stop codon (TAA, TAG, or TGA)
     * @param sequence    The complete gene sequence including codons
     */
    public Gene(int startIndex, int stopIndex, String startCodon, String stopCodon, String sequence) {
        this.startIndex = startIndex;
        this.stopIndex = stopIndex;
        this.startCodon = startCodon;
        this.stopCodon = stopCodon;
        this.sequence = sequence;
        this.length = sequence.length();
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getStopIndex() {
        return stopIndex;
    }

    public String getStartCodon() {
        return startCodon;
    }

    public String getStopCodon() {
        return stopCodon;
    }

    public String getSequence() {
        return sequence;
    }

    public int getLength() {
        return length;
    }

    /**
     * Returns the number of codons in this gene.
     */
    public int getCodonCount() {
        return length / 3;
    }

    /**
     * Calculates the GC content percentage of this gene.
     * GC content is the percentage of G and C nucleotides.
     */
    public double getGCContent() {
        int gcCount = 0;
        for (char nucleotide : sequence.toCharArray()) {
            if (nucleotide == 'G' || nucleotide == 'C') {
                gcCount++;
            }
        }
        return (gcCount * 100.0) / length;
    }

    @Override
    public String toString() {
        return String.format("Gene[%d-%d] %s...%s (%d bp, %.1f%% GC)",
                startIndex, stopIndex + 2, startCodon, stopCodon, length, getGCContent());
    }

    /**
     * Returns a detailed string representation of the gene.
     */
    public String toDetailedString() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════════════\n");
        sb.append(String.format("  Gene Position: %d - %d\n", startIndex, stopIndex + 2));
        sb.append(String.format("  Start Codon:   %s (at position %d)\n", startCodon, startIndex));
        sb.append(String.format("  Stop Codon:    %s (at position %d)\n", stopCodon, stopIndex));
        sb.append(String.format("  Length:        %d base pairs (%d codons)\n", length, getCodonCount()));
        sb.append(String.format("  GC Content:    %.2f%%\n", getGCContent()));
        sb.append("  Sequence:      ");
        
        if (length <= 60) {
            sb.append(sequence);
        } else {
            sb.append(sequence.substring(0, 30))
              .append("...")
              .append(sequence.substring(length - 27));
        }
        sb.append("\n");
        sb.append("═══════════════════════════════════════════════════════════════\n");
        return sb.toString();
    }
}
