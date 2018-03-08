package com.example.dhairyakumar.booklisting;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;



public class BookAdapter extends ArrayAdapter<Book> {

    public BookAdapter(Context context, List<Book> books) {
        super(context, 0, books);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.book_list, parent, false);
        }
        Book currentBook = getItem(position);
        ViewHolder holder = new ViewHolder();
        if (currentBook != null)
        {
            String book_title = currentBook.getTitle();
            String book_author = currentBook.getAuthor();
            String book_publisher = currentBook.getPublisher();
            holder.author = listItemView.findViewById(R.id.textView_author);
            holder.title = listItemView.findViewById(R.id.textView_title);
            holder.publisher = listItemView.findViewById(R.id.textView_publisher);
            holder.author.setText(book_author);
            holder.title.setText(book_title);
            holder.publisher.setText(book_publisher);
        }
        return listItemView;
    }
}