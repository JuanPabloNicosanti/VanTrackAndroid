package utn.proy2k18.vantrack.mainFunctionality.search;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import utn.proy2k18.vantrack.R;

public class TripsAdapter extends RecyclerView.Adapter<TripsAdapter.ModelViewHolder> {

    private List<Trip> items;

    public TripsAdapter(List<Trip> items) {
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public TripsAdapter.ModelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ModelViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.trip, parent, false));
    }

    @Override
    public void onBindViewHolder(TripsAdapter.ModelViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public class ModelViewHolder extends RecyclerView.ViewHolder{

        private TextView companyName;
        private TextView companyCalification;
        private TextView date;
        private TextView origin;
        private TextView destination;
        private TextView hour;
        private TextView price;
        //TODO: Poner todos los atributos de la reserva para bindearlos

        public ModelViewHolder(View itemView) {
            super(itemView);
            this.companyName = itemView.findViewById(R.id.companyName);
            this.companyCalification = itemView.findViewById(R.id.companyCalification);
            this.date = itemView.findViewById(R.id.date);
            this.origin = itemView.findViewById(R.id.origin);
            this.destination = itemView.findViewById(R.id.destination);
            this.hour = itemView.findViewById(R.id.hour);
            this.price = itemView.findViewById(R.id.price);
        }

        public void bind(Trip trip) {
            companyName.setText(trip.getCompanyName());
            companyCalification.setText(String.valueOf(trip.getCompanyCalification()));
            date.setText(trip.getFormattedDate());
            origin.setText(trip.getOrigin());
            destination.setText(trip.getDestination());
            hour.setText(String.valueOf(trip.getTimeHour()));
            price.setText(String.valueOf(trip.getPrice()));
        }
    }

}
