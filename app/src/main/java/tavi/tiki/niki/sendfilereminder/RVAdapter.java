package tavi.tiki.niki.sendfilereminder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Никита on 13.11.2015.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PlanViewHolder> {
    static final int FILE_SELECT_CODE = 0;
    static final int PICK_CONTACT = 1;

    public static class PlanViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView filename;
        TextView contactname;


        PlanViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            filename = (TextView) itemView.findViewById(R.id.filename_id);
            contactname = (TextView) itemView.findViewById(R.id.contact_id);

        }
    }

    List<Plan> plans;

    RVAdapter(List<Plan> plans) {
        this.plans = plans;
    }

    @Override
    public int getItemCount() {
        return plans.size();
    }

    @Override
    public PlanViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.plan_card_view, viewGroup, false);
        PlanViewHolder pvh = new PlanViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PlanViewHolder planViewHolder, int i) {
        planViewHolder.contactname.setText(plans.get(i).getContact());
        planViewHolder.filename.setText(plans.get(i).getFile());
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void removeItem(int position) {
        plans.remove(position);
        notifyItemRemoved(position);
    }
}