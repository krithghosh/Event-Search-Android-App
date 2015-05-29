package com.example.krith.eventmanagement_v11;

import android.content.Intent;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;


public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {


    private List<EventInfo> eventList;
    private static OnItemClickListener onItemClickListener;

    public EventAdapter(List<EventInfo> eventList) {
        this.eventList = eventList;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.card_layout, viewGroup, false);
        return new EventViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(EventViewHolder eventViewHolder, int i) {

        EventInfo event = eventList.get(i);
        eventViewHolder.eventName.setText(event.getEventName());
        eventViewHolder.eventCity.setText(event.getEventCity());
        eventViewHolder.eventDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(event.getEventDate()));
        //eventViewHolder.image.setTag(event.getImage());
        //new ImageLoader(eventViewHolder.image).execute("http://kenmclane.org/images/party_bg.jpg");
        new ImageLoader(eventViewHolder.image).execute(event.getImage());
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {

        protected TextView eventName;
        protected TextView eventCity;
        protected TextView eventDate;
        protected ImageView image;

        public EventViewHolder(View itemView) {
            super(itemView);

            eventName = (TextView) itemView.findViewById(R.id.eventName);
            eventCity = (TextView) itemView.findViewById(R.id.eventCity);
            eventDate = (TextView) itemView.findViewById(R.id.eventDate);
            image = (ImageView) itemView.findViewById(R.id.imageView);

           /* itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = EventViewHolder.super.getAdapterPosition();
                    onItemClickListener.onItemClick(v,position);
                }
            }); */
        }
    }
}