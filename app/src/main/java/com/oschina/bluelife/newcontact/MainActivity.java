package com.oschina.bluelife.newcontact;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import com.oschina.bluelife.newcontact.Utils.Format;
import com.oschina.bluelife.newcontact.Utils.UIHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        fragmentManager=getSupportFragmentManager();
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, new EditTestFragment());
        fragmentTransaction.commit();
        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Log.w("ssss",getSupportFragmentManager().getBackStackEntryCount()+"");
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                } else {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                }
            }
        });
        /*TextView title= UIHelper.getToolbarTitleView(this,toolbar);
        Log.w("ddd",title.getParent()+"");
        Toolbar.LayoutParams layoutParams=new Toolbar.LayoutParams(Gravity.CENTER);
        title.setLayoutParams(layoutParams);
        title.setGravity(Gravity.CENTER);*/
        int index="☆".charAt(0);
        int index1="B".charAt(0);
        Log.w("ssss",index+","+index1);
    }



    public void openFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }
}
