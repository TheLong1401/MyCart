package hanu.a2_1901040128.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.os.HandlerCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import hanu.a2_1901040128.MainActivity;
import hanu.a2_1901040128.MyCard;
import hanu.a2_1901040128.R;
import hanu.a2_1901040128.db.DatabaseHandler;
import hanu.a2_1901040128.models.Products;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductHolder> {
    private ArrayList<Products> products = new ArrayList<>();
    private ArrayList<Products> checkList = new ArrayList<>();
    private Context context;

    public ProductsAdapter(ArrayList<Products> products, Context context) {
        this.products = products;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductsAdapter.ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.list_product, parent, false);
        DatabaseHandler dbHandler = new DatabaseHandler(parent.getContext());
        return new ProductHolder(itemView, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.bind(products.get(position));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void filterList(ArrayList<Products> list) {
    }

    public class ProductHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView thumbnail, addCartBtn;
        private TextView name, unitPrice, quantity, sumPrice;
        private DatabaseHandler dbHandle;
        private Products p;
        public ProductHolder(@NonNull View itemView, Context context) {
            super(itemView);

            thumbnail = this.itemView.findViewById(R.id.imgProduct);
            name = this.itemView.findViewById(R.id.tvProductName);
            unitPrice = this.itemView.findViewById(R.id.tvProductPrice);
            addCartBtn = this.itemView.findViewById(R.id.addCartBtn);
            addCartBtn.setOnClickListener(this);
        }

        public void bind (Products p) {
            this.p = p;

            dbHandle = new DatabaseHandler(itemView.getContext());

            name.setText(p.getName());

            String link = p.getThumbnail();

            Handler handler = HandlerCompat.createAsync(Looper.getMainLooper());
            MainActivity.Constants.executor.execute(new Runnable() {
                @Override
                public void run() {
                    Bitmap bitmap = loadImg(link);
                    if (bitmap != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                thumbnail.setImageBitmap(bitmap);
                            }
                        });
                    }
                }
            });

            Locale locale = new Locale("vi", "VN");

            NumberFormat format = NumberFormat.getNumberInstance(locale);

            Integer rawPrice = p.getUnitPrice();

            String priceFormat = format.format(rawPrice);
            unitPrice.setText("₫ " + priceFormat);

            p.setQuantity(p.getQuantity() + 1);

        }

        @Override
        public void onClick(View view){

            int rawSumPrice = p.getQuantity() * p.getUnitPrice();
            p.setSumPrice(rawSumPrice);

            checkList.clear();
            Cursor rs = dbHandle.getAll();
            while(rs.moveToNext()) {
                checkList.add(new Products(rs.getInt(0),
                        rs.getString(1),
                        rs.getString(2),
                        rs.getInt(3),
                        rs.getInt(4),
                        rs.getInt(5)));
            };

            if(checkList.size() > 0){
                boolean exists = false;
                for(int i = 0; i < checkList.size(); i++){
                    if(checkList.get(i).getID() == p.getID()){
                        p.setQuantity(checkList.get(i).getQuantity() + 1);
                        int newSumPrice = p.getQuantity() * p.getUnitPrice();
                        p.setSumPrice(newSumPrice);
                        dbHandle.Update(new Products(p.getID(), p.getThumbnail(), p.getName(), p.getUnitPrice(), p.getQuantity(), p.getSumPrice()));
                        exists = true;
                    }
                }
                if(exists == false){
                    dbHandle.Insert(new Products(p.getID(), p.getThumbnail(), p.getName(), p.getUnitPrice(), p.getQuantity(), p.getSumPrice()));
                }
            } else {
                dbHandle.Insert(new Products(p.getID(), p.getThumbnail(), p.getName(), p.getUnitPrice(), p.getQuantity(), p.getSumPrice()));
            }
            Toast.makeText(view.getContext(), "Add to cart successfully", Toast.LENGTH_SHORT).show();
        }

        private Bitmap loadImg(String link) {
            try {
                URL url = new URL(link);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream is = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                return bitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return  null;
        }
    }
}

