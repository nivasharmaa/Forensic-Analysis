package forensic;


/**
 * This class represents a forensic analysis system that manages DNA data using
 * BSTs.
 * Contains methods to create, read, update, delete, and flag profiles.
 * 
 * @author Kal Pandit
 */
public class ForensicAnalysis {

    private TreeNode treeRoot;            // BST's root
    private String firstUnknownSequence;
    private String secondUnknownSequence;

    public ForensicAnalysis () {
        treeRoot = null;
        firstUnknownSequence = null;
        secondUnknownSequence = null;
    }

    /**
     * Builds a simplified forensic analysis database as a BST and populates unknown sequences.
     * The input file is formatted as follows:
     * 1. one line containing the number of people in the database, say p
     * 2. one line containing first unknown sequence
     * 3. one line containing second unknown sequence
     * 2. for each person (p), this method:
     * - reads the person's name
     * - calls buildSingleProfile to return a single profile.
     * - calls insertPerson on the profile built to insert into BST.
     *      Use the BST insertion algorithm from class to insert.
     * 
     * DO NOT EDIT this method, IMPLEMENT buildSingleProfile and insertPerson.
     * 
     * @param filename the name of the file to read from
     */
    public void buildTree(String filename) {
        // DO NOT EDIT THIS CODE
        StdIn.setFile(filename); // DO NOT remove this line

        // Reads unknown sequences
        String sequence1 = StdIn.readLine();
        firstUnknownSequence = sequence1;
        String sequence2 = StdIn.readLine();
        secondUnknownSequence = sequence2;
        
        int numberOfPeople = Integer.parseInt(StdIn.readLine()); 

        for (int i = 0; i < numberOfPeople; i++) {
            // Reads name, count of STRs
            String fname = StdIn.readString();
            String lname = StdIn.readString();
            String fullName = lname + ", " + fname;
            // Calls buildSingleProfile to create
            Profile profileToAdd = createSingleProfile();
            // Calls insertPerson on that profile: inserts a key-value pair (name, profile)
            insertPerson(fullName, profileToAdd);
        }
    }

    /** 
     * Reads ONE profile from input file and returns a new Profile.
     * Do not add a StdIn.setFile statement, that is done for you in buildTree.
    */
    public Profile createSingleProfile() {

        // WRITE YOUR CODE HERE
        // Read the number of STRs associated with the person
            int numSTRs = StdIn.readInt();

            // Create an array of STR objects of length numSTRs
            STR[] strArray = new STR[numSTRs];

            // For each STR:
            for (int index = 0; index < numSTRs; index++) {
                // Read the STR name and number of occurrences
                String strName = StdIn.readString();
                int strOccurrences = StdIn.readInt();
                
                // Create a new STR object and add it to the next open space in the array
                strArray[index] = new STR(strName, strOccurrences);
            }

            // Create a Profile object using the data you just read and return it
            return new Profile(strArray);

        
    }

    /**
     * Inserts a node with a new (key, value) pair into
     * the binary search tree rooted at treeRoot.
     * 
     * Names are the keys, Profiles are the values.
     * USE the compareTo method on keys.
     * 
     * @param newProfile the profile to be inserted
     */
    public void insertPerson(String name, Profile newProfile) {

        // WRITE YOUR CODE HERE
        // Create a new TreeNode with the given name and profile
        TreeNode newNode = new TreeNode(name, newProfile, null, null);

        // If the tree is empty, set the new node as the root
        if (treeRoot == null) {
            treeRoot = newNode;
            return;
        }

        // Find the appropriate position for insertion
        TreeNode current = treeRoot;
        TreeNode parent = null;
        while (true) {
            parent = current;
            // Compare the names using compareTo method
            int comparison = name.compareTo(current.getName());
            if (comparison < 0) {
                current = current.getLeft();
                if (current == null) {
                    parent.setLeft(newNode);
                    return;
                }
            } else if (comparison > 0) {
                current = current.getRight();
                if (current == null) {
                    parent.setRight(newNode);
                    return;
                }
            } else {
                
                return;
            }
        }


    }

