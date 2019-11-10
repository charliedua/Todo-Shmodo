package com.example.todoapp.adaptors;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.MainActivity;
import com.example.todoapp.R;
import com.example.todoapp.models.ToDoItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RecyclerViewItemAdaptor extends RecyclerView.Adapter<RecyclerViewItemAdaptor.ViewHolder>
{
    private final DatabaseReference database;
    private final String listID;
    private ArrayList<ToDoItem> Items;

    public void setItems(ArrayList<ToDoItem> items)
    {
        Items = items;
    }

    private LayoutInflater inflater;

    public RecyclerViewItemAdaptor(
            Context context,
            ArrayList<ToDoItem> items,
            String listID
                                  )
    {
        Items = items == null ? new ArrayList<ToDoItem>() : items;
        this.inflater = LayoutInflater.from(context);
        this.listID = listID;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null)
        {
            database = FirebaseDatabase.getInstance()
                    .getReference(user.getUid());
        }
        else
        {
            database = null;
            Intent i = new Intent(context,MainActivity.class);
            context.startActivity(i);
        }
    }

    public ArrayList<ToDoItem> getItems()
    {
        return Items;
    }

    @Override
    public long getItemId(int i)
    {
        return R.id.rv_ToDo_List;
    }

    @NonNull
    @Override
    public RecyclerViewItemAdaptor.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
                                                                )
    {
        View view = inflater.inflate(R.layout.todo_list_item, parent, false);
        return new RecyclerViewItemAdaptor.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewItemAdaptor.ViewHolder holder, int position)
    {
        holder.tv_ToDoItemName.setText(Items.get(position).getName());
        holder.cb_ToDoItem.setChecked(Items.get(position).getChecked());

        holder.bind(Items.get(position), position);
    }

    @Override
    public int getItemCount()
    {
        return Items.size();
    }

    public int AddList(ToDoItem item)
    {
        if (item != null)
            Items.add(item);
        return Items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        CheckBox cb_ToDoItem;
        TextView tv_ToDoItemName;
        Button btn_ToDo_addItem;

        ViewHolder(View itemView)
        {
            super(itemView);
            cb_ToDoItem = itemView.findViewById(R.id.cb_ToDoItem);
            tv_ToDoItemName = itemView.findViewById(R.id.tv_ToDoItemName);
            btn_ToDo_addItem = itemView.findViewById(R.id.btn_ToDo_addItem);
        }


        public void bind(final ToDoItem item, final int position)
        {
            cb_ToDoItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b)
                {
                    database.child(listID).child("Items").child(String.valueOf(position)).child("Checked").setValue(b);
                }
            });
        }
    }
}
