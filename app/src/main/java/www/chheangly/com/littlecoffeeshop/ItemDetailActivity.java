package www.chheangly.com.littlecoffeeshop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class ItemDetailActivity extends AppCompatActivity {

    ImageView itemImageView;
    TextView priceTextView, titleTextView, totalPriceTextView;
    Spinner qtySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        initComponent();
        getData();
        setListener();
    }

    private  void setListener() {

        qtySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int qty = Integer.parseInt(qtySpinner.getSelectedItem().toString());
                double price = Double.parseDouble(priceTextView.getText().toString().replace("$",""));
                totalPriceTextView.setText("$"+(qty*price));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void initComponent() {
        itemImageView = findViewById(R.id.itemImageView);
        priceTextView = findViewById(R.id.priceTextView);
        titleTextView = findViewById(R.id.titleTextView);
        totalPriceTextView = findViewById(R.id.totalPriceTextView);
        qtySpinner = findViewById(R.id.qtySpinner);
    }

    private void getData() {
        String img = getIntent().getExtras().get("img").toString();
        String title = getIntent().getExtras().get("title").toString();
        String price = getIntent().getExtras().get("price").toString();
        Glide.with(getApplicationContext()).load(img).into(itemImageView);
        titleTextView.setText(title);
        priceTextView.setText(price);
    }
}
