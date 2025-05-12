import java.util.*;

public class Board {
    // Creates instance of board class
    static int dimension;
    static int numPegs;
    static String winningState;
    static int[] directions;
    public Map<Integer, String> allWinningStates;
    public Map<String, Move> cameFromMove;
    static int[][] adjLists;
    static final int INVALIDMOVE = 0;
    static final int OFFBOARD = -1;
    static final char SPACE = '0';
    static final int LEFT = 0;
    static final int UPLEFT = 1;
    static final int UPRIGHT = 2;
    static final int RIGHT = 3;
    static final int DOWNRIGHT = 4;
    static final int DOWNLEFT = 5;

    public Board(int n) {
        dimension = n;

        int pegs = 0;
        for (int i = 1; i <= n; i++) {
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

    // Returns the # of pegs left in any given board state
    public int countPegs(String boardState){
        int count = 0;
        for (int i = 0; i < boardState.length(); i++){
            if (boardState.charAt(i) == '1'){
                count++;
            }
        }
        return count;
    }

    // Returns the winning board that is a result of the shortest path to the strongest victory
    public String findWinningBoard(Map<Integer, String> allWinningStates, Map<String, Move> cameFromMove){
        winningState = null;
        int currentMinMoves = Integer.MAX_VALUE;
        for (int pegCount = 1; pegCount <= 3; pegCount++) {
                // Iterate through the cameFromMove map to find the state with the fewest moves that has the current peg count
                for (Map.Entry<String, Move> each : cameFromMove.entrySet()) {
                    String state = each.getKey();
                    Move move = each.getValue();
                    int count = countPegs(state);

                    // Only consider states that are winning states & that have right peg count
                    if (count == pegCount && allWinningStates.containsValue(state) && allWinningStates.get(pegCount).equals(state)) {
                        if (move.numMoves < currentMinMoves) {
                            winningState = state;
                        }
                    }
                }
                if (winningState != null) break;
            }
        return winningState;
    }
    // Finds every possible board state & corresponding move. Once path is complete, retraces the shortest winning path
    public ArrayList<Move> findWinningPath(String currentState, String startState){
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        cameFromMove = new HashMap<>();
        allWinningStates = new HashMap<>();

        queue.add(currentState);
        visited.add(currentState);
        cameFromMove.put(currentState, new Move(-1, -1, -1, currentState, null, 0));

        while (!queue.isEmpty()) {
            String current = queue.remove();
            int pegCount = countPegs(current);

            // Base case: if only 1 peg remains on the board
            if (pegCount == 1) {
                allWinningStates.put(pegCount, current);
            }
            // Next best: 2 pegs left
            else if(pegCount == 2){
                allWinningStates.put(pegCount, current);
            }
            // Next best: 3 pegs left
            else if(pegCount == 3){
                allWinningStates.put(pegCount, current);
            }

            // For every possible move given the current board state, find future moves
            for (Move nextMove : findAllMoves(current, cameFromMove.get(current).numMoves)) {
                String nextState = nextMove.currentState;

                if (!visited.contains(nextState)) {
                    visited.add(nextState);
                    queue.add(nextState);
                    // Correct thing to do??
                    nextMove.incrementNumMoves();
                    cameFromMove.put(nextState, nextMove);
                }
            }
        }

        // If no victory is found
        if (allWinningStates.isEmpty()){
            return new ArrayList<>();
        }
        //Otherwise, trace the moves upward from victory point to find the winning path
        ArrayList<Move> bestPath = new ArrayList<>();
        String current = findWinningBoard(allWinningStates, cameFromMove);

        while (current != null && !current.equals(startState)) {
            Move move = cameFromMove.get(current);
            if (move.oldState == null){
                break;
            }
            bestPath.add(move);
            current = move.oldState;
        }
        Collections.reverse(bestPath);

        return bestPath;
    }

        // Updates & returns the currentState based on any given peg movement
        public String updateState (String currentState,int[] move){
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

            // Do I need to update the adjacency lists here?
            return next;
        }

        // If a move is valid, this returns the index of the jumped peg + current peg's destination
        // Parameters: any peg index & intended direction of movement
        public int[] isValidMove (int start, int direction, String boardState){
            // If there's a neighbor peg in the proper direction
            int neighborPeg = Board.adjLists[start][direction];
            if (neighborPeg != OFFBOARD) {
                if (boardState.charAt(neighborPeg) != SPACE){
                    int neighborNeighbor = Board.adjLists[neighborPeg][direction];
                    // If the neighborNeighbor is valid AND there's an open space one peg past the neighbor
                    if (neighborNeighbor > INVALIDMOVE && boardState.charAt(neighborNeighbor) == '0') {
                        int[] bundle = {neighborPeg, neighborNeighbor};
                        return bundle;
                    }
                    // I do this exact line 2x...can I remove one?
                    int[] bundle = {INVALIDMOVE};
                    return bundle;
                }
            }
            int[] bundle = {INVALIDMOVE};
            return bundle;
        }

        // Overloading method with different parameters
        public int[] isValidMove (String boardState, int jumped, int destination){
            if (jumped != OFFBOARD) {
                if (boardState.charAt(jumped) != SPACE){
                    // If the neighborNeighbor is valid AND there's an open space one peg past the neighbor
                    if (destination> INVALIDMOVE && boardState.charAt(destination) == '0') {
                        int[] bundle = {jumped, destination};
                        return bundle;
                    }
                    // I do this exact line 2x...can I remove one?
                    int[] bundle = {INVALIDMOVE};
                    return bundle;
                }
            }
            int[] bundle = {INVALIDMOVE};
            return bundle;
        }

        // Returns list of all possible moves given a current board state (parameter)
        public ArrayList<Move> findAllMoves (String currentState, int moveDepth){
            ArrayList<Move> allMoves = new ArrayList<>();
            // For every peg on the board (NO EMPTY SPACES)
            for (int i = 1; i <= numPegs; i++) {
                if (currentState.charAt(i) == '1') {
                    // Check all directions in which it could possibly move & see if they're valid
                    for (int j = LEFT; j <= DOWNLEFT; j++) {
                        int[] jumpAndDest = isValidMove(i, directions[j], currentState);
                        int start = i;
                        int jumped = jumpAndDest[0];
                        if (jumped != INVALIDMOVE) {
                            int end = jumpAndDest[1];
                            String nextState = updateState(currentState, new int[]{start, jumped, end});
                            Move move = new Move(i, jumpAndDest[0], jumpAndDest[1], nextState, currentState, moveDepth);
                            allMoves.add(move);
                        }
                    }
                }
            }
            return allMoves;
        }

        // Creates adjacency lists for every peg on the board
        public int[][] makeAdjacencyLists ( int numPegs){
            int[][] adjLists = new int[numPegs + 1][6];
            // For every peg, find adjacency lists for all neighboring pegs
            // This is currently indexing at peg 1 = 1. Try to switch to 0-indexed later!
            for (int i = 1; i <= numPegs; i++) {
                adjLists[i] = getNeighbors(i);
            }
            return adjLists;
        }

        // Returns neighbor list for any peg on the board
        // Doesn't account for spaces! Just lists the peg # where there could possibly be a neighbor at any point
        public int[] getNeighbors (int pegNum){
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
            if (offset != 1) {
                neighborPegs[LEFT] = pegNum - 1;
                neighborPegs[UPLEFT] = pegNum - row;
            }
            // All "else" cases represent invalid neighbors/spots that don't exist on the board
            else {
                neighborPegs[LEFT] = -1;
                neighborPegs[UPLEFT] = -1;
            }
            // Up + right diagonal neighbor
            // Right neighbor
            if (offset != row) {
                neighborPegs[UPRIGHT] = pegNum - row + 1;
                neighborPegs[RIGHT] = pegNum + 1;
            } else {
                neighborPegs[UPRIGHT] = -1;
                neighborPegs[RIGHT] = -1;
            }
            // Bottom right + left neighbors
            // All pegs except for bottom row have 2 bottom neighbors
            if (row != Board.dimension) {
                neighborPegs[DOWNRIGHT] = pegNum + row + 1;
                neighborPegs[DOWNLEFT] = pegNum + row;
            } else {
                neighborPegs[DOWNRIGHT] = -1;
                neighborPegs[DOWNLEFT] = -1;
            }
            return neighborPegs;
        }


        public static void main (String[] args){
            Scanner s = new Scanner(System.in);
            System.out.println("What are the dimensions of your triangle? Enter a number, 3-6: ");
            int n = s.nextInt();
            Board triangle = new Board(n);

            //Later, change this to be 0-indexed
            // Construct start board where every spot is filled
            StringBuilder startState = new StringBuilder("x");
            for (int i = 0; i < numPegs; i++){
                startState.append('1');
            }

            System.out.println("What is your first move? Enter a peg #, 1-" + numPegs);
            int startPeg = s.nextInt();
            s.nextLine();
            String startBoard = String.valueOf(startState);

            // Update the board to reflect user's first move
            startState.setCharAt(startPeg, '0');
            String current = String.valueOf(startState);

            // Finds the best path to victory that stems from the player's 1st move
            ArrayList<Move> path = triangle.findWinningPath(current, startBoard);
            int moveIndex = 0;

            // Continue playing until the user is out of moves
            // I have move depth == 1. WHAT SHOULD IT BE?
            while (!triangle.findAllMoves(current, 1).isEmpty()){
                Move ideal = path.get(moveIndex);
                System.out.println("Optimal move from this position: ");
                System.out.println("Move " + ideal.start + " over " + ideal.jumped + " into empty spot " + ideal.end);
                System.out.println("Would you like to make this move? Y or N");
                String status = s.nextLine().trim();

                if (status.equals("Y") || status.equals("y")){
                    current = triangle.updateState(current, new int[]{ideal.start, ideal.jumped, ideal.end});
                    System.out.println("updated board w/ ideal move.");
                    moveIndex += 1;
                }
                else{
                    System.out.println("Enter your desired move in the following format: # over # to #");
                    String[] move = s.nextLine().trim().split(" ");
                    if (move.length == 5){
                        int start = Integer.parseInt(move[0]);
                        int jumped = Integer.parseInt(move[2]);
                        int end = Integer.parseInt(move[4]);
                        // ADD CHECK FOR INVALID MOVES
                        if (triangle.isValidMove(current, jumped, end).length > 1){
                            current = triangle.updateState(current, new int[]{start, jumped, end});
                            System.out.println("updated w/ custom move");
                            // Need to find new win path, since user has diverged from the old one
                            path = triangle.findWinningPath(current, startBoard);
                            moveIndex = 0;
                        }
                        else{
                            System.out.println("Invalid move.");
                        }
                    }
                    else{
                        System.out.println("Invalid format");
                    }
                }
            }
            // Evaluate whether the user won or lost
            if (triangle.allWinningStates.containsValue(current)) {
                int pegsLeft = triangle.countPegs(current);
                if (pegsLeft == 1){
                    System.out.println("Congratulations! You won, with " + pegsLeft + " peg remaining.");
                }
                else{
                    System.out.println("Congratulations! You won, with " + pegsLeft + " pegs remaining.");
                }
            } else {
                System.out.println("No more moves. Better luck next time!");
            }
        }
    }