package com.example.tictactoe.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tictactoe.R;
import com.example.tictactoe.databinding.ActivityMain2Binding;
import com.example.tictactoe.models.GameRoom;
import com.example.tictactoe.models.TicTacToeModel;
import com.example.tictactoe.services.FBRef;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Runs the minimal Firebase Realtime Database multiplayer Tic-Tac-Toe flow.
 *
 * <p>The activity intentionally trusts every database value. Its role and turn
 * checks only control this client UI and are not security checks.</p>
 */
public class Main2Activity extends AppCompatActivity {

    /** View Binding access to the RTDB lobby and game board. */
    private ActivityMain2Binding binding;

    /** Local board model rebuilt whenever Firebase publishes the room snapshot. */
    private TicTacToeModel model;

    /** Firebase reference containing every room published by this tutorial. */
    private DatabaseReference gamesReference;

    /** Listener that keeps the room Spinner synchronized with Firebase. */
    private ValueEventListener gamesListener;

    /** Firebase reference for the room currently being played or watched. */
    private DatabaseReference selectedRoomReference;

    /** Listener that redraws the selected game after every room change. */
    private ValueEventListener selectedRoomListener;

    /** Room objects shown in the same order as the Spinner labels. */
    private final List<GameRoom> rooms = new ArrayList<>();

    /** Firebase push keys shown in the same order as the Spinner labels. */
    private final List<String> roomIds = new ArrayList<>();

    /** Human-readable room labels displayed by the Spinner. */
    private final List<String> roomLabels = new ArrayList<>();

    /** Adapter that presents the current room labels in the Spinner. */
    private ArrayAdapter<String> roomsAdapter;

    /** Most recent complete value received for the selected room. */
    private GameRoom selectedRoom;

    /** Firebase user ID of the person using this activity. */
    private String currentUid;

    /** Local role: {@code X}, {@code O}, or an empty string for a spectator. */
    private String localPlayer = "";

