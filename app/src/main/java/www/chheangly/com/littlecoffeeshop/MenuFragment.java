package www.chheangly.com.littlecoffeeshop;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MenuFragment extends Fragment {

    Context mContext;
    ImageButton breakfastButton, lunchButton, espressoButton, munchiesButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_fragment, container, false);
        breakfastButton = view.findViewById(R.id.breakfastButton);
        lunchButton = view.findViewById(R.id.lunchButton);
        espressoButton = view.findViewById(R.id.espressoButton);
        munchiesButton = view.findViewById(R.id.munchiesButton);
        setButtonOnClick();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        mContext = context;
        super.onAttach(context);
    }

    private void setButtonOnClick() {
        breakfastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent itemsType = new Intent(mContext,ItemsTypeActivity.class);
                itemsType.putExtra("type", "breakfast");
                startActivity(itemsType);
            }
        });

        lunchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent itemsType = new Intent(mContext,ItemsTypeActivity.class);
                itemsType.putExtra("type", "lunch");
                startActivity(itemsType);
            }
        });

        espressoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent itemsType = new Intent(mContext,ItemsTypeActivity.class);
                itemsType.putExtra("type", "espresso");
                startActivity(itemsType);
            }
        });

        munchiesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent itemsType = new Intent(mContext,ItemsTypeActivity.class);
                itemsType.putExtra("type", "munchies");
                startActivity(itemsType);
            }
        });
    }
}
