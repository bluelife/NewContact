package com.oschina.bluelife.newcontact;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.oschina.bluelife.newcontact.Utils.ContactManager;
import com.oschina.bluelife.newcontact.Utils.Format;
import com.oschina.bluelife.newcontact.model.ContactSource;
import com.oschina.bluelife.newcontact.model.Person;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by HiWin10 on 2016/10/19.
 */

public class AddContactFragment extends Fragment {

    @BindView(R.id.add_contact_name)
    EditText name;
    @BindView(R.id.add_contact_email)
    EditText email;
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

    @BindView(R.id.add_contact_website)
    EditText website;
    @BindView(R.id.add_contact_homephone)
    EditText homePhone;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.add_contact_layout,container,false);
        ButterKnife.bind(this,view);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.add_new_contact_title));
        setHasOptionsMenu(true);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return view;
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
                if(isValid){
                    Person person=new Person();
                    person.name=name.getText().toString();
                    person.phone=phone.getText().toString();
                    person.email=email.getText().toString();
                    person.address=address.getText().toString();
                    person.company=company.getText().toString();
                    person.department=department.getText().toString();
                    person.title=place.getText().toString();
                    person.extra=extra.getText().toString();
                    person.spell=Format.getPingYin(person.name);
                    person.url=website.getText().toString();
                    person.homePhone=homePhone.getText().toString();


                    //ContactSource.getInstance().addContact(person);
                    ContactManager.insert(getActivity().getContentResolver(),person);
                    MainActivity mainActivity=(MainActivity)getActivity();
                    mainActivity.openFragment(new ContactListFragment());
                }
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                email.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
