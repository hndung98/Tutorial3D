package com.sorimachi.remoteconfigexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.text.UnicodeSetSpanner;
import android.os.Bundle;
import android.text.style.BackgroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    Button btn_top, btn_bottom, btn_dynamic_link;
    ImageView imageView;

    FirebaseRemoteConfig firebaseRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView)findViewById(R.id.image_view);
        btn_top = (Button)findViewById(R.id.btn_top);
        btn_bottom = (Button)findViewById(R.id.btn_bottom);
        btn_dynamic_link = (Button)findViewById(R.id.btn_dynamic);

        //Init
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder().build();
        firebaseRemoteConfig.setConfigSettings(configSettings);

        //set default value
        final Map<String, Object> defaultData = new HashMap<>();
        defaultData.put("btn_top", "version 1.0.0");
        defaultData.put("btn_enable", false);
        defaultData.put("image_link","https://static.vecteezy.com/system/resources/previews/000/433/082/non_2x/nature-scene-with-rainy-day-in-the-park-vector.jpg");
        firebaseRemoteConfig.setDefaults(defaultData);

        //load image
        Picasso.get().load("https://i.pinimg.com/564x/08/bb/1f/08bb1f3ee9dbceaaca66500c3e5621ba.jpg")
                .into(imageView);

        btn_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseRemoteConfig.fetch(0)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    firebaseRemoteConfig.activateFetched();
                                    btn_top.setText(firebaseRemoteConfig.getString("version"));
                                    btn_top.setEnabled(firebaseRemoteConfig.getBoolean("btn_enable"));

                                    Picasso.get().load(firebaseRemoteConfig.getString("image_link"))
                                            .into(imageView);
                                }
                                else
                                {
                                    Toast.makeText(MainActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        btn_dynamic_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent DynamicLinkActivity = new Intent(MainActivity.this, DynamicLinkTestActivity.class);
                startActivity(DynamicLinkActivity);
                finish();
            }
        });

    }
}
