package com.stuffexchange.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.stuffexchange.model.Comment;

import java.util.List;

class CommentArrayAdapter extends ArrayAdapter<Comment> {
    private Context context;

    public CommentArrayAdapter(Context context, List<Comment> comments) {
        super(context, R.layout.comment_layout, comments);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        CommentViewHolder viewHolder = null;
        if (row == null) {
            row = LayoutInflater.from(context).inflate(R.layout.comment_layout, parent, false);
            viewHolder = new CommentViewHolder(row);
            row.setTag(viewHolder);
        } else {
            viewHolder = (CommentViewHolder) row.getTag();
        }

        Comment comment = getItem(position);
        viewHolder.usernameTextView.setText(comment.getUsername());
        viewHolder.timestampTextView.setText(comment.getTimestamp().toString());
        viewHolder.contentTextView.setText(comment.getContent());

        return row;
    }

    public class CommentViewHolder {
        TextView usernameTextView;
        TextView timestampTextView;
        TextView contentTextView;

        public CommentViewHolder(View view) {
            usernameTextView = (TextView) view.findViewById(R.id.username);
            timestampTextView = (TextView) view.findViewById(R.id.timestamp);
            contentTextView = (TextView) view.findViewById(R.id.content);
        }
    }
}
