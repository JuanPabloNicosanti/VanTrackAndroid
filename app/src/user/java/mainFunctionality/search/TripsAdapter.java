package mainFunctionality.search;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.search.Trip;

public class TripsAdapter extends RecyclerView.Adapter<TripsAdapter.ModelViewHolder> implements View.OnClickListener {

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip, parent, false);
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

        private TextView companyName;
        private TextView companyCalification;
        private TextView origin;
        private TextView destination;
        private TextView hour;
        private TextView date;
        private TextView price;

        public ModelViewHolder(View itemView) {
            super(itemView);
            this.companyName = itemView.findViewById(R.id.companyName);
            this.companyCalification = itemView.findViewById(R.id.companyCalification);
            this.origin = itemView.findViewById(R.id.origin);
            this.destination = itemView.findViewById(R.id.destination);
            this.date = itemView.findViewById(R.id.date);
            this.hour = itemView.findViewById(R.id.hour);
            this.price = itemView.findViewById(R.id.price);

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
            companyName.setText(trip.getCompanyName());
            companyCalification.setText(String.format(Locale.ENGLISH, "%.3g%n",
                    trip.getCompanyCalification()));
            origin.setText(trip.getOrigin());
            destination.setText(trip.getDestination());
            date.setText(trip.getFormattedDate());
            hour.setText(String.valueOf(trip.getFormattedTime()));
            price.setText(String.valueOf(trip.getPrice()));
        }
    }
}
