package www.chheangly.com.littlecoffeeshop;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class FavoriteFragment extends Fragment {

    RecyclerView recyclerView;
    LinearRecyclerViewAdapter mAdapter;
    DatabaseReference mDatabase;
    List<Item> items = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.favorite_fragment, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAdapter = new LinearRecyclerViewAdapter(getApplicationContext(), items);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        showData();

        return view;
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
        String email = getArguments().getString("email");
        email = email.replace(".com","");
        mDatabase.child("fav").child(email).addListenerForSingleValueEvent(new ValueEventListener() {
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
    }
}
