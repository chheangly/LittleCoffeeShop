package www.chheangly.com.littlecoffeeshop;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity
{
    final int splash_time = 2000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null)
                {
                    Uri photo = user.getPhotoUrl();
                    Intent menu = new Intent(getApplicationContext(), MenuActivity.class);
                    menu.putExtra("user", user);
                    menu.putExtra("photo", photo);
                    menu.putExtra("type", "social");
                    startActivity(menu);
                }
                else
                {
                    Intent get_start = new Intent(SplashScreen.this, GetStartActivity.class);
                    startActivity(get_start);
                }
                finish();
            }
        }, splash_time);
    }
}
