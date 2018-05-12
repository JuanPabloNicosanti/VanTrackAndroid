package utn.proy2k18.vantrack.mainFunctionality.reservations;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import utn.proy2k18.vantrack.R;

public class ReservationsAdapter extends RecyclerView.Adapter<ReservationsAdapter.ModelViewHolder> {

    private List<Reservation> items;

    public ReservationsAdapter(List<Reservation> items) {
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public ReservationsAdapter.ModelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ModelViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.reservation, parent, false));
    }

    @Override
    public void onBindViewHolder(ReservationsAdapter.ModelViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public class ModelViewHolder extends RecyclerView.ViewHolder{

        private TextView company;
        private TextView date;
        private TextView origin;
        private TextView destination;
        //TODO: Poner todos los atributos de la reserva para bindearlos

        public ModelViewHolder(View itemView) {
            super(itemView);
            this.company = itemView.findViewById(R.id.company);
            this.date = itemView.findViewById(R.id.date);
            this.origin = itemView.findViewById(R.id.origin);
            this.destination = itemView.findViewById(R.id.destination);
        }

        public void bind(Reservation reservation) {
            company.setText(reservation.getCompany());
            date.setText(reservation.getFormattedDate());
            origin.setText(reservation.getOrigin());
            destination.setText(reservation.getDestination());
        }
    }

}
