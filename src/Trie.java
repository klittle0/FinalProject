class Trie {
    String startBoard;

    // Constructor
    public Trie(){
        startBoard = "111111111111111";
    }

    // Insert a particular word into the Trie
    public void insert(String s){
        insertHelper(startBoard, s, 0);
    }

    // DON'T NEED THIS? HOW TO EDIT?
    public void insertHelper(String currentBoard, String s, int depth){
        // BASE CASE:
        // Once you've reached the end of the word, aka the depth = string length, stop recursion
        if (depth == s.length()){
            // set "isWord" to true
            currentBoard.setWord();
            return;
        }

        int currentLetter = s.charAt(depth);
        // RECURSIVE STEPS:
        // If letter isn't a child of the current node, create a new child node
        if (currentBoard.getNext()[currentLetter] == null){
            currentBoard.getNext()[currentLetter] = new Node();
        }
        // Recurse, one node/level down
        insertHelper(currentNode.getNext()[currentLetter], s, depth + 1);
    }

    public boolean lookup(String s){
        return lookupHelper(root, s, 0);
    }

    // Identify whether a particular word exists in the Trie
    public boolean lookupHelper(Node currentNode, String s, int depth){
        // BASE CASES
        // aka if you've reached the end of Trie & haven't found the word, the word doesn't exist
        // Should be if the current board doesn't equal one of the victory boards
        if (currentNode == null){
            return false;
        }

        // BASE CASE: if the current board doesn't equal one of the baords w/ 2 remaining pegs

        // If you've reached the end of the word, return whether it exists in Trie or not
        if (depth == s.length()){
            return currentNode.isWord();
        }
        // RECURSIVE CALL:
        int currentLetter = s.charAt(depth);
        return lookupHelper(currentNode.getNext()[currentLetter], s, depth + 1);
    }
}