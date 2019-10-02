package www.chheangly.com.littlecoffeeshop;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class SignUpActivity extends AppCompatActivity
{

    DatabaseReference mDatabase;

    FirebaseAuth mAuth;

    Button closeButton, signUpButton;
    EditText fullNameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    ImageView profileImage;
    FloatingActionButton uploadPhotoButton;

    Uri profile_photo;
    Bitmap profile_photo_bitmap;

    boolean existEmail = false;

    StorageReference mStorage;

    LinearLayout cameraLinear, galleryLinear;
    BottomSheetDialog bottomSheetDialog;

    int GALLERY_CODE = 1100;
    int CAMERA_CODE = 1200;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        linkComponentToView();
        setButtonOnClick();
        createBottomSheet();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_CODE)
        {
            Uri image = data.getData();
            profile_photo = image;
            try
            {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), image);
                profileImage.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
//            profileImage.setImageURI(image);
        }
        if (requestCode == CAMERA_CODE && resultCode == RESULT_OK)
        {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            profile_photo_bitmap = photo;
            profileImage.setImageBitmap(photo);
        }
    }

    private void uploadProfilePhoto(Uri photo)
    {
        if(photo != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this);
            progressDialog.setTitle("Processing...");
            progressDialog.show();
            String image = "image/"+ UUID.randomUUID().toString()+".jpg";
            final StorageReference ref = mStorage.child(image);
            ref.putFile(profile_photo)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                    {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                        {

                            Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                            task.addOnSuccessListener(new OnSuccessListener<Uri>()
                            {
                                @Override
                                public void onSuccess(Uri uri)
                                {
                                    String name = fullNameEditText.getText().toString();
                                    String email = emailEditText.getText().toString();
                                    email = email.replace(".",",");
                                    String password = passwordEditText.getText().toString();
                                    DatabaseReference mRef = mDatabase.child("users").child(email);
                                    mRef.child("Username").setValue(name);
                                    mRef.child("Password").setValue(password);
                                    mRef.child("ImageURL").setValue(uri.toString());
                                    mRef.child("Title").setValue("");
                                }
                            });

                            progressDialog.dismiss();
                            Toast.makeText(SignUpActivity.this, "Account Was Created.. :)", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            progressDialog.dismiss();
                            Toast.makeText(SignUpActivity.this, "Failed : "+e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>()
                    {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot)
                        {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploading : " + (int)progress + "%");
                        }
                    });
        }
    }

    private void createBottomSheet()
    {
        if(bottomSheetDialog == null)
        {
            View view = LayoutInflater.from(SignUpActivity.this).inflate(R.layout.bottom_sheet, null);
            bottomSheetDialog = new BottomSheetDialog(SignUpActivity.this);
            cameraLinear = view.findViewById(R.id.cameraLinear);
            galleryLinear = view.findViewById(R.id.galleryLinear);
            cameraLinear.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    cameraUpload();
                    bottomSheetDialog.hide();
                }
            });
            galleryLinear.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    galleryUpload();
                    bottomSheetDialog.hide();
                }
            });
            bottomSheetDialog.setContentView(view);
        }
    }

    private void galleryUpload()
    {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeType = {"image/jpeg", "image/png", "image/jpg"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeType);
        startActivityForResult(intent, GALLERY_CODE);
    }

    private void cameraUpload()
    {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_CODE);
    }

    private void showUploadOption()
    {
        bottomSheetDialog.show();
    }

    private void setButtonOnClick()
    {
        closeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });

        uploadPhotoButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                showUploadOption();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                boolean error = false;
                String name = fullNameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();

                if(name.equals(""))
                {
                    error = true;
                    fullNameEditText.setError("Input your Name");
                }

                if(email.equals(""))
                {
                    error = true;
                    emailEditText.setError("Input Your Email");
                }

                if(password.equals(""))
                {
                    error = true;
                    passwordEditText.setError("Input Your Password");
                }

                if(!(password.equals(confirmPassword)))
                {
                    error = true;
                    confirmPasswordEditText.setError("Password Didn't Match");
                }

                if(!error)
                {
//                    uploadProfilePhoto(profile_photo);
                    insertUserToDatabase();
                }
            }
        });
    }

    private void insertUserToDatabase()
    {
        String em = emailEditText.getText().toString();
        em = em.replace(".",",");
        mDatabase.child("users").child(em).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    emailEditText.setError("Email Was Token by other");
                    emailEditText.requestFocus();
                }
                else
                {
                    uploadProfilePhoto(profile_photo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    private void linkComponentToView()
    {
        closeButton = findViewById(R.id.closeButton);
        signUpButton = findViewById(R.id.singUpButton);
        fullNameEditText = findViewById(R.id.full_name_edit_text);
        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        profileImage = findViewById(R.id.upload_imageView);
        uploadPhotoButton = findViewById(R.id.floatingActionButton);
    }
}
