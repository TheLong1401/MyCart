package hanu.a2_1901040128;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.os.HandlerCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import hanu.a2_1901040128.adapter.ProductsAdapter;
import hanu.a2_1901040128.db.DatabaseHandler;
import hanu.a2_1901040128.models.Products;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ArrayList<Products> products;
    private ArrayList<Products> productsOld;
    private RecyclerView rvProduct;
    private Integer columnGrid = 2;
    private ProductsAdapter productsAdapter;
    private SearchView searchView;
    DatabaseHandler dbHandle = new DatabaseHandler(MainActivity.this);

    @Override
    protected void onStart() {
        super.onStart();
        dbHandle.openDB();
    }

    @Override
    protected void onStop() {
        super.onStop();
        dbHandle.closeDB();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        this.deleteDatabase("productsCart.db");


        rvProduct = findViewById(R.id.rvProduct);
        GridLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, columnGrid);
        rvProduct.setLayoutManager(layoutManager);
        rvProduct.setHasFixedSize(true);
        products = new ArrayList<>();

        String url = "https://mpr-cart-api.herokuapp.com/products";
        Handler handler = HandlerCompat.createAsync(Looper.getMainLooper());
        Constants.executor.execute(new Runnable() {
            @Override
            public void run() {
                String loadJson = loadJSON(url);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(loadJson == null){
                            Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try {
                            JSONArray jsonArray = new JSONArray(loadJson);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                int ID = object.getInt("id");
                                String thumbnail = object.getString("thumbnail");
                                String name = object.getString("name");
                                int unitPrice = object.getInt("unitPrice");
                                products.add(new Products(ID, thumbnail, name, unitPrice));
                                rvProduct.setAdapter(new ProductsAdapter(products, MainActivity.this));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart_view, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cart:
                Intent intent = new Intent(getApplicationContext(), MyCard.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public String loadJSON(String link) {
        URL url;
        HttpURLConnection urlConnection;

        try {
            url = new URL(link);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            InputStream is = urlConnection.getInputStream();
            Scanner sc = new Scanner(is);
            StringBuilder result = new StringBuilder();
            String line;
            while(sc.hasNextLine()) {
                line = sc.nextLine();
                result.append(line);
            }
            return result.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class Constants {
        public static final ExecutorService executor = Executors.newFixedThreadPool(4);
    }

    @Override
    public void onBackPressed() {
        if(!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }
}