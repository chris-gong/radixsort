package apps;

import java.io.IOException;
import java.util.Scanner;

import structures.Node;

/**
 * This class sorts a given list of strings which represent numbers in
 * the given radix system. For instance, radix=10 means decimal numbers;
 * radix=16 means hexadecimal numbers. 
 * 
 * @author ru-nb-cs112
 */
public class Radixsort {

	/**
	 * Master list that holds all items, starting with input, and updated after every pass
	 * of the radixsort algorithm. Holds sorted result after the final pass. This is a
	 * circular linked list in which every item is stored in its textual string form (even
	 * though the items represent numbers). This masterListRear field points to the last 
	 * node in the CLL.
	 */
	Node<String> masterListRear;
	
	/**
	 * Array of linked lists that holds the digit-wise distribution of the items during
	 * each pass of the radixsort algorithm. 
	 */
	Node<String>[] buckets;
	
	/** 
	 * The sort radix, defaults to 10.
	 */
	int radix=10;
	
	/**
	 * Initializes this object with the given radix (10 or 16)
	 * 
	 * @param radix
	 */
	public Radixsort() {
		masterListRear = null;
		buckets = null;
	}
	
	/**
	 * Sorts the items in the input file, and returns a CLL containing the sorted result
	 * in ascending order. The first line in the input file is the radix. Every subsequent
	 * line is a number, to be read in as a string.
	 * 
	 * The items in the input are first read and stored in the master list, which is a CLL that is referenced
	 * by the masterListRear field. Next, the max number of digits in the items is determined. Then, 
	 * scatter and gather are called, for each pass through the items. Pass 0 is for the least
	 * significant digit, pass 1 for the second-to-least significant digit, etc. After each pass,
	 * the master list is updated with items in the order determined at the end of that pass.
	 * 
	 * NO NEW NODES are created in the sort process - the nodes of the master list are recycled
	 * through all the intermediate stages of the sorting process.
	 * 
	 * @param sc Scanner that points to the input file of radix + items to be sorted
	 * @return Sorted (in ascending order) circular list of items
	 * @throws IOException If there is an exception in reading the input file
	 */
	public Node<String> sort(Scanner sc) 
	throws IOException {
		// first line is radix
		if (!sc.hasNext()) { // empty file, nothing to sort
			return null;
		}
		
		// read radix from file, and set up buckets for linked lists
		radix = sc.nextInt();
		buckets = (Node<String>[])new Node[radix];
		
		// create master list from input
		createMasterListFromInput(sc);
		// find the string with the maximum length
		int maxDigits = getMaxDigits();
		
		for (int i=0; i < maxDigits; i++) {
			scatter(i);
			gather();
		}
		
		return masterListRear;
	}
	
	/**
	 * Reads entries to be sorted from input file and stores them as 
	 * strings in the master CLL (pointed by the instance field masterListRear, 
	 * in the order in which they are read. In other words, the first entry in the linked 
	 * list is the first entry in the input, the second entry in the linked list is the 
	 * second entry in the input, and so on. 
	 * 
	 * @param sc Scanner pointing to the input file
	 * @throws IOException If there is any error in reading the input
	 */
	public void createMasterListFromInput(Scanner sc) 
	throws IOException {
		// WRITE YOUR CODE HERE
		//???????????? below, have to call .nextLine() twice? 
		//otherwise won't work
		String numInput = sc.nextLine();
		numInput = sc.nextLine();
		
		//creation of first node
		//creation of object occurs first, so declaring new node
		//and its next pointer must be separated into two statements
		masterListRear = new Node<String>(numInput,null);
		masterListRear.next = masterListRear;
		
		//extra node used to keep track of the node right before the tail
		Node<String> prev = masterListRear;
		//if there are anymore numbers in the text file
		while(sc.hasNextLine()){
			//reset scanner input for next iteration
			numInput = sc.nextLine();
			//tail.next is front!
			//must reset the tail as the newest element added
			masterListRear = new Node<String>(numInput,masterListRear.next);
			//set the node before it to point to the tail
			prev.next = masterListRear;
			prev = masterListRear;
			
		}
		
	}
	
	/**
	 * Determines the maximum number of digits over all the entries in the master list
	 * 
	 * @return Maximum number of digits over all the entries
	 */
	public int getMaxDigits() {
		int maxDigits = masterListRear.data.length();
		Node<String> ptr = masterListRear.next;
		while (ptr != masterListRear) {
			int length = ptr.data.length();
			if (length > maxDigits) {
				maxDigits = length;
			}
			ptr = ptr.next;
		}
		return maxDigits;
	}
	
