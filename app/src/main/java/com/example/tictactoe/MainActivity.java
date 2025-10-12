package com.example.tictactoe;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
    private TicTacToeModel model;
    private final SignalRService signalRService = new SignalRService();
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        model = new TicTacToeModel();
    }

    public void onConnectClick(View view) {
        EditText ipEdit = findViewById(R.id.editServerIP);
        String ip = ipEdit.getText().toString().trim();
        if (ip.isEmpty()) {
            // Fall back to hint if empty
            ip = ipEdit.getHint() != null ? ipEdit.getHint().toString() : "192.168.88.4";
        }

        String finalIp = ip;
        Toast.makeText(this, "Connecting to " + finalIp + ":8081", Toast.LENGTH_SHORT).show();
        signalRService.connect(finalIp, new SignalRService.Listener() {
            @Override
            public void onConnected() {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Connected to hub", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onDisconnected(Throwable error) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Hub error: " + (error != null ? error.getMessage() : "unknown"), Toast.LENGTH_LONG).show());
            }

            @Override
            public void onReceiveMessage(String user, String message) {
                Log.d(TAG, "we got " + message + " from " + user);
            }

            @Override
            public void onReceiveKey(String key) {
                // Placeholder: handle remote key for gameplay sync
                Log.d(TAG, "HandleOthersKey: " + key);
            }
        });
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
