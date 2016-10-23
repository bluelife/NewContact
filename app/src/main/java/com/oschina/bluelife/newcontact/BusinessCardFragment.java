package com.oschina.bluelife.newcontact;

import android.graphics.Bitmap;
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
import com.oschina.bluelife.newcontact.model.BusinessCardSource;
import com.oschina.bluelife.newcontact.model.Vcard;

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
    public static String KEY_ICON="icon";
    int index;
    int resId;

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
            index = bundle.getInt(KEY_INDEX);
            resId = bundle.getInt(KEY_ICON);
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
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String info= Const.getQRContent();
        Bitmap bmp = ((BitmapDrawable) ContextCompat.getDrawable(getActivity(),
                resId)).getBitmap();
        QRCodeEncoder qrCodeEncoder=new QRCodeEncoder(info,320,bmp);
        try {
            Bitmap bitmap=qrCodeEncoder.encodeAsBitmap();
            cardView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        Vcard vcard= BusinessCardSource.get().getCard(index);
        name.setText(vcard.name);
        place.setText(vcard.place);
        company.setText(vcard.company);
        mobilephone.setText(vcard.mobilePhone);
        linephone.setText(vcard.linePhone);
        email.setText(vcard.email);
        website.setText(vcard.website);
        address.setText(vcard.address);
    }
}
