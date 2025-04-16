import java.util.Scanner;

public class Board {
    // Creates instance of board class
    static int dimension;
    static int numPegs;
    static int[][] adjLists;
    public Board(int n) {
        dimension = n;

        int pegs = 0;
        for (int i = 1; i <= n; i++){
            pegs += i;
        }
        numPegs = pegs;
        adjLists = makeAdjacencyLists(numPegs);
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
    return path;
    }

    // Returns true if a spot on the board represents a valid move for
    // This method works based on integer indeces, rather than the
    public boolean isValid(int spotIndex){
        // Need to check 2 criteria:
            // There is a neighbor spot that has a peg
            // Right beyond that neighbor peg, there is an empty spot


        // Check to see if there is an empty spot right beyond any of the neighbors
        // If yes for ANY of the neighbors, return True
        return true;
    }

    // Creates adjacency lists for every peg on the board
    public int[][] makeAdjacencyLists(int numPegs){
        int[][] adjLists = new int[numPegs + 1][6];
        // For every peg, find adjacency lists for all neighboring pegs
        // This is currently indexing at peg 1 = 1. Try to switch to 0-indexed later!
        for (int i = 1; i <= numPegs; i++){
            adjLists[i] = getNeighbors(i);
        }
        return adjLists;
    }

    // Returns neighbor list for any peg on the board
    public int[] getNeighbors(int pegNum) {
        // Max neighbors = 6
        int[] neighborPegs = new int[6];

        // Calculate n's row & offset within the row
        int row = 0;
        int offset = 0;
        int rowMax = 0;
        for (int i = 1; i <= Board.dimension; i++) {
            rowMax += i;
            if (pegNum <= rowMax) {
                // Row goes from 1 to board.dimension
                row = i;
                // Maybe come up with better formula...Goes from 1 to row width
                offset = row - (rowMax - pegNum);
                break;
            }
        }
        // Left neighbor
        // Up + left diagonal neighbor
        // DON'T NEED TO ACCOUNT FOR ROW HERE BECAUSE WHEN ROW = 1, OFFSET = 1, SO ROW 1 IS CAPTURED
        if (offset != 1){
            neighborPegs[0] = pegNum - 1;
            neighborPegs[1] = pegNum - row;
        }
        // Up + right diagonal neighbor
        // Right neighbor
        if (offset != row){
            neighborPegs[2] = pegNum - row + 1;
            neighborPegs[3] = pegNum + 1;
        }
        // Bottom right + left neighbors
        // All pegs except for bottom row have 2 bottom neighbors
        if (row != Board.dimension) {
            neighborPegs[4] = pegNum + row + 1;
            neighborPegs[5] = pegNum + row;
        }
        return neighborPegs;
    }


    public static void main(String[] args){
        Scanner s = new Scanner(System.in);
        System.out.println("Enter the dimension of your triangle: ");
        int n = s.nextInt();
        Board triangle = new Board(n);

        // Should I make adjLists a global variable that belongs to the board? Since it never changes
        for (int[] each: triangle.adjLists){
            System.out.print("[");
            for (int neighbor: each){
                System.out.print(neighbor);
            }
            System.out.println("]");
        }
    }
}

