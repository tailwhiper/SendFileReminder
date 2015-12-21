package tavi.tiki.niki.sendfilereminder;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by Никита on 13.11.2015.
 */
public class PlanTouchHelper extends ItemTouchHelper.SimpleCallback {
    private RVAdapter mplanAdapter;


    public PlanTouchHelper(RVAdapter planAdapter) {
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.mplanAdapter = planAdapter;

    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        //TODO: Not implemented here
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        //Remove item
        mplanAdapter.removeItem(viewHolder.getAdapterPosition());
    }

}