import java.util.PriorityQueue;

/**
 * Class Monitor
 * To synchronize dining philosophers.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */
public class Monitor
{
	/*
	 * ------------
	 * Data members
	 * ------------
	 */
	private enum STATE {THINKING, HUNGRY, EATING};
	// number of philosophers
	private int numPhilosophers;
	// array that will hold the states
	private STATE[] states;
	// boolean to check if a philosopher is talking
	private boolean is_talking;

	private PriorityQueue<Integer> hungryList;
	private int availablePepperShakers = 2; // Assuming there are 2 pepper shakers


	/**
	 * Constructor
	 */
	public Monitor(int piNumberOfPhilosophers)
	{
		// TODO: set appropriate number of chopsticks based on the # of philosophers
		numPhilosophers = piNumberOfPhilosophers;
		states = new STATE[numPhilosophers];

		// initialize all philosophers to thinking
		for(int i = 0; i < numPhilosophers; i++) {
			states[i] = STATE.THINKING;
		}

		/**
		 * Creating a new hungryList for the constructor
		 */
		hungryList = new PriorityQueue<>();

		// initially, no philosopher is talking
		is_talking = false;
	}

	/*
	 * -------------------------------
	 * User-defined monitor procedures
	 * -------------------------------
	 */


	public synchronized void checkActions(int philosopherPos){
		try { 
			while(true){

				/* Here we have the (philosopherPos +1 is left-handed side and right-handed side
				being philosopherPos + (numPhilosophers - 1)) % numPhilosophers>.
				 */

				if(states[(philosopherPos + 1) % numPhilosophers] != STATE.EATING && states[(philosopherPos + (numPhilosophers - 1)) % numPhilosophers] != STATE.EATING
						&& states[philosopherPos] == STATE.HUNGRY){
					states[philosopherPos] = STATE.EATING; // We know here that the philosopher can start eating
					break;
				}
				else {
					wait();
				}
			}
		}

		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Grants request (returns) to eat when both chopsticks/forks are available.
	 * Else forces the philosopher to wait()
	 */
	public synchronized void pickUp(final int piTID)
	{
		int PhilosopherPos = piTID - 1;

		// changing state to hungry
		states[PhilosopherPos] = STATE.HUNGRY;

		// adding the philosopher to the hungry list
		hungryList.add(piTID);

		//test the philosopher
		checkActions(PhilosopherPos);

		// remove the philosopher from the hungry list since they are already eating
		hungryList.remove();
	}

	/**
	 * When a given philosopher's done eating, they put the chopstiks/forks down
	 * and let others know they are available.
	 */
	public synchronized void putDown(final int piTID)
	{
		int philosopherPos = piTID - 1;

		// changing state to thinking
		states[philosopherPos] = STATE.THINKING;

		// Notifies the other threads, so that they may be able to see if they are able to see the actions of their neighbors
		notifyAll();
	}

	/**
	 * Only one philosopher at a time is allowed to philosophy
	 * (while she is not eating).
	 */
	public synchronized void requestTalk()
	{
		if(is_talking) {
			try {

				/**
				 * Wait until another philosopher is still talking, when he's done, you request to talk
				 */
				wait();
				requestTalk();
			} 

			catch(InterruptedException e) {
				System.out.println("A philosopher is currently speaking something very useful. Please wait to philosophy");
			}

			// the philosopher is talking
			is_talking = true;
		}
	}

	/**
	 * When one philosopher is done talking stuff, others
	 * can feel free to start talking.
	 */
	public synchronized void endTalk()
	{
		// the philosopher is no longer talking
		is_talking = false;

		// Notify all the other threads that you are the one done talking, and now they have a chance to speak as well.
		notifyAll();
	}

	// Philosophers call this method when they wish to eat and require a pepper shaker
    public synchronized void pickUpChopsticksAndPepperShaker(final int piTID) {
        int philosopherPosition = piTID - 1;
        states[philosopherPosition] = STATE.HUNGRY;
        checkPhilosopher(philosopherPosition);
        // Wait until a pepper shaker is available
        while (availablePepperShakers == 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Take one of the pepper shakers
        availablePepperShakers--;
        // Allow philosopher to eat
    }

    // Philosophers call this method when they have finished eating and release their pepper shaker
    public synchronized void putDownChopsticksAndPepperShaker(final int piTID) {
        int philosopherPosition = piTID - 1;
        states[philosopherPosition] = STATE.THINKING;
        // Return the pepper shaker
        availablePepperShakers++;
        notifyAll(); // Notify others that a pepper shaker is now available
        checkPhilosopher((philosopherPosition + 1) % numPhilosophers);
        checkPhilosopher((philosopherPosition + numPhilosophers - 1) % numPhilosophers);
    }

    private void checkPhilosopher(int philosopherPosition) {
        // Check if the philosopher can eat (existing logic), now including pepper shaker availability
        if (states[(philosopherPosition + 1) % numPhilosophers] != STATE.EATING &&
            states[(philosopherPosition + (numPhilosophers - 1)) % numPhilosophers] != STATE.EATING &&
            states[philosopherPosition] == STATE.HUNGRY) {
            // Allow philosopher to eat
            states[philosopherPosition] = STATE.EATING;
            notifyAll(); // Notify the philosopher that it can start eating
        }
    }
}

// EOF
