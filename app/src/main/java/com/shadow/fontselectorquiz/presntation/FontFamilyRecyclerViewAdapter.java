package com.shadow.fontselectorquiz.presntation;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shadow.fontselectorquiz.R;
import com.shadow.fontselectorquiz.domain.executor.FontDecorator;
import com.shadow.fontselectorquiz.domain.model.FontFamily;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class FontFamilyRecyclerViewAdapter extends PagedListAdapter<FontFamily,FontFamilyRecyclerViewAdapter.ViewHolder> {

    private final itemSelector itemSelector;
    private final FontDecorator decorator;
    private int selectPosition = -1;
    private String selectFamily = "";

    public FontFamilyRecyclerViewAdapter(FontFamilyRecyclerViewAdapter.itemSelector itemSelector, FontDecorator decorator) {
        super(new DiffUtil.ItemCallback<FontFamily>() {
            @Override
            public boolean areItemsTheSame(@NonNull FontFamily oldItem, @NonNull FontFamily newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areContentsTheSame(@NonNull FontFamily oldItem, @NonNull FontFamily newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.itemSelector = itemSelector;
        this.decorator = decorator;
    }

    interface itemSelector {
        void pickFont(Typeface typeface);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_font, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        if(getItem(position) != null){
            viewHolder.bind(getItem(position), decorator, position);
        }
    }

//    public void orderByFamily(){
//        Collections.sort(families, (o1, o2) -> o1.family().compareTo(o2.family()));
//        notifyDataSetChanged();
//    }
//
//    public void orderByLastModified(){
//        Collections.sort(families, (o1, o2) -> o1.lastModified().compareTo(o2.lastModified()));
//        notifyDataSetChanged();
//    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView font;
        private final ImageView iv_select;
        private Disposable disposable;

        ViewHolder(View view) {
            super(view);
            font = view.findViewById(R.id.tv_font);
            iv_select = view.findViewById(R.id.iv_select);
        }

        void bind(FontFamily fontFamily, FontDecorator decorator, int pos) {
            font.setText(fontFamily.family());
            if (disposable != null && !disposable.isDisposed()) {
                disposable.dispose();
            }
            disposable = decorator.getFontTypeFace(itemView.getContext(), fontFamily)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(font::setTypeface, Throwable::printStackTrace);
            boolean isSelected = fontFamily.family().equals(selectFamily);
            if(isSelected){
                selectPosition = pos;
            }
            iv_select.setVisibility(isSelected? View.VISIBLE : View.INVISIBLE);
            itemView.setOnClickListener(v -> {
                selectFamily = fontFamily.family();
                int old = selectPosition;
                selectPosition = pos;
                notifyItemChanged(old);
                notifyItemChanged(pos);
                itemSelector.pickFont(font.getTypeface());
            });
        }
    }
}
