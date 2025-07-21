// Yash Kulkarni
// Professor: Nathan James Brunelle
// TA: Srihari
// Date: 5 - 07 - 2025
// Section: AK
// Mini-git

import java.util.*;
import java.text.SimpleDateFormat;

// Repository is a version control system that tracks commits. 
// A commit stores a message, timestamp, a reference to the previous commit in the chain,
// and a unique ID. Repository can add commits, examine history, check for certain commits,
// and combine two repository histories together using timestamp as ordering.
public class Repository {

    private String name;
    private Commit head; 

    // B: Creates a new and empty repository with a specific name. 
    // E: Throws IllegalArgumentException if the name is null or empty. 
    // R: Returns a repository without commits.
    // P: name is the name of the repository. 
    public Repository(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException();
        }
        this.head = null;
        this.name = name; 
    } 
    
    // B: Finds the ID of the head, or most recent commit in the repository. 
    // E: No exceptions
    // R: returns null if the head is null, otherwise returns the unique id of the 
    // R: head in the repository.
    // P: None 
    public String getRepoHead() {
        if (head == null) {
            return null;
        }
        return head.id; 
    }
    
    // B: Determines the size of the repository.
    // E: None
    // R: Returns an integer representing the size of the repository.
    // P: None.
    public int getRepoSize() {
        int count = 0;
        Commit curr = head;
        while (curr != null) {
            count++;
            curr = curr.past; 
        }
        return count; 
    }
    
    // B: Creates a string describing the repository's state.
    // E: None
    // R: Returns a string describing the name of the repository and the head, or
    // R: the name of the repository and a string saying there are no commits. 
    // P: none
    public String toString() {
        if (head == null) {
            return name + " - No commits";
        }
        return name + " - Current head: " + head.toString();
    }

    // B: Checks if a commit with targetID is in the repository.
    // E: if the targetID is null, throws an IllegalArgumentException. 
    // R: Returns a boolean, true if the commit with targetID is in the repository,
    // R: false if a commit with targetID is not in the repository. 
    // P: targetID is the id of the commit that needs to be searched for. 
    public boolean contains(String targetID) {
        if (targetID == null) {
            throw new IllegalArgumentException();
        }
        Commit sub = head;
        while (sub != null) {
            if (sub.id.equals(targetID)) {
                return true; 
            }
            sub = sub.past;         
        }
        return false; 
    }
   
    // B: Finds the n most recent commits from the repository. 
    // E: Throws IllegalArgumentException if n is non positive.
    // R: Returns n strings containing descriptions of commits seperated by newlines. 
    // R: If number of commits is smaller than n, returns all of their descriptions 
    // R: seperated by newlines. 
    // P: n is the number of recent commits to return. 
    public String getHistory(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException();
        }
        Commit sub = head;
        String history = "";
        int count = 0;
        while (sub != null && count < n) {
            history += sub.toString() + "\n";
            sub = sub.past;
            count++;
        }  
        return history;
    }   
        
    // B: Creates and adds a commit with a message to the repository.
    // E: If the message is null, throws IllegalArgumentExceptin.
    // R: Returns the id of the new commit that was added to the repository.
    // P: the parameter is message - a description of changes made in this commit. 
    public String commit(String message) {
        if (message == null) {
            throw new IllegalArgumentException();
        } 
        Commit newMessage = new Commit(message, head);
        head = newMessage;
        return newMessage.id;       
    }
    
    // B: Finds commit with targetID and removes it from repository.
    // E: Throws illegalargumentexception if the targetID is null.
    // R: Returns a boolean; true if the commit with targetID was removed,
    // R: false if it was not found or removed.
    // P: targetID is the id of the commit that is trying to be removed. 
    public boolean drop(String targetId) {
        if (targetId == null) {
            throw new IllegalArgumentException();
        } 
        if (head != null && head.id.equals(targetId)) {
            head = head.past;
            return true; 
        }
        //middle case
        Commit sub = head;
        while (sub != null && sub.past != null) {
            if (sub.past.id.equals(targetId)) {
                sub.past = sub.past.past;
                return true;
            } else {
                sub = sub.past; 
            }
        }
        return false; 
    }

    // B: Merges commits from another repository into "this" repository using timestamp ordering.
    // B: the "other" repository is emptied out after. 
    // E: Throws IllegalArgumentException of the other repository is null.
    // R: None.
    // P: other is the other repository who has its commits moved into "this" repository.
    public void synchronize(Repository other) {
        if (other == null) {
            throw new IllegalArgumentException();
        } 
        if (other.head != null) {
            if (head == null) {
                this.head = other.head; 
                other.head = null;
            } else {
                Commit thisPointer = this.head;
                Commit otherPointer = other.head;
                Commit result = null;
                Commit current = null;
                while (thisPointer != null && otherPointer != null) {
                    Commit nextNode = null; 
                    if (thisPointer.timeStamp > otherPointer.timeStamp) {
                        nextNode = thisPointer;
                        thisPointer = thisPointer.past;
                    } else {
                        nextNode = otherPointer;
                        otherPointer = otherPointer.past;
                    } 
                    if (result == null) {
                        result = nextNode;
                        current = nextNode; 
                    } else {
                        current.past = nextNode;
                        current = nextNode; 
                    }
                }
                if (thisPointer != null) {
                    current.past = thisPointer; 
                } 
                if (otherPointer != null) {
                    current.past = otherPointer; 
                }
                other.head = null; 
                head = result;
            }
        }
    }
       
            
    /**
     * DO NOT MODIFY
     * A class that represents a single commit in the repository.
     * Commits are characterized by an identifier, a commit message,
     * and the time that the commit was made. A commit also stores
     * a reference to the immediately previous commit if it exists.
     *
     * Staff Note: You may notice that the comments in this 
     * class openly mention the fields of the class. This is fine 
     * because the fields of the Commit class are public. In general, 
     * be careful about revealing implementation details!
     */
    public static class Commit {

        private static int currentCommitID;

        /**
         * The time, in milliseconds, at which this commit was created.
         */
        public final long timeStamp;

        /**
         * A unique identifier for this commit.
         */
        public final String id;

        /**
         * A message describing the changes made in this commit.
         */
        public final String message;

        /**
         * A reference to the previous commit, if it exists. Otherwise, null.
         */
        public Commit past;

        /**
         * Constructs a commit object. The unique identifier and timestamp
         * are automatically generated.
         * @param message A message describing the changes made in this commit. Should be non-null.
         * @param past A reference to the commit made immediately before this
         *             commit.
         */
        public Commit(String message, Commit past) {
            this.id = "" + currentCommitID++;
            this.message = message;
            this.timeStamp = System.currentTimeMillis();
            this.past = past;
        }

        /**
         * Constructs a commit object with no previous commit. The unique
         * identifier and timestamp are automatically generated.
         * @param message A message describing the changes made in this commit. Should be non-null.
         */
        public Commit(String message) {
            this(message, null);
        }

        /**
         * Returns a string representation of this commit. The string
         * representation consists of this commit's unique identifier,
         * timestamp, and message, in the following form:
         *      "[identifier] at [timestamp]: [message]"
         * @return The string representation of this collection.
         */
        @Override
        public String toString() {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
            Date date = new Date(timeStamp);

            return id + " at " + formatter.format(date) + ": " + message;
        }

        /**
        * Resets the IDs of the commit nodes such that they reset to 0.
        * Primarily for testing purposes.
        */
        public static void resetIds() {
            Commit.currentCommitID = 0;
        }
    }
}
