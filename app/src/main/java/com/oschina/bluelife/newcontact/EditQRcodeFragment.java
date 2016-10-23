package com.oschina.bluelife.newcontact;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.oschina.bluelife.newcontact.Utils.Format;
import com.oschina.bluelife.newcontact.model.BusinessCardSource;
import com.oschina.bluelife.newcontact.model.Vcard;
import com.theartofdev.edmodo.cropper.CropImage;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * Created by HiWin10 on 2016/10/21.
 */

public class EditQRcodeFragment extends Fragment {

    @BindView(R.id.add_qrcode_address)
    EditText address;
    @BindView(R.id.add_qrcode_company)
    EditText company;
    @BindView(R.id.add_qrcode_email)
    EditText email;
    @BindView(R.id.add_qrcode_mobile)
    EditText mobile;
    @BindView(R.id.add_qrcode_line)
    EditText line;
    @BindView(R.id.add_qrcode_website)
    EditText website;
    @BindView(R.id.add_qrcode_place)
    EditText place;
    @BindView(R.id.add_qrcode_name)
    EditText name;
    @BindView(R.id.add_qrcode_icon)
    ImageView icon;
    private static final int REQUEST_PICK_FROM_FILE = 2;
    private static final int REQUEST_CROP_INTENT = 3;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.add_qrcode_layout,container,false);
        setHasOptionsMenu(true);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.edit_card_title));
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_add_new_contact,menu);
        final MenuItem menuItem=menu.findItem(R.id.menu_add_done);
        MenuItemCompat.setActionView(menuItem,R.layout.menu_add_done_layout);
        MenuItemCompat.getActionView(menuItem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isValid=true;
                if(name.getText().toString().trim().equalsIgnoreCase("")){
                    name.setError("username cannot be empty.");
                    isValid=false;
                }
                if(!Format.isValidEmail(email.getText())){
                    email.setError("email invalid.");
                    isValid=false;
                }
                if(!Format.isValidPhone(mobile.getText())){
                    mobile.setError("phone invalid.");
                    isValid=false;
                }
                if(isValid){
                    Vcard vcard=new Vcard();
                    vcard.website=website.getText().toString();
                    vcard.place=place.getText().toString();
                    vcard.name=name.getText().toString();
                    vcard.mobilePhone=mobile.getText().toString();
                    vcard.linePhone=line.getText().toString();
                    vcard.address=address.getText().toString();
                    vcard.company=company.getText().toString();
                    vcard.email=email.getText().toString();
                    BusinessCardSource.get().addCard(vcard);
                    Bundle bundle=new Bundle();
                    bundle.putInt(BusinessCardFragment.KEY_INDEX,BusinessCardSource.get().getLastIndex());
                    bundle.putInt(BusinessCardFragment.KEY_ICON,R.drawable.youtube_small);
                    MainActivity mainActivity=(MainActivity)getActivity();
                    BusinessCardFragment fragment=new BusinessCardFragment();
                    fragment.setArguments(bundle);
                    mainActivity.openFragment(fragment);
                }
            }
        });
    }

    @OnClick(R.id.add_qrcode_icon)
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

                icon.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
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
