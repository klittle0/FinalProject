import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class TrianglePegGame extends JPanel implements MouseListener {
    private int dimension;
    private Board boardLogic;
    private String boardState;
    private int selectedPeg = -1;
    private ArrayList<Point> pegPositions = new ArrayList<>();
    private Image background;

    public TrianglePegGame(int dimension) {
        this.dimension = dimension;
        boardLogic = new Board(dimension);
        // Read the background
        try {
            background = ImageIO.read(new File("Trianglebackground.png")); // or PNG, etc.
        } catch (IOException e) {
            e.printStackTrace();
        }

        int numPegs = dimension * (dimension + 1) / 2;

        // Initialize state string with all pegs (1), and remove one
        StringBuilder state = new StringBuilder("x");
        for (int i = 0; i < numPegs; i++) {
            state.append('1');
        }
        state.setCharAt(1, '0'); // remove one peg to start (index 1, 0-indexed)

        boardState = state.toString();
        addMouseListener(this);
        setPreferredSize(new Dimension(1618, 906));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw background image
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }

        // Draw the pegs in a triangle formation
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.WHITE);

        int pegSize = 60;
        int pegSpacing = 120;
        double rowSpacing = (Math.sqrt(3) / 2) * pegSpacing;

        int totalHeight = (int) (rowSpacing * (dimension - 1) + pegSize);
        int yOffset = (getHeight() - totalHeight) / 2 + 50;

        for (int row = 0; row < dimension; row++) {
            for (int col = 0; col <= row; col++) {
                int x = getWidth() / 2 - (row * pegSpacing / 2) + col * pegSpacing - 20;
                int y = yOffset + (int) ((dimension - 1 - row) * rowSpacing);
                g2.fillOval(x, y, pegSize, pegSize);
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int pegRadius = 30;
        for (int i = 0; i < pegPositions.size(); i++) {
            Point p = pegPositions.get(i);
            double dx = e.getX() - p.x;
            double dy = e.getY() - p.y;
            if (dx * dx + dy * dy <= pegRadius * pegRadius / 4) {
                handlePegClick(i);
                break;
            }
        }
    }

    private void handlePegClick(int clickedIndex) {
        if (selectedPeg == -1) {
            selectedPeg = clickedIndex;
        } else {
            // Try every possible jump from selectedPeg over something into clickedIndex
            boolean moved = false;
            for (int jumped = 0; jumped < pegPositions.size(); jumped++) {
                int[] test = boardLogic.isValidMove(boardState, jumped, clickedIndex);
                if (test.length == 3 && test[0] == selectedPeg && boardState.charAt(jumped + 1) == '1') {
                    boardState = boardLogic.updateState(boardState, test);
                    moved = true;
                    break;
                }
            }

            if (!moved) {
                JOptionPane.showMessageDialog(this, "Invalid move!");
            }

            selectedPeg = -1;

            // Check for win/loss after the move
            if (boardLogic.findAllMoves(boardState, 1).isEmpty()) {
                if (boardLogic.allWinningStates.containsValue(boardState)) {
                    JOptionPane.showMessageDialog(this, "Congratulations, you won!");
                } else {
                    JOptionPane.showMessageDialog(this, "No more moves. Better luck next time!");
                }
            }

            repaint();
        }
    }

    // Unused methods
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

    public static void main(String[] args) {
        int n = 5; // Or prompt the user
        JFrame frame = new JFrame("Triangle Peg Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new TrianglePegGame(n));
        frame.pack();
        frame.setVisible(true);
    }
}