package com.example.maps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.yandex.mapkit.GeoObjectCollection;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CameraUpdateSource;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.VisibleRegionUtils;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.search.Response;
import com.yandex.mapkit.search.SearchFactory;
import com.yandex.mapkit.search.SearchManagerType;
import com.yandex.mapkit.search.SearchOptions;
import com.yandex.mapkit.search.SearchManager;
import com.yandex.mapkit.search.Session;
import com.yandex.mapkit.Animation;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;
import com.yandex.mapkit.map.MapObjectCollection;



import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity implements Session.SearchListener {

    private MapView mapView;
    private EditText searchBox;
    private Button searchButton;
    private SearchManager searchManager;
    private Session searchSession;

    public void onSearchResponse(Response response) {
        MapObjectCollection mapObjects = mapView.getMap().getMapObjects();
        mapObjects.clear();

        for (GeoObjectCollection.Item searchResult : response.getCollection().getChildren()) {
            Point resultLocation = searchResult.getObj().getGeometry().get(0).getPoint();
            if (resultLocation != null) {
                mapView.getMap().move(
                        new CameraPosition(resultLocation, 14.0f, 0.0f, 0.0f));

                mapObjects.addPlacemark(resultLocation, ImageProvider.fromResource(mapView.getContext(), R.drawable.search_point));
                break;
            }
        }
    }

    @Override
    public void onSearchError(Error error) {
    }

    private void submitQuery(String query) {

        searchManager.submit(query, VisibleRegionUtils.toPolygon(mapView.getMap().getVisibleRegion()), new SearchOptions(), this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapKitFactory.setApiKey("16f7bf2c-c069-4a1e-9c66-da0465aea013");
        MapKitFactory.initialize(this);
        SearchFactory.initialize(this);
        setContentView(R.layout.activity_main);
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED);
        mapView = (MapView)findViewById(R.id.mapView);
        searchBox = (EditText)findViewById(R.id.searchBox);
        searchButton = (Button)findViewById(R.id.searchButton);
        mapView.getMap().move(
                new CameraPosition(new Point(55.751574, 37.573856), 11.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null);
        searchManager = SearchFactory.getInstance().createSearchManager(
                SearchManagerType.ONLINE
        );
    }

    public void OnSearchButtonClick(View view)
    {
        submitQuery(searchBox.getText().toString());
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        MapKitFactory.getInstance().onStart();
    }
}