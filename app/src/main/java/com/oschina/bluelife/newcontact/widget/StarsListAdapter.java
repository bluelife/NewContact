package com.oschina.bluelife.newcontact.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oschina.bluelife.newcontact.R;
import com.oschina.bluelife.newcontact.model.Person;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by slomka.jin on 2016/10/20.
 */

public class StarsListAdapter extends RecyclerView.Adapter<StarsListAdapter.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    private List<Person> persons;

    public StarsListAdapter(Context context){
        this.context=context;
        inflater=LayoutInflater.from(context);

    }
    public void setData(List<Person> personList){
        persons=personList;
        notifyDataSetChanged();
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.contact_sub_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(persons.get(position));
    }

    @Override
    public int getItemCount() {
        return persons.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.contact_list_sub_name)
        TextView name;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
        public void bind(Person person){
            name.setText(person.name);
        }
    }
}
