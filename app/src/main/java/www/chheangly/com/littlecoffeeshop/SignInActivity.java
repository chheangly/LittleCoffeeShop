package www.chheangly.com.littlecoffeeshop;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

public class SignInActivity extends AppCompatActivity
{
    private static final int RC_SIGN_IN = 1400;
    Button signUpButton;
    Button signInButton;
    ImageButton googleButton;
    ImageButton facebookButton;
    EditText userEditText;
    EditText passwordEditText;
    ImageView appLogo;
    LoginButton facebookLoginButton;

    DatabaseReference mDatabase;

    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        hideNotificationBar();
        setContentView(R.layout.activity_sign_in);

        googleConfig();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        facebookConfig();

        linkButtonToView();
        setButtonOnClick();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try
            {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            }
            catch (ApiException e)
            {
                Log.w("Google Sign In", "Google sign in failed", e);
                updateUI(null);
            }
        }
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void userLogin(String email, String password)
    {
        final String pw = password;
        final String em = email.replace(".",",");
        mDatabase.child("users").child(em).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    String pass = dataSnapshot.child("Password").getValue(String.class);
                    if(pass.equals(pw))
                    {
//                        Toast.makeText(SignInActivity.this, "Success", Toast.LENGTH_SHORT).show();

                        String email = em.replace(",",".");
                        Intent menu = new Intent(SignInActivity.this, MenuActivity.class);
                        menu.putExtra("email",email);
                        menu.putExtra("type","user");
                        startActivity(menu);
                        finish();
                    }
                    else
                    {
                        passwordEditText.setError("Incorrect Password");
                    }
                }
                else
                {
                    userEditText.setError("Email not found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    private void facebookConfig()
    {
        mCallbackManager = CallbackManager.Factory.create();

        facebookLoginButton = findViewById(R.id.facebookLoginButton);
        facebookLoginButton.setReadPermissions("email", "public_profile");
        facebookLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult)
            {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel()
            {
                updateUI(null);
            }

            @Override
            public void onError(FacebookException error)
            {
                updateUI(null);
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token)
    {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);

                            //Facebook Account
                            Intent menu = new Intent(SignInActivity.this, MenuActivity.class);
                            menu.putExtra("user",user);
                            menu.putExtra("photo", user.getPhotoUrl());
                            menu.putExtra("type","facebook");
                            startActivity(menu);
                            finish();

                        }
                        else
                        {
                            updateUI(null);
                        }
                    }
                });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acc)
    {
        Log.d("Google", "firebaseAuthWithGoogle:" + acc.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acc.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            Log.d("Google", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);

                            //Google Account
                            Intent menu = new Intent(SignInActivity.this, MenuActivity.class);
                            menu.putExtra("user",user);
                            menu.putExtra("photo", user.getPhotoUrl());
                            menu.putExtra("type","google");
                            startActivity(menu);
                            finish();
                        }
                        else
                        {
                            Log.w("Google", "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }

    private void googleConfig()
    {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void linkButtonToView()
    {
        signUpButton = findViewById(R.id.signUpButton);
        googleButton = findViewById(R.id.googleButton);
        signInButton = findViewById(R.id.signInButton);
        facebookButton = findViewById(R.id.facebookButton);
        userEditText = findViewById(R.id.userEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        appLogo = findViewById(R.id.app_logo);
    }

    private void googleSignIn()
    {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut()
    {
        mAuth.signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        updateUI(null);
                    }
                });
    }

    private void revokeAccess()
    {
        mAuth.signOut();
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user)
    {
        if(user != null)
        {
            Uri photo = user.getPhotoUrl();
            Intent menu = new Intent(SignInActivity.this, MenuActivity.class);
            menu.putExtra("user", user);
            menu.putExtra("photo", photo);
            startActivity(menu);
            finish();
        }
        else
        {
            Toast.makeText(SignInActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private void setButtonOnClick()
    {
        signUpButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent sign_up = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(sign_up);
            }
        });
        googleButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                googleSignIn();
            }
        });

        facebookButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                facebookLoginButton.performClick();
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String email = userEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                userLogin(email, password);
            }
        });
    }

    private void hideNotificationBar()
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

}
