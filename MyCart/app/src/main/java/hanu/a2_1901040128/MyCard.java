package hanu.a2_1901040128;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import hanu.a2_1901040128.adapter.ProductCartAdapter;
import hanu.a2_1901040128.db.DatabaseHandler;
import hanu.a2_1901040128.models.Products;

public class MyCard extends AppCompatActivity {
    private ArrayList<Products> products = new ArrayList<>();
    private ProductCartAdapter productsCartAdapter;
    private RecyclerView rvProductCart;
    public static TextView totalPrice;
    public DatabaseHandler dbHandler = new DatabaseHandler(MyCard.this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_card);

        Cursor rs = dbHandler.getAll();
        while(rs.moveToNext()) {
            products.add(new Products(rs.getInt(0),
                    rs.getString(1),
                    rs.getString(2),
                    rs.getInt(3),
                    rs.getInt(4),
                    rs.getInt(5)));
        };

        rvProductCart = findViewById(R.id.rvProductCart);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MyCard.this, LinearLayoutManager.VERTICAL, false);
        rvProductCart.setLayoutManager(layoutManager);
        rvProductCart.setHasFixedSize(true);
        rvProductCart.setAdapter(new ProductCartAdapter(products, this));

        totalPrice = findViewById(R.id.totalPrice);

        int getTotalPrice = 0;

        for(int i = 0; i < products.size(); i++){
            getTotalPrice += products.get(i).getSumPrice();
        }

        Locale locale = new Locale("vi", "VN");
        NumberFormat format = NumberFormat.getNumberInstance(locale);
        String newTotalPrice = format.format(getTotalPrice);
        totalPrice.setText("₫ " + newTotalPrice);
    }
}