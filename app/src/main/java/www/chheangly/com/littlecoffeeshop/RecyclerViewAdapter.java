package www.chheangly.com.littlecoffeeshop;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private Context context;
    private List<Item> items;
    DatabaseReference mDatabase;
    String email;

    public RecyclerViewAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.cardview_item, viewGroup, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {
        myViewHolder.titleTextView.setText(items.get(i).getTitle());
        myViewHolder.priceTextView.setText(items.get(i).getPrice());
        Glide.with(context).load(items.get(i).getImgURL()).into(myViewHolder.itemImageView);
        myViewHolder.favoriteToggle.setChecked(items.get(i).getFav());
        myViewHolder.favoriteToggle.setEnabled(false);

        myViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent item_detail = new Intent(context, ItemDetailActivity.class);
                item_detail.putExtra("img",items.get(i).getImgURL());
                item_detail.putExtra("title", items.get(i).getTitle());
                item_detail.putExtra("price", items.get(i).getPrice());
                context.startActivity(item_detail);
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, priceTextView;
        ImageView itemImageView;
        ToggleButton favoriteToggle;
        CardView cardView;
        public MyViewHolder(View view) {
            super(view);
            titleTextView = view.findViewById(R.id.titleTextView);
            priceTextView = view.findViewById(R.id.priceTextView);
            itemImageView = view.findViewById(R.id.itemImageView);
            favoriteToggle = view.findViewById(R.id.favoriteToggle);
            cardView = view.findViewById(R.id.itemCardView);
        }
    }
}
