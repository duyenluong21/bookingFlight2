package com.example.bookingflight.activity;


import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;



import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookingflight.R;
import com.example.bookingflight.adapter.SuggestionAdapter;
import com.example.bookingflight.model.LocationSuggestion;
import com.example.bookingflight.model.Shop;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import android.os.Handler;


public class MapActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private MapView mapView;
    private ImageView backButton;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location currentLocation;
    private RecyclerView suggestionList;
    private SuggestionAdapter suggestionAdapter;
    private List<String> suggestions = new ArrayList<>();
    private List<LocationSuggestion> locationSuggestions = new ArrayList<>(); // List to store suggestions with coordinates
    private SearchView mapSearch;
    private Marker currentMarker; // Marker for current location
    private TextView locationName; // TextView để hiển thị tên vị trí
    private RelativeLayout banner, upperBanner; // Banner chứa thông tin vị trí
    Button getDirectionsButton;
    private double lastLat; // Thêm biến để lưu trữ vị trí trước đó
    private double lastLng;
    private double selectedDestinationLat;
    private double selectedDestinationLng;
    private boolean suggestionsLoaded = false;
    private boolean isSearching = false;
    private ImageView turnIcon;
    private TextView distance, roadInfo;
    private boolean isUpperBannerVisible = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Configuration for OpenStreetMap
        Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        mapView = findViewById(R.id.map);
        backButton = findViewById(R.id.backButton);
        getDirectionsButton = findViewById(R.id.getDirectionsButton);
        mapSearch = findViewById(R.id.mapSearch);
        //Banner ở dưới
        banner = findViewById(R.id.banner);
        locationName = findViewById(R.id.locationName);
        banner.setVisibility(View.GONE);
        //Banner ở trên
        upperBanner = findViewById(R.id.upperBanner);
        turnIcon = findViewById(R.id.turnIcon);
        distance = findViewById(R.id.distance);
        roadInfo = findViewById(R.id.roadInfo);
        upperBanner.setVisibility(View.GONE);
        hideUpperBanner();


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // Initialize RecyclerView for suggestions
        suggestionList = findViewById(R.id.suggestionList);
        suggestionList.setLayoutManager(new LinearLayoutManager(this));
        // Khi người dùng chọn gợi ý từ danh sách
        suggestionAdapter = new SuggestionAdapter(suggestions, selectedSuggestion -> {
            for (LocationSuggestion suggestion : locationSuggestions) {
                if (suggestion.getName().equals(selectedSuggestion)) {
                    double lat = suggestion.getLatitude();
                    double lng = suggestion.getLongitude();
                    moveToLocation(lat, lng);
                    upperBanner.setVisibility(View.GONE);
                    showBanner(selectedSuggestion);
                    // Lưu tọa độ đến từ gợi ý đã chọn
                    selectedDestinationLat = lat;
                    selectedDestinationLng = lng;
                    break;
                }
            }
            suggestionList.setVisibility(View.GONE);
        });
        getDirectionsButton.setOnClickListener(v -> {
            if (currentLocation != null) {
                double currentLat = lastLat; // vĩ độ
                double currentLng = lastLng; // kinh độ
                double destinationLat = selectedDestinationLat; // vĩ độ
                double destinationLng = selectedDestinationLng; // kinh độ
                getDirections(currentLat, currentLng, destinationLat, destinationLng);
                // Ẩn banner phía dưới và hiển thị banner phía trên
                banner.setVisibility(View.GONE); // Ẩn banner phía dưới
                showDirectionBanner(); // Hiển thị banner phía trên
            } else {
                Toast.makeText(MapActivity.this, "Vị trí hiện tại không khả dụng", Toast.LENGTH_SHORT).show();
            }
        });
        suggestionList.setAdapter(suggestionAdapter);

        // Check location permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLastLocation();
        }

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(MapActivity.this, Home.class);
            startActivity(intent);
            finish();
        });

        // Set up the map
        mapView.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        // Set up search listener
        mapSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchLocations(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    // Khi người dùng xóa ký tự, ẩn gợi ý
                    suggestions.clear();
                    locationSuggestions.clear();
                    suggestionAdapter.notifyDataSetChanged();
                    suggestionList.setVisibility(View.GONE);  // Ẩn danh sách gợi ý
                    resetMapState(); // Ẩn upperBanner khi tìm kiếm rỗng
                } else {
                    // Nếu đang nhập văn bản, hãy ẩn upperBanner
                    hideUpperBanner();
                }
                return false;
            }
        });

        // Tự động hiển thị gợi ý
        mapSearch.setOnSearchClickListener(v -> {
            hideUpperBanner();
            if (currentLocation != null) {
                if (!isSearching) {
                    updateNearestShops();
                }
            } else {
                Toast.makeText(MapActivity.this, "Vị trí hiện tại không khả dụng", Toast.LENGTH_SHORT).show();
            }
        });

        mapSearch.setOnCloseListener(() -> {
            hideUpperBanner();
            suggestions.clear();
            suggestionAdapter.notifyDataSetChanged();
            suggestionList.setVisibility(View.GONE);  // Ẩn danh sách gợi ý
            return false;
        });
    }
    private void resetMapState() {
        // Xóa các đường đi cũ
        mapView.getOverlays().clear(); // Xóa tất cả overlay trên bản đồ

        hideUpperBanner();

        // Đặt lại trạng thái các biến
        suggestions.clear(); // Xóa danh sách gợi ý hiện tại
        upperBanner.setVisibility(View.GONE); // Đảm bảo banner trên được ẩn
        locationSuggestions.clear(); // Xóa danh sách gợi ý vị trí
        suggestionAdapter.notifyDataSetChanged(); // Cập nhật adapter để phản ánh trạng thái mới
    }

    private void moveToLocation(double lat, double lng) {
        // Lưu vị trí hiện tại trước khi di chuyển
        lastLat = currentLocation.getLatitude();
        lastLng = currentLocation.getLongitude();

        if (currentMarker != null) {
            mapView.getOverlays().remove(currentMarker); // Remove existing marker
        }
        currentMarker = new Marker(mapView);
        currentMarker.setPosition(new GeoPoint(lat, lng));
        currentMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapView.getOverlays().add(currentMarker);
        mapView.invalidate(); // Refresh map
        mapView.getController().setCenter(new GeoPoint(lat, lng));
        mapView.getController().setZoom(15.0); // Adjust zoom
    }
    // Goi y 3 vi tri gan nhat
    private void updateNearestShops() {
        String url = "http://192.168.1.4/TTCS/app/api/readShopMap.php"; // URL lấy danh sách phòng vé

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    JSONArray jsonArray;
                    try {
                        jsonArray = new JSONArray(jsonData);

                        // Xóa danh sách cũ
                        suggestions.clear();
                        locationSuggestions.clear();

                        // Kiểm tra xem có vị trí hiện tại hay không
                        if (currentLocation == null) {
                            runOnUiThread(() -> Toast.makeText(MapActivity.this, "Vị trí hiện tại không khả dụng", Toast.LENGTH_SHORT).show());
                            return;
                        }

                        double currentLat = currentLocation.getLatitude();
                        double currentLng = currentLocation.getLongitude();

                        // Danh sách để lưu trữ các phòng vé gần nhất
                        List<LocationSuggestion> nearestShops = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String placeName = jsonObject.getString("tenShop");
                            double latitude = jsonObject.getDouble("vido");
                            double longitude = jsonObject.getDouble("kinhdo");

                            // Tính khoảng cách
                            double distance = calculateDistance(currentLat, currentLng, latitude, longitude);
                            nearestShops.add(new LocationSuggestion(placeName, latitude, longitude, distance)); // Lưu khoảng cách

                            // Giới hạn khoảng cách là 50 km
                            if (distance <= 50.0) {
                                locationSuggestions.add(new LocationSuggestion(placeName, latitude, longitude));
                            }
                        }

                        // Sắp xếp danh sách các phòng vé gần nhất theo khoảng cách
                        Collections.sort(nearestShops, Comparator.comparingDouble(LocationSuggestion::getDistance));

                        // Chỉ giữ lại 3 phòng vé gần nhất
                        nearestShops = nearestShops.subList(0, Math.min(3, nearestShops.size()));

                        // Cập nhật suggestions với các phòng vé gần nhất
                        suggestions.clear(); // Xóa danh sách gợi ý cũ
                        for (LocationSuggestion suggestion : nearestShops) {
                            suggestions.add(suggestion.getName());
                        }
                        final List<LocationSuggestion> nearestShopsFinal = nearestShops;
                        // Cập nhật adapter
                        runOnUiThread(() -> {
                            suggestionAdapter.notifyDataSetChanged();
                            suggestionList.setVisibility(View.VISIBLE); // Hiển thị danh sách gợi ý
                            hideUpperBanner();
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(MapActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(MapActivity.this, "Failed to get shops", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(MapActivity.this, "Failed to get shops", Toast.LENGTH_SHORT).show());
            }
        });
    }
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Bán kính Trái Đất (kilomet)
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // chuyển đổi sang kilomet
        return distance;
    }
    // Lay vi tri hien tai

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    currentLocation = location;
                    Log.d("CurrentLocation", "Lat: " + currentLocation.getLatitude() + " Lng: " + currentLocation.getLongitude());
                    moveToCurrentLocation();

                    // Gọi updateNearestShops() để tự động hiển thị gợi ý
                    updateNearestShops();
                } else {
                    Toast.makeText(MapActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void moveToCurrentLocation() {
        // Lưu vị trí hiện tại trước khi di chuyển
        lastLat = currentLocation.getLatitude();
        lastLng = currentLocation.getLongitude();

        // Đánh dấu vị trí hiện tại
        if (currentMarker != null) {
            mapView.getOverlays().remove(currentMarker); // Xóa marker cũ
        }
        currentMarker = new Marker(mapView);
        currentMarker.setPosition(new GeoPoint(lastLat, lastLng));
        currentMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapView.getOverlays().add(currentMarker);

        moveToLocation(lastLat, lastLng); // Di chuyển đến vị trí hiện tại
    }
    // Hàm hiển thị banner theo gợi ý
    private void showBanner(String name) {
        locationName.setText(name); // Cập nhật tên vị trí
        banner.setVisibility(View.VISIBLE); // Hiện banner
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDetach(); // Free map resources
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(this, "Location permission is needed to use this feature", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void searchLocations(String query) {
        hideUpperBanner();
        suggestions.clear();
        locationSuggestions.clear();
        String url = "http://192.168.1.4/TTCS/app/api/readShopMap.php?q=" + query;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonData = response.body().string(); // Use .string() to get JSON data
                    JSONArray jsonArray;
                    try {
                        jsonArray = new JSONArray(jsonData);

                        // Check if jsonArray contains data
                        if (jsonArray.length() == 0) {
                            runOnUiThread(() -> Toast.makeText(MapActivity.this, "No suggestions found", Toast.LENGTH_SHORT).show());
                            return;
                        }
                        Set<String> uniqueSuggestions = new HashSet<>(); // Set to hold unique suggestions
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String placeName = jsonObject.getString("tenShop");
                            double latitude = jsonObject.getDouble("vido");
                            double longitude = jsonObject.getDouble("kinhdo");
                            // Add unique suggestion to the set
                            if (uniqueSuggestions.add(placeName)) { // Only add if it's unique
                                // Save location suggestion with coordinates
                                LocationSuggestion suggestion = new LocationSuggestion(placeName, latitude, longitude);
                                suggestions.add(suggestion.getName()); // Save only name for RecyclerView
                                locationSuggestions.add(suggestion); // Save full suggestion with coordinates for later use
                                Log.d("Search Result", "Found place: " + placeName + " at " + latitude + ", " + longitude);
                            }
                        }

                        runOnUiThread(() -> {
                            suggestionAdapter.notifyDataSetChanged();
                            suggestionList.setVisibility(View.VISIBLE);
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(MapActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(MapActivity.this, "Failed to get suggestions", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(MapActivity.this, "Failed to get suggestions", Toast.LENGTH_SHORT).show());
            }
        });
    }
    // Ve duong di
    private void getDirections(double startLat, double startLng, double destLat, double destLng) {
        String url = "https://graphhopper.com/api/1/route?point=" + startLat + "," + startLng + "&point=" + destLat + "," + destLng + "&vehicle=car&key=2ae70f7f-5a88-4234-aba0-2dbf562df84b";
        Log.d("OSRM_URL", "URL: " + url);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    parseRouteData(jsonData); // Phân tích dữ liệu đường đi
                } else {
                    runOnUiThread(() -> Toast.makeText(MapActivity.this, "Failed to get directions", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(MapActivity.this, "Failed to get directions", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void parseRouteData(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray paths = jsonObject.getJSONArray("paths");

            if (paths.length() > 0) {
                JSONObject path = paths.getJSONObject(0);
                String encodedPoints = path.getString("points");
                List<GeoPoint> geoPoints = decodePolyline(encodedPoints);
                // Lấy danh sách các instructions
                JSONArray instructions = path.getJSONArray("instructions");

                // Hiển thị và xử lý các hướng dẫn chỉ đường
                handleInstructions(instructions);

                // Vẽ đường đi trên UI thread
                runOnUiThread(() -> drawRoute(geoPoints));
            } else {
                runOnUiThread(() -> Toast.makeText(MapActivity.this, "No routes found", Toast.LENGTH_SHORT).show());
            }
        } catch (JSONException e) {
            e.printStackTrace();
            runOnUiThread(() -> Toast.makeText(MapActivity.this, "Error parsing directions", Toast.LENGTH_SHORT).show());
        }
    }
    // Phương thức để giải mã polyline
    private List<GeoPoint> decodePolyline(String encoded) {
        List<GeoPoint> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            GeoPoint p = new GeoPoint((double) ((lat * 1e-5)), (double) ((lng * 1e-5)));
            poly.add(p);
        }

        return poly;
    }
    private void drawRoute(List<GeoPoint> points) {
        // Xóa các route cũ
        mapView.getOverlays().clear(); // Xóa tất cả overlay cũ, bạn có thể chọn cách này hoặc chỉ xóa polyline

        // Vẽ đường đi mới
        Polyline polyline = new Polyline();
        polyline.setPoints(points);
        polyline.setColor(Color.BLUE); // Đặt màu cho đường đi
        polyline.setWidth(5.0f); // Đặt độ dày cho đường đi
        mapView.getOverlays().add(polyline); // Thêm polyline vào bản đồ

        // Trung tâm bản đồ trên đường đi
        if (!points.isEmpty()) {
            mapView.getController().setCenter(points.get(0)); // Trung tâm bản đồ tại điểm bắt đầu
            mapView.getController().setZoom(15.0); // Điều chỉnh mức zoom
        }

        mapView.invalidate(); // Làm mới bản đồ để hiển thị đường đi
    }
    // banner chỉ đường
    private void handleInstructions(JSONArray instructions) {
        runOnUiThread(() -> {
            try {
                for (int i = 0; i < instructions.length(); i++) {
                    JSONObject instruction = instructions.getJSONObject(i);
                    String text = instruction.getString("text");
                    String streetName = instruction.optString("street_name", "");
                    double distanceValue = instruction.getDouble("distance");
                    int sign = instruction.getInt("sign");
                    String direction = getDirectionFromSign(sign);

                    // Cập nhật banner chỉ đường
                    updateDirectionBanner(text, streetName, distanceValue, direction);

                    // Đợi một thời gian trước khi hiển thị hướng dẫn tiếp theo
                    if (i < instructions.length() - 1) {
                        final int nextIndex = i + 1;
                        new Handler().postDelayed(() -> handleInstructions(instructions), 3000);
                        return; // Dừng vòng lặp tại đây để đợi
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    private void showDirectionBanner() {
        // Tìm các view trong upperBanner
        if (upperBanner == null) {
            upperBanner = findViewById(R.id.upperBanner);
        }
        if (turnIcon == null) {
            turnIcon = upperBanner.findViewById(R.id.turnIcon);
        }
        if (roadInfo == null) {
            roadInfo = upperBanner.findViewById(R.id.roadInfo);
        }
        if (distance == null) {
            distance = upperBanner.findViewById(R.id.distance);
        }

        // Hiển thị banner phía trên
        // Chỉ hiển thị banner nếu nó không đang hiển thị
        if (upperBanner.getVisibility() != View.VISIBLE) {
            upperBanner.setVisibility(View.VISIBLE);
        }
    }
    private void updateDirectionBanner(String instructionText, String streetName, double distanceValue, String direction) {
        hideUpperBanner();
        // Hiển thị banner nếu chưa có
        if (upperBanner.getVisibility() != View.VISIBLE) {
            showDirectionBanner();
        }
        // Cập nhật thông tin trên banner
        roadInfo.setText(streetName.isEmpty() ? "Unknown road" : streetName);
        distance.setText(String.format("%.0f m", distanceValue));

        // Cập nhật icon dựa trên hướng rẽ
        switch (direction) {
            case "left":
                turnIcon.setImageResource(R.drawable.ic_turn_left);
                break;
            case "right":
                turnIcon.setImageResource(R.drawable.ic_turn_right);
                break;
            case "straight":
                turnIcon.setImageResource(R.drawable.ic_straight);
                break;
            case "slight_left":
                turnIcon.setImageResource(R.drawable.ic_turn_slight_left);
                break;
            case "slight_right":
                turnIcon.setImageResource(R.drawable.ic_turn_slight_right);
                break;
            case "sharp_left":
                turnIcon.setImageResource(R.drawable.ic_turn_sharp_left);
                break;
            case "sharp_right":
                turnIcon.setImageResource(R.drawable.ic_turn_sharp_right);
                break;
            case "uturn":
                turnIcon.setImageResource(R.drawable.ic_uturn_left);
                break;
            default:
                turnIcon.setImageResource(R.drawable.ic_unknown_turn); // Hướng rẽ không xác định
                break;
        }

        // Hiển thị banner chỉ đường
        // Hiển thị banner chỉ đường nếu chưa hiển thị
        if (upperBanner.getVisibility() != View.VISIBLE) {
            showDirectionBanner();
        }
    }
    private String getDirectionFromSign(int sign) {
        hideUpperBanner();
        switch (sign) {
            case -3:
                return "sharp_left";
            case -2:
                return "left";
            case -1:
                return "slight_left";
            case 0:
                return "straight";
            case 1:
                return "slight_right";
            case 2:
                return "right";
            case 3:
                return "sharp_right";
            case 4:
                return "uturn";
            default:
                return "unknown";
        }
    }
    private void hideUpperBanner() {
        if (upperBanner != null) {
            upperBanner.setVisibility(View.GONE); // Ẩn banner
        }
    }

}
