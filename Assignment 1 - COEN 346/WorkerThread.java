/* Mamadou Diao Kaba (27070179)
 * Multithreaded Zero-Day Attack 
 * Winter 2024
 */

import java.util.List;

public class WorkerThread extends Thread {
    private List<String> logStatements;
    private String vulnerabilityPattern;
    private MasterThread masterThread;
    private LevenshteinDistance levenshteinDistance;

    public WorkerThread(List<String> logStatements, String vulnerabilityPattern, MasterThread masterThread) {
        this.logStatements = logStatements;
        this.vulnerabilityPattern = vulnerabilityPattern;
        this.masterThread = masterThread;
        this.levenshteinDistance = new LevenshteinDistance();
    }

    @Override
    public void run() {
        for (String logStatement : logStatements) {
            // Iterate through each substring of the log statement with the length of the vulnerability pattern
            for (int i = 0; i <= logStatement.length() - vulnerabilityPattern.length(); i++) {
                String substring = logStatement.substring(i, i + vulnerabilityPattern.length());
                // Calculate the Levenshtein distance between the substring and the vulnerability pattern
                int distance = levenshteinDistance.Calculate(substring, vulnerabilityPattern);
                // Check if the change ratio indicates an acceptable change
                if (levenshteinDistance.isAcceptable_change()) {
                    // If it does, increment the count in the master thread
                    masterThread.incrementCount();
                    // Break the loop since one occurrence is enough to count
                    break;
                }
            }
        }
    }
}
