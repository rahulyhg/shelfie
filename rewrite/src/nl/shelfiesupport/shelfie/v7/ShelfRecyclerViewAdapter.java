package nl.shelfiesupport.shelfie.v7;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import nl.shelfiesupport.shelfie.EditShelfFragment;
import nl.shelfiesupport.shelfie.R;
import nl.shelfiesupport.shelfie.ShelfItem;

import java.util.List;

public class ShelfRecyclerViewAdapter extends RecyclerView.Adapter<ShelfRowViewHolder> {
    private final Context context;
    private final List<ShelfItem> objects;
    private final EditShelfFragment editShelfFragment;

    public ShelfRecyclerViewAdapter(final Context context, List<ShelfItem> objects, final EditShelfFragment editShelfFragment) {
        super();
        this.editShelfFragment = editShelfFragment;
        this.context = context;
        this.objects = objects;
    }

    @Override
    public ShelfRowViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LinearLayout layout = (LinearLayout) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.shelf_row, viewGroup, false);

        ShelfRowViewHolder vh = new ShelfRowViewHolder(layout, context, editShelfFragment, this, objects);
        return vh;

    }

    @Override
    public void onBindViewHolder(ShelfRowViewHolder shelfRowViewHolder, int i) {
        shelfRowViewHolder.setShelfItem(objects.get(i), i);
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

}
