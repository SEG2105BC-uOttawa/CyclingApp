package com.example.cyclingapp;

import static com.example.cyclingapp.ui.event.EventAdapter.EventAdapterListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import com.example.cyclingapp.data.model.AppDatabase;
import com.example.cyclingapp.data.model.ClubProfile;
import com.example.cyclingapp.data.model.Event;
import com.example.cyclingapp.data.model.LoggedInUser;
import com.example.cyclingapp.data.model.Role;
import com.example.cyclingapp.ui.event.EventAdapter;
import com.example.cyclingapp.ui.event.EventCreate;
import com.example.cyclingapp.ui.event.EventList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Activity for displaying and updating a club's profile.
 * Provides functionality to view and edit club profile details, including selecting a club logo.
 */
public class ProfilePage extends AppCompatActivity implements EventAdapter.EventAdapterListener {

    private TextView clubNameTextView; // TextView for displaying the club name
    private TextView socialMediaLinkTextView; // TextView for displaying the social media link
    private TextView phoneNumberTextView; // TextView for displaying the phone number
    private TextView mainContactNameTextView; // TextView for displaying the main contact name
    private ImageView clubLogoImageView; // ImageView for displaying the club logo
    private ClubProfileViewModel clubProfileViewModel; // ViewModel for the club profile
    private ClubProfileEventViewModel clubProfileEventViewModel; //ViewModel for events
    private RecyclerView recyclerView; // RecyclerView to display the list of events
    private EventAdapter adapter; // Adapter for the RecyclerView
    private AppDatabase db; // Database instance
    private List<Event> eventList = new ArrayList<>(); // List of events
    private List<Event> filteredList = new ArrayList<>(); // List of filtered events
    private int[] imageIds = new int[]{R.drawable.logo1, R.drawable.logo2, R.drawable.logo3};

    String clubName;

    /**
     * Initializes the activity.
     * Sets up the user interface, view model, and initializes the process to load club profile data.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this bundle contains the most recent data supplied by onSaveInstanceState.
     *                           Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        // Initialize the database
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "cycling-db").build();

        Role userRole = (Role) getIntent().getSerializableExtra("role");

        // Setup RecyclerView
        recyclerView = findViewById(R.id.clubEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EventAdapter(filteredList, this, userRole);
        recyclerView.setAdapter(adapter);

        // Load events from the database

        // Initialize UI components
        clubNameTextView = findViewById(R.id.clubName);
        socialMediaLinkTextView = findViewById(R.id.socialMediaLink);
        phoneNumberTextView = findViewById(R.id.phoneNumber);
        mainContactNameTextView = findViewById(R.id.mainContactName);
        clubLogoImageView = findViewById(R.id.clubLogo);


        clubLogoImageView.setOnClickListener(view -> showLogoSelectionDialog());

        // Get ViewModel
        clubProfileViewModel = new ViewModelProvider(this).get(ClubProfileViewModel.class);
        clubProfileEventViewModel = new ViewModelProvider(this).get(ClubProfileEventViewModel.class);

        int clubProfileId = getIntent().getIntExtra("CLUB_PROFILE_ID", -1);
        if (clubProfileId != -1) {
            clubProfileViewModel.getProfileById(clubProfileId).observe(this, clubProfile -> {
                if (clubProfile != null) {
                    updateUI(clubProfile);
                    loadEvents(clubProfile.getClubName());
                }
            });
        }


        String displayName = getIntent().getStringExtra("displayName");
        if (displayName != null) {
            clubProfileViewModel.getProfileByDisplayName(displayName).observe(this, clubProfile -> {
                if (clubProfile != null) {
                    updateUI(clubProfile);
                    loadEvents(clubProfile.getClubName());
                }
            });
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private void loadEvents(String clubName) {
        if (clubName != null && !clubName.isEmpty()) {
            new Thread(() -> {
                try {
                    List<Event> events = db.eventDao().getEventsByClubName(clubName);
                    runOnUiThread(() -> {
                        filteredList.clear();
                        filteredList.addAll(events); // Use the directly fetched events list
                        adapter.notifyDataSetChanged();
                    });
                } catch (Exception e) {
                    e.printStackTrace(); // Log the exception
                }
            }).start();
        }
    }

    /**
     * Updates the UI elements with the provided club profile data.
     *
     * @param clubProfile The club profile to display.
     */
    private void updateUI(ClubProfile clubProfile) {
        clubNameTextView.setText(clubProfile.getClubName());
        socialMediaLinkTextView.setText(clubProfile.getSocialMediaLink());
        phoneNumberTextView.setText(clubProfile.getPhoneNumber());
        mainContactNameTextView.setText(clubProfile.getMainContactName());
        clubName = clubProfile.getClubName();
    }

    /**
     * Displays a dialog allowing the user to select a logo from predefined drawable resources.
     * Updates the club logo ImageView with the selected image.
     */
    private void showLogoSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_image_picker, null);
        GridView gridView = dialogView.findViewById(R.id.gridView);
        gridView.setAdapter(new ImageAdapter(this, imageIds));

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            clubLogoImageView.setImageResource(imageIds[position]);
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public void onEditClick(Event event) {
        Intent intent = new Intent(ProfilePage.this, EventCreate.class);
        intent.putExtra("EDIT_EVENT", event);
        startActivity(intent);
    }

    /**
     * Handles the delete button click for an event.
     *
     * @param event The event to delete.
     */
    @Override
    public void onDeleteClick(Event event) {
        new Thread(() -> {
            try {
                db.eventDao().deleteEvent(event);
                runOnUiThread(() -> loadEvents(clubName)); // Corrected call to loadEvents
            } catch (Exception e) {
                e.printStackTrace(); // Log the exception
            }
        }).start();
    }


    @Override
    public void onJoinClick(Event event) {

    }
}
