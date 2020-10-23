package com.sorimachi.remoteconfigexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

public class DynamicLinkTestActivity extends AppCompatActivity {

    FirebaseDynamicLinks firebaseDynamicLinks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_link_test);

        ImageView imageView = (ImageView)findViewById(R.id.image_view);
        Button btnOpenLink = (Button)findViewById(R.id.btn_open_link);
        Button btnReturn = (Button)findViewById(R.id.btn_return);
        Button btnCreateLink = (Button)findViewById(R.id.btn_create_link);
        Button btnGetLink = (Button)findViewById(R.id.btn_get_link);

        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent main_activity = new Intent(DynamicLinkTestActivity.this, MainActivity.class);
                startActivity(main_activity);
                finish();
            }
        });

        btnOpenLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenLink();

            }
        });

        btnCreateLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateLink();
            }
        });

        btnGetLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(DynamicLinkTestActivity.this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        Uri deepLink = null;
                        if(pendingDynamicLinkData != null)
                        {
                            deepLink = pendingDynamicLinkData.getLink();
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(deepLink.toString()));
                            startActivity(intent);
                            Toast.makeText(DynamicLinkTestActivity.this, "link:" + deepLink.toString(), Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(DynamicLinkTestActivity.this, "get link failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(DynamicLinkTestActivity.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DynamicLinkTestActivity.this, "failure share link", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    private void CreateLink()
    {
        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.youtube.com/watch?v=nkY-HmKzgG8"))
                .setDomainUriPrefix("https://dynamiclinktutorial.page.link")
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())//Open links with this app on Androind
                .setIosParameters(new DynamicLink.IosParameters.Builder("com.example.ios").build())
                .buildDynamicLink();
        Uri dynamicLinkUri = dynamicLink.getUri();

        String sharelinktext = "https://dynamiclinktutorial.page.link/?" +
                "link=https://youtube.com/watch?v=nkY-HmKzgG8" +
                "&apn=" + getPackageName() +
                "&st=" + "My Link" +
                "&sd=" + "good video"+
                "&si=" + "https://cdytqn.edu.vn/wp-content/uploads/2018/03/Slide4-1000x450.png";

        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(Uri.parse(sharelinktext))
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if(task.isSuccessful())
                        {
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();

                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_TEXT, shortLink.toString());
                            intent.setType("text/plain");
                            startActivity(intent);
                        }
                    }
                });

    }
    private void OpenLink()
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr=20.344,34.34&daddr=20.5666,45.345"));
        startActivity(intent);
    }
}
