package com.lemoulinstudio.bikefriend.drawer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lemoulinstudio.bikefriend.R;

import java.util.Arrays;
import java.util.List;

public class DrawerListAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private List<Class> itemClasses;
    private List<Object> items;

    public DrawerListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        itemClasses = Arrays.<Class>asList(DrawerListSection.class, DrawerListItem.class);
        items = Arrays.<Object>asList(
                new DrawerListItem(R.drawable.ic_action_map, R.string.drawer_map),
                new DrawerListItem(R.drawable.ic_action_important, R.string.drawer_preferred_stations),
                new DrawerListItem(R.drawable.ic_action_chronometer_grey, R.string.drawer_chronometer),
                //new DrawerListItem(0, R.string.drawer_history),
                //new DrawerListItem(R.drawable.ic_action_lost_and_found_grey, R.string.drawer_lost_and_found),
                //new DrawerListItem(R.drawable.ic_action_about, R.string.drawer_service_information),
                //new DrawerListItem(R.drawable.ic_action_red_cross, R.string.drawer_emergency),
                new DrawerListItem(R.drawable.ic_action_settings_grey, R.string.menu_settings));
    }

    @Override
    public int getViewTypeCount() {
        return itemClasses.size();
    }

    @Override
    public int getItemViewType(int index) {
        return itemClasses.indexOf(items.get(index).getClass());
    }

    @Override
    public int getCount() {
        return items.size();
    }

    private static class SectionViewHolder {
        public TextView sectionName;
    }

    private static class ItemViewHolder {
        ImageView itemIcon;
        TextView itemName;
    }

    @Override
    public View getView(int index, View convertView, ViewGroup parent) {
        Object item = items.get(index);
        int typeIndex = itemClasses.indexOf(items.get(index).getClass());

        switch (typeIndex) {
            // Section
            case 0: {
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.drawer_list_section, parent, false);
                    SectionViewHolder holder = new SectionViewHolder();
                    holder.sectionName = (TextView) convertView.findViewById(R.id.item_name);
                    convertView.setTag(holder);
                }

                SectionViewHolder holder = (SectionViewHolder) convertView.getTag();
                DrawerListSection listSection = (DrawerListSection) item;
                holder.sectionName.setText(listSection.nameId);

                break;
            }
            // Item
            case 1: {
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.drawer_list_item, parent, false);
                    ItemViewHolder holder = new ItemViewHolder();
                    holder.itemIcon = (ImageView) convertView.findViewById(R.id.item_icon);
                    holder.itemName = (TextView) convertView.findViewById(R.id.item_name);
                    convertView.setTag(holder);
                }

                ItemViewHolder holder = (ItemViewHolder) convertView.getTag();
                DrawerListItem listItem = (DrawerListItem) item;
                holder.itemIcon.setImageResource(listItem.iconId);
                holder.itemName.setText(listItem.nameId);

                break;
            }

        }

        return convertView;
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public Object getItem(int index) {
        return items.get(index);
    }
}
