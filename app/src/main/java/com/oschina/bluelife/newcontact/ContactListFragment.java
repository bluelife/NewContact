package com.oschina.bluelife.newcontact;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.oschina.bluelife.newcontact.Utils.Const;

import com.oschina.bluelife.newcontact.event.UpdateFastScrollEvent;
import com.oschina.bluelife.newcontact.model.ContactSource;
import com.oschina.bluelife.newcontact.model.ContactViewModel;
import com.oschina.bluelife.newcontact.model.MostConnectViewModel;
import com.oschina.bluelife.newcontact.model.Person;
import com.oschina.bluelife.newcontact.model.PersonCompare;
import com.oschina.bluelife.newcontact.model.PersonViewModel;
import com.oschina.bluelife.newcontact.model.SectionViewModel;
import com.oschina.bluelife.newcontact.widget.ContactListAdapter;
import com.oschina.bluelife.newcontact.widget.RecyclerViewFastScroller;
import com.oschina.bluelife.newcontact.widget.model.AlphabetItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by slomka.jin on 2016/10/19.
 */

public class ContactListFragment extends Fragment implements
        ActionMode.Callback, ContactListAdapter.ItemListener {
    @BindView(R.id.list_contacts)
    RecyclerView contactsView;

    @BindView(R.id.fastscroller)
    RecyclerViewFastScroller fastScroller;
    private ArrayList<AlphabetItem> alphabetItems;
    private List<ContactViewModel> contactViewModels;
    ActionMode actionMode;
    private ContactListAdapter contactListAdapter;
    GestureDetectorCompat gestureDetector;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.contact_list,container,false);
        ButterKnife.bind(this,view);
        setHasOptionsMenu(true);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.contact_list_title));
        toolbar.setSubtitle("123445556@cctv.com");
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_contact_list, menu);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ContactSource contactSource=ContactSource.getInstance();
        contactListAdapter=new ContactListAdapter(getActivity(),contactSource.getContactViewModels());
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        layoutManager.setAutoMeasureEnabled(true);
        contactsView.setLayoutManager(layoutManager);
        contactsView.setAdapter(contactListAdapter);
        contactsView.setItemAnimator(new DefaultItemAnimator());
        fastScroller.setRecyclerView(contactsView);
        fastScroller.setUpAlphabet(contactSource.getAlphabetItems());
        contactListAdapter.setItemListener(this);

    }

    private List<ContactViewModel> addPersonAndSections(){
        String[] names=getActivity().getResources().getStringArray(R.array.list_persons);
        List<Person> staredPersons=new ArrayList<>();
        staredPersons.add(new Person("平安财富宝","aa@aa.com",""));
        staredPersons.add(new Person("平安随行","aa@aa.com",""));
        staredPersons.add(new Person("随行红包","aa@aa.com",""));
        staredPersons.add(new Person("支付宝","aa@aa.com",""));
        staredPersons.add(new Person("fb","fb@aa.com",""));
        MostConnectViewModel mostConnectViewModel=new MostConnectViewModel(staredPersons);
        contactViewModels=new ArrayList<>();
        contactViewModels.add(mostConnectViewModel);
        List<PersonViewModel> persons=getPersons(names);
        List<ContactViewModel> sortViewModels=new ArrayList<>();
        contactViewModels.addAll(persons);

        List<String> alphabets=new ArrayList<>();
        alphabetItems=new ArrayList<>();
        for (int i = 0; i < contactViewModels.size(); i++) {
            ContactViewModel contactViewModel=contactViewModels.get(i);
            String code=contactViewModel.getSortCode();
            String word= code;
            SectionViewModel sectionViewModel=null;
            if(!alphabets.contains(word)){
                String label=code==Const.STAR?getString(R.string.contact_list_most_connect):code;
                sectionViewModel=new SectionViewModel(label,code);
                for (int j = i; j < contactViewModels.size(); j++) {
                    if(contactViewModels.get(j).getSortCode().equalsIgnoreCase(code)){
                        sectionViewModel.plus();
                    }
                    else{

                        break;
                    }
                }

                sortViewModels.add(sectionViewModel);
                alphabets.add(word);
            }
            sortViewModels.add(contactViewModels.get(i));
        }
        //add alphabets
        alphabets.clear();
        for (int i = 0; i < sortViewModels.size(); i++) {
            String word=sortViewModels.get(i).getSortCode();
            if(!alphabets.contains(word)){
                alphabets.add(word);
                alphabetItems.add(new AlphabetItem(i,word,false));
            }
        }
        return sortViewModels;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
                break;
            case R.id.menu_contact_search:
                break;
            case R.id.menu_contact_add:
                MainActivity mainActivity=(MainActivity)getActivity();
                mainActivity.openFragment(new AddContactFragment());
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.menu_contact_multi_del, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_del_contact:
                List<Integer> selectedItemPositions = contactListAdapter.getSelectedItems();
                int currPos;
                for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
                    currPos = selectedItemPositions.get(i);
                    ContactSource.getInstance().removeContact(currPos);
                    //contactListAdapter.removeData(currPos);
                }
                contactListAdapter.notifyDataSetChanged();
                actionMode.finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        actionMode=null;
        contactListAdapter.clearSelections();
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        contactListAdapter.notifyDataSetChanged();
    }

    @Subscribe
    public void onUpdateFast(UpdateFastScrollEvent updateFastScrollEvent) {
        fastScroller.updateData();
    }



    @Override
    public void onLongClickItem(int pos) {
        if (actionMode != null) {
            return;
        }
        actionMode = ((AppCompatActivity)getActivity()).startSupportActionMode(ContactListFragment.this);

        toggleSelection(pos);
    }

    @Override
    public void onClickItem(int pos) {
        if(actionMode!=null){
            toggleSelection(pos);
            return;
        }
        MainActivity mainActivity=(MainActivity)getActivity();
        Bundle bundle=new Bundle();
        bundle.putBoolean(EditContactFragment.KEY_EXIST,true);
        bundle.putInt(EditContactFragment.KEY_INDEX,pos);
        Fragment fragment=new EditContactFragment();
        fragment.setArguments(bundle);
        mainActivity.openFragment(fragment);
        //open edit fragment

    }

    private void toggleSelection(int index) {
        contactListAdapter.toggleSelection(index);
        String selectCount=getString(R.string.select_count,contactListAdapter.getSelectedItemCount());
        actionMode.setTitle(selectCount);
    }

}
