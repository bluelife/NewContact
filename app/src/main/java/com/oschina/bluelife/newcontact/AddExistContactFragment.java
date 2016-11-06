package com.oschina.bluelife.newcontact;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.bumptech.glide.Glide;
import com.oschina.bluelife.newcontact.Utils.ContactManager;
import com.oschina.bluelife.newcontact.Utils.UIHelper;
import com.oschina.bluelife.newcontact.model.Person;
import com.oschina.bluelife.newcontact.widget.ContactFetcher;
import com.oschina.bluelife.newcontact.widget.transform.RoundedCornersTransformation;
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
    TextView nameText;
    @BindView(R.id.contact_view_avatar)
    ImageView avatar;
    private static final int REQUEST_PICK_FROM_FILE = 2;
    private static final int REQUEST_CROP_INTENT = 3;
    private Person person;
    private String name="test";

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
        ContactFetcher fetcher=new ContactFetcher(getContext());

        boolean hasContact= ContactManager.contactExists(getActivity().getContentResolver(),name);
        if(hasContact){
            person=fetcher.fetchSingle(name);
            nameText.setText(person.name);
            email.setText(person.email);
            //if(person.icon!=null) {
            setIcon();
            //}
        }
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
        int maxSize= UIHelper.getMaxContactPhotoSize(getContext());
        if(requestCode==REQUEST_PICK_FROM_FILE){
            CropImage.activity(data.getData())
                    .setAspectRatio(1,1)
                    .setMaxCropResultSize(maxSize,maxSize)
                    .setMinCropResultSize(96,96)
                    .start(getContext(), this);
        }
        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                ContactManager.updatePhoto(getActivity().getContentResolver(),result.getUri().getPath(),person.rowId);
                //Uri resultUri = result.getUri();
                setIcon();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void setIcon(){
        //Bitmap bitmap =  MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), Uri.parse(path));
        byte[] bytes=ContactManager.openPhoto(getActivity().getContentResolver(),Long.parseLong(person.rowId));
        Glide.with(this).load(bytes)
                .bitmapTransform(new RoundedCornersTransformation(getContext(),15,2))
                .placeholder(R.drawable.contacts_contactslist_head).into(avatar);
    }

    private void setIcon(Uri imageUri){
        Glide.with(this).load(imageUri).bitmapTransform(new RoundedCornersTransformation(getContext(),15,2))
                .placeholder(R.drawable.contacts_contactslist_head)
                .into(avatar);
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
                bundle.putString(EditContactFragment.KEY_NAME,name);
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
