package com.oschina.bluelife.newcontact;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oschina.bluelife.newcontact.Utils.Const;
import com.oschina.bluelife.newcontact.Utils.Format;
import com.oschina.bluelife.newcontact.model.ContactViewModel;
import com.oschina.bluelife.newcontact.model.MostConnectViewModel;
import com.oschina.bluelife.newcontact.model.Person;
import com.oschina.bluelife.newcontact.model.PersonCompare;
import com.oschina.bluelife.newcontact.model.PersonViewModel;
import com.oschina.bluelife.newcontact.widget.ContactListAdapter;
import com.oschina.bluelife.newcontact.widget.RecyclerViewFastScroller;
import com.oschina.bluelife.newcontact.widget.model.AlphabetItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by slomka.jin on 2016/10/19.
 */

public class ContactListFragment extends Fragment {
    @BindView(R.id.list_contacts)
    RecyclerView contactsView;

    @BindView(R.id.fastscroller)
    RecyclerViewFastScroller fastScroller;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.contact_list,container,false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String[] names=getActivity().getResources().getStringArray(R.array.list_persons);
        List<PersonViewModel> persons=getPersons(names);
        List<Person> staredPersons=new ArrayList<>();
        staredPersons.add(new Person("平安财富宝","aa@aa.com",""));
        staredPersons.add(new Person("平安随行","aa@aa.com",""));
        staredPersons.add(new Person("随行红包","aa@aa.com",""));
        staredPersons.add(new Person("支付宝","aa@aa.com",""));
        staredPersons.add(new Person("fb","fb@aa.com",""));
        MostConnectViewModel mostConnectViewModel=new MostConnectViewModel(staredPersons);
        List<ContactViewModel> contactViewModels=new ArrayList<>();
        contactViewModels.add(mostConnectViewModel);
        contactViewModels.addAll(persons);

        ContactListAdapter contactListAdapter=new ContactListAdapter(getActivity(),contactViewModels);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        layoutManager.setAutoMeasureEnabled(true);
        contactsView.setLayoutManager(layoutManager);
        contactsView.setAdapter(contactListAdapter);
        fastScroller.setRecyclerView(contactsView);
        fastScroller.setUpAlphabet(getAlphabets(contactViewModels));
    }

    private List<AlphabetItem> getAlphabets(List<ContactViewModel> viewModels){
        List<String> alphabets=new ArrayList<>();
        List<AlphabetItem> alphabetItems=new ArrayList<>();
        for (int i = 0; i < viewModels.size(); i++) {
            String code=viewModels.get(i).getSortCode();
            String word= code;
            if(!alphabets.contains(word)){
                alphabets.add(word);
                alphabetItems.add(new AlphabetItem(i,word,false));
            }
        }
        return alphabetItems;
    }
    private List<PersonViewModel> getPersons(String[] names){
        List<PersonViewModel> personModels=new ArrayList<>();
        List<Person> persons=new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            Person person=new Person(names[i],"example@domain.com","");
            persons.add(person);
        }
        Collections.sort(persons,new PersonCompare());
        for (int i = 0; i < persons.size(); i++) {
            PersonViewModel viewModel=new PersonViewModel(persons.get(i));
            personModels.add(viewModel);
        }
        return personModels;
    }
}
