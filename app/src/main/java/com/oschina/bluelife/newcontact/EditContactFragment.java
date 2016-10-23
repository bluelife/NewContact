package com.oschina.bluelife.newcontact;

import android.os.Bundle;
import android.os.Handler;
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
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.oschina.bluelife.newcontact.Utils.Format;

import com.oschina.bluelife.newcontact.model.ContactSource;
import com.oschina.bluelife.newcontact.model.Person;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by slomka.jin on 2016/10/20.
 */

public class EditContactFragment extends Fragment {
    private Handler handler=new Handler();
    @BindView(R.id.add_contact_email)
    EditText email;
    @BindView(R.id.add_contact_name)
    EditText name;
    @BindView(R.id.add_contact_phone)
    EditText phone;
    @BindView(R.id.add_contact_company)
    EditText company;
    @BindView(R.id.add_contact_department)
    EditText department;
    @BindView(R.id.add_contact_address)
    EditText address;
    @BindView(R.id.add_contact_place)
    EditText place;
    @BindView(R.id.add_contact_extra)
    EditText extra;
    @BindView(R.id.edit_contact_del_btn)
    Button delBtn;
    public static final String KEY_EXIST="exist";
    public static final String KEY_INDEX ="index";
    private int index;
    private boolean isExistItem;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.edit_contact_layout,container,false);
        ButterKnife.bind(this,view);
        setHasOptionsMenu(true);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.edit_contact_title));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputMethodManager =  (InputMethodManager)getActivity().getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInputFromWindow(email.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                email.requestFocus();
            }
        },300);
        init();
        return view;
    }
    private void init(){
        Bundle bundle=getArguments();
        if(bundle!=null){
            isExistItem=bundle.getBoolean(KEY_EXIST);
            if(isExistItem){
                index=bundle.getInt(KEY_INDEX);
                delBtn.setVisibility(View.VISIBLE);
                Person person= ContactSource.getInstance().getPerson(index);
                email.setText(person.email);
                name.setText(person.name);
                phone.setText(person.phone);
                company.setText(person.company);
                place.setText(person.place);
                address.setText(person.address);
                extra.setText(person.extra);
                department.setText(person.department);
            }
            else{
                delBtn.setVisibility(View.GONE);
            }
        }
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
                if(name.getText().toString().trim().equals("")){
                    name.setError("name cannot be empty");
                }
                if(!Format.isValidEmail(email.getText())){
                    email.setError("email invalid.");
                    isValid=false;
                }
                if(!Format.isValidPhone(phone.getText())){
                    phone.setError("phone invalid.");
                    isValid=false;
                }
            }
        });
    }

    @OnClick(R.id.edit_contact_del_btn)
    void onDelete(){
        ContactSource.getInstance().removeContact(index);
        getActivity().getSupportFragmentManager().popBackStack();
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
