package com.example.tinywins;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WinsAdapter extends RecyclerView.Adapter<WinsAdapter.ViewHolder> {

    public interface Listener {
        void onToggle(WinItem item);
        void onDelete(WinItem item);
    }

    private List<WinItem> items;
    private Listener listener;

    public WinsAdapter(List<WinItem> items, Listener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_win, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        WinItem it = items.get(pos);

        h.text.setText(it.getText());
        h.checkBox.setChecked(it.isDone());

        h.checkBox.setOnCheckedChangeListener((b, c) -> {
            it.setDone(c);
            listener.onToggle(it);
        });

        h.delete.setOnClickListener(v -> listener.onDelete(it));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setList(List<WinItem> newList) {
        this.items = newList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView text;
        ImageButton delete;

        ViewHolder(View v) {
            super(v);
            checkBox = v.findViewById(R.id.checkBox);
            text = v.findViewById(R.id.textWin);
            delete = v.findViewById(R.id.buttonDelete);
        }
    }
}
