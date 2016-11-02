package com.oschina.bluelife.newcontact.widget;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.oschina.bluelife.newcontact.R;
import com.oschina.bluelife.newcontact.model.ContactViewModel;
import com.oschina.bluelife.newcontact.model.MostConnectViewModel;
import com.oschina.bluelife.newcontact.model.PersonViewModel;
import com.oschina.bluelife.newcontact.model.SectionViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by slomka.jin on 2016/10/20.
 */

public class ContactListAdapter extends RecyclerView.Adapter<BaseViewHolder> implements RecyclerViewFastScroller.BubbleTextGetter {
    private int viewTypeStar= R.layout.contact_sub_list;
    private int viewTypeNormal=R.layout.contact_list_item;
    private int viewTypeSection=R.layout.contact_list_section_item;
    private Context context;
    private LayoutInflater inflater;
    private List<ContactViewModel> contactViewModels;
    private SparseBooleanArray selectedItems;

    public ContactListAdapter(Context context,List<ContactViewModel> viewModels){
        this.context=context;
        inflater=LayoutInflater.from(context);
        contactViewModels=viewModels;
        selectedItems=new SparseBooleanArray();
    }
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(viewType,parent,false);
        if(viewType==viewTypeStar){
            return new StarViewHolder(view);
        }
        else if(viewType==viewTypeNormal){
            return new NormalViewHolder(view);
        }
        else if(viewType==viewTypeSection){
            return new SectionViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
            holder.bind(contactViewModels.get(position));
    }


    @Override
    public int getItemCount() {
        return contactViewModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        return contactViewModels.get(position).getType();
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
    public void toggleSelection(int pos) {
        if(!contactViewModels.get(pos).selectable()){
            return;
        }
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
        }
        else {
            selectedItems.put(pos, true);
        }
        notifyItemChanged(pos);
    }
    public int getModelPosition(ContactViewModel model){
        for (int i = 0; i < contactViewModels.size(); i++) {
            if(model==contactViewModels.get(i)){
                return i;
            }
        }
        return -1;
    }

    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items =
                new ArrayList<Integer>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }
    public void removeData(int pos){

        notifyDataSetChanged();
    }
    private ItemListener itemListener;
    public void setItemListener(ItemListener itemListener){
        this.itemListener=itemListener;
    }
    public interface ItemListener{

        void onClickItem(int pos);
        void onLongClickItem(int pos);
    }

    class StarViewHolder extends BaseViewHolder<MostConnectViewModel>{
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
        public void bind(MostConnectViewModel viewModel){

            listAdapter.setData(viewModel.getPersons());
        }
    }

    class NormalViewHolder extends BaseViewHolder<PersonViewModel>{

        @BindView(R.id.contact_list_item_avatar)
        ImageView avatar;
        @BindView(R.id.contact_list_item_email)
        TextView email;
        @BindView(R.id.contact_list_item_name)
        TextView name;
        View rootView;
        public NormalViewHolder(View itemView) {
            super(itemView);
            rootView=itemView;
            ButterKnife.bind(this,itemView);
        }
        public void bind(final PersonViewModel model){
            final int pos=getModelPosition(model);

            ViewCompat.setActivated(rootView,selectedItems.get(pos,false));

            if(null!=model.getPerson().icon) {
                avatar.setImageURI(Uri.parse(model.getPerson().icon));
                Log.w("tttttttt", model.getPerson().icon);
            }
            email.setText(model.getPerson().email);
            name.setText(model.getPerson().name);
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(null!=itemListener)
                        itemListener.onClickItem(pos);
                }
            });
            rootView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(null!=itemListener)
                        itemListener.onLongClickItem(getModelPosition(model));
                    return true;
                }
            });
        }
    }

    class SectionViewHolder extends BaseViewHolder<SectionViewModel>{

        @BindView(R.id.contact_list_section_label)
        TextView sectionLabel;
        public SectionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
        public void bind(SectionViewModel viewModel){
            sectionLabel.setText(viewModel.getLabel());
        }
    }
}
