package com.shadow.fontselectorquiz.presntation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shadow.fontselectorquiz.R;
import com.shadow.fontselectorquiz.domain.model.FontFamily;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FontFamilyRecyclerViewAdapter extends RecyclerView.Adapter<FontFamilyRecyclerViewAdapter.ViewHolder> {
    private final List<FontFamily> families = new ArrayList<>();

    public void update(List<FontFamily> fontFamilies){
        families.clear();
        families.addAll(fontFamilies);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_font, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.bind(families.get(position));
    }

    @Override
    public int getItemCount() {
        return families.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView font;

        ViewHolder(View view) {
            super(view);
            font = view.findViewById(R.id.tv_font);
        }

        void bind(FontFamily fontFamily) {
            font.setText(fontFamily.family());
        }
    }
}