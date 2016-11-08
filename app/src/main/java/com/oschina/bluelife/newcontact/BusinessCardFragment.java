package com.oschina.bluelife.newcontact;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.oschina.bluelife.newcontact.Utils.Const;
import com.oschina.bluelife.newcontact.Utils.ContactManager;
import com.oschina.bluelife.newcontact.Utils.UIHelper;
import com.oschina.bluelife.newcontact.database.LogoManager;
import com.oschina.bluelife.newcontact.model.BusinessCardSource;
import com.oschina.bluelife.newcontact.model.ContactSource;
import com.oschina.bluelife.newcontact.model.OrgLogo;
import com.oschina.bluelife.newcontact.model.Person;
import com.oschina.bluelife.newcontact.model.Vcard;
import com.oschina.bluelife.newcontact.widget.ContactFetcher;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * Created by HiWin10 on 2016/10/21.
 */

public class BusinessCardFragment extends Fragment {

    @BindView(R.id.qrcode_view)
    ImageView cardView;
    @BindView(R.id.qrcode_person_name)
    TextView name;
    @BindView(R.id.qrcode_person_place)
    TextView place;
    @BindView(R.id.qrcode_person_company)
    TextView company;
    @BindView(R.id.qrcode_person_mobilephone)
    TextView mobilephone;
    @BindView(R.id.qrcode_person_linephone)
    TextView linephone;
    @BindView(R.id.qrcode_person_email)
    TextView email;
    @BindView(R.id.qrcode_person_website)
    TextView website;
    @BindView(R.id.qrcode_person_address)
    TextView address;
    public static String KEY_INDEX="index";
    //public static String KEY_ICON="icon";
    String index;
    String resId;
    private Person person;
    private String logoPath;
    private LogoManager logoManager;
    private boolean hasLogo;
    private static final int REQUEST_PICK_FROM_FILE = 2;
    private static final int REQUEST_CROP_INTENT = 3;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.vcard_layout,container,false);
        ButterKnife.bind(this,view);
        setHasOptionsMenu(true);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.business_card_title));
        Bundle bundle=getArguments();
        if(null!=bundle) {
            index = bundle.getString(KEY_INDEX);
            //resId = bundle.getString(KEY_ICON);
        }
        return view;
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
    private Bitmap getIcon(){
        Bitmap bmp;
        //byte[] bytes= ContactManager.openPhoto(getActivity().getContentResolver(),Long.parseLong(person.rowId));

        if(logoPath!=null){
            BitmapFactory.Options options = new BitmapFactory.Options();
            //options.inMutable = true;
            //Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
            Bitmap bitmap=BitmapFactory.decodeFile(logoPath,options);
            bmp = Bitmap.createScaledBitmap(bitmap,Const.ICON_SIZE,Const.ICON_SIZE,true);
        }
        else{
            bmp = ((BitmapDrawable) ContextCompat.getDrawable(getActivity(),
                    R.drawable.logo_wenzi)).getBitmap();
        }
        return bmp;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        logoManager=new LogoManager(getContext());
        ContactFetcher fetcher=new ContactFetcher(getContext());
        person=fetcher.fetchSingle(index);
        Cursor cursor=logoManager.query(Long.parseLong(person.rowId));
        if(cursor!=null) {
            if(cursor.moveToFirst()) {
                OrgLogo orgLogo = OrgLogo.FACTORY.select_LogoMapper().map(cursor);
                logoPath=orgLogo.image();
                hasLogo=true;
                Log.w("savedlogo",logoPath+"");
            }
            cursor.close();
        }
        //Vcard vcard= BusinessCardSource.get().getCard(index);
        name.setText(person.name);
        place.setText(person.title);
        company.setText(person.company);
        mobilephone.setText(person.phone);
        linephone.setText(person.homePhone);
        email.setText(person.email);
        website.setText(person.url);
        address.setText(person.address);
        createQRLogo();
    }
    private void createQRLogo(){
        Bitmap bmp = getIcon();
        Moshi moshi=new Moshi.Builder().build();
        JsonAdapter<Person> jsonAdapter=moshi.adapter(Person.class);
        String info= jsonAdapter.toJson(person);
        QRCodeEncoder qrCodeEncoder=new QRCodeEncoder(info,Const.ICON_SIZE,bmp);
        try {
            Bitmap bitmap=qrCodeEncoder.encodeAsBitmap();
            cardView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.qrcode_view)
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
                logoPath=result.getUri().getPath();
                Log.w("updatelogo","haslogo:"+hasLogo);
                if(hasLogo)
                   logoManager.update(logoPath,Long.parseLong(person.rowId));
                else{
                    logoManager.insert(Long.parseLong(person.rowId),logoPath,person.name);
                }
                createQRLogo();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
