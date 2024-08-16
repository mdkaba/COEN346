/* Mamadou Diao Kaba (27070179)
 * Multithreaded Zero-Day Attack 
 * Winter 2024
 */

public class Main {
    public static void main(String[] args) {
        // Define the file path and vulnerability pattern
        String filePath = "C:\\Users\\mamad\\OneDrive\\Documents\\Winter 2024\\COEN 346\\COEN 346. PA1\\COEN 346. PA1\\DataSet\\vm_1.txt"; // TO BE MODIFIED!!!!!!!!!!!!!!!!!
        String vulnerabilityPattern = "V04K4B63CL5BK0B";

        // Create and start the master thread
        MasterThread masterThread = new MasterThread(filePath, vulnerabilityPattern);
        masterThread.start();

        // Wait for the master thread to finish
        try {
            masterThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Output the number of detected vulnerabilities
        System.out.println("Number of detected vulnerabilities: " + masterThread.getCount());
        System.out.println("output of some lines of the vulnerabilities: " + masterThread.getName());
    }
}
