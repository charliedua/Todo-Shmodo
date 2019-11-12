package com.example.todoapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.adaptors.RecyclerViewItemAdaptor;
import com.example.todoapp.models.ToDoItem;
import com.example.todoapp.models.ToDoList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ToDoListDisplayActivity extends AppCompatActivity
{

    RecyclerView rv_todo_items;
    //ArrayList<ToDoItem> list;
    private DatabaseReference database;

    private Button add_btn;
    private EditText et_add_list_item_name;
    private CheckBox cb_add_list_Item;
    private String currTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list_display);
        Toolbar toolbar = findViewById(R.id.tb_main);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        ToDoList list = bundle.getParcelable("list");
        rv_todo_items = findViewById(R.id.rv_todo_items);
        rv_todo_items.setLayoutManager(new LinearLayoutManager(this));
        rv_todo_items.setAdapter(new RecyclerViewItemAdaptor(
                this,
                new ArrayList<ToDoItem>(),
                list.getID()
        ));

        currTitle = list.getName();

        add_btn = findViewById(R.id.btn_add_list_add_item);
        et_add_list_item_name = findViewById(R.id.et_add_list_item_name);
        cb_add_list_Item = findViewById(R.id.cb_add_list_Item);

        database = FirebaseDatabase.getInstance()
                .getReference(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(list.getID())
                .child("Items");

        database.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                ((RecyclerViewItemAdaptor) rv_todo_items.getAdapter()).setItems(new ArrayList<ToDoItem>());
                ((RecyclerViewItemAdaptor) rv_todo_items.getAdapter()).notifyDataSetChanged();
                for (int i = 0; i < dataSnapshot.getChildrenCount(); i++)
                {
                    String name = (String) dataSnapshot.child(String.valueOf(i))
                            .child("Name")
                            .getValue();

                    boolean checked = false;
                    try
                    {
                        checked = (boolean) dataSnapshot.child(String.valueOf(i))
                                .child("Checked")
                                .getValue();

                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    AddItem(new ToDoItem(name, checked));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        add_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                database.child(String.valueOf(((RecyclerViewItemAdaptor) rv_todo_items.getAdapter())
                                                      .getItems()
                                                      .size()))
                        .child("Name")
                        .setValue(et_add_list_item_name.getText().toString());
                database.child(String.valueOf(((RecyclerViewItemAdaptor) rv_todo_items.getAdapter())
                                                      .getItems()
                                                      .size())).child("Checked").setValue(cb_add_list_Item.isChecked());
            }
        });
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        getSupportActionBar().setTitle(currTitle);
    }

    public void AddItem(ToDoItem item)
    {
        int length = ((RecyclerViewItemAdaptor) rv_todo_items.getAdapter()).AddList(item);
        rv_todo_items.getAdapter().notifyItemInserted(length - 1);
    }
}
