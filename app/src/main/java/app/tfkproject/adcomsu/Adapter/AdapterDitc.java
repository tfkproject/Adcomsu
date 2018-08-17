package app.tfkproject.adcomsu.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import app.tfkproject.adcomsu.DictDetailActivity;
import app.tfkproject.adcomsu.Model.Dict;
import app.tfkproject.adcomsu.R;

/**
 * Created by taufik on 06/05/18.
 */

public class AdapterDitc extends RecyclerView.Adapter<AdapterDitc.ViewHolder>{

    List<Dict> items;
    Context context;

    public AdapterDitc(Context context, List<Dict> items){
        this.context = context;
        this.items = items;
    }

    @Override
    public AdapterDitc.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dict, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.adjView.setText(items.get(position).getAdj());
        holder.lyParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DictDetailActivity.class);
                intent.putExtra("key_word", items.get(position).getAdj());
                intent.putExtra("key_color", "#b1aeab");
                intent.putExtra("key_cmp", items.get(position).getCmp());
                intent.putExtra("key_sup", items.get(position).getSup());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout lyParent;
        TextView adjView;

        public ViewHolder(View itemView){
            super(itemView);

            lyParent = (LinearLayout) itemView.findViewById(R.id.parent);
            adjView = (TextView) itemView.findViewById(R.id.tv_adj);
        }

    }
}
