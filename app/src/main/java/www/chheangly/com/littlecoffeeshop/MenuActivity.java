package www.chheangly.com.littlecoffeeshop;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{

    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    Button signOutButton;
    String email;
    DatabaseReference mDatabase;
    Button cartButton;
    Bundle settingBundle = new Bundle();

    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        changeActionBar();
        getUser();
        googleConfig();
        linkToView();
        setOnClick();
    }

    private void googleConfig()
    {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void linkToView()
    {
        signOutButton = findViewById(R.id.signOutButton);
        cartButton = toolbar.findViewById(R.id.cartButton);
    }

    private void setOnClick()
    {
        signOutButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                mGoogleSignInClient.signOut();
                LoginManager.getInstance().logOut();
                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                finish();
            }
        });
        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
    }

    private void changeActionBar()
    {
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.darkGray));
        toolbar.setTitleTextColor(getResources().getColor(R.color.darkGray));
    }

    private void getUser()
    {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View view = navigationView.getHeaderView(0);

        final TextView user_name = view.findViewById(R.id.user_name);
        final TextView user_email = view.findViewById(R.id.user_email);
        final CircleImageView imageView = view.findViewById(R.id.imageView);

        String type = getIntent().getExtras().get("type").toString();

        if(type.equals("user"))
        {
            String email = getIntent().getExtras().get("email").toString();
            this.email = email;
            final String em = email.replace(".",",");
            mDatabase.child("users").child(em).addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if(dataSnapshot.exists())
                    {
                        String name = dataSnapshot.child("Username").getValue(String.class);
                        String email = em.replace(",",".");
                        String image_url = dataSnapshot.child("ImageURL").getValue(String.class);
                        String password = dataSnapshot.child("Password").getValue(String.class);
                        Log.d("IMAGEURL",image_url);
                        user_name.setText(name);
                        user_email.setText(email);
                        Glide.with(getApplicationContext()).load(image_url).into(imageView);

                        settingBundle.putString("email", email);
                        settingBundle.putString("user", name);
                        settingBundle.putString("img", image_url);
                        settingBundle.putString("password", password);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError)
                {

                }
            });
        }
        else
        {
            FirebaseUser user = (FirebaseUser) getIntent().getExtras().get("user");
            Uri photo = (Uri) getIntent().getExtras().get("photo");
            user_name.setText(user.getDisplayName());
            user_email.setText(user.getEmail());
            email = user.getEmail();
            Glide.with(getApplicationContext()).load(photo).into(imageView);
        }
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    //Adding 3 dot menu
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu)
//    {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return true;
//    }
//      When 3 dot clicked
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item)
//    {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
////        if (id == R.id.action_settings)
////        {
////            return true;
////        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        Fragment selectedFragment = null;
        TextView toolbarTitle = toolbar.findViewById(R.id.toolbarTitle);

        if (id == R.id.nav_home)
        {
            selectedFragment = new HomeFragment();
            Bundle bundle = new Bundle();
            bundle.putString("email", email);
            selectedFragment.setArguments(bundle);
            toolbarTitle.setText("Home");
        }
        else if (id == R.id.nav_menu)
        {
            selectedFragment = new MenuFragment();
        }
        else if (id == R.id.nav_favorite)
        {
            Bundle bundle = new Bundle();
            bundle.putString("email", email);
            selectedFragment = new FavoriteFragment();
            selectedFragment.setArguments(bundle);
            toolbarTitle.setText("Favorite");
        }
//        else if (id == R.id.nav_profile)
//        {
//            selectedFragment = new ProfileFragment();
//            toolbarTitle.setText("Profile");
//        }
        else if (id == R.id.nav_settings)
        {
            String type = getIntent().getExtras().get("type").toString();
            if (!(type.equals("user"))) {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Warning");
                alert.setMessage("You're Logged In With Social Account.\nAction Wont Allowed In Here.");
                alert.setCancelable(true);
                alert.create();
                alert.show();
                return false;
            } else {
                selectedFragment = new SettingFragment();
                selectedFragment.setArguments(settingBundle);
            }
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment != null ? selectedFragment : new HomeFragment()).commit();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