    /**
     * Finds the number of profiles in the BST whose interest status matches
     * isOfInterest.
     *
     * @param isOfInterest the search mode: whether we are searching for unmarked or
     *                     marked profiles. true if yes, false otherwise
     * @return the number of profiles according to the search mode marked
     */
    public int getMatchingProfileCount(boolean isOfInterest) {
        
        // WRITE YOUR CODE HERE
        return getMatchingProfileCountRecursive(treeRoot, isOfInterest);
    }

    private int getMatchingProfileCountRecursive(TreeNode node, boolean isOfInterest) {    
        if (node == null) {
            return 0;
        }
        
        int matchingCount = 0;
        Profile currentProfile = node.getProfile();
        if (currentProfile != null && currentProfile.getMarkedStatus() == isOfInterest) {
            matchingCount++;
        }
        matchingCount += getMatchingProfileCountRecursive(node.getLeft(), isOfInterest);
        matchingCount += getMatchingProfileCountRecursive(node.getRight(), isOfInterest);
        return matchingCount;
        
    }

    /**
     * Helper method that counts the # of STR occurrences in a sequence.
     * Provided method - DO NOT UPDATE.
     * 
     * @param sequence the sequence to search
     * @param STR      the STR to count occurrences of
     * @return the number of times STR appears in sequence
     */
    private int numberOfOccurrences(String sequence, String STR) {
        
        // DO NOT EDIT THIS CODE
        
        int repeats = 0;
        // STRs can't be greater than a sequence
        if (STR.length() > sequence.length())
            return 0;
        
            // indexOf returns the first index of STR in sequence, -1 if not found
        int lastOccurrence = sequence.indexOf(STR);
        
        while (lastOccurrence != -1) {
            repeats++;
            // Move start index beyond the last found occurrence
            lastOccurrence = sequence.indexOf(STR, lastOccurrence + STR.length());
        }
        return repeats;
    }

    /**
     * Traverses the BST at treeRoot to mark profiles if:
     * - For each STR in profile STRs: at least half of STR occurrences match (round
     * UP)
     * - If occurrences THROUGHOUT DNA (first + second sequence combined) matches
     * occurrences, add a match
     */
    public void flagProfilesOfInterest() {

        // WRITE YOUR CODE HERE
        if (treeRoot == null) {
            return;
        }
        
        forensic.Queue<TreeNode> nodeQueue = new forensic.Queue<>();
        nodeQueue.enqueue(treeRoot);
        while (!nodeQueue.isEmpty()) {
            TreeNode currentNode = nodeQueue.dequeue();
            Profile currentProfile = currentNode.getProfile();
            if (currentProfile != null) {
                STR[] profileSTRs = currentProfile.getStrs();
                int matchingSTRCount = 0;
                for (STR currentSTR : profileSTRs) {
                    int profileOccurrences = currentSTR.getOccurrences();
                    int combinedOccurrences = numberOfOccurrences(firstUnknownSequence, currentSTR.getStrString()) + numberOfOccurrences(secondUnknownSequence, currentSTR.getStrString());
                    if (combinedOccurrences == profileOccurrences) {
                        matchingSTRCount++;
                    }
                }
                if (matchingSTRCount >= (profileSTRs.length + 1) / 2) {
                    currentProfile.setInterestStatus(true);
                }
            }
            if (currentNode.getLeft() != null) {
                nodeQueue.enqueue(currentNode.getLeft());
            }
            if (currentNode.getRight() != null) {
                nodeQueue.enqueue(currentNode.getRight());
            }
        }
        
    }

    /**
     * Uses a level-order traversal to populate an array of unmarked Strings representing unmarked people's names.
     * - USE the getMatchingProfileCount method to get the resulting array length.
     * - USE the provided Queue class to investigate a node and enqueue its
     * neighbors.
     * 
     * @return the array of unmarked people
     */
    public String[] getUnmarkedPeople() {

        // WRITE YOUR CODE HERE
        int unmatchedCount = getMatchingProfileCount(false); 
        String[] unmatchedNames = new String[unmatchedCount]; 

        if (unmatchedCount == 0) {
            return unmatchedNames; 
        }

        Queue<TreeNode> nodeQueue = new Queue<>(); 
        nodeQueue.enqueue(treeRoot); 
        int index = 0;
        while (!nodeQueue.isEmpty()) {
            TreeNode currentNode = nodeQueue.dequeue(); 
            Profile currentProfile = currentNode.getProfile();
            if (currentProfile != null && !currentProfile.getMarkedStatus()) { 
                unmatchedNames[index++] = currentNode.getName(); 
            }
            if (currentNode.getLeft() != null) {
                nodeQueue.enqueue(currentNode.getLeft());
            }
            if (currentNode.getRight() != null) {
                nodeQueue.enqueue(currentNode.getRight());
            }
        }
        return unmatchedNames;
    }

