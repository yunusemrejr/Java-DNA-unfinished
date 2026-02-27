import java.util.*;

/**
 * Analyzes DNA sequences to find genes and compute bioinformatics statistics.
 */
public class DNAAnalyzer {
    private static final String START_CODON = "ATG";
    private static final String[] STOP_CODONS = {"TAA", "TAG", "TGA"};
    private static final Set<String> STOP_CODON_SET = new HashSet<>(Arrays.asList(STOP_CODONS));

    private final String dnaSequence;
    private List<Gene> genes;

    /**
     * Constructs a DNAAnalyzer for the given DNA sequence.
     *
     * @param dnaSequence The DNA sequence to analyze
     */
    public DNAAnalyzer(String dnaSequence) {
        this.dnaSequence = dnaSequence.toUpperCase();
        this.genes = null;
    }

    /**
     * Finds all genes in the DNA sequence.
     * A gene is defined as a sequence starting with ATG and ending with a stop codon,
     * where the length is divisible by 3 (valid reading frame).
     *
     * @return List of genes found in the sequence
     */
    public List<Gene> findGenes() {
        if (genes != null) {
            return genes;
        }

        genes = new ArrayList<>();
        
        // Find all start codon positions
        List<Integer> startPositions = findCodonPositions(START_CODON);
        
        // For each start position, find the nearest valid stop codon
        for (int startPos : startPositions) {
            Gene gene = findGeneFromStart(startPos);
            if (gene != null) {
                genes.add(gene);
            }
        }

        // Sort genes by start position
        genes.sort(Comparator.comparingInt(Gene::getStartIndex));
        
        return genes;
    }

    /**
     * Finds a gene starting from the given position.
     *
     * @param startPos The position of the start codon
     * @return A Gene object if a valid gene is found, null otherwise
     */
    private Gene findGeneFromStart(int startPos) {
        // Start reading after the start codon
        int readPos = startPos + 3;
        
        // Read codons until we find a stop codon or reach the end
        while (readPos + 2 < dnaSequence.length()) {
            String codon = dnaSequence.substring(readPos, readPos + 3);
            
            if (STOP_CODON_SET.contains(codon)) {
                // Found a stop codon - extract the gene sequence
                String geneSequence = dnaSequence.substring(startPos, readPos + 3);
                return new Gene(startPos, readPos, START_CODON, codon, geneSequence);
            }
            
            // Move to next codon (maintain reading frame)
            readPos += 3;
        }
        
        return null; // No valid stop codon found
    }

    /**
     * Finds all positions of a specific codon in the DNA sequence.
     *
     * @param codon The codon to search for
     * @return List of positions where the codon appears
     */
    private List<Integer> findCodonPositions(String codon) {
        List<Integer> positions = new ArrayList<>();
        int index = dnaSequence.indexOf(codon);
        
        while (index != -1) {
            positions.add(index);
            index = dnaSequence.indexOf(codon, index + 1);
        }
        
        return positions;
    }

    /**
     * Calculates the GC content of the entire DNA sequence.
     * GC content is the percentage of G and C nucleotides.
     *
     * @return GC content as a percentage
     */
    public double calculateGCContent() {
        int gcCount = 0;
        for (char nucleotide : dnaSequence.toCharArray()) {
            if (nucleotide == 'G' || nucleotide == 'C') {
                gcCount++;
            }
        }
        return (gcCount * 100.0) / dnaSequence.length();
    }

    /**
     * Counts the occurrences of each nucleotide in the DNA sequence.
     *
     * @return Map of nucleotide counts
     */
    public Map<Character, Integer> getNucleotideCounts() {
        Map<Character, Integer> counts = new HashMap<>();
        counts.put('A', 0);
        counts.put('T', 0);
        counts.put('G', 0);
        counts.put('C', 0);
        
        for (char nucleotide : dnaSequence.toCharArray()) {
            counts.put(nucleotide, counts.getOrDefault(nucleotide, 0) + 1);
        }
        
        return counts;
    }

    /**
     * Returns the reverse complement of the DNA sequence.
     * A↔T and G↔C
     *
     * @return The reverse complement sequence
     */
    public String getReverseComplement() {
        StringBuilder complement = new StringBuilder();
        
        for (int i = dnaSequence.length() - 1; i >= 0; i--) {
            char nucleotide = dnaSequence.charAt(i);
            switch (nucleotide) {
                case 'A':
                    complement.append('T');
                    break;
                case 'T':
                    complement.append('A');
                    break;
                case 'G':
                    complement.append('C');
                    break;
                case 'C':
                    complement.append('G');
                    break;
                default:
                    complement.append(nucleotide);
            }
        }
        
        return complement.toString();
    }

