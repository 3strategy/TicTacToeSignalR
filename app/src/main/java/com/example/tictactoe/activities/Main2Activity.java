package com.example.tictactoe.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tictactoe.R;
import com.example.tictactoe.models.TicTacToeModel;

public class Main2Activity extends AppCompatActivity {
    private TicTacToeModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        model = new TicTacToeModel();
    }

    public void onCellClick(View view) {
        Button button = (Button) view;
        String tag = button.getTag().toString();
        String[] position = tag.split(",");
        int row = Integer.parseInt(position[0]);
        int col = Integer.parseInt(position[1]);

        if (model.isLegal(row, col)) {
            model.makeMove(row, col);
            button.setText(model.getCurrentPlayer());

            if (model.checkWin()) {
                model.changePlayer();
                Toast.makeText(this, "Player " + model.getCurrentPlayer() + " wins!", Toast.LENGTH_SHORT).show();
                model.resetGame();
                resetBoard();
            } else if (model.isTie()) {
                Toast.makeText(this, "It's a tie!", Toast.LENGTH_SHORT).show();
                model.resetGame();
                resetBoard();
            } else {
                model.changePlayer();
            }
        }
    }

    public void onConnectClick(View view) {
        Toast.makeText(this, R.string.local_mode_connect_disabled, Toast.LENGTH_SHORT).show();
    }

    private void resetBoard() {
        int[] buttonIds = {
                R.id.button00, R.id.button01, R.id.button02,
                R.id.button10, R.id.button11, R.id.button12,
                R.id.button20, R.id.button21, R.id.button22
        };

        for (int id : buttonIds) {
            Button button = findViewById(id);
            button.setText("");
        }
    }
}
