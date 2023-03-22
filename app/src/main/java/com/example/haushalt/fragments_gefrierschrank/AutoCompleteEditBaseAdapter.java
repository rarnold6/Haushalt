package com.example.haushalt.fragments_gefrierschrank;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.haushalt.EinkaufszettelElement;
import com.example.haushalt.Food;
import com.example.haushalt.Gefrierschrank;
import com.example.haushalt.R;
import com.example.haushalt.UnitsAndCategories;
import com.example.haushalt.data.HaushaltContract;

import org.w3c.dom.Text;

import java.util.LinkedList;
import java.util.List;
import java.util.zip.Inflater;

public class AutoCompleteEditBaseAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private LinkedList<Food> foodLinkedList;
    private LinkedList<Food> foodLinkedListCopy;
    private boolean gefrierschrankEdit;

    public void addElement(Food food){
        /*if(foodLinkedListCopy.contains(food)){
            return;
        }*/
        //foodLinkedList.add(food);
        notifyDataSetChanged();
    }

    public AutoCompleteEditBaseAdapter(Context context, LinkedList<Food> foodLinkedList, boolean gefrierschrankEdit){
        this.context = context;
        this.foodLinkedList = foodLinkedList;
        this.foodLinkedListCopy = (LinkedList<Food>) foodLinkedList.clone();
        this.gefrierschrankEdit = gefrierschrankEdit;

    }

    @Override
    public int getCount() {
        return this.foodLinkedListCopy.size();
    }

    @Override
    public Object getItem(int i) {
        return this.foodLinkedListCopy.get(i);
    }

    @Override
    public long getItemId(int i) {
        return this.foodLinkedListCopy.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(this.context).inflate(R.layout.autocomplete_search, viewGroup, false);

        Food food = (Food) getItem(i);

        TextView foodName = (TextView) view.findViewById(R.id.autoCompleteFoodName);
        TextView unit = (TextView) view.findViewById(R.id.autoCompleteUnit);

        foodName.setText(food.getEssensname());
        unit.setText(UnitsAndCategories.getUnit(food.getEinheit_id()));

        return view;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults filterResults = new FilterResults();

            LinkedList<Food> foundElements = new LinkedList<>();
            for(Food food : foodLinkedList){
                if(food.getEssensname().toLowerCase().matches(charSequence.toString().toLowerCase() + ".*")){
                    if(!gefrierschrankEdit || food.getStoredInPast() == 1){
                        foundElements.add(food);
                    }
                }
            }

            filterResults.values = foundElements;
            filterResults.count = foundElements.size();
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            if(filterResults.count > 0) {
                foodLinkedListCopy.clear();
                foodLinkedListCopy.addAll((List) filterResults.values);
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((Food) resultValue).getEssensname();
        }
    };
}
