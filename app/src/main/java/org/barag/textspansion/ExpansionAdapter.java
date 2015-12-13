package org.barag.textspansion;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

/**
 * Provides access to persisted text expansions for {@link RecyclerView} implementations.
 */
public class ExpansionAdapter extends RecyclerView.Adapter {

    private List<String> dataset;

    private static class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout heldView;

        public ViewHolder(LinearLayout layout) {
            super(layout);
            this.heldView = layout;
        }

        public LinearLayout getHeldView() {
            return heldView;
        }
    }

    public ExpansionAdapter(List<String> seedData) {
        this.dataset = seedData;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.expansion_row, parent, false);
        return new ViewHolder((LinearLayout) view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String modelToDisplay = this.dataset.get(position); // TODO: switch to Expansion

        LinearLayout layout = ((ViewHolder) holder).getHeldView();
        TextView key = (TextView) layout.findViewById(R.id.expansion_key);
        key.setText(modelToDisplay);
    }

    @Override
    public int getItemCount() {
        return this.dataset.size();
    }
}
