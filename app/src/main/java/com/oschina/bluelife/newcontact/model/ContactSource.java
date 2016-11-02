package com.oschina.bluelife.newcontact.model;

import android.util.Log;

import com.oschina.bluelife.newcontact.R;
import com.oschina.bluelife.newcontact.Utils.Const;
import com.oschina.bluelife.newcontact.Utils.ContactManager;
import com.oschina.bluelife.newcontact.event.UpdateFastScrollEvent;
import com.oschina.bluelife.newcontact.widget.model.AlphabetItem;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by HiWin10 on 2016/10/23.
 */

public class ContactSource {
    private static ContactSource contactSource;
    MostConnectViewModel mostConnectViewModel;
    private List<ContactViewModel> contactViewModels;
    private List<AlphabetItem> alphabetItems;
    private String mostLabel;
    private List<Person> persons;

    private ContactSource(){

    }
    public static ContactSource getInstance(){
        if(contactSource==null){
            contactSource=new ContactSource();
        }
        return contactSource;
    }
    //just call once;
    public void init(List<Person> persons,String aLabel){
        initMoseConnect();
        mostLabel=aLabel;
        alphabetItems=new ArrayList<>();
        contactViewModels=new ArrayList<>();
        setPersons(persons);
        updateContacts();

    }
    public Person getPerson(int index){
        ContactViewModel contactViewModel=contactViewModels.get(index);
        if(contactViewModel instanceof PersonViewModel){
            return ((PersonViewModel) contactViewModel).getPerson();
        }
        return null;
    }
    private void updateContacts(){
        List<ContactViewModel> tempList=new ArrayList<>();
        contactViewModels.clear();
        tempList.add(mostConnectViewModel);
        tempList.addAll(getSortedPersons());
        List<String> alphabets=new ArrayList<>();
        for (int i = 0; i < tempList.size(); i++) {
            ContactViewModel contactViewModel=tempList.get(i);
            String code=contactViewModel.getSortCode();
            String word= code;
            SectionViewModel sectionViewModel=null;
            if(!alphabets.contains(word)){
                String label=code== Const.STAR?mostLabel:code;
                sectionViewModel=new SectionViewModel(label,code);
                for (int j = i; j < tempList.size(); j++) {
                    if(tempList.get(j).getSortCode().equalsIgnoreCase(code)){
                        sectionViewModel.plus();
                    }
                    else{

                        break;
                    }
                }

                contactViewModels.add(sectionViewModel);
                alphabets.add(word);
            }
            contactViewModels.add(tempList.get(i));
        }
        //add alphabets
        alphabets.clear();
        alphabetItems.clear();
        for (int i = 0; i < contactViewModels.size(); i++) {
            String word=contactViewModels.get(i).getSortCode();
            if(!alphabets.contains(word)){
                alphabets.add(word);
                alphabetItems.add(new AlphabetItem(i,word,false));
            }
        }
    }
    public void addContact(Person person){
        persons.add(person);
        updateContacts();
    }
    public void removeContact(int pos){
        ContactViewModel model=contactViewModels.get(pos);
        for (int i = 0; i < contactViewModels.size(); i++) {
            ContactViewModel viewModel=contactViewModels.get(i);
            if(viewModel instanceof SectionViewModel){
                String word=model.getSortCode();
                if(viewModel.getSortCode().equalsIgnoreCase(word)){
                    ((SectionViewModel) viewModel).minus();
                    if(((SectionViewModel) viewModel).getCount()==0){
                        removeAlphabet(word);
                        contactViewModels.remove(viewModel);
                        EventBus.getDefault().post(new UpdateFastScrollEvent());
                    }
                    break;
                }
            }
        }

        contactViewModels.remove(model);

    }
    private void removeAlphabet(String word){
        for (int i = 0; i < alphabetItems.size(); i++) {
            if(word.equalsIgnoreCase(alphabetItems.get(i).word)){
                alphabetItems.remove(i);
                break;
            }
        }
    }
    public List<AlphabetItem> getAlphabetItems(){
        return alphabetItems;
    }
    public List<ContactViewModel> getContactViewModels(){
        return contactViewModels;
    }

    private void setPersons(List<Person> personList){
        persons=personList;
    }
    private List<PersonViewModel> getSortedPersons(){
        List<PersonViewModel> personModels=new ArrayList<>();
        Collections.sort(persons,new PersonCompare());
        for (int i = 0; i < persons.size(); i++) {
            PersonViewModel viewModel=new PersonViewModel(persons.get(i));
            personModels.add(viewModel);
        }
        return personModels;
    }
    private void initMoseConnect(){
        List<Person> staredPersons=new ArrayList<>();
        staredPersons.add(new Person("平安财富宝","aa@aa.com",""));
        staredPersons.add(new Person("平安随行","aa@aa.com",""));
        staredPersons.add(new Person("随行红包","aa@aa.com",""));
        staredPersons.add(new Person("支付宝","aa@aa.com",""));
        staredPersons.add(new Person("fb","fb@aa.com",""));
        mostConnectViewModel=new MostConnectViewModel(staredPersons);
    }
}
