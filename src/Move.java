class Move {
    int start;
    int jumped;
    int end;
    String currentState;
    String oldState;
    int numMoves;

    // Stores the relationship between boards: essentially parent + child board
    // as well as the move required to get between them
    public Move(int start, int jumped, int end, String currentState, String oldState, int numMoves) {
        this.start = start;
        this.jumped = jumped;
        this.end = end;
        this.currentState = currentState;
        this.oldState = oldState;
        // Represents # of moves it took to get to a certain board state
        this.numMoves = numMoves;
    }

    public void incrementNumMoves() {
        this.numMoves += 1;
    }

    public String toString() {
        return "(" + start + " -> " + end + ", jumped " + jumped + ")";
    }
}