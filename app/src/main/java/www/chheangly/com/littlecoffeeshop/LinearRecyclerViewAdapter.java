package www.chheangly.com.littlecoffeeshop;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import java.util.List;

public class LinearRecyclerViewAdapter extends RecyclerView.Adapter<LinearRecyclerViewAdapter.MyViewHolder> {

    private Context mContext;
    private List<Item> items;

    public LinearRecyclerViewAdapter(Context mContext, List<Item> items) {
        this.mContext = mContext;
        this.items = items;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.item_type_layout, viewGroup, false);

        return new LinearRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        myViewHolder.titleTextView.setText(items.get(i).getTitle());
        myViewHolder.priceTextView.setText(items.get(i).getPrice());
        Glide.with(mContext).load(items.get(i).getImgURL()).into(myViewHolder.itemImageView);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, priceTextView;
        ImageView itemImageView;
        public MyViewHolder(View view) {
            super(view);
            titleTextView = view.findViewById(R.id.titleTextView);
            priceTextView = view.findViewById(R.id.priceTextView);
            itemImageView = view.findViewById(R.id.imageView);
//            titleTextView = view.findViewById(R.id.titleTextView);
//            priceTextView = view.findViewById(R.id.priceTextView);
//            itemImageView = view.findViewById(R.id.itemImageView);
//            favoriteToggle = view.findViewById(R.id.favoriteToggle);
//            cardView = view.findViewById(R.id.itemCardView);
        }
    }

}
