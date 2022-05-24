package hanu.a2_1901040128.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

public class ProductCartAdapter extends RecyclerView.Adapter<ProductCartAdapter.ProductCartHolder> {
    private ArrayList<Products> products = new ArrayList<>();
    private Context context;

    public ProductCartAdapter(ArrayList<Products> products, Context context) {
        this.products = products;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductCartAdapter.ProductCartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        DatabaseHandler dbHandle = new DatabaseHandler(parent.getContext());
        View itemView = inflater.inflate(R.layout.list_product_cart, parent, false);
        return new ProductCartAdapter.ProductCartHolder(itemView, context);
    }


    @Override
    public void onBindViewHolder(@NonNull ProductCartHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.bind(products.get(position));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ProductCartHolder extends RecyclerView.ViewHolder {
        private ImageView thumbnail, plusBtn, minusBtn, addCartBtn;
        private TextView name, sumPrice, quantity, unitPrice;
        public DatabaseHandler dbHandle;
        private Products p;
        public ProductCartHolder(@NonNull View itemView, Context context) {
            super(itemView);

            thumbnail = this.itemView.findViewById(R.id.imgProductCart);
            name = this.itemView.findViewById(R.id.productCartName);
            unitPrice = this.itemView.findViewById(R.id.productCartPrice);
            sumPrice = this.itemView.findViewById(R.id.sumPrice);
            quantity = this.itemView.findViewById(R.id.quantity);
            plusBtn = this.itemView.findViewById(R.id.plusBtn);
            minusBtn = this.itemView.findViewById(R.id.minusBtn);
            dbHandle = new DatabaseHandler(itemView.getContext());
        }

        public void bind (Products p) {
            this.p = p;

            name.setText(p.getName());

            int ID = p.getID();

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

            String newUnitPrice = format.format(rawPrice);
            unitPrice.setText("₫ " + newUnitPrice);

            quantity.setText("" + p.getQuantity());

            int rawSumPrice = p.getQuantity() * p.getUnitPrice();
            String newSumPrice = format.format(rawSumPrice);
            p.setSumPrice(rawSumPrice);
            sumPrice.setText("₫ " + newSumPrice);


            plusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int newQuantity = p.getQuantity() + 1;
                    p.setQuantity(newQuantity);
                    quantity.setText("" + p.getQuantity());
                    int sPrice = p.getQuantity() * p.getUnitPrice();
                    String newSPrice = format.format(sPrice);
                    p.setSumPrice(sPrice);
                    sumPrice.setText("₫ " + newSPrice);
                    dbHandle.Update(new Products(p.getID(), p.getThumbnail(), p.getName(), p.getUnitPrice(), p.getQuantity(), p.getSumPrice()));
                    int getTotalPrice = 0;
                    for(int i = 0; i < products.size(); i++){
                        getTotalPrice += products.get(i).getSumPrice();
                    }
                    String newTotalPrice = format.format(getTotalPrice);
                    TextView changeTotalPrice = MyCard.totalPrice;
                    changeTotalPrice.setText("₫ " +newTotalPrice);

                    notifyDataSetChanged();
                }
            });

            minusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int newQuantity = p.getQuantity() - 1;
                    p.setQuantity(newQuantity);
                    if(p.getQuantity() == 0){
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        builder.setTitle("Delete " + p.getName() + " ?");
                        builder.setMessage("Are you sure you want to delete " + p.getName() + " ?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                products.remove(p);
                                dbHandle.Delete(p.getID());
                                notifyDataSetChanged();
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                        builder.create().show();
                    }else {
                        quantity.setText("" + p.getQuantity());
                        int sPrice = p.getQuantity() * p.getUnitPrice();
                        String newSPrice = format.format(sPrice);
                        p.setSumPrice(sPrice);
                        sumPrice.setText("₫ " + newSPrice);
                        dbHandle.Update(new Products(p.getID(), p.getThumbnail(), p.getName(), p.getUnitPrice(), p.getQuantity(), p.getSumPrice()));
                    }
                    int getTotalPrice = 0;
                    for(int i = 0; i < products.size(); i++){
                        getTotalPrice += products.get(i).getSumPrice();
                    }
                    String newTotalPrice = format.format(getTotalPrice);
                    TextView changeTotalPrice = MyCard.totalPrice;
                    changeTotalPrice.setText("₫ " +newTotalPrice);
                    notifyDataSetChanged();
                }
            });
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

