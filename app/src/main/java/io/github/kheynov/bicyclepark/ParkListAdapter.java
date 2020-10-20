package io.github.kheynov.bicyclepark;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smart_bike_park.R;

import java.util.ArrayList;

public class ParkListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<ParkPlaceListItem> list;

    public ParkListAdapter(Context context, ArrayList<ParkPlaceListItem> list) {
        this.list = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ParkPlaceListItem getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View view = convertView;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.list_item, parent, false);
            holder.lockImage = view.findViewById(R.id.imageViewListItem);
            holder.name = view.findViewById(R.id.textViewListItemParkName);
            holder.status = view.findViewById(R.id.textViewListItemParkStatus);
            view.setTag(holder);
        }
        holder = (ViewHolder) view.getTag();
        ParkPlaceListItem parkPlaceListItem = getItem(position);
        holder.lockImage.setImageResource(parkPlaceListItem.getLockImageId());
        holder.name.setText(view.getResources().getString(R.string.park_place_number) + parkPlaceListItem.getId());
        if (parkPlaceListItem.getStatus() == ParkPlaceListItem.parkPlaceStates.OPENED) {
            holder.status.setText(view.getResources().getString(R.string.opened_title));
        } else {
            holder.status.setText(view.getResources().getString(R.string.closed_title));
        }
        return view;
    }

    private static class ViewHolder {
        private ImageView lockImage;
        private TextView name;
        private TextView status;
    }
}
