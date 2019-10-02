package www.chheangly.com.littlecoffeeshop;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment
{
    Context context;
    RecyclerView recyclerView;
    List<Item> items = new ArrayList<>();
    DatabaseReference mDatabase;
    RecyclerViewAdapter mAdapter;
    String email;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        recyclerView = view.findViewById(R.id.homeRecyclerView);
        email = getArguments().getString("email");

        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        mAdapter = new RecyclerViewAdapter(context, items);
        recyclerView.setAdapter(mAdapter);

        loadData();
        setFav();
        return view;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        this.context = context;
    }

    private void setFav() {
        String mail = email.replace(".com","");
        mDatabase.child("fav").child(mail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String data = dataSnapshot.child("items").getValue().toString();
                    String[] item = data.split(",");
                    for (String str : item) {
                        for (Item i : items) {
                            if (i.getTitle().equals(str)) {
                                i.setFav(true);
                            }
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

    private void loadData()
    {
        items.clear();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("shop").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                String type;
                for (final DataSnapshot data : dataSnapshot.getChildren())
                {
                    type = data.getKey();
                    final String t = type;
                    for (DataSnapshot item : data.getChildren())
                    {
                        String title = item.getKey();
                        String price = item.child("price").getValue().toString();
                        String imgURL = item.child("imgURL").getValue().toString();
//                        Toast.makeText(context, items.size()+"", Toast.LENGTH_SHORT).show();
                        items.add(new Item(title, price, imgURL, type));
                    }
                }
                setFav();
                mAdapter.notifyDataSetChanged();
                recyclerView.invalidate();
//                RecyclerViewAdapter adapter = new RecyclerViewAdapter(context, items);
//                recyclerView.setAdapter(adapter);
//                recyclerView.setLayoutManager(new GridLayoutManager(context, 2));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

}
