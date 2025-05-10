class Move {
    int start;
    int jumped;
    int end;
    String currentState;
    String oldState;

    // Stores the relationship between boards: essentially parent + child board
    // as well as the move required to get between them
    public Move(int start, int jumped, int end, String currentState, String oldState) {
        this.start = start;
        this.jumped = jumped;
        this.end = end;
        this.currentState = currentState;
        this.oldState = oldState;
    }

    public String toString() {
        return "(" + start + " -> " + end + ", jumped " + jumped + ")";
    }
}