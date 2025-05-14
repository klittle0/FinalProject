DESCRIPTION
My program solves the triangle peg brainteaser from any starting position. It uses BFS to iterate through all possible board states
until it reaches an end state. It catalogs all the end states that could be categorized as a victory (1, 2, or 3 pegs left).
It will then iterate through all victory states and trace the best one's path to the start, instructing the user how
to reach the best victory (fewest pegs left) with the fewest moves.

TIME COMPLEXITY, ONE METHOD AT A TIME:

getNeighbors():
for every peg on the board: O(N)
    -find the row + offset of each peg: CONSTANT TIME
    -check for 6 possible neighbors: CONSTANT TIME

OVERALL: O(N)

findAllMoves():
for every peg on the board: O(N)
    -Check for valid moves in 6 directions: CONSTANT TIME

OVERALL: O(N)

findWinningPath():
**This consists of 2 parts. BFS and the reconstruction of the best path.

BFS PART:
    -Go through (up to) all possible board states (represented by A)
    -For each board state, call findAllMoves(): O(N)

OVERALL: O(A * N)

PATH RECONSTRUCTION PART:
    -Retrace the moves back to the starting board: O(A)

OVERALL: O(A)

METHOD OVERALL: O(A * N) + O(A)

findWinningBoard():
for states with 1, 2, or 3 pegs left: O(3)
    -iterate through all visited states: O(A)

OVERALL: O(3A), aka O(A)






