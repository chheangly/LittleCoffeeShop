package www.chheangly.com.littlecoffeeshop;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingFragment extends Fragment
{
    de.hdodenhof.circleimageview.CircleImageView imageView;
    EditText userNameEditText, passwordEditText, confirmPasswordEditText, emailEditText, locationEditText;
    Button saveButton;
    DatabaseReference mDatabase;
    Context mContext;
    String email;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.setting_fragment, container, false);
        userNameEditText = view.findViewById(R.id.userNameEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        confirmPasswordEditText = view.findViewById(R.id.confirmPasswordEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        locationEditText = view.findViewById(R.id.locationEditText);
        saveButton = view.findViewById(R.id.saveButton);
        imageView = view.findViewById(R.id.profileImageVIew);

        emailEditText.setEnabled(false);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        loadData();
        onButtonClick();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        mContext = context;
        super.onAttach(context);
    }

    private void onButtonClick() {
        saveButton.setOnClickListener(new View.OnClickListener() {

            AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
            @Override
            public void onClick(View view) {
                String username = userNameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();
                String location = locationEditText.getText().toString();

                if (confirmPassword.equals(password)) {
                    String em = email.replace(".", ",");
                    mDatabase.child("user").child(em).child("Username").setValue(username);
                    mDatabase.child("user").child(em).child("Location").setValue(location);
                    mDatabase.child("user").child(em).child("Password").setValue(password);
                    alert.setTitle("Data Saved");
                    alert.setMessage("Please Restart The Application To See Changed");
                    alert.create();
                    alert.show();
                } else {
                    alert.setTitle("Error");
                    alert.setCancelable(true);
                    alert.setMessage("Password Did Not Match");
                    alert.create();
                    alert.show();
                }
            }
        });
    }

    private void loadData()
    {
        email = getArguments().getString("email");
        String img_url = getArguments().getString("img");
        String username = getArguments().getString("user");
        String password = getArguments().getString("password");

        emailEditText.setText(email);
        passwordEditText.setText(password);
        userNameEditText.setText(username);
        if (img_url != null) {
            Glide.with(mContext).load(img_url).into(imageView);
        }

        String em = email.replace(".", ",");

        mDatabase.child("users").child(em).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("Location").exists()) {
                    locationEditText.setText(dataSnapshot.getValue(String.class));
                } else {
                    locationEditText.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
