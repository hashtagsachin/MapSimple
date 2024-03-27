package com.android.example.mapsimple;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.example.mapsimple.model.PathInfo;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<PathInfo> fetchedPaths = new ArrayList<>(); // Class level variable to store fetched paths
    private boolean isAdminMode = false;

    private Spinner startSpinner, endSpinner;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        startSpinner = findViewById(R.id.startSpinner);
        endSpinner = findViewById(R.id.endSpinner);
        Button navigateButton = findViewById(R.id.navigateButton);
        final FloorplanView floorplanView = findViewById(R.id.floorplanView);

        //admin drawer menu
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);



        // Call to fetch paths from Firestore
        fetchPathsFromFirestore();

        // Fetch paths and update location names in Spinners from Firestore
        fetchPathsAndUpdateLocations();

        // Setup navigateButton click listener
        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get selected locations and draw path
                String startLocation = startSpinner.getSelectedItem().toString();
                String endLocation = endSpinner.getSelectedItem().toString();
                drawPath(startLocation, endLocation, floorplanView);
            }
        });



        // Setup the navigation drawer (admin mode) item click listener
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_admin) {
                // Open a password prompt dialog for the Admin option
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Enter Admin Password");

                final EditText input = new EditText(MainActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                builder.setView(input);

                builder.setPositiveButton("OK", (dialog, which) -> {
                    String password = input.getText().toString();
                    if (password.equals("1234")) { // super weak password duhh. testing purposes init
                        Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
                builder.show();

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
            return false;
        });
    }

    //fetch location names to update spinners
    private void fetchPathsAndUpdateLocations() {
        db.collection("paths")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Set<String> uniqueLocations = new HashSet<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String startLocation = document.getString("startLocation");
                            String endLocation = document.getString("endLocation");
                            if (startLocation != null) uniqueLocations.add(startLocation);
                            if (endLocation != null) uniqueLocations.add(endLocation);
                        }
                        List<String> locations = new ArrayList<>(uniqueLocations);
                        Collections.sort(locations); // Sort the locations alphabetically
                        updateSpinners(locations);
                    } else {
                        Log.d("MainActivity", "Error getting documents: ", task.getException());
                    }
                });
    }

//    // Updates both Spinners with the fetched location names
    private void updateSpinners(List<String> locations) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, locations);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        startSpinner.setAdapter(adapter);
        endSpinner.setAdapter(adapter);
    }




    // fetchPathsFromFirestore and store it in the fetchedPaths variable
    private void fetchPathsFromFirestore() {
        db.collection("paths")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        fetchedPaths.clear(); // Clear existing paths before adding new ones
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String documentId = document.getId();
                            String startLocation = document.getString("startLocation");
                            String endLocation = document.getString("endLocation");
                            List<Map<String, Double>> firestoreCoordinates = (List<Map<String, Double>>) document.get("nodeCoordinates");
                            List<PointF> nodeCoordinates = convertFirestoreCoordinatesToPointF(firestoreCoordinates);
                            fetchedPaths.add(new PathInfo(documentId, startLocation, endLocation, nodeCoordinates));
                        }

                    } else {
                        Log.w("MainActivity", "Error getting documents: ", task.getException());
                    }
                });
    }

    //convertFirestoreCoordinatesToPointF     MOVED  THIS METHOD TO UTILITY FOR EASIER TESTING
     List<PointF> convertFirestoreCoordinatesToPointF(List<Map<String, Double>> firestoreCoordinates) {
        List<PointF> nodeCoordinates = new ArrayList<>();
        if (firestoreCoordinates != null) {
            for (Map<String, Double> coord : firestoreCoordinates) {
                nodeCoordinates.add(new PointF(coord.get("x").floatValue(), coord.get("y").floatValue()));
            }
        }
        return nodeCoordinates;
    }


    // Draw path on floorplan (but not really. drawPath in FloorplanView does the actual drawing
    private void drawPath(String startLocation, String endLocation, FloorplanView floorplanView) {

        List<PointF> pathCoordinates = getPathCoordinates(startLocation, endLocation);
        floorplanView.drawPath(pathCoordinates);
    }

    // Get path coordinates for given start and end locations
    private List<PointF> getPathCoordinates(String startLocation, String endLocation) {
        for (PathInfo path : fetchedPaths) {
            if (path.getStartLocation().equals(startLocation) && path.getEndLocation().equals(endLocation)) {
                return path.getNodeCoordinates(); // Return the coordinates for the matching path
            }
        }
        return new ArrayList<>(); // Return an empty list if no match is found
    }

}