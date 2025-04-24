import java.util.*;

public class Board {
    // Creates instance of board class
    static int dimension;
    static int numPegs;
    static int[] directions;
    static int[][] adjLists;
    static final int INVALIDMOVE = 0;
    static final int OFFBOARD = -1;
    static final int SPACE = 0;
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

    // BFS method
    // Returns the ideal path to victory, in terms of peg index
    public ArrayList<int[]> findSolution(String currentState){
        // Referenced ChatGPT for the hashmap idea here, since I was confused about how to keep track
        // of all paths being created simultaneously
        HashMap<String, ArrayList<int[]>> pathMap = new HashMap<>();
        Queue<String> toBeVisited = new LinkedList<>();
        ArrayList<String> visited = new ArrayList<>();

        toBeVisited.add(currentState);
        pathMap.put(currentState, new ArrayList<>());

        while (!toBeVisited.isEmpty()){
            String current = toBeVisited.remove();
            visited.add(current);

            // Base case: if only one peg is left
            // WHAT SHOULD I ACTUALLY RETURN??
            // Referenced ChatGPT for this line, so I didn't have to use 2 for loops
            if (current.chars().filter(ch -> ch == '1').count() == 1){
                return pathMap.get(current);
            }

            // Find all possible moves for the current board
            for (int[] move : findAllMoves(current)){
                // Update board state per new move
                String newState = updateState(currentState, move);
                // If I haven't visited the new state, mark it to be explored
                if (!visited.contains(newState)){
                    visited.add(newState);
                    toBeVisited.add(newState);

                    // Update path based on current move & add to path map
                    ArrayList<int[]> newPath = new ArrayList<>(pathMap.get(current));
                    newPath.add(move);
                    pathMap.put(newState, newPath);
            }

        }
        // If there's no solution to victory
    return new ArrayList<>();
    }

    // Updates & returns the currentState based on any given peg movement
    public String updateState(String currentState, int[] move){
        int start = move[0];
        int jumpedPeg = move[1];
        int end = move[2];

        StringBuilder nextState = new StringBuilder(currentState);
        // Remove peg from start location
        nextState.setCharAt(start, '0');
        // Remove the jumped peg
        nextState.setCharAt(jumpedPeg, '0');
        // Place the peg at new location
        nextState.setCharAt(end, '1');
        String next = nextState.toString();
        return next;
    }


    // If a move is valid, this returns the index of the jumped peg + current peg's destination
    // Parameters: any peg index & intended direction of movement
    public int[] isValidMove(int spotIndex, int direction, String boardState){
        // If there's a neighbor peg in the proper direction
        int neighborPeg = Board.adjLists[spotIndex][direction];
        if (neighborPeg != SPACE && neighborPeg != OFFBOARD){
            int neighborNeighbor = Board.adjLists[neighborPeg][direction];
            // If the neighborNeighbor is valid AND there's an open space one peg past the neighbor
            if (neighborNeighbor > INVALIDMOVE && boardState.charAt(neighborNeighbor) == '0'){
                int[] bundle = {neighborPeg, neighborNeighbor};
                return bundle;
            }
            // I do this exact line 2x...can I remove one?
            int[] bundle = {INVALIDMOVE};
            return bundle;
        }
        int[] bundle = {INVALIDMOVE};
        return bundle;
    }

    // Returns list of all possible moves given a current board state (parameter)
    // Each move is formatted w/ index 0 = start value, index 1 = destination
    public ArrayList<int[]> findAllMoves(String currentState){
        ArrayList<int[]> allMoves = new ArrayList<>();
        // For every peg on the board (NO EMPTY SPACES)
        for (int i = 1; i <= numPegs; i++){
            if (currentState.charAt(i) == '1'){
                // Check all directions in which it could possibly move & see if they're valid
                for (int j = LEFT; j <= DOWNLEFT; j++){
                    int[] jumpAndDest = isValidMove(i, directions[j], currentState);
                    if (jumpAndDest[0] != INVALIDMOVE){
                        int[] move = {i, jumpAndDest[0], jumpAndDest[1]};
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

        //Later, change this to be 1-indexed
        String madeUpState = "x111111111111110";
        ArrayList<int[]> moves = triangle.findAllMoves(madeUpState);
    }
}