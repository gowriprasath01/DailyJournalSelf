package ui;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.self.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import Util.JournalApi;
import model.Journal;

public class JournalRecyclerview extends RecyclerView.Adapter<JournalRecyclerview.ViewHolder> {

    private Context context;
    private List<Journal> journalList;

    public JournalRecyclerview(Context context, List<Journal> journalList) {
        this.context = context;
        this.journalList = journalList;
    }

    @NonNull
    @Override

    public JournalRecyclerview.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_list, parent,false);

        return new ViewHolder(view,context);
        }

    @Override
    public void onBindViewHolder(@NonNull JournalRecyclerview.ViewHolder holder, int position) {

        Journal journal= journalList.get(position);

        holder.Title.setText(journal.getTitle());
        holder.Thoughts.setText(journal.getThought());
        String imageUrl;
        imageUrl = journal.getImageUrl();
        holder.name.setText(JournalApi.getInstance().getUsername());

//        String timeAgo= (String) DateUtils.getRelativeTimeSpanString(context,1000);
//        holder.dateAdded.setText(timeAgo);

        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.download)
                .fit()
                .into(holder.image);





    }

    @Override
    public int getItemCount() {
        return journalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView Title;
        public TextView Thoughts;
        public TextView dateAdded;
        public TextView name;

        String userName;
        String userId;
        public ImageView image;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context= ctx;

            Title=itemView.findViewById(R.id.Title_list);
            Thoughts= itemView.findViewById(R.id.Thoughts_list);
            dateAdded =itemView.findViewById(R.id.journal_timestamp_list);
            image = itemView.findViewById(R.id.journal_image_list);
            name= itemView.findViewById(R.id.journal_name_list);

        }
    }
}
