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

    /** The set of all {@link org.barag.textspansion.domain.Expansion}s to be displayed. */
    private List<Expansion> dataset;

    /**
     * A simple implementation of {@link android.support.v7.widget.RecyclerView.ViewHolder}, copied mostly from
     * http://developer.android.com/training/material/lists-cards.html#RecyclerView.
     */
    private static class ViewHolder extends RecyclerView.ViewHolder {
        /** The view to be held by the ViewHolder.  In this case, the parent view for a single Expansion. */
        private LinearLayout heldView;

        /**
         * Creates a new ViewHolder.
         * @param layout the parent view for a single Expansion.
         */
        public ViewHolder(final LinearLayout layout) {
            super(layout);
            this.heldView = layout;
        }

        /**
         * Returns the held view that acts as the parent for a single Expansion element.
         * @return the held view.
         */
        public LinearLayout getHeldView() {
            return heldView;
        }
    }

    /**
     * Creates an adapter to a seed list of {@link Expansion} elements.
     * @param seedData a starting list of {@link Expansion}s to contain.
     */
    public ExpansionAdapter(final List<Expansion> seedData) {
        this.dataset = seedData;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.expansion_row, parent, false);
        return new ViewHolder((LinearLayout) view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
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
