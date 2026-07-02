package com.example.mapageo;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.slider.Slider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mapa;
    private Double lat = -1.01239, lng = -79.46922;
    private float radio = 1;
    private int idc = -1;
    private int idsc = -1;
    private Circle circulo = null;
    private LatLng ultimaPosConsultada = null;
    private ArrayList<Marker> markers = new ArrayList<>();

    private EditText txtLatitud, txtLongitud;
    private Slider sliderRadio;
    private Spinner spnCategoria, spnSubcategoria;

    private RequestQueue requestQueue;

    // Lists for spinners
    private List<String> listaNombresCat = new ArrayList<>();
    private List<Integer> listaIdsCat = new ArrayList<>();
    private List<String> listaNombresSub = new ArrayList<>();
    private List<Integer> listaIdsSub = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtLatitud = findViewById(R.id.txtLatitud);
        txtLongitud = findViewById(R.id.txtLongitud);
        sliderRadio = findViewById(R.id.sliderRadio);
        spnCategoria = findViewById(R.id.spnCategoria);
        spnSubcategoria = findViewById(R.id.spnSubcategoria);

        requestQueue = Volley.newRequestQueue(this);

        obtenerCategorias();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        sliderRadio.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {}

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                radio = slider.getValue();
                updateInterfaz();
            }
        });

        spnCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < listaIdsCat.size()) {
                    idc = listaIdsCat.get(position);
                    obtenerSubcategorias(idc);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spnSubcategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < listaIdsSub.size()) {
                    idsc = listaIdsSub.get(position);
                    updateInterfaz();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


    }

    private void obtenerCategorias() {
        String url = "http://35.153.103.86/turismo10022025/categoria/json_getlistado";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray data = response.getJSONArray("data");
                        listaNombresCat.clear();
                        listaIdsCat.clear();

                        listaNombresCat.add("Todos");
                        listaIdsCat.add(-1);

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject obj = data.getJSONObject(i);
                            listaNombresCat.add(obj.getString("descripcion"));
                            listaIdsCat.add(obj.getInt("id"));
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaNombresCat);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spnCategoria.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Log.e("MainActivity", "Error categorías: " + error.getMessage()));
        requestQueue.add(request);
    }

    private void obtenerSubcategorias(int idc) {
        String url = "http://35.153.103.86/turismo10022025/subcategoria/json_getlistado";
        if (idc != -1) {
            url += "?idc=" + idc;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray data = response.getJSONArray("data");
                        listaNombresSub.clear();
                        listaIdsSub.clear();

                        listaNombresSub.add("Todos");
                        listaIdsSub.add(-1);

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject obj = data.getJSONObject(i);

                            if (idc != -1) {
                                int catIdRespuesta = obj.getInt("categoria_id");
                                if (catIdRespuesta != idc) {
                                    continue; // saltar esta subcategoría, no pertenece a la categoría seleccionada
                                }
                            }

                            listaNombresSub.add(obj.getString("descripcion"));
                            listaIdsSub.add(obj.getInt("id"));
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaNombresSub);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spnSubcategoria.setAdapter(adapter);

                        idsc = -1;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Log.e("MainActivity", "Error subcategorías: " + error.getMessage()));
        requestQueue.add(request);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mapa = googleMap;
        mapa.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mapa.getUiSettings().setZoomControlsEnabled(true);
        mapa.getUiSettings().setZoomGesturesEnabled(true);

        LatLng initialPos = new LatLng(lat, lng);
        mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(initialPos, 15));

        mapa.setOnCameraIdleListener(() -> {
            LatLng center = mapa.getCameraPosition().target;
            lat = center.latitude;
            lng = center.longitude;

            if (ultimaPosConsultada == null || distanciaSignificativa(center, ultimaPosConsultada)) {
                ultimaPosConsultada = center;
                updateInterfaz();
            } else {
                txtLatitud.setText(String.format("%.4f", lat));
                txtLongitud.setText(String.format("%.4f", lng));
            }
        });

        mapa.setOnMarkerClickListener(marker -> {
            marker.showInfoWindow();
            return true;
        });

        updateInterfaz();
    }

    private boolean distanciaSignificativa(LatLng a, LatLng b) {
        float[] resultado = new float[1];
        android.location.Location.distanceBetween(a.latitude, a.longitude, b.latitude, b.longitude, resultado);
        return resultado[0] > 20; // metros de tolerancia, ajusta a tu gusto
    }

    private void updateInterfaz() {
        if (mapa == null) return;
        txtLatitud.setText(String.format("%.4f", lat));
        txtLongitud.setText(String.format("%.4f", lng));
        if (circulo != null) circulo.remove();
        circulo = mapa.addCircle(new CircleOptions()
                .center(new LatLng(lat, lng))
                .radius(radio * 100)
                .strokeColor(Color.RED)
                .fillColor(Color.argb(50, 150, 50, 50)));
        obtenerLugares();
    }

    private void obtenerLugares() {
        for (Marker marker : markers) marker.remove();
        markers.clear();

        StringBuilder url = new StringBuilder("http://35.153.103.86/turismo10022025/lugar_turistico/json_getlistadoMapa?lat=" + lat +
                "&lng=" + lng + "&radio=" + radio);

        if (idc != -1) url.append("&idc=").append(idc);
        if (idsc != -1) url.append("&idsc=").append(idsc);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url.toString(), null,
                response -> {
                    try {
                        JSONArray jsonLista = response.getJSONArray("data");
                        float radioMetros = radio * 100;

                        for (int i = 0; i < jsonLista.length(); i++) {
                            JSONObject lugar = jsonLista.getJSONObject(i);
                            double lugarLat = lugar.getDouble("lat");
                            double lugarLng = lugar.getDouble("lng");

                            float[] resultado = new float[1];
                            android.location.Location.distanceBetween(lat, lng, lugarLat, lugarLng, resultado);

                            if (resultado[0] <= radioMetros) {
                                LatLng pos = new LatLng(lugarLat, lugarLng);
                                Marker marker = mapa.addMarker(new MarkerOptions()
                                        .position(pos)
                                        .title(lugar.getString("nombre")));
                                markers.add(marker);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Log.e("MainActivity", "Error Volley: " + error.getMessage()));
        requestQueue.add(request);
    }
}