    /**
     * Removes a SINGLE node from the BST rooted at treeRoot, given a full name (Last, First)
     * This is similar to the BST delete we have seen in class.
     * 
     * If a profile containing fullName doesn't exist, do nothing.
     * You may assume that all names are distinct.
     * 
     * @param fullName the full name of the person to delete
     */
    public void removePerson(String fullName) {
        // WRITE YOUR CODE HERE
        if (treeRoot == null) {
            return;
        }
        TreeNode parentNode = null;
        TreeNode currentNode = treeRoot;
        while (currentNode != null) {
            int compareResult = fullName.compareTo(currentNode.getName());
            if (compareResult < 0) {
                parentNode = currentNode;
                currentNode = currentNode.getLeft();
            }
            else if (compareResult > 0) {
                parentNode = currentNode;
                currentNode = currentNode.getRight();
            } else {
                currentNode.setProfile(null);
                if (currentNode.getLeft() == null || currentNode.getRight() == null) {
                    TreeNode newChild = (currentNode.getLeft() != null) ? currentNode.getLeft() : currentNode.getRight();
                    if (parentNode == null) {
                        treeRoot = newChild;
                    } else if (currentNode == parentNode.getLeft()) {
                        parentNode.setLeft(newChild);
                    } else {
                        parentNode.setRight(newChild);
                    }
                }
                else {
                    TreeNode successorNode = currentNode.getRight();
                    TreeNode successorParentNode = currentNode;  
                    while (successorNode.getLeft() != null) {
                        successorParentNode = successorNode;
                        successorNode = successorNode.getLeft();
                    }
                    currentNode.setName(successorNode.getName());
                    currentNode.setProfile(successorNode.getProfile());
                    if (successorNode == successorParentNode.getLeft()) {
                        successorParentNode.setLeft(successorNode.getRight());
                    } else {
                        successorParentNode.setRight(successorNode.getRight());
                    }
                }
                return;
            }
        }
            
        }
        
    /**
     * Clean up the tree by using previously written methods to remove unmarked
     * profiles.
     * Requires the use of getUnmarkedPeople and removePerson.
     */
    public void cleanupTree() {
        // WRITE YOUR CODE HERE
        String[] unmarkedPeople = getUnmarkedPeople();
        for (String fullName : unmarkedPeople) {
            removePerson(fullName);
}

}


    /**
     * Gets the root of the binary search tree.
     *
     * @return The root of the binary search tree.
     */
    public TreeNode getTreeRoot() {
        return treeRoot;
    }

    /**
     * Sets the root of the binary search tree.
     *
     * @param newRoot The new root of the binary search tree.
     */
    public void setTreeRoot(TreeNode newRoot) {
        treeRoot = newRoot;
    }

    /**
     * Gets the first unknown sequence.
     * 
     * @return the first unknown sequence.
     */
    public String getFirstUnknownSequence() {
        return firstUnknownSequence;
    }

    /**
     * Sets the first unknown sequence.
     * 
     * @param newFirst the value to set.
     */
    public void setFirstUnknownSequence(String newFirst) {
        firstUnknownSequence = newFirst;
    }

    /**
     * Gets the second unknown sequence.
     * 
     * @return the second unknown sequence.
     */
    public String getSecondUnknownSequence() {
        return secondUnknownSequence;
    }

    /**
     * Sets the second unknown sequence.
     * 
     * @param newSecond the value to set.
     */
    public void setSecondUnknownSequence(String newSecond) {
        secondUnknownSequence = newSecond;
    }
 } 


