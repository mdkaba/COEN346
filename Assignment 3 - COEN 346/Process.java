class Process extends Thread {
    private final String processId; // Process ID
    private final int arrivalTime; // Arrival time of the process
    private int executionTime; // Remaining burst time of the process
    private boolean started; // Flag to indicate if the process has started execution
    public boolean executedOnce; // Flag to indicate if the process has executed at least once

    // Constructor to initialize the process
    public Process(String processId, int arrivalTime, int executionTime) {
        this.processId = processId;
        this.arrivalTime = arrivalTime;
        this.executionTime = executionTime;
        this.started = false;
        this.executedOnce = false;
    }

    // Getter method to retrieve burst time of the process
    public int getExecutionTime() {
        return executionTime;
    }

    // Getter method to retrieve arrival time of the process
    public int getArrivalTime() {
        return arrivalTime;
    }

    // Getter method to retrieve process ID
    public String getProcessId() {
        return processId;
    }

    // Getter method to check if the process has executed at least once
    public boolean isExecutedOnce() {
        return executedOnce;
    }

    // Method to execute the process for a certain duration
    public synchronized void execute(int executeTime, int currentTime) {
        if (!started) {
            // If the process is starting for the first time, print a start message
            System.out.println("Time " + currentTime + ", Process " + processId + ", Started");
            started = true;
        }
        try {
            // Simulate process execution by sleeping for the specified duration
            Thread.sleep(executeTime);
        } catch (InterruptedException e) {
            // If the execution is interrupted, print an error message
            System.err.println("Process " + processId + " interrupted");
            return;
        }
        // Update the remaining burst time of the process after execution
        executionTime -= executeTime;
        if (executionTime == 0) {
            // If the burst time becomes zero, print finish message
            System.out.println("Time " + (currentTime + executeTime - 1) + ", Process " + processId + ", Resumed");
            System.out.println("Time " + (currentTime + executeTime) + ", Process " + processId + ", Paused");
            System.out.println("Time " + (currentTime + executeTime) + ", Process " + processId + ", Finished");
        } else {
            // If burst time is not zero, print pause message
            System.out.println("Time " + (currentTime + executeTime - 1) + ", Process " + processId + ", Resumed");
            System.out.println("Time " + (currentTime + executeTime) + ", Process " + processId + ", Paused");
        }
    }

    // Method to run the process
    @Override
    public void run() {
        execute(executionTime, 0);
    }
}