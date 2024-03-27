package com.android.example.mapsimple;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.example.mapsimple.model.PathInfo;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_STORAGE = 1;
    private static final int PICK_IMAGE_REQUEST = 2;
    private ImageView floorplanImageView;

    private FirebaseFirestore db;

    private String startLocationName = "";
    private String endLocationName = "";

    private DrawingView drawingView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        drawingView = findViewById(R.id.drawingView);

        floorplanImageView = findViewById(R.id.floorplanImageView);
        Button uploadFloorplanButton = findViewById(R.id.uploadFloorplanButton);

        Button drawPathButton = findViewById(R.id.drawPathButton);
        Button startLocationButton = findViewById(R.id.startLocationButton);
        Button endLocationButton = findViewById(R.id.endLocationButton);
        RecyclerView savedPathsRecyclerView = findViewById(R.id.savedPathsRecyclerView);
        //initialize firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        savedPathsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create the OnItemLongClickListener implementation
        PathsAdapter.OnItemLongClickListener longClickListener = new PathsAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClicked(String documentId) {

                showOptionsDialog(documentId);
            }
        };

        PathsAdapter adapter = new PathsAdapter(new ArrayList<>(), longClickListener);
        savedPathsRecyclerView.setAdapter(adapter);


        startLocationButton.setOnClickListener(view -> showLocationInputDialog("Start Location"));
        endLocationButton.setOnClickListener(view -> showLocationInputDialog("End Location"));

        Button savePathButton = findViewById(R.id.savePathButton);
        savePathButton.setOnClickListener(view -> {

            String startLocation = startLocationName;
            String endLocation = endLocationName;
            List<PointF> nodeCoordinates = drawingView.getNodes();

            savePathToFirestore(startLocation, endLocation, nodeCoordinates);
        });


        // Fetch paths from Firestore
        fetchPathsFromFirestore();
        drawPathButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawingView.drawPaths();
            }
        });

        uploadFloorplanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestStoragePermission();
            }
        });
    }

    private void showOptionsDialog(String documentId) {

        new AlertDialog.Builder(this)
                .setTitle("Path Options")
                .setItems(new CharSequence[]{"Delete", "Cancel"}, (dialog, which) -> {
                    if (which == 0) {

                        deletePath(documentId);
                    }
                })
                .show();
    }

    private void deletePath(String documentId) {

        db.collection("paths").document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d("AdminActivity", "Path successfully deleted"))
                .addOnFailureListener(e -> Log.w("AdminActivity", "Error deleting path", e));

    }

    // triggered by "Save" button
    private void attemptSavePath() {
        List<PointF> nodeCoordinates = drawingView.getNodes();
        savePathToFirestore(startLocationName, endLocationName, nodeCoordinates);
    }



    private void fetchPathsFromFirestore() {
        // Reference to the "paths" collection
        db.collection("paths")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("AdminActivity", "Listen failed.", e);
                            return;
                        }

                        List<PathInfo> paths = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : snapshots) {
                            if (doc.get("nodeCoordinates") != null) {
                                String documentId = doc.getId(); // Get the document ID
                                String startLocation = doc.getString("startLocation");
                                String endLocation = doc.getString("endLocation");


                                List<Map<String, Double>> firestoreCoordinates = (List<Map<String, Double>>) doc.get("nodeCoordinates");
                                List<PointF> nodeCoordinates = new ArrayList<>();
                                for (Map<String, Double> coord : firestoreCoordinates) {
                                    nodeCoordinates.add(new PointF(coord.get("x").floatValue(), coord.get("y").floatValue()));
                                }
                                paths.add(new PathInfo(documentId, startLocation, endLocation, nodeCoordinates));
                            }
                        }
                        updateRecyclerView(paths);
                    }
                });
    }


    private void savePathToFirestore(String startLocation, String endLocation, List<PointF> nodeCoordinates) {
        Map<String, Object> pathData = new HashMap<>();
        pathData.put("startLocation", startLocation);
        pathData.put("endLocation", endLocation);

        // Converts nodeCoordinates to a Firestore-friendly format
        List<Map<String, Double>> firestoreNodeCoordinates = new ArrayList<>();
        for (PointF point : nodeCoordinates) {
            Map<String, Double> coordinate = new HashMap<>();
            coordinate.put("x", (double) point.x);
            coordinate.put("y", (double) point.y);
            firestoreNodeCoordinates.add(coordinate);
        }

        pathData.put("nodeCoordinates", firestoreNodeCoordinates);

        // Save the path data to Firestore
        db.collection("paths")
                .add(pathData)
                .addOnSuccessListener(documentReference -> Log.d("AdminActivity", "Path saved with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w("AdminActivity", "Error saving path", e));
    }


    private void updateRecyclerView(List<PathInfo> paths) {
        RecyclerView savedPathsRecyclerView = findViewById(R.id.savedPathsRecyclerView);
        PathsAdapter adapter = (PathsAdapter) savedPathsRecyclerView.getAdapter();
        adapter.setPaths(paths);
        adapter.notifyDataSetChanged();
    }

    // Method to show an input dialog
    private void showLocationInputDialog(String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);

        final EditText inputField = new EditText(this);
        inputField.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(inputField);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String locationName = inputField.getText().toString();
            if (title.equals("Start Location")) {
                startLocationName = locationName; // Save start location name
            } else if (title.equals("End Location")) {
                endLocationName = locationName; // Save end location name
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }


    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, PERMISSION_REQUEST_STORAGE);
        } else {
            openImageSelector();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImageSelector();
            } else {
                Toast.makeText(this, "Permission needed to access photos", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openImageSelector() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            floorplanImageView.setImageURI(imageUri);
        }
    }
}
