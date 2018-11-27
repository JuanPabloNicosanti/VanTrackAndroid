package mainFunctionality.driverTrips;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.mainFunctionality.search.Trip;

public class TripsAdapter extends RecyclerView.Adapter<TripsAdapter.ModelViewHolder>
        implements View.OnClickListener {

    private List<Trip> items;
    private OnItemClickListener mlistener;


    @Override
    public void onClick(View v) {
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mlistener = listener;
    }

    public TripsAdapter(List<Trip> items) {
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public TripsAdapter.ModelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_driver, parent,
                false);
        view.setOnClickListener(this);
        return new ModelViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    @Override
    public void onBindViewHolder(TripsAdapter.ModelViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    public void setItems(List<Trip> items) {
        this.items = items;
    }

    public class ModelViewHolder extends RecyclerView.ViewHolder{

        private TextView origin;
        private TextView destination;
        private TextView date;
        private TextView time;
        private DateTimeFormatter dtf = DateTimeFormat.forPattern("dd-MM-yyyy");
        private DateTimeFormatter tf = DateTimeFormat.forPattern("HH:mm");

        public ModelViewHolder(View itemView) {
            super(itemView);
            this.origin = itemView.findViewById(R.id.origin);
            this.destination = itemView.findViewById(R.id.destination);
            this.date = itemView.findViewById(R.id.date);
            this.time = itemView.findViewById(R.id.time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mlistener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mlistener.onItemClick(position);
                        }
                    }
                }
            });
        }

        public void bind(Trip trip) {
            origin.setText(trip.getOrigin());
            destination.setText(trip.getDestination());
            date.setText(trip.getDate().toString(dtf));
            time.setText(trip.getTime().toString(tf));
        }
    }
}
