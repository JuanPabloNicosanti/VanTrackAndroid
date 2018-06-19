package utn.proy2k18.vantrack.mainFunctionality.reservations;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public class ModelViewHolder extends RecyclerView.ViewHolder{

        private TextView company;
//        private TextView reservationDate;
        private TextView bookedTripDate;
        private TextView origin;
        private TextView destination;
        private ImageButton remove_button;
        //TODO: Poner todos los atributos de la reserva para bindearlos

        public ModelViewHolder(View itemView) {
            super(itemView);
            this.company = itemView.findViewById(R.id.company);
            this.bookedTripDate = itemView.findViewById(R.id.date);
//            this.reservationDate = itemView.findViewById(R.id.date);
            this.origin = itemView.findViewById(R.id.origin);
            this.destination = itemView.findViewById(R.id.destination);
            this.remove_button= itemView.findViewById(R.id.remove_button);
        }

        public void bind(Reservation reservation) {
            company.setText(reservation.getTripCompanyName());
            bookedTripDate.setText(reservation.getTripFormattedDate());
            origin.setText(reservation.getTripOrigin());
            destination.setText(reservation.getTripDestination());
        }
    }


    @Override
    public void onBindViewHolder(ReservationsAdapter.ModelViewHolder holder, final int position) {
        holder.bind(items.get(position));

        holder.remove_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Remove the item on remove/button click
                items.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, items.size());
            }
        });
    }



}
