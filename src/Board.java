import java.util.ArrayList;
import java.util.Scanner;

public class Board {
    // Creates instance of board class
    static int dimension;
    static int numPegs;
    static int[] directions;
    static int[][] adjLists;
    static final int LEFT = 0;
    static final int UPLEFT = 1;
    static final int UPRIGHT = 2;
    static final int RIGHT = 3;
    static final int DOWNRIGHT = 4;
    static final int DOWNLEFT = 5;
    public Board(int n) {
        dimension = n;

        int pegs = 0;
        for (int i = 1; i <= n; i++){
            pegs += i;
        }
        numPegs = pegs;
        adjLists = makeAdjacencyLists(numPegs);
        // CHECK: is there a better way I can do this??
        directions = new int[6];
        directions[0] = LEFT;
        directions[1] = UPLEFT;
        directions[2] = UPRIGHT;
        directions[3] = RIGHT;
        directions[4] = DOWNRIGHT;
        directions[5] = DOWNLEFT;

    }

    // Returns a long, where each index (either 0 or 1) represents whether a board spot is a legal move
    // Q: SHOULD INPUT BE A STRING BUILDER?
    // IS THIS METHOD EVEN NECESSARY NOW, GIVEN THE OTHERS I'VE MADE??
//    public StringBuilder legalMoves(String currentState){
//        StringBuilder moves = new StringBuilder();
//
//        // Go through each spot on the board & see if it's a possible move
//        for (int i = 1; i <= currentState.length(); i++){
//            char spot = currentState.charAt(i);
//            // If the spot is empty, there is no valid move
//            if (spot == '0'){
//                moves.append('0');
//            }
//            // Otherwise, check to see if it has anywhere it can move
//            // Change this! Make it so that I check each direction
//            if (isValidMove(i)){
//                moves.append('1');
//            }
//            else{
//                moves.append('0');
//            }
//        }
//        return moves;
//    }

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

    // If a move is valid, this returns the index of the peg's destination
    // If not valid, it returns 0
    // Parameters: any peg index & intended direction of movement
    public int isValidMove(int spotIndex, int direction, String boardState){
        // If there's a neighbor peg in the proper direction
        int neighborPeg = Board.adjLists[spotIndex][direction];
        if (neighborPeg != 0 && neighborPeg != -1){
            int neighborNeighbor = Board.adjLists[neighborPeg][direction];
            // If the neighborNeighbor is valid AND there's an open space one peg past the neighbor
            if (neighborNeighbor > 0 && boardState.charAt(neighborNeighbor - 1) == '0'){
                return neighborNeighbor;
            }
            return 0;
        }
        return 0;
    }

    // Returns list of all possible moves given a current board state (parameter)
    public ArrayList<int[]> findAllMoves(String currentState){
        ArrayList<int[]> allMoves = new ArrayList<>();
        // For every peg on the board (NO EMPTY SPACES)
        for (int i = 0; i < numPegs; i++){
            if (currentState.charAt(i) == '1'){
                // Check all directions in which it could possibly move & see if they're valid
                for (int j = 0; j < 6; j++){
                    int destination = isValidMove(i, directions[j], currentState);
                    if (destination != 0){
                        int[] move = {i, destination};
                        allMoves.add(move);
                    }
                }
            }
        }
        return allMoves;
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
        if (offset != 1){
            neighborPegs[LEFT] = pegNum - 1;
            neighborPegs[UPLEFT] = pegNum - row;
        }
        // All "else" cases represent invalid neighbors/spots that don't exist on the board
        else{
            neighborPegs[LEFT] = -1;
            neighborPegs[UPLEFT] = -1;
        }
        // Up + right diagonal neighbor
        // Right neighbor
        if (offset != row){
            neighborPegs[UPRIGHT] = pegNum - row + 1;
            neighborPegs[RIGHT] = pegNum + 1;
        }
        else{
            neighborPegs[UPRIGHT] = -1;
            neighborPegs[RIGHT] = -1;
        }
        // Bottom right + left neighbors
        // All pegs except for bottom row have 2 bottom neighbors
        if (row != Board.dimension) {
            neighborPegs[DOWNRIGHT] = pegNum + row + 1;
            neighborPegs[DOWNLEFT] = pegNum + row;
        }
        else{
            neighborPegs[DOWNRIGHT] = -1;
            neighborPegs[DOWNLEFT] = -1;
        }
        return neighborPegs;
    }


    public static void main(String[] args){
        Scanner s = new Scanner(System.in);
        System.out.println("Enter the dimension of your triangle: ");
        int n = s.nextInt();
        Board triangle = new Board(n);

        // Start state should actually be all 1s. I should first prompt the user to click which peg they want to remove first
        // This is what triggers the program to really start analyzing board states

        String madeUpState = "111111111110110";
        ArrayList<int[]> moves = triangle.findAllMoves(madeUpState);
        System.out.println(moves.size());
        for (int[] each: moves){
            System.out.print("start peg: ");
            System.out.println(each[0]);
            System.out.print("end peg: ");
            System.out.println(each[1]);
        }
    }
}

