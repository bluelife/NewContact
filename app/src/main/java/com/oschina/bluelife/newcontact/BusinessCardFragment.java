package com.oschina.bluelife.newcontact;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.oschina.bluelife.newcontact.Utils.Const;
import com.oschina.bluelife.newcontact.Utils.ContactManager;
import com.oschina.bluelife.newcontact.model.BusinessCardSource;
import com.oschina.bluelife.newcontact.model.ContactSource;
import com.oschina.bluelife.newcontact.model.Person;
import com.oschina.bluelife.newcontact.model.Vcard;
import com.oschina.bluelife.newcontact.widget.ContactFetcher;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import butterknife.BindView;
import butterknife.ButterKnife;

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
        byte[] bytes= ContactManager.openPhoto(getActivity().getContentResolver(),Long.parseLong(person.rowId));

        if(bytes!=null){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
            bmp = Bitmap.createScaledBitmap(bitmap,Const.ICON_SIZE,Const.ICON_SIZE,true);
        }
        else{
            bmp = ((BitmapDrawable) ContextCompat.getDrawable(getActivity(),
                    R.drawable.contacts_contactslist_head)).getBitmap();
        }
        return bmp;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        ContactFetcher fetcher=new ContactFetcher(getContext());
        person=fetcher.fetchSingle(index);

        //Vcard vcard= BusinessCardSource.get().getCard(index);
        name.setText(person.name);
        place.setText(person.address);
        company.setText(person.company);
        mobilephone.setText(person.phone);
        linephone.setText(person.homePhone);
        email.setText(person.email);
        website.setText(person.url);
        address.setText(person.address);
        Moshi moshi=new Moshi.Builder().build();
        JsonAdapter<Person> jsonAdapter=moshi.adapter(Person.class);
        String info= jsonAdapter.toJson(person);
        Bitmap bmp = getIcon();
        QRCodeEncoder qrCodeEncoder=new QRCodeEncoder(info,Const.ICON_SIZE,bmp);
        try {
            Bitmap bitmap=qrCodeEncoder.encodeAsBitmap();
            cardView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
