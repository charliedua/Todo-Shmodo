package com.example.todoapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.adaptors.RecyclerViewItemAdaptor;
import com.example.todoapp.models.ToDoItem;
import com.example.todoapp.models.ToDoList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AddListFormActivity extends AppCompatActivity
{
    ToDoList List;
    RecyclerView rv_add_list;
    FloatingActionButton fbtn_add_List;
    Button btn_add_list_add_item;
    EditText et_add_list_item_name, et_Name;
    CheckBox cb_add_list_Item;

    Button.OnClickListener btn_add_item_OnClickListner = new Button.OnClickListener()
    {
        public void onClick(View view)
        {
            if (validate())
            {
                List.getItems()
                        .add(
                                new ToDoItem(
                                        et_add_list_item_name.getText().toString(),
                                        cb_add_list_Item.isChecked()
                                )
                            );
                rv_add_list.getAdapter().notifyItemInserted(List.getItems().size() - 1);
            }
        }
    };

    FloatingActionButton.OnClickListener fbtn_add_List_OnClickListener = new FloatingActionButton.OnClickListener()
    {
        public void onClick(View view)
        {

            if (validate())
            {
                Intent returnIntent = new Intent();
                List.setName(et_Name.getText().toString());
                returnIntent.putExtra("List", List);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        }
    };

    private boolean validate()
    {
        boolean returnValue = true;
        String name = et_add_list_item_name.getText().toString();

        if (name.isEmpty())
        {
            et_add_list_item_name.setError("No empty Names accepted");
            returnValue = false;
        }

        return returnValue;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list_form);

        List = new ToDoList();

        btn_add_list_add_item = findViewById(R.id.btn_add_list_add_item);
        btn_add_list_add_item.setOnClickListener(btn_add_item_OnClickListner);

        fbtn_add_List = findViewById(R.id.fbtn_add_List_form);
        fbtn_add_List.setOnClickListener(fbtn_add_List_OnClickListener);

        et_add_list_item_name = findViewById(R.id.et_add_list_item_name);
        cb_add_list_Item = findViewById(R.id.cb_add_list_Item);
        et_Name = findViewById(R.id.et_Name);

        rv_add_list = findViewById(R.id.rv_add_list);
        rv_add_list.setLayoutManager(new LinearLayoutManager(this));
        rv_add_list.setAdapter(new RecyclerViewItemAdaptor(this, List.getItems(), List.getID()));
    }
}
