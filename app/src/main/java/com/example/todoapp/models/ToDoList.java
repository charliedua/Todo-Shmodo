package com.example.todoapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ToDoList implements Parcelable
{
    public ToDoList(String id, String name, ArrayList<ToDoItem> items)
    {
        ID = id;
        this.name = name;
        Items = items;
    }

    public ToDoList()
    {
        this(null, "undefined", new ArrayList<ToDoItem>());
    }

    public String getID()
    {
        return ID;
    }

    public void setID(String ID)
    {
        this.ID = ID;
    }

    private String ID;
    private String name;

    protected ToDoList(Parcel in)
    {
        ID = in.readString();
        name = in.readString();
        Items = in.createTypedArrayList(ToDoItem.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(ID);
        dest.writeString(name);
        dest.writeTypedList(Items);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<ToDoList> CREATOR = new Creator<ToDoList>()
    {
        @Override
        public ToDoList createFromParcel(Parcel in)
        {
            return new ToDoList(in);
        }

        @Override
        public ToDoList[] newArray(int size)
        {
            return new ToDoList[size];
        }
    };

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public ArrayList<ToDoItem> getItems()
    {
        return Items;
    }

    public void setItems(ArrayList<ToDoItem> items)
    {
        Items = items;
    }

    private ArrayList<ToDoItem> Items;

}
