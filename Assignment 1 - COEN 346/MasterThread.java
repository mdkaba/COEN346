/* Mamadou Diao Kaba (27070179)
 * Multithreaded Zero-Day Attack 
 * Winter 2024
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MasterThread extends Thread {
    // Attributes
    private String filePath; 
    private String vulnerabilityPattern; 
    private int workerNumber; 
    private int count; 
    private double avg; 
    private double approximateAvg; 
    private List<String> logStatements; 

    // Constructor
    public MasterThread(String filePath, String vulnerabilityPattern) {
        this.filePath = filePath;
        this.vulnerabilityPattern = vulnerabilityPattern;
        this.workerNumber = 2; 
        this.count = 0; 
        this.avg = 0.0; 
        this.approximateAvg = 0.0; 
        this.logStatements = new ArrayList<>(); 

        try {
            // Load log statements from file
            loadLogStatements();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to load log statements from file
    private void loadLogStatements() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        // Read each line from the file and add it to the logStatements list
        while ((line = reader.readLine()) != null) {
            logStatements.add(line);
        }
        reader.close();
    }

    // Run method of the master thread
    @Override
    public void run() {
        while (true) {
            // Create a list to store worker threads
            List<WorkerThread> workerThreads = new ArrayList<>();
            // Divide the log statements into sublists and create worker threads for each sublist
            for (int i = 0; i < workerNumber; i++) {
                int startIndex = i * (logStatements.size() / workerNumber);
                int endIndex = (i + 1) * (logStatements.size() / workerNumber);
                // Get a sublist of log statements for the current worker thread
                List<String> subLogStatements = logStatements.subList(startIndex, endIndex);
                // Create a new worker thread with the sublist of log statements
                WorkerThread workerThread = new WorkerThread(subLogStatements, vulnerabilityPattern, this);
                workerThreads.add(workerThread); // Add the worker thread to the list
                workerThread.start();
            }

            // Wait for all worker threads to finish execution
            for (WorkerThread workerThread : workerThreads) {
                try {
                    workerThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            updateApproximateAvg();

            // If the difference between the current average and approximate average is greater than 20% of the average,
            // sleep for 2000 milliseconds and update the worker number
            if (Math.abs(avg - approximateAvg) > 0.2 * avg) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                workerNumber += 2;
                avg = approximateAvg;
            } else {
                break;
            }
        }
    }

    // Method to update the approximate average
    private synchronized void updateApproximateAvg() {
        double totalVulnerabilities = count;
        double totalLines = logStatements.size();
        approximateAvg = totalVulnerabilities / totalLines;
    }

    // Method to increment the count of detected vulnerabilities
    public synchronized void incrementCount() {
        count++;
    }

    // Getter method for the count attribute
    public int getCount() {
        return count;
    }
}


