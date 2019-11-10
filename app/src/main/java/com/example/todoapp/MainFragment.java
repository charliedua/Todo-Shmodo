package com.example.todoapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.adaptors.RecyclerViewListAdaptor;
import com.example.todoapp.models.ToDoItem;
import com.example.todoapp.models.ToDoList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;


public class MainFragment extends Fragment
{
    private RecyclerView rv_ToDoList;
    private Context context;
    private ArrayList<ToDoList> currLists;
    private DatabaseReference database;

    @Override
    public void onAttach(@NonNull Context context)
    {
        this.context = context;
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable Bundle savedInstanceState
                            )
    {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        FloatingActionButton fbtn_add_List = view.findViewById(R.id.fbtn_add_List);
        fbtn_add_List.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent i = new Intent(context, AddListFormActivity.class);
                startActivityForResult(i, 1);
            }
        });

        Snackbar.make(view, "Fetching lists", Snackbar.LENGTH_LONG).show();

        currLists = new ArrayList<>();

        rv_ToDoList = view.findViewById(R.id.rv_ToDo_List);
        rv_ToDoList.setLayoutManager(new LinearLayoutManager(context));

        rv_ToDoList.setAdapter(new RecyclerViewListAdaptor(context, currLists));

        database = FirebaseDatabase.getInstance()
                .getReference(FirebaseAuth.getInstance().getCurrentUser().getUid());


        database.addValueEventListener(new ValueEventListener()
        {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                ((RecyclerViewListAdaptor) rv_ToDoList.getAdapter()).setLists(new ArrayList<ToDoList>());
                Iterator<DataSnapshot> iter = dataSnapshot.getChildren().iterator();

                DataSnapshot list;
                ToDoList toDoList;

                while (iter.hasNext())
                {
                    list = iter.next();

                    ArrayList<ToDoItem> items = new ArrayList<>();

                    for (int i = 0; i < list.child("Items").getChildrenCount(); i++)
                    {
                        String Name = (String) list.child("Items")
                                .child(String.valueOf(i))
                                .child("Name")
                                .getValue();

                        boolean checked = false;
                        try
                        {
                            checked = (boolean) list.child("Items")
                                    .child(String.valueOf(i))
                                    .child("Checked")
                                    .getValue();
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        items.add(new ToDoItem(Name, checked));
                    }

                    toDoList = new ToDoList(
                            list.getKey(),
                            (String) list.child("Name").getValue(),
                            items
                    );

                    AddList(toDoList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Bundle bundle = null;

        if (data != null)
            bundle = data.getExtras();

        if (bundle != null)
        {
            ToDoList List = bundle.getParcelable("List");

            AddList(List);

            String id = database.push().getKey();

            database.child(id).child("Name").setValue(List.getName());

            for (int i = 0; i < List.getItems().size(); i++)
            {
                ToDoItem item = List.getItems().get(i);
                database.child(id)
                        .child("Items")
                        .child(String.valueOf(i))
                        .child("Name")
                        .setValue(item.getName());
                database.child(id)
                        .child("Items")
                        .child(String.valueOf(i))
                        .child("Checked")
                        .setValue(item.getChecked());
            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    public void AddList(ToDoList list)
    {
        int length = ((RecyclerViewListAdaptor) rv_ToDoList.getAdapter()).AddList(list);
        rv_ToDoList.getAdapter().notifyItemInserted(length - 1);

    }


}
