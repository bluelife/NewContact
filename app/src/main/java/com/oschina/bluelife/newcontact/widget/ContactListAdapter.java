package com.oschina.bluelife.newcontact.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oschina.bluelife.newcontact.R;
import com.oschina.bluelife.newcontact.model.ContactViewModel;
import com.oschina.bluelife.newcontact.model.MostConnectViewModel;
import com.oschina.bluelife.newcontact.model.PersonViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by slomka.jin on 2016/10/20.
 */

public class ContactListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements RecyclerViewFastScroller.BubbleTextGetter {
    private int viewTypeStar= R.layout.contact_sub_list;
    private int viewTypeNormal=R.layout.contact_list_item;
    private Context context;
    private LayoutInflater inflater;
    private List<ContactViewModel> contactViewModels;

    public ContactListAdapter(Context context,List<ContactViewModel> viewModels){
        this.context=context;
        inflater=LayoutInflater.from(context);
        contactViewModels=viewModels;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType==viewTypeStar){
            View view=inflater.inflate(viewTypeStar,parent,false);
            return new StarViewHolder(view);
        }
        else if(viewType==viewTypeNormal){
            View view=inflater.inflate(viewTypeNormal,parent,false);
            return new NormalViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType=getItemViewType(position);
        if(viewType==viewTypeStar){
            StarViewHolder starViewHolder= (StarViewHolder) holder;
            starViewHolder.bind(contactViewModels.get(position));
        }
        else if(viewType==viewTypeNormal){
            NormalViewHolder normalViewHolder=(NormalViewHolder)holder;
            normalViewHolder.bind(contactViewModels.get(position));
        }
    }


    @Override
    public int getItemCount() {
        return contactViewModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(position==0){
            return viewTypeStar;
        }
        else{
            return viewTypeNormal;
        }
    }
    @Override
    public String getTextToShowInBubble(int pos) {
        if (pos < 0 || pos >= contactViewModels.size())
            return null;

        String name = contactViewModels.get(pos).getSortCode();
        if (name == null || name.length() < 1)
            return null;

        return name;
    }

    class StarViewHolder extends RecyclerView.ViewHolder{
        RecyclerView starsRecyclerView;
        StarsListAdapter listAdapter;
        public StarViewHolder(View itemView) {
            super(itemView);
            starsRecyclerView=(RecyclerView)itemView.findViewById(R.id.contact_sub_list_view);
            RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
            starsRecyclerView.setLayoutManager(layoutManager);
            listAdapter=new StarsListAdapter(context);
            starsRecyclerView.setAdapter(listAdapter);
            ButterKnife.bind(this,itemView);
        }
        public void bind(ContactViewModel viewModel){
            MostConnectViewModel model=(MostConnectViewModel)viewModel;
            listAdapter.setData(model.getPersons());
        }
    }

    class NormalViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.contact_list_item_email)
        TextView email;
        @BindView(R.id.contact_list_item_name)
        TextView name;
        public NormalViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
        public void bind(ContactViewModel viewModel){
            PersonViewModel model=(PersonViewModel) viewModel;
            email.setText(model.getPerson().email);
            name.setText(model.getPerson().name);
        }
    }
}
