package com.example.todoapp.adaptors;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.R;
import com.example.todoapp.ToDoListDisplayActivity;
import com.example.todoapp.models.ToDoList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RecyclerViewListAdaptor extends RecyclerView.Adapter<RecyclerViewListAdaptor.ViewHolder>
{
    private final Context context;
    private ArrayList<ToDoList> Lists;
    private LayoutInflater inflater;
    private DatabaseReference database;
    public RecyclerViewListAdaptor(
            Context context,
            ArrayList<ToDoList> lists
                                  )
    {
        this.context = context;
        Lists = lists;
        this.inflater = LayoutInflater.from(context);
        database = FirebaseDatabase.getInstance()
                .getReference(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    public ArrayList<ToDoList> getLists()
    {
        return Lists;
    }

    public void setLists(ArrayList<ToDoList> lists)
    {
        Lists = lists;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int i)
    {
        return R.id.rv_ToDo_List;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = inflater.inflate(R.layout.fragment_main_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.tv_ToDoItemName.setText(Lists.get(position).getName());

        holder.bind(Lists.get(position));
    }

    @Override
    public int getItemCount()
    {
        return Lists.size();
    }

    // Returns the new length of the list
    public int AddList(ToDoList list)
    {
        Lists.add(list);
        return Lists.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView tv_ToDoItemName;
        Button btn_ToDo_addItem;
        ImageButton imgbtn_list_item_edit, imgbtn_list_item_delete;

        ViewHolder(View itemView)
        {
            super(itemView);
            tv_ToDoItemName = itemView.findViewById(R.id.tv_ToDoItemName);
            btn_ToDo_addItem = itemView.findViewById(R.id.btn_ToDo_addItem);
            imgbtn_list_item_delete = itemView.findViewById(R.id.imgbtn_list_item_delete);
            imgbtn_list_item_edit = itemView.findViewById(R.id.imgbtn_list_item_edit);
        }

        void bind(final ToDoList list)
        {
            btn_ToDo_addItem.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent i = new Intent(context, ToDoListDisplayActivity.class);
                    i.putExtra("list", list);
                    context.startActivity(i);
                }
            });

            imgbtn_list_item_delete.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    String id = list.getID();

                    database.child(id).removeValue();
                }
            });
        }
    }
}
