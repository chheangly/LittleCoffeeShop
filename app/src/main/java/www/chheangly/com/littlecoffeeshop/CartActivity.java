package www.chheangly.com.littlecoffeeshop;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    LinearRecyclerViewAdapter mAdapter;
    List<Item> items = new ArrayList<>();
    DatabaseReference mDatabase;
    TextView totalTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.recyclerView);
        totalTextView = findViewById(R.id.totalTextView);
        mAdapter = new LinearRecyclerViewAdapter(getApplicationContext(), items);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        mDatabase = FirebaseDatabase.getInstance().getReference();

        setSwipeToDelete();

        showData();
    }

    private void getTotal() {
        String price;
        Double total = 0.00;
        for (Item i : items) {
            price = i.getPrice();
            price = price.replace("$","");
            total += Double.parseDouble(price);
        }
        totalTextView.setText("Total : $"+total);

    }

    private void setSwipeToDelete() {
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                int position = viewHolder.getAdapterPosition();
                items.remove(position);
                mAdapter.notifyDataSetChanged();
                getTotal();
            }
        });
        helper.attachToRecyclerView(recyclerView);
    }

    private void getData(String str) {
        final String item = str;
        mDatabase.child("shop").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (data.child(item).exists()) {
                        String title = data.child(item).getKey().toString();
                        String price = data.child(item).child("price").getValue().toString();
                        String imgURL = data.child(item).child("imgURL").getValue().toString();
                        items.add(new Item(title, price, imgURL, data.getKey(), false));

                        mAdapter.notifyDataSetChanged();
                        recyclerView.invalidate();
                        getTotal();

                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showData() {
        items.clear();
        String email = getIntent().getExtras().get("email").toString();
        email = email.replace(".com","");
        mDatabase.child("cart").child(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String items = dataSnapshot.child("items").getValue().toString();
                    if (items.length()>0) {
                        String[] item = items.split(",");
                        for (String str : item) {
                            getData(str);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mAdapter.notifyDataSetChanged();
        recyclerView.invalidate();
        getTotal();
    }
}