    /**
     * Finds the longest gene in the sequence.
     *
     * @return The longest gene, or null if no genes found
     */
    public Gene getLongestGene() {
        List<Gene> allGenes = findGenes();
        if (allGenes.isEmpty()) {
            return null;
        }
        
        return allGenes.stream()
                .max(Comparator.comparingInt(Gene::getLength))
                .orElse(null);
    }

    /**
     * Gets statistics about the codon usage in the sequence.
     *
     * @return Map of codon counts
     */
    public Map<String, Integer> getCodonStatistics() {
        Map<String, Integer> codonCounts = new HashMap<>();
        
        for (String stopCodon : STOP_CODONS) {
            codonCounts.put(stopCodon, findCodonPositions(stopCodon).size());
        }
        codonCounts.put(START_CODON, findCodonPositions(START_CODON).size());
        
        return codonCounts;
    }

    /**
     * Generates a comprehensive analysis report.
     *
     * @return Formatted analysis report
     */
    public String generateReport() {
        StringBuilder report = new StringBuilder();
        
        report.append("\n╔═══════════════════════════════════════════════════════════════╗\n");
        report.append("║              DNA SEQUENCE ANALYSIS REPORT                     ║\n");
        report.append("╚═══════════════════════════════════════════════════════════════╝\n\n");
        
        // Sequence information
        report.append("SEQUENCE INFORMATION:\n");
        report.append("─────────────────────────────────────────────────────────────────\n");
        report.append(String.format("  Total Length:     %,d base pairs\n", dnaSequence.length()));
        report.append(String.format("  GC Content:       %.2f%%\n", calculateGCContent()));
        
        // Nucleotide composition
        Map<Character, Integer> nucleotideCounts = getNucleotideCounts();
        report.append("\n  Nucleotide Composition:\n");
        report.append(String.format("    A: %,7d (%.2f%%)    T: %,7d (%.2f%%)\n",
                nucleotideCounts.get('A'), (nucleotideCounts.get('A') * 100.0) / dnaSequence.length(),
                nucleotideCounts.get('T'), (nucleotideCounts.get('T') * 100.0) / dnaSequence.length()));
        report.append(String.format("    G: %,7d (%.2f%%)    C: %,7d (%.2f%%)\n",
                nucleotideCounts.get('G'), (nucleotideCounts.get('G') * 100.0) / dnaSequence.length(),
                nucleotideCounts.get('C'), (nucleotideCounts.get('C') * 100.0) / dnaSequence.length()));
        
        // Codon statistics
        report.append("\nCODON STATISTICS:\n");
        report.append("─────────────────────────────────────────────────────────────────\n");
        Map<String, Integer> codonStats = getCodonStatistics();
        report.append(String.format("  Start Codons (ATG): %,d occurrences\n", codonStats.get(START_CODON)));
        report.append("  Stop Codons:\n");
        for (String stopCodon : STOP_CODONS) {
            report.append(String.format("    %s: %,d occurrences\n", stopCodon, codonStats.get(stopCodon)));
        }
        
        // Gene analysis
        List<Gene> foundGenes = findGenes();
        report.append("\nGENE ANALYSIS:\n");
        report.append("─────────────────────────────────────────────────────────────────\n");
        report.append(String.format("  Total Genes Found: %,d\n", foundGenes.size()));
        
        if (!foundGenes.isEmpty()) {
            Gene longest = getLongestGene();
            int totalGeneLength = foundGenes.stream().mapToInt(Gene::getLength).sum();
            double avgLength = foundGenes.stream().mapToInt(Gene::getLength).average().orElse(0);
            double codingPercentage = (totalGeneLength * 100.0) / dnaSequence.length();
            
            report.append(String.format("  Coding Regions:    %.2f%% of sequence\n", codingPercentage));
            report.append(String.format("  Average Gene Size: %.0f base pairs\n", avgLength));
            report.append(String.format("  Longest Gene:     %,d base pairs (at position %,d)\n",
                    longest.getLength(), longest.getStartIndex()));
        }
        
        report.append("\n");
        return report.toString();
    }

    public String getDnaSequence() {
        return dnaSequence;
    }
}
