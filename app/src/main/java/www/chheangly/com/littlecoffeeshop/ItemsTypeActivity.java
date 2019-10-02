package www.chheangly.com.littlecoffeeshop;

import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ItemsTypeActivity extends AppCompatActivity {

    String type;
    Toolbar toolbar;
    RecyclerView recyclerView;
    DatabaseReference mDatabase;
    LinearRecyclerViewAdapter mAdapter;
    List<Item> items;
    ImageView toolBarImageView;
    AppBarLayout appBarLayout;

    CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_type);
        recyclerView = findViewById(R.id.collapsingRecyclerView);
        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.app_bar_layout);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("");
        collapsingToolbarLayout.setExpandedTitleGravity(Gravity.CENTER_HORIZONTAL);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.white));
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.darkGray));

        setOnOffsetChanged();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setTitleTextColor(getResources().getColor(R.color.darkGray));

        toolBarImageView = findViewById(R.id.imageView);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        items = new ArrayList<>();
        mAdapter = new LinearRecyclerViewAdapter(getApplicationContext(),items);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        type = getIntent().getExtras().get("type").toString();

        init();
    }

    private void setOnOffsetChanged() {
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                if (Math.abs(i)-appBarLayout.getTotalScrollRange() == 0) {
                    // Collapsed
                    toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.darkGray), PorterDuff.Mode.SRC_ATOP);
                } else {
                    // Expanded
                    toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {

        switch (type) {
            case "breakfast":
                collapsingToolbarLayout.setTitle("Breakfast");
                toolBarImageView.setImageResource(R.drawable.breakfast_head_image);
                mDatabase.child("shop").child("breakfast").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            String title = data.getKey();
                            String price = data.child("price").getValue().toString();
                            String imgURL = data.child("imgURL").getValue().toString();
                            items.add(new Item(title, price, imgURL, type, false));
                            mAdapter.notifyDataSetChanged();
                            recyclerView.invalidate();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                break;
            case "lunch":
                collapsingToolbarLayout.setTitle("Lunch");
                toolBarImageView.setImageResource(R.drawable.lunch_head_image);
                mDatabase.child("shop").child("lunch").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            String title = data.getKey();
                            String price = data.child("price").getValue().toString();
                            String imgURL = data.child("imgURL").getValue().toString();
                            items.add(new Item(title, price, imgURL, type, false));
                            mAdapter.notifyDataSetChanged();
                            recyclerView.invalidate();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                break;
            case "espresso":
                collapsingToolbarLayout.setTitle("Espresso");
                toolBarImageView.setImageResource(R.drawable.espresso_head_image);
                mDatabase.child("shop").child("coffee").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            String title = data.getKey();
                            String price = data.child("price").getValue().toString();
                            String imgURL = data.child("imgURL").getValue().toString();
                            items.add(new Item(title, price, imgURL, type, false));
                            mAdapter.notifyDataSetChanged();
                            recyclerView.invalidate();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                break;
            case "munchies":
                collapsingToolbarLayout.setTitle("Munchies");
                toolBarImageView.setImageResource(R.drawable.munchies_head_image);
                mDatabase.child("shop").child("Munchies").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            String title = data.getKey();
                            String price = data.child("price").getValue().toString();
                            String imgURL = data.child("imgURL").getValue().toString();
                            items.add(new Item(title, price, imgURL, type, false));
                            mAdapter.notifyDataSetChanged();
                            recyclerView.invalidate();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                break;
        }

    }

}
