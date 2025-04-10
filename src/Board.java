import java.util.ArrayList;

public class Board {
    // Creates instance of board class
    public Board() {
    }


    // Returns a long, where each index (either 0 or 1) represents whether a board spot is a legal move
    // Q: SHOULD INPUT BE A STRING BUILDER?
    public StringBuilder legalMoves(String currentState){
        StringBuilder moves = new StringBuilder();

        // Go through each spot on the board & see if it's a possible move
        for (int i = 1; i <= currentState.length(); i++){
            char spot = currentState.charAt(i);
            // If the spot is empty, there is no valid move
            if (spot == '0'){
                moves.append('0');
            }
            // Otherwise, check to see if it's valid
            if (isValid(i)){
                moves.append('1');
            }
            else{
                moves.append('0');
            }
        }
        return moves;
    }

    // Returns the ideal path to victory, in terms of peg index
    public int[] findSolution(String currentState){
        // Base case: if only one peg is left
        int[] path = new int[15];
        int pegCount = 0;
        for (int i = 0; i < currentState.length(); i++){
            char spot = currentState.charAt(i);
            if (spot == '1'){
                pegCount++;
            }
            if (pegCount > 1){
                break;
            }
        }
        if (pegCount == 1){
            int end = currentState.indexOf("1");
            return path;
        }

    }

    // Returns true if a spot on the board represents a valid move for
    // This method works based on integer indeces, rather than the
    public Boolean isValid(int spotIndex){
        // Need to check 2 criteria:
            // There is a neighbor spot that has a peg
            // Right beyond that neighbor peg, there is an empty spot
        // Track all neighbor pegs —
        ArrayList<Integer> neighborPegs = new ArrayList<>();

        // First, calculate which row it's in
        // Find left + right neighbors
        if



        // Find up + down neighbors

        // Check to see if there is an empty spot right beyond any of the neighbors
        // If yes for ANY of the neighbors, return True


    }

    public void makeAdjacencyLists(int n){
        // Calculate # of pegs in board
        int numPegs = 0;
        for (int i = 1; i <= n; i++){
            numPegs += i;
        }
        ArrayList<Integer>[] adjLists = new ArrayList[numPegs];
        // For every peg, calculate all potential neighbors
        // AKA find adjacency lists for all pegs
        // This is currently indexing at peg 1 = 1. Try to switch to 0-indexed later!
        for (int i = 1; i <= numPegs; i++){
            // Initialize each arraylist in adjLists — referenced Geeksforgeeks for this line
            adjLists[i] = new ArrayList<Integer>();
            // Add all possible neighbors to list – 6 possible
            // calculate offset & row later!!!
            int offset = 1;
            int row = 3;
            if (offset != 0){
                adjLists[i].add(i - 1);

            }
            if (offset != 0 || row != 1){
                adjLists[i].add(i - row);
            }
            if (offset != row){
                adjLists[i].add(i - row + 1);
                adjLists[i].add(i + 1);
            }
            if (row != i){
                i + row;
            }





        }
    }



    // Is this method even necessary?
    @Override
    public String toString() {
        return "Board{}";
    }


    public static void main(){

    }
}

