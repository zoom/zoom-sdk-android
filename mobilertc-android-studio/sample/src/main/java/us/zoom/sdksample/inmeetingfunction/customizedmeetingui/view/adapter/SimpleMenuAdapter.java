package us.zoom.sdksample.inmeetingfunction.customizedmeetingui.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import us.zoom.sdksample.R;

/**
 * Created by Jacky on 2018/1/23.
 */

public class SimpleMenuAdapter extends BaseAdapter{
    private List<SimpleMenuItem> mList = new ArrayList<SimpleMenuItem>();
    private Context mContext;

    public SimpleMenuAdapter(Context context) {
        mContext = context;
    }

    public void addAll(List<SimpleMenuItem> items) {
        for(SimpleMenuItem item : items) {
            addItem(item);
        }
    }
    public void addItem(SimpleMenuItem item) {
        mList.add(item);
    }

    public void clear() {
        mList.clear();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.menu_item, parent, false);
        }

        TextView txtLabel = (TextView)convertView.findViewById(us.zoom.androidlib.R.id.txtLabel);

        SimpleMenuItem item = mList.get(position);
        if(item == null)
            return null;

        txtLabel.setText(item.getLabel());

        return convertView;
    }
}
