package com.example.bookbuddy.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.bookbuddy.R;
import com.example.bookbuddy.data.entity.Book;
import java.util.ArrayList;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {
    private List<Book> books = new ArrayList<>();
    private OnBookClickListener clickListener;
    private OnBookLongClickListener longClickListener;

    public interface OnBookClickListener {
        void onBookClick(Book book);
    }

    public interface OnBookLongClickListener {
        void onBookLongClick(Book book);
    }

    public void setOnBookClickListener(OnBookClickListener listener) { clickListener = listener; }
    public void setOnBookLongClickListener(OnBookLongClickListener listener) { longClickListener = listener; }

    public void setBooks(List<Book> books) {
        this.books = books != null ? books : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book book = books.get(position);

        holder.titleView.setText(book.getTitle());
        holder.authorView.setText(book.getAuthor());

        String statusText;
        switch (book.getStatus()) {
            case "reading": statusText = "在读"; break;
            case "finished": statusText = "已读"; break;
            default: statusText = "想读"; break;
        }
        holder.statusView.setText(statusText);

        if (book.getCoverUrl() != null && !book.getCoverUrl().isEmpty()) {
            Glide.with(holder.coverView.getContext())
                    .load(book.getCoverUrl())
                    .into(holder.coverView);
        } else {
            holder.coverView.setImageResource(0);
        }

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) clickListener.onBookClick(book);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onBookLongClick(book);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() { return books.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView coverView;
        TextView titleView, authorView, statusView;

        ViewHolder(View itemView) {
            super(itemView);
            coverView = itemView.findViewById(R.id.item_cover);
            titleView = itemView.findViewById(R.id.item_title);
            authorView = itemView.findViewById(R.id.item_author);
            statusView = itemView.findViewById(R.id.item_status);
        }
    }
}