    /**
     * Creates the lobby, reads the authenticated user, and starts the room subscription.
     *
     * @param savedInstanceState previously saved Android state, when available
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseUser currentUser = FBRef.refAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, R.string.rtdb_login_required, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentUid = currentUser.getUid();
        model = new TicTacToeModel();
        gamesReference = FBRef.refGames;

        setupRoomSpinner();
        binding.buttonStartGame.setOnClickListener(view -> startGameAndWait());
        binding.buttonOpenGame.setOnClickListener(view -> openSelectedGame());
        listenForRooms();
    }

    /**
     * Creates the standard Android Spinner adapter used for room selection.
     */
    private void setupRoomSpinner() {
        roomsAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                roomLabels
        );
        roomsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerGames.setAdapter(roomsAdapter);
        binding.buttonOpenGame.setEnabled(false);
    }

    /**
     * Subscribes to the games branch and rebuilds the room chooser after each change.
     */
    private void listenForRooms() {
        gamesListener = new ValueEventListener() {
            /**
             * Replaces the Spinner contents with the latest complete games snapshot.
             *
             * @param snapshot current contents of the games branch
             */
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                rooms.clear();
                roomIds.clear();
                roomLabels.clear();

                for (DataSnapshot roomSnapshot : snapshot.getChildren()) {
                    GameRoom room = roomSnapshot.getValue(GameRoom.class);
                    rooms.add(room);
                    roomIds.add(roomSnapshot.getKey());

                    String state = room.getPlayerO().isEmpty()
                            ? getString(R.string.rtdb_room_waiting)
                            : getString(R.string.rtdb_room_playing);
                    roomLabels.add(getString(R.string.rtdb_room_label, room.getName(), state));
                }

                roomsAdapter.notifyDataSetChanged();
                boolean hasRooms = !rooms.isEmpty();
                binding.textNoGames.setVisibility(hasRooms ? View.GONE : View.VISIBLE);
                binding.buttonOpenGame.setEnabled(hasRooms);
            }

            /**
             * Shows a Firebase message when the games subscription is cancelled.
             *
             * @param error reason Firebase cancelled the subscription
             */
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(
                        Main2Activity.this,
                        getString(R.string.rtdb_read_failed, error.getMessage()),
                        Toast.LENGTH_LONG
                ).show();
            }
        };

        gamesReference.addValueEventListener(gamesListener);
    }

    /**
     * Publishes a named room with the current user assigned to X.
     */
    private void startGameAndWait() {
        String gameName = binding.editGameName.getText().toString().trim();
        if (gameName.isEmpty()) {
            binding.editGameName.setError(getString(R.string.rtdb_game_name_required));
            binding.editGameName.requestFocus();
            return;
        }

        DatabaseReference newRoomReference = gamesReference.push();
        GameRoom room = new GameRoom(gameName, currentUid, "", "");
        newRoomReference.setValue(room)
                .addOnSuccessListener(unused -> openRoom(newRoomReference.getKey()))
                .addOnFailureListener(error -> Toast.makeText(
                        this,
                        getString(R.string.rtdb_write_failed, error.getMessage()),
                        Toast.LENGTH_LONG
                ).show());
    }

    /**
     * Joins the selected waiting room or watches the selected playing room.
     */
    private void openSelectedGame() {
        int position = binding.spinnerGames.getSelectedItemPosition();
        if (position < 0 || position >= rooms.size()) {
            return;
        }

        GameRoom room = rooms.get(position);
        String roomId = roomIds.get(position);

        if (currentUid.equals(room.getPlayerX()) || currentUid.equals(room.getPlayerO())) {
            openRoom(roomId);
        } else if (room.getPlayerO().isEmpty()) {
            gamesReference.child(roomId).child("playerO").setValue(currentUid)
                    .addOnSuccessListener(unused -> openRoom(roomId))
                    .addOnFailureListener(error -> Toast.makeText(
                            this,
                            getString(R.string.rtdb_write_failed, error.getMessage()),
                            Toast.LENGTH_LONG
                    ).show());
        } else {
            openRoom(roomId);
        }
    }

    /**
     * Shows the board and subscribes to one complete game-room snapshot.
     *
     * @param roomId Firebase push key of the room to open
     */
    private void openRoom(String roomId) {
        binding.lobbyLayout.setVisibility(View.GONE);
        binding.gameLayout.setVisibility(View.VISIBLE);
        setBoardEnabled(false);

        selectedRoomReference = gamesReference.child(roomId);
        selectedRoomListener = new ValueEventListener() {
            /**
             * Redraws the selected board from its latest complete room snapshot.
             *
             * @param snapshot current contents of the selected room
             */
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                selectedRoom = snapshot.getValue(GameRoom.class);
                if (selectedRoom != null) {
                    showSelectedRoom();
                }
            }

            /**
             * Shows a Firebase message when the room subscription is cancelled.
             *
             * @param error reason Firebase cancelled the subscription
             */
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(
                        Main2Activity.this,
                        getString(R.string.rtdb_read_failed, error.getMessage()),
                        Toast.LENGTH_LONG
                ).show();
            }
        };
        selectedRoomReference.addValueEventListener(selectedRoomListener);
    }

    /**
     * Rebuilds the board and status text from the latest complete room snapshot.
     */
    private void showSelectedRoom() {
        binding.textGameName.setText(selectedRoom.getName());
        model.resetGame();
        resetBoard();

        String moves = selectedRoom.getMoves();
        if (!moves.isEmpty()) {
            String[] moveList = moves.split(";");
            for (String move : moveList) {
                String[] parts = move.split(",");
                int row = Integer.parseInt(parts[0]);
                int col = Integer.parseInt(parts[1]);
                String player = parts[2];

                model.setMove(row, col, player);
                buttonFor(row, col).setText(player);
                model.changePlayer();
            }
        }

        if (currentUid.equals(selectedRoom.getPlayerX())) {
            localPlayer = "X";
        } else if (currentUid.equals(selectedRoom.getPlayerO())) {
            localPlayer = "O";
        } else {
            localPlayer = "";
        }

        showGameStatus();
    }

    /**
     * Shows whether this client is waiting, watching, or allowed to play now.
     */
    private void showGameStatus() {
        if (selectedRoom.getPlayerO().isEmpty()) {
            binding.textGameStatus.setText(R.string.rtdb_waiting_for_player);
            setBoardEnabled(false);
        } else if (localPlayer.isEmpty()) {
            binding.textGameStatus.setText(R.string.rtdb_watching_game);
            setBoardEnabled(false);
        } else if (localPlayer.equals(model.getCurrentPlayer())) {
            binding.textGameStatus.setText(getString(R.string.rtdb_your_turn, localPlayer));
            setBoardEnabled(true);
        } else {
            binding.textGameStatus.setText(getString(R.string.rtdb_other_turn, localPlayer));
            setBoardEnabled(false);
        }
    }

    /**
     * Publishes a locally permitted move as a new complete sequence string.
     *
     * @param view board button clicked by the user
     */
    public void onCellClick(View view) {
        if (selectedRoom == null
                || selectedRoom.getPlayerO().isEmpty()
                || localPlayer.isEmpty()
                || !localPlayer.equals(model.getCurrentPlayer())) {
            return;
        }

        Button button = (Button) view;
        String[] position = button.getTag().toString().split(",");
        int row = Integer.parseInt(position[0]);
        int col = Integer.parseInt(position[1]);

        if (!model.isLegal(row, col)) {
            return;
        }

        String move = row + "," + col + "," + localPlayer;
        String previousMoves = selectedRoom.getMoves();
        String updatedMoves = previousMoves.isEmpty() ? move : previousMoves + ";" + move;

        setBoardEnabled(false);
        selectedRoomReference.child("moves").setValue(updatedMoves)
                .addOnFailureListener(error -> {
                    showGameStatus();
                    Toast.makeText(
                            this,
                            getString(R.string.rtdb_write_failed, error.getMessage()),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    /**
     * Finds the bound board button at a row and column.
     *
     * @param row board row from zero to two
     * @param col board column from zero to two
     * @return matching board button
     */
    private Button buttonFor(int row, int col) {
        if (row == 0 && col == 0) return binding.button00;
        if (row == 0 && col == 1) return binding.button01;
        if (row == 0 && col == 2) return binding.button02;
        if (row == 1 && col == 0) return binding.button10;
        if (row == 1 && col == 1) return binding.button11;
        if (row == 1 && col == 2) return binding.button12;
        if (row == 2 && col == 0) return binding.button20;
        if (row == 2 && col == 1) return binding.button21;
        if (row == 2 && col == 2) return binding.button22;
        return null;
    }

    /**
     * Returns every board button in display order.
     *
     * @return array containing the nine board buttons
     */
    private Button[] boardButtons() {
        return new Button[]{
                binding.button00, binding.button01, binding.button02,
                binding.button10, binding.button11, binding.button12,
                binding.button20, binding.button21, binding.button22
        };
    }

    /**
     * Clears the text displayed by every board button.
     */
    private void resetBoard() {
        for (Button button : boardButtons()) {
            button.setText("");
        }
    }

    /**
     * Enables or disables all local board buttons without changing Firebase rules.
     *
     * @param enabled whether this client may press board buttons
     */
    private void setBoardEnabled(boolean enabled) {
        for (Button button : boardButtons()) {
            button.setEnabled(enabled);
        }
    }

    /**
     * Removes both Firebase subscriptions when this activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        if (gamesListener != null) {
            gamesReference.removeEventListener(gamesListener);
        }
        if (selectedRoomListener != null) {
            selectedRoomReference.removeEventListener(selectedRoomListener);
        }
        super.onDestroy();
    }
}
