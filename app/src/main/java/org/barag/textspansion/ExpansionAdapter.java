package org.barag.textspansion;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.barag.textspansion.domain.Expansion;

import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Provides access to persisted text expansions for {@link RecyclerView} implementations.
 */
public class ExpansionAdapter extends RecyclerView.Adapter {

    private List<Expansion> dataset;

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

    public ExpansionAdapter(List<Expansion> seedData) {
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
        Expansion modelToDisplay = this.dataset.get(position);

        LinearLayout layout = ((ViewHolder) holder).getHeldView();
        ButterKnife.bind(this, layout);

        TextView keyView = ButterKnife.findById(layout, R.id.expansion_key);
        keyView.setText(modelToDisplay.getKey());

        TextView valueView = ButterKnife.findById(layout, R.id.expansion_value);
        valueView.setText(modelToDisplay.getValue());
    }

    @Override
    public int getItemCount() {
        return this.dataset.size();
    }
}
