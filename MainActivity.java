package com.example.myxo;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button[][] buttons = new Button[3][3];
    private boolean player1Turn = true;
    private int roundCount;
    private int player1Points;
    private int player2Points;
    private TextView textViewPlayer1;
    private TextView textViewPlayer2;
    private TextView textViewTurn;
    private boolean isSinglePlayerMode = true;

    private Button buttonMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewPlayer1 = findViewById(R.id.text_view_p1);
        textViewPlayer2 = findViewById(R.id.text_view_p2);
        textViewTurn = findViewById(R.id.text_view_turn);
        buttonMode = findViewById(R.id.button_mode);
        buttonMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleGameMode();
            }
        });

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String buttonID = "button_" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                buttons[i][j] = findViewById(resID);
                buttons[i][j].setOnClickListener(this);
            }
        }

        Button buttonReset = findViewById(R.id.button_reset);
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showResetConfirmationDialog();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (!((Button) v).getText().toString().equals("")) {
            return;
        }

        if (player1Turn) {
            ((Button) v).setText("X");
        } else {
            ((Button) v).setText("O");
        }

        roundCount++;

        if (checkForWin()) {
            if (player1Turn) {
                player1Wins();
            } else {
                player2Wins();
            }
        } else if (roundCount == 9) {
            draw();
        } else {
            player1Turn = !player1Turn;
            if (isSinglePlayerMode && !player1Turn) {
                makeComputerMove();
            }
            updateTurnIndicator();
        }
    }

    private boolean checkForWin() {
        String[][] field = new String[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                field[i][j] = buttons[i][j].getText().toString();
            }
        }

        for (int i = 0; i < 3; i++) {
            if (field[i][0].equals(field[i][1]) &&
                    field[i][0].equals(field[i][2]) &&
                    !field[i][0].equals("")) {
                return true;
            }

            if (field[0][i].equals(field[1][i]) &&
                    field[0][i].equals(field[2][i]) &&
                    !field[0][i].equals("")) {
                return true;
            }
        }

        if (field[0][0].equals(field[1][1]) &&
                field[0][0].equals(field[2][2]) &&
                !field[0][0].equals("")) {
            return true;
        }

        if (field[0][2].equals(field[1][1]) &&
                field[0][2].equals(field[2][0]) &&
                !field[0][2].equals("")) {
            return true;
        }

        return false;
    }

    private void player1Wins() {
        player1Points++;
        Toast.makeText(this, "Player 1 wins!", Toast.LENGTH_SHORT).show();
        updatePointsText();
        resetBoard();
    }

    private void player2Wins() {
        player2Points++;
        Toast.makeText(this, "Player 2 wins!", Toast.LENGTH_SHORT).show();
        updatePointsText();
        resetBoard();
    }

    private void draw() {
        Toast.makeText(this, "Draw!", Toast.LENGTH_SHORT).show();
        resetBoard();
    }

    private void updatePointsText() {
        textViewPlayer1.setText("Player 1: " + player1Points);
        textViewPlayer2.setText("Player 2: " + player2Points);
    }

    private void resetBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
            }
        }

        roundCount = 0;
        player1Turn = true;
        updateTurnIndicator();
    }

    private void updateTurnIndicator() {
        if (player1Turn) {
            textViewTurn.setText("Player 1's Turn (X)");
        } else {
            textViewTurn.setText("Player 2's Turn (O)");
        }
    }

    private void showResetConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset Game");
        builder.setMessage("Are you sure you want to reset the game?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                resetGame();
            }
        });
        builder.setNegativeButton("No", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void resetGame() {
        player1Points = 0;
        player2Points = 0;
        updatePointsText();
        resetBoard();
    }

    private void toggleGameMode() {
        isSinglePlayerMode = !isSinglePlayerMode;
        if (isSinglePlayerMode) {
            buttonMode.setText("Single Player Mode");
        } else {
            buttonMode.setText("Two Player Mode");
        }
        resetGame();
    }

    private void makeComputerMove() {
        int bestScore = Integer.MIN_VALUE;
        int bestRow = -1;
        int bestCol = -1;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getText().toString().equals("")) {
                    buttons[i][j].setText("O");
                    int score = minimax(0, false);
                    buttons[i][j].setText("");

                    if (score > bestScore) {
                        bestScore = score;
                        bestRow = i;
                        bestCol = j;
                    }
                }
            }
        }

        if (bestRow != -1 && bestCol != -1) {
            buttons[bestRow][bestCol].performClick();
        }
    }

    private int minimax(int depth, boolean isMaximizingPlayer) {
        if (checkForWin()) {
            if (isMaximizingPlayer) {
                return -1; // The human player wins
            } else {
                return 1; // The computer player wins
            }
        } else if (roundCount == 9) {
            return 0; // It's a draw
        }

        if (isMaximizingPlayer) {
            int bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (buttons[i][j].getText().toString().equals("")) {
                        buttons[i][j].setText("O");
                        int score = minimax(depth + 1, false);
                        buttons[i][j].setText("");
                        bestScore = Math.max(bestScore, score);
                    }
                }
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (buttons[i][j].getText().toString().equals("")) {
                        buttons[i][j].setText("X");
                        int score = minimax(depth + 1, true);
                        buttons[i][j].setText("");
                        bestScore = Math.min(bestScore, score);
                    }
                }
            }
            return bestScore;
        }
    }
}
