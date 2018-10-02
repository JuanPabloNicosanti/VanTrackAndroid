package mainFunctionality.driverTrips;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import mainFunctionality.driverTrips.ConfirmPassengersFragment.OnListFragmentInteractionListener;

import java.util.List;
import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.models.PassengerReservation;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PassengerReservation} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class ConfirmPassengerRecyclerViewAdapter extends RecyclerView.Adapter<ConfirmPassengerRecyclerViewAdapter.ViewHolder> {

    interface OnItemCheckListener {
        void onItemCheck(PassengerReservation passenger, Integer index);
        void onItemUncheck(PassengerReservation passenger, Integer index);
    }

    private OnItemCheckListener onItemCheckListener;
    private final List<PassengerReservation> mValues;
    private final List<Integer> mCheckedValues;
    private final OnListFragmentInteractionListener mListener;


    public ConfirmPassengerRecyclerViewAdapter(List<PassengerReservation> items,
                                               List<Integer> checkedItems,
                                               OnListFragmentInteractionListener listener,
                                               OnItemCheckListener checkListener) {
        mValues = items;
        mCheckedValues = checkedItems;
        mListener = listener;
        onItemCheckListener = checkListener;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_confirm_passenger, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final PassengerReservation currentItem = mValues.get(position);
        holder.mItem = currentItem;
        holder.mContentView.setText(mValues.get(position).getPassenger().getNameAndSurname());
        if(mCheckedValues.contains(position)) {
            holder.checkbox.setChecked(true);
        }
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.checkbox.setChecked(
                        !holder.checkbox.isChecked());
                if (holder.checkbox.isChecked()) {
                    onItemCheckListener.onItemCheck(currentItem, holder.getAdapterPosition());
                } else {
                    onItemCheckListener.onItemUncheck(currentItem, holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private final TextView mContentView;
        private CheckBox checkbox;
        public PassengerReservation mItem;

        private ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = view.findViewById(R.id.nameAndSurname);
            checkbox = itemView.findViewById(R.id.isPresent);
            checkbox.setClickable(false);
        }

        public void setOnClickListener(View.OnClickListener onClickListener) {
            itemView.setOnClickListener(onClickListener);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
