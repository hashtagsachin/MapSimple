package com.android.example.mapsimple;

import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner startSpinner = findViewById(R.id.startSpinner);
        Spinner endSpinner = findViewById(R.id.endSpinner);
        Button navigateButton = findViewById(R.id.navigateButton);
        final FloorplanView floorplanView = findViewById(R.id.floorplanView);

        // Setup startSpinner
        ArrayAdapter<CharSequence> startAdapter = ArrayAdapter.createFromResource(this,
                R.array.start_locations_array, android.R.layout.simple_spinner_item);
        startAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startSpinner.setAdapter(startAdapter);

        // Setup endSpinner
        ArrayAdapter<CharSequence> endAdapter = ArrayAdapter.createFromResource(this,
                R.array.end_locations_array, android.R.layout.simple_spinner_item);
        endAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        endSpinner.setAdapter(endAdapter);

        // Setup navigateButton click listener
        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String startLocation = startSpinner.getSelectedItem().toString();
                String endLocation = endSpinner.getSelectedItem().toString();
                drawPath(startLocation, endLocation, floorplanView);
            }
        });
    }

    private void drawPath(String startLocation, String endLocation, FloorplanView floorplanView) {
        // Example implementation. You will need to adapt this to your specific logic and paths
        List<Point> pathCoordinates = getPathCoordinates(startLocation, endLocation);
        floorplanView.drawPath(pathCoordinates);
    }

    private List<Point> getPathCoordinates(String startLocation, String endLocation) {
        // Here you should implement the logic to return the list of points based on the start and end location
        List<Point> coordinates = new ArrayList<>();
        // This is a placeholder. Replace it with your actual logic
        if (startLocation.equals("Main Entrance") && endLocation.equals("Office 1")) {
            coordinates.add(new Point(700, 1300)); // Example starting point
            coordinates.add(new Point(700, 800)); // Example intermediate point
            coordinates.add(new Point(595, 800)); // Example ending point
            coordinates.add(new Point(595, 700));
            coordinates.add(new Point(500, 700));
        }
        if (startLocation.equals("Main Entrance") && endLocation.equals("Rest Room 2")) {
            coordinates.add(new Point(700, 1300)); // Example starting point
            coordinates.add(new Point(700, 920)); // Example intermediate point
            coordinates.add(new Point(900, 920)); // Example ending point
        }
        // Add more conditions for different start/end combinations
        return coordinates;
    }
}