import java.io.*;
import java.util.ArrayList;

public class ProcessScheduling {
    // Method to read processes from input file
    public static ArrayList<Process> readProcesses(String inputFilePath) {
        ArrayList<Process> processes = new ArrayList<>();
        int processID = 1;

        try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(" ");
                int arrivalTime = Integer.parseInt(tokens[0]);
                int burstTime = Integer.parseInt(tokens[1]);
                Process process = new Process(String.valueOf(processID), arrivalTime, burstTime);
                processes.add(process);
                processID++;
            }
            // Print the details of each process read from the file
            for (Process process : processes) {
                System.out.println("Process ID: " + process.getProcessId() + ", Arrival Time: " + process.getArrivalTime() + ", Burst Time: " + process.getExecutionTime());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return processes;
    }

    // Method to run the scheduler
    public static void runScheduler(Scheduler scheduler, ArrayList<Process> processes) {
        while (!processes.isEmpty()) {
            scheduler.run();
        }
    }

    // Main method
    public static void main(String[] args) {
        // Read processes from input file
        ArrayList<Process> processesList = readProcesses("src/input.txt");
        // Initialize scheduler with quantum and number of processes
        Scheduler scheduler = new Scheduler(1, processesList.size(),processesList);
        // Redirect output to a stream for writing to file
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        // Run the scheduler
        runScheduler(scheduler, processesList);
        // Print waiting times of processes
        scheduler.printWaitingTimes();

        // Flush the output stream and restore the standard output
        System.out.flush();
        System.setOut(old);

        // Write output to file
        try (PrintStream out = new PrintStream(new FileOutputStream("src/output.txt"))) {
            out.print(baos.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}