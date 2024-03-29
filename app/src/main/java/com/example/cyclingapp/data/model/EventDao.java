package com.example.cyclingapp.data.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;

/**
 * Data Access Object (DAO) for events.
 * This interface defines the database interactions used to manage event data.
 * It uses annotations to define SQL queries and associate them with method calls.
 */
@Dao
public interface EventDao {

    /**
     * Inserts an event into the database.
     *
     * @param event The event object to be inserted.
     */
    @Insert
    void insertEvent(Event event);

    /**
     * Updates an existing event in the database.
     *
     * @param event The event object to be updated.
     */
    @Update
    void updateEvent(Event event);

    /**
     * Deletes an event from the database.
     *
     * @param event The event object to be deleted.
     */
    @Delete
    void deleteEvent(Event event);

    /**
     * Retrieves all events from the database.
     *
     * @return A list of all events.
     */
    @Query("SELECT * FROM events")
    List<Event> getAllEvents();

    @Query("SELECT * FROM events WHERE clubName = :clubName")
    List<Event> getEventsByClubName(String clubName);


}
