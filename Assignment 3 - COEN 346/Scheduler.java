import java.util.ArrayList;

class Scheduler extends Thread {
    private final int[] turnAroundTimes; // Array to store turnaround times of processes
    private final int[] waitingTimes; // Array to store waiting times of processes
    private final int initialQuantum; // Initial quantum for Round Robin scheduling
    private int clock = 0; // Clock to track the current time
    private final ArrayList<Process> processes; // List of processes to be scheduled

    // Constructor to initialize scheduler
    public Scheduler(int initialQuantum, int maxProcessId, ArrayList<Process> processes) {
        this.initialQuantum = initialQuantum;
        this.turnAroundTimes = new int[maxProcessId + 1]; // Index 0 is not used
        this.waitingTimes = new int[maxProcessId + 1]; // Index 0 is not used
        this.processes = processes;
    }

    // Method to execute processes
    @Override
    public void run() {
        boolean executed = false;
        while (!executed && !processes.isEmpty()) {
            Process chosenProcess = null;
            boolean useRoundRobin = false;
            // Check if Round Robin scheduling should be used
            for (Process process : processes) {
                if (!process.isExecutedOnce()) {
                    useRoundRobin = true;
                    break;
                }
            }
            if (useRoundRobin) {
                // Apply Round Robin scheduling for the first execution of each process
                for (Process process : processes) {
                    if (process.getArrivalTime() <= clock) {
                        chosenProcess = process;
                        break;
                    }
                }
            } else {
                // Apply Shortest Job First scheduling for subsequent resumptions
                chosenProcess = shortestJobFirst(processes);
            }
            if (chosenProcess != null) {
                executed = true;
                int remainingExecutionTime = chosenProcess.getExecutionTime();
                int quantum = Math.max((int) Math.ceil(remainingExecutionTime * 0.1), 1);
                quantum = Math.min(quantum, remainingExecutionTime); // Ensure quantum is not greater than remaining execution time
                chosenProcess.execute(quantum, clock);
                clock += quantum;

                if (!chosenProcess.isExecutedOnce()) {
                    chosenProcess.executedOnce = true;
                }

                if (chosenProcess.getExecutionTime() == 0) {
                    turnAroundTimes[Integer.parseInt(chosenProcess.getProcessId())] = clock - chosenProcess.getArrivalTime();
                    processes.remove(chosenProcess);
                } else {
                    processes.remove(chosenProcess);
                    processes.add(chosenProcess);
                }

                // Update waiting time for other processes in the queue
                for (Process process : processes) {
                    if (!process.getProcessId().equals(chosenProcess.getProcessId())) {
                        waitingTimes[Integer.parseInt(process.getProcessId())] += quantum;
                    }
                }
            } else {
                clock++;
            }
        }
    }

    // Method to find the shortest job in the queue
    private Process shortestJobFirst(ArrayList<Process> processes) {
        Process shortestJob = null;
        int shortestBurstTime = Integer.MAX_VALUE;
        for (Process process : processes) {
            if (process.getArrivalTime() <= clock && process.getExecutionTime() < shortestBurstTime) {
                shortestJob = process;
                shortestBurstTime = process.getExecutionTime();
            }
        }
        return shortestJob;
    }

    // Method to print waiting times of processes
    public void printWaitingTimes() {
        System.out.println("------------------------------");
        System.out.println("Waiting Times:");
        for (int i = 1; i < waitingTimes.length; i++) {
            System.out.println("Process " + i + ": " + Math.max(waitingTimes[i], 0));
        }
    }
}