package com.company;
import java.util.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

public class Minesweeper implements ActionListener {
    int row = 14;
    int column = 14;
    int nrMines = ((row * column) / 7);
    JFrame frame = new JFrame("Minesweeper");
    JPanel topPanel = new JPanel();
    JLabel topText = new JLabel("Minesweeper");
    Icon icon = new ImageIcon("C:\\Users\\Maxwell\\Desktop\\IDEA Projects\\Minesweeper\\src\\com\\company\\mine_icon41.png");
    Icon dedicon = new ImageIcon("C:\\Users\\Maxwell\\Desktop\\IDEA Projects\\Minesweeper\\src\\com\\company\\mine_icon_dead41.png");
    Icon glasses = new ImageIcon("C:\\Users\\Maxwell\\Desktop\\IDEA Projects\\Minesweeper\\src\\com\\company\\glasses.jpg");
    JButton resetButton = new JButton(icon);
    JPanel buttonPanel = new JPanel();
    JButton[][] buttonArray = new JButton[row][column];
    boolean[][] mineGrid = new boolean[row][column];
    int[][] numberGrid = new int[row][column];
    boolean firstMove = true;
    boolean[][] explodedGrid = new boolean[row][column];
    JLabel mineText = new JLabel(); //String.valueOf(nrMines)
    boolean victory;

    Minesweeper() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(155, 155, 155));


        topText.setOpaque(false);
        topText.setFont(new Font("Arial", Font.BOLD, 50));
        topText.setHorizontalAlignment(JLabel.CENTER);
        topText.setForeground(Color.magenta);

        topPanel.setBackground(new Color(0, 0, 0));
        topPanel.setVisible(true);
        topPanel.setBounds(0, 0, 800, 100);
        topPanel.setLayout(new FlowLayout(10, 40, 10));

        buttonPanel.setLayout(new GridLayout(row, column));
        buttonPanel.setBackground(new Color(0, 0, 0));

        mineText.setOpaque(false);
        mineText.setFont(new Font("Arial", Font.BOLD, 25));
        mineText.setForeground(Color.magenta);
        mineText.setHorizontalAlignment(JLabel.RIGHT);


        for (int i = 0; i < row; i++) {
            buttonArray[i] = new JButton[column];
            for (int j = 0; j < column; j++) {
                buttonArray[i][j] = new JButton();
                buttonPanel.add(buttonArray[i][j]);
                buttonArray[i][j].setFont(new Font("Arial", Font.BOLD, 17));
                buttonArray[i][j].setForeground(Color.BLACK);
                buttonArray[i][j].setBackground(Color.DARK_GRAY);
                buttonArray[i][j].addActionListener(this);
                buttonArray[i][j].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        for (int i = 0; i < row; i++)
                            for (int j = 0; j < column; j++) {
                                if (nrMines > 0 && !firstMove && buttonArray[i][j].isEnabled()&& e.getButton() == MouseEvent.BUTTON3 && e.getSource() == buttonArray[i][j] && (buttonArray[i][j].getBackground() == Color.DARK_GRAY || buttonArray[i][j].getBackground() == Color.RED)) {
                                    buttonArray[i][j].setBackground(Color.ORANGE);
                                    nrMines--;
                                    mineText.setText(String.valueOf(nrMines));
                                    victory=false;
                                    if(nrMines==0)
                                        victory=checkVictory(row,column,buttonArray,mineGrid);
                                    if(victory) {
                                        topText.setForeground(Color.GREEN);
                                        resetButton.setIcon(glasses);
                                        for (int ii = 0; ii < row; ii++)
                                            for (int jj = 0; jj < column; jj++)
                                                buttonArray[ii][jj].setEnabled(false);
                                    }

                                } else if (buttonArray[i][j].isEnabled() && e.getButton() == MouseEvent.BUTTON3 && e.getSource() == buttonArray[i][j] && buttonArray[i][j].getBackground() == Color.ORANGE) {
                                    buttonArray[i][j].setBackground((Color.DARK_GRAY));
                                    nrMines++;
                                    mineText.setText(String.valueOf(nrMines));
                                }

                            }
                    }
                });
                buttonArray[i][j].setFocusable(false);
            }
        }
        resetButton.addActionListener(this);
        resetButton.setHorizontalAlignment(FlowLayout.LEFT);
        resetButton.setPreferredSize(new Dimension(41, 41));

        topPanel.add(topText);
        topPanel.add(resetButton);
        topPanel.add(mineText);
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(buttonPanel);
        frame.setVisible(true);
        topPanel.setVisible(true);

        startGame();
        //showNumbers();
        //catalogMines();

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (int i = 0; i < row; i++)
            for (int j = 0; j < column; j++)
                if (e.getSource() == buttonArray[i][j]) {
                    //System.out.println("Button nr: " + ((i + 1) * (j + 1)));
                    if (!firstMove) {
                        if (!mineGrid[i][j] && buttonArray[i][j].getBackground() != Color.ORANGE)
                            if (numberGrid[i][j] == 0)
                                explodeZeroes(i, j);
                            else showTile(i, j);
                        else if (mineGrid[i][j] && buttonArray[i][j].getBackground() != Color.ORANGE) defeat();
                    } else {
                        firstExplode(i, j);
                        firstMove = false;
                        mineText.setText(String.valueOf(nrMines));
                    }

                }
        if (e.getSource() == resetButton)
            resetGame();
    }

    public void startGame() {
        System.out.println("adding mines");
        addMines((row * column) / 7);
        setNumbers();
    }

    public void addMines(int nrMines) {
        int copyMine = nrMines;
        Random x = new Random();
        while (nrMines > 0) {
            for (int i = 0; i < row; i++)
                for (int j = 0; j < column; j++) {
                    if (x.nextInt(copyMine * 40) < copyMine && nrMines > 0) {
                        if (!mineGrid[i][j]) {
                            mineGrid[i][j] = true;
                            nrMines--;
                        }

                    } else if (nrMines == 0) break;
                }
        }
        //System.out.println(Arrays.deepToString(mineGrid));
    }

    public void catalogMines() {
        for (int i = 0; i < row; i++)
            for (int j = 0; j < column; j++) {
                if (mineGrid[i][j])
                    buttonArray[i][j].setBackground(Color.RED);
            }
    }

    public void setNumbers() {
        int mineNumber = 0;
        //Top Left
        if (!mineGrid[0][0]) {
            if (mineGrid[0][1])
                mineNumber++;
            if (mineGrid[1][1])
                mineNumber++;
            if (mineGrid[1][0])
                mineNumber++;
            numberGrid[0][0] = mineNumber;
        }
        mineNumber = 0;

        //Top Right
        if (!mineGrid[0][column - 1]) {
            if (mineGrid[0][column - 2])
                mineNumber++;
            if (mineGrid[1][column - 1])
                mineNumber++;
            if (mineGrid[1][column - 2])
                mineNumber++;
            numberGrid[0][column - 1] = mineNumber;
        }
        mineNumber = 0;

        //Bottom Left
        if (!mineGrid[row - 1][0]) {
            if (mineGrid[row - 1][1])
                mineNumber++;
            if (mineGrid[row - 2][0])
                mineNumber++;
            if (mineGrid[row - 2][1])
                mineNumber++;
            numberGrid[row - 1][0] = mineNumber;
        }
        mineNumber = 0;

        //Bottom Right
        if (!mineGrid[row - 1][column - 1]) {
            if (mineGrid[row - 1][column - 2])
                mineNumber++;
            if (mineGrid[row - 2][column - 1])
                mineNumber++;
            if (mineGrid[row - 2][column - 2])
                mineNumber++;
            numberGrid[row - 1][column - 1] = mineNumber;
        }
        mineNumber = 0;

        //North
        for (int i = 1; i < column - 1; i++)
            if (!mineGrid[0][i]) {
                if (mineGrid[1][i - 1])
                    mineNumber++;
                if (mineGrid[1][i])
                    mineNumber++;
                if (mineGrid[1][i + 1])
                    mineNumber++;
                if (mineGrid[0][i - 1])
                    mineNumber++;
                if (mineGrid[0][i + 1])
                    mineNumber++;
                numberGrid[0][i] = mineNumber;
                mineNumber = 0;
            }

        //West
        for (int i = 1; i < row - 1; i++)
            if (!mineGrid[i][0]) {
                if (mineGrid[i - 1][0])
                    mineNumber++;
                if (mineGrid[i - 1][1])
                    mineNumber++;
                if (mineGrid[i][1])
                    mineNumber++;
                if (mineGrid[i + 1][1])
                    mineNumber++;
                if (mineGrid[i + 1][0])
                    mineNumber++;
                numberGrid[i][0] = mineNumber;
                mineNumber = 0;
            }

        //East
        for (int i = 1; i < row - 1; i++)
            if (!mineGrid[i][column - 1]) {
                if (mineGrid[i - 1][column - 1])
                    mineNumber++;
                if (mineGrid[i - 1][column - 2])
                    mineNumber++;
                if (mineGrid[i][column - 2])
                    mineNumber++;
                if (mineGrid[i + 1][column - 2])
                    mineNumber++;
                if (mineGrid[i + 1][column - 1])
                    mineNumber++;
                numberGrid[i][column - 1] = mineNumber;
                mineNumber = 0;
            }

        //South

        for (int i = 1; i < column - 1; i++)
            if (!mineGrid[row - 1][i]) {
                if (mineGrid[row - 1][i - 1])
                    mineNumber++;
                if (mineGrid[row - 2][i - 1])
                    mineNumber++;
                if (mineGrid[row - 2][i])
                    mineNumber++;
                if (mineGrid[row - 2][i + 1])
                    mineNumber++;
                if (mineGrid[row - 1][i + 1])
                    mineNumber++;
                numberGrid[row - 1][i] = mineNumber;
                mineNumber = 0;
            }

        //Center

        for (int i = 1; i < row - 1; i++)
            for (int j = 1; j < column - 1; j++)
                if (!mineGrid[i][j]) {
                    if (mineGrid[i - 1][j - 1])
                        mineNumber++;
                    if (mineGrid[i - 1][j])
                        mineNumber++;
                    if (mineGrid[i - 1][j + 1])
                        mineNumber++;
                    if (mineGrid[i][j - 1])
                        mineNumber++;
                    if (mineGrid[i][j + 1])
                        mineNumber++;
                    if (mineGrid[i + 1][j - 1])
                        mineNumber++;
                    if (mineGrid[i + 1][j])
                        mineNumber++;
                    if (mineGrid[i + 1][j + 1])
                        mineNumber++;
                    numberGrid[i][j] = mineNumber;
                    mineNumber = 0;
                }

    }

    public void defeat() {
        //topText.setText("Defeat");
        topText.setForeground(Color.RED);
        catalogMines();
        resetButton.setIcon(dedicon);
        for (int i = 0; i < row; i++)
            for (int j = 0; j < column; j++)
                buttonArray[i][j].setEnabled(false);


    }

    public void showNumbers() {
        for (int i = 0; i < row; i++)
            for (int j = 0; j < column; j++) {
                if (!mineGrid[i][j])
                    buttonArray[i][j].setText(String.valueOf(numberGrid[i][j]));
            }
    }

    public void explodeArea(int i, int j) {
        if (!mineGrid[i][j]) {
            if (i > 0 && j > 0 && !mineGrid[i - 1][j - 1]) {
                showTile(i - 1, j - 1);
                if (numberGrid[i - 1][j - 1] == 0)
                    explodeZeroes(i - 1, j - 1);
            }
            if (i > 0 && !mineGrid[i - 1][j]) {
                showTile(i - 1, j);
                if (numberGrid[i - 1][j] == 0)
                    explodeZeroes(i - 1, j);
            }
            if (i > 0 && j < column - 1 && !mineGrid[i - 1][j + 1]) {
                showTile(i - 1, j + 1);
                if (numberGrid[i - 1][j + 1] == 0)
                    explodeZeroes(i - 1, j + 1);
            }
            if (j > 0 && !mineGrid[i][j - 1]) {
                showTile(i, j - 1);
                if (numberGrid[i][j - 1] == 0)
                    explodeZeroes(i, j - 1);
            }
            if (j < column - 1 && !mineGrid[i][j + 1]) {
                showTile(i, j + 1);
                if (numberGrid[i][j + 1] == 0)
                    explodeZeroes(i, j + 1);
            }
            if (i < row - 1 && j > 0 && !mineGrid[i + 1][j - 1]) {
                showTile(i + 1, j - 1);
                if (numberGrid[i + 1][j - 1] == 0)
                    explodeZeroes(i + 1, j - 1);
            }
            if (i < row - 1 && !mineGrid[i + 1][j]) {
                showTile(i + 1, j);
                if (numberGrid[i + 1][j] == 0)
                    explodeZeroes(i + 1, j);
            }
            if (i < row - 1 && j < column - 1 && !mineGrid[i + 1][j + 1]) {
                showTile(i + 1, j + 1);
                if (numberGrid[i + 1][j + 1] == 0)
                    explodeZeroes(i + 1, j + 1);
            }
            if (numberGrid[i][j] == 0 && !explodedGrid[i][j])
                showTile(i, j);
            else if (!mineGrid[i][j])
                showTile(i, j);
        }
    }

    public void showTile(int i, int j) {
        if (numberGrid[i][j] != 0)
            buttonArray[i][j].setText(String.valueOf(numberGrid[i][j]));
        buttonArray[i][j].setBackground(new Color(220, 220, 220));
    }

    public void explodeZeroes(int i, int j) {
        if (numberGrid[i][j] == 0 && !explodedGrid[i][j]) {
            showTile(i, j);
            explodedGrid[i][j] = true;
            explodeArea(i, j);
            if (i > 0 && j > 0)
                explodeZeroes(i - 1, j - 1);
            if (i > 0)
                explodeZeroes(i - 1, j);
            if (i > 0 && j < column - 1)
                explodeZeroes(i - 1, j + 1);
            if (j > 0)
                explodeZeroes(i, j - 1);
            if (j < column - 1)
                explodeZeroes(i, j + 1);
            if (i < row - 1 && j > 0)
                explodeZeroes(i + 1, j - 1);
            if (i < row - 1)
                explodeZeroes(i + 1, j);
            if (i < row - 1 && j < column - 1)
                explodeZeroes(i + 1, j + 1);
        }
    }

    public void firstExplode(int i, int j) {
        if(mineGrid[i][j]) {
            nrMines--;
            mineGrid[i][j] = false;
            mineText.setText(String.valueOf(nrMines));
        }
        setNumbers();
        if (numberGrid[i][j] == 0)
            explodeZeroes(i, j);
        else explodeArea(i, j);

    }

    public void resetGame() {
        firstMove = true;
        for (int i = 0; i < row; i++)
            for (int j = 0; j < column; j++) {
                mineGrid[i][j] = false;
                explodedGrid[i][j] = false;
                buttonArray[i][j].setBackground(Color.DARK_GRAY);
                buttonArray[i][j].setText(" ");
                buttonArray[i][j].setEnabled(true);
            }
        topText.setForeground(Color.magenta);
        topText.setText("Minesweeper");
        resetButton.setIcon(icon);
        nrMines=(row * column) / 7;
        mineText.setText(" ");
        startGame();
        //catalogMines();
    }

    public boolean checkVictory(int row, int column,JButton[][] buttonArray, boolean[][] mineGrid) {
        for (int i = 0; i < row; i++)
            for (int j = 0; j < column; j++) {
                if(buttonArray[i][j].getBackground()==Color.ORANGE && !mineGrid[i][j])
                    return false;
            }
        return true;
    }
}
