import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class TrianglePegGame extends JFrame implements MouseListener {
    private String boardState;
    private int selectedPeg = -1;
    private ArrayList<Point> pegPositions = new ArrayList<>();
    private Image background;
    private Board board;
    private TrianglePegGame game;
    private JTextArea gameText;
    private JButton moveButton, resetButton;
    private JTextField moveInput;

    public TrianglePegGame(int dimension) {
        // Read the background
        try {
            background = ImageIO.read(new File("Trianglebackground.png")); // or PNG, etc.
        } catch (IOException e) {
            e.printStackTrace();
        }

        setTitle("Triangle Peg Game");
        setSize(1618, 906);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Game text area
        gameText = new JTextArea();
        gameText.setEditable(false);
        gameText.setFont(new Font("Monospaced", Font.PLAIN, 14));
        add(new JScrollPane(gameText), BorderLayout.CENTER);

        // Move input field
        JPanel inputPanel = new JPanel();
        moveInput = new JTextField(20);
        moveButton = new JButton("Make Move");
        inputPanel.add(moveInput);
        inputPanel.add(moveButton);
        add(inputPanel, BorderLayout.SOUTH);

        // Reset button
        resetButton = new JButton("Reset Game");
        add(resetButton, BorderLayout.NORTH);

        // Button listeners
        moveButton.addActionListener(new MoveListener());
        resetButton.addActionListener(e -> resetGame());

        // Initialize game
        startGame();
    }

    private void startGame() {
        String input = JOptionPane.showInputDialog(this, "Welcome to the triangle peg game!\nEnter starting position (0–14):");
        if (input == null) return;

        try {
            int startPos = Integer.parseInt(input);
            board = new Board(startPos);
            gameText.setText("Welcome to the triangle peg game!\n");
            gameText.append("Enter your moves in the format 'from to'. Type 'q' to quit.\n\n");
        } catch (NumberFormatException ex) {
            gameText.append("Invalid input. Please enter a number between 0 and 14.\n");
            startGame();
        }
    }


    public void paint(Graphics g){
        BufferStrategy bf = this.getBufferStrategy();
        if (bf == null)
            return;
        Graphics g2 = null;
        try {
            g2 = bf.getDrawGraphics();
            myPaint(g2);
        }
        finally {
            g2.dispose();
        }
        bf.show();
        Toolkit.getDefaultToolkit().sync();
    }

    public void myPaint(Graphics g) {

        // Draw background image
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }

        // Draw the pegs in a triangle formation
        Graphics2D g2 = (Graphics2D) g;

        int pegSize = 60;
        int pegSpacing = 120;
        double rowSpacing = (Math.sqrt(3) / 2) * pegSpacing;

        int totalHeight = (int) (rowSpacing * (dimension - 1) + pegSize);
        int yOffset = (getHeight() - totalHeight) / 2 + 50;
        int pegCount = 1;

        for (int row = 0; row < dimension; row++) {
            for (int col = 0; col <= row; col++) {
                int x = getWidth() / 2 - (row * pegSpacing / 2) + col * pegSpacing - 20;
                int y = yOffset + (int) ((dimension - 1 - row) * rowSpacing);
                g2.setColor(Color.WHITE);
                g2.fillOval(x, y, pegSize, pegSize);
                // Set custom color to label each peg
                g2.setColor(new Color(45, 83, 237));

                // Center the peg number on the circle
                FontMetrics metrics = g2.getFontMetrics();
                String pegNumber = String.valueOf(pegCount);
                int textWidth = metrics.stringWidth(pegNumber);
                int textHeight = metrics.getHeight();

                // Draw the number in the center of the peg
                g2.drawString(pegNumber, x + (pegSize - textWidth) / 2, y + (pegSize + textHeight) / 2 - 10);

                // Increment peg number
                pegCount++;
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