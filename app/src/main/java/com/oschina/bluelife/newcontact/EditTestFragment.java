package com.oschina.bluelife.newcontact;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by HiWin10 on 2016/10/19.
 */

public class EditTestFragment extends Fragment {

    private static String TAG= "edittest";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.edit_test_layout,container,false);
        ButterKnife.bind(this,view);
        return view;
    }

    @OnClick(R.id.edit_test_new)
    void openAddPeople(){
        Log.w(TAG, "openAddPeople: ");
        MainActivity mainActivity=(MainActivity)getActivity();
        Fragment fragment=new AddContactFragment();
        mainActivity.openFragment(fragment);
    }

    @OnClick(R.id.edit_test_exist)
    void openAddExist(){
        MainActivity mainActivity=(MainActivity)getActivity();
        Fragment fragment=new AddExistContactFragment();
        mainActivity.openFragment(fragment);
    }
}
