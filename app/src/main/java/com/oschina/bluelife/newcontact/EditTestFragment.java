package com.oschina.bluelife.newcontact;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oschina.bluelife.newcontact.Utils.ContactManager;
import com.oschina.bluelife.newcontact.model.Person;
import com.oschina.bluelife.newcontact.widget.ContactFetcher;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by HiWin10 on 2016/10/19.
 */

public class EditTestFragment extends Fragment {

    private static String TAG= "edittest";
    MainActivity mainActivity;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.edit_test_layout,container,false);
        ButterKnife.bind(this,view);
        mainActivity=(MainActivity)getActivity();
        return view;
    }

    @OnClick(R.id.edit_test_new)
    void openAddPeople(){
        Log.w(TAG, "openAddPeople: ");
        Fragment fragment=new AddContactFragment();
        mainActivity.openFragment(fragment);
    }

    @OnClick(R.id.edit_test_exist)
    void openAddExist(){
        Fragment fragment=new AddExistContactFragment();
        mainActivity.openFragment(fragment);
    }

    @OnClick(R.id.open_contacts)
    void openContacts(){
        Fragment fragment=new ContactListFragment();
        mainActivity.openFragment(fragment);
    }

    @OnClick(R.id.create_qrcode)
    void openCreatedQRCode(){
        String name="aas";

        boolean hasContact= ContactManager.contactExists(getActivity().getContentResolver(),name);
        if(hasContact){

            Bundle bundle=new Bundle();
            bundle.putString(BusinessCardFragment.KEY_INDEX,name);

            Fragment fragment=new BusinessCardFragment();
            fragment.setArguments(bundle);
            mainActivity.openFragment(fragment);
        }

    }
    @OnClick(R.id.open_qrcode)
    void openQRCode(){
        Fragment fragment=new EditQRcodeFragment();
        mainActivity.openFragment(fragment);
    }

    @OnClick(R.id.scan_qrcode)
    void openScan(){
        Fragment fragment=new ScanQRFragment();
        mainActivity.openFragment(fragment);
    }
}
