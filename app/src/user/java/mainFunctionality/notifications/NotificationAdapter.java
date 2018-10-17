package mainFunctionality.notifications;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.models.Notification;
import utn.proy2k18.vantrack.viewModels.NotificationsViewModel;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ModelViewHolder>
        implements View.OnClickListener{

    private List<Notification> items;
    private NotificationAdapter.OnItemClickListener mlistener;

    @Override
    public void onClick(View v) {

    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(NotificationAdapter.OnItemClickListener listener) {
        mlistener = listener;
    }

    public NotificationAdapter(List<Notification> items) {
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public NotificationAdapter.ModelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification, parent, false);
        view.setOnClickListener(this);
        return new ModelViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    @Override
    public void onBindViewHolder(NotificationAdapter.ModelViewHolder holder, final int position) {
        holder.bind(items.get(position));
    }


    public class ModelViewHolder extends RecyclerView.ViewHolder {

        private TextView notification_desc;
        private ImageView notification_icon;

        //TODO: Poner todos los atributos de la notificacion para bindearlos

        public ModelViewHolder(View itemView) {
            super(itemView);
            this.notification_desc = itemView.findViewById(R.id.notification_desc);
            this.notification_icon = itemView.findViewById(R.id.notification_icon);

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

        public void bind(Notification notification) {
            // TODO: add if to decide which icon to show based on message id
            notification_desc.setText(notification.getDescription());
            if (notification.getNotificationMessageId().equals(NotificationsViewModel.CANCELATION_ID)) {
                notification_icon.setImageResource(R.drawable.cancel_icon);
            } else {
                notification_icon.setImageResource(R.mipmap.ic_edit);
            }
        }
    }

}