	/**
	 * Scatters entries of master list (referenced by instance field masterListReat) 
	 * to buckets for a given pass.
	 * 
	 * Passes are digit by digit, starting with the rightmost digit -
	 * the rightmost digit is the "0-th", i.e. pass=0 for rightmost digit, pass=1 for 
	 * second to rightmost, and so on. 
	 * 
	 * Each digit is extracted as a character, 
	 * then converted into the appropriate numeric value in the given radix
	 * using the java.lang.Character.digit(char ch, int radix) method
	 * 
	 * @param pass Pass is 0 for rightmost digit, 1 for second to rightmost, etc
	 */
	public void scatter(int pass) {
		// WRITE YOUR CODE HERE
		
		Node<String> curr = masterListRear.next;
		Node<String> next = curr.next;
		Node<String> endCondition = curr;
		do{
			if(curr.data.length()-1 < pass){
				if(buckets[0]==null){
					buckets[0] = curr;
					buckets[0].next = buckets[0];
					curr = next;
					next = next.next;
				}
				else{
					Node<String> front = buckets[0].next;
					buckets[0].next = curr;
					buckets[0] = curr;
					buckets[0].next = front;
					curr = next;
					next = next.next;
				}
			}
			else{
				if(buckets[Character.digit(curr.data.charAt(curr.data.length()-pass-1), radix)]==null){
					buckets[Character.digit(curr.data.charAt(curr.data.length()-pass-1), radix)] = curr;
					buckets[Character.digit(curr.data.charAt(curr.data.length()-pass-1), radix)].next = buckets[Character.digit(curr.data.charAt(curr.data.length()-pass-1), radix)];
					curr = next;
					next = next.next;
				}
				else{
					Node<String> front = buckets[Character.digit(curr.data.charAt(curr.data.length()-pass-1), radix)].next;
					buckets[Character.digit(curr.data.charAt(curr.data.length()-pass-1), radix)].next = curr;
					buckets[Character.digit(curr.data.charAt(curr.data.length()-pass-1), radix)] = curr;
					buckets[Character.digit(curr.data.charAt(curr.data.length()-pass-1), radix)].next = front;
					curr = next;
					next = next.next;
				}
				
			}
			
		}while(curr!=endCondition);
		
	}

	/**
	 * Gathers all the CLLs in all the buckets into the master list, referenced
	 * by the instance field masterListRear
	 * 
	 * @param buckets Buckets of CLLs
	 */
	public void gather() {
		// WRITE YOUR CODE HERE
		Node<String> masterFront=null;
		
		boolean firstBucketHasBeenFound = false;
		boolean newMasterListIsEmpty = true;
		for(int i = 0; i < radix;i++){
			//iterating through each bucket
			//if bucket list is empty then move on to next bucket list
			if(buckets[i]==null){
				continue;
			}
			//we need a reference to the masterFront in order to properly reset the tail
			//after each iteration
			
			if(!firstBucketHasBeenFound){
				//if we have found our first bucket node, then set it to the masterFront for
				//later reference when resetting the tail.next aka the front of the masterList
				masterFront = buckets[i].next;
				firstBucketHasBeenFound = true;
				
				
			}
				//must update curr as we iterate through each bucket circular linked list
			Node<String> curr = buckets[i].next;
			Node<String> bucketFront = curr;
				
			do{
				
				//if there is at least one element in new master list, then you have to set
				//the current tail's next variable to the node that is about to be the new tail
				//then, continue as follows
				//first step, set a new tail in the master list
				//second step, reset current as we iterate through the bucket list
				//last step, now reset the front in the master list
				if(newMasterListIsEmpty){
					masterListRear = curr;
					curr = curr.next;
					masterListRear.next = masterFront;
					newMasterListIsEmpty = false;
				}
				else{
					masterListRear.next = curr;
					masterListRear = curr;
					curr = curr.next;
					masterListRear.next = masterFront;
				}
				
			}while(curr != bucketFront);
			//reset ENTIRE buckets circular linked list 
			//in preparation for the next scatter cycle
			//remember that the buckets array should be completely empty before each scatter call
			buckets[i] = null;
		}
		
	}	
	
}

