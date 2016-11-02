package com.oschina.bluelife.newcontact;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.oschina.bluelife.newcontact.Utils.ContactManager;
import com.theartofdev.edmodo.cropper.CropImage;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * Created by HiWin10 on 2016/10/19.
 */

public class AddExistContactFragment extends Fragment {
    @BindView(R.id.contact_exist_email)
    TextView email;
    @BindView(R.id.contact_exist_name)
    TextView name;
    @BindView(R.id.contact_view_avatar)
    ImageView avatar;
    private static final int REQUEST_PICK_FROM_FILE = 2;
    private static final int REQUEST_CROP_INTENT = 3;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.contact_view_layout,container,false);
        ButterKnife.bind(this,view);
        setHasOptionsMenu(true);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.exist_contact_title));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        boolean hasContact= ContactManager.contactExists(getActivity().getContentResolver(),"a1");
        Log.w("has",hasContact+"");
    }

    @OnClick(R.id.contact_view_avatar)
    void pickImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), REQUEST_PICK_FROM_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if(requestCode==REQUEST_PICK_FROM_FILE){
            CropImage.activity(data.getData())
                    .setAspectRatio(1,1)
                    .setMaxCropResultSize(400,400)
                    .setMinCropResultSize(400,400)
                    .start(getContext(), this);
        }
        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                avatar.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_view_contact,menu);
        final MenuItem menuItem=menu.findItem(R.id.menu_view_contact);
        MenuItemCompat.setActionView(menuItem,R.layout.menu_contact_edit_layout);
        MenuItemCompat.getActionView(menuItem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity=(MainActivity)getActivity();
                Bundle bundle=new Bundle();
                bundle.putBoolean(EditContactFragment.KEY_EXIST,false);
                Fragment fragment=new EditContactFragment();
                fragment.setArguments(bundle);
                mainActivity.openFragment(fragment);
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
