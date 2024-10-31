package com.example.bookingflight.activity;

import android.content.SearchRecentSuggestionsProvider ;

public class AirportSuggestion extends SearchRecentSuggestionsProvider  {
    public static final String AUTHORITY = "com.example.bookingflight.AirportSuggestion";
    public static final int MODE = DATABASE_MODE_QUERIES;

    public AirportSuggestion() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
