DESCRIPTION
My program solves the triangle peg brainteaser from any starting position. It uses BFS to iterate through all possible board states
until it reaches an end state. It catalogs all the end states that could be categorized as a victory (1, 2, or 3 pegs left).
It will then iterate through all victory states and trace the path backward to instruct the user how
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

findWinningBoard():




