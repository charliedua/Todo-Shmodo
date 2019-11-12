package com.example.todoapp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ToDoItem implements Parcelable
{
    public static final Creator<ToDoItem> CREATOR = new Creator<ToDoItem>()
    {
        @Override
        public ToDoItem createFromParcel(Parcel in)
        {
            return new ToDoItem(in);
        }

        @Override
        public ToDoItem[] newArray(int size)
        {
            return new ToDoItem[size];
        }
    };

    private String name;
    private Boolean checked;

    public ToDoItem(String name, boolean checked)
    {
        this.name = name;
        this.checked = checked;
    }

    public ToDoItem()
    {
        this("undefined", false);
    }

    public ToDoItem(Parcel in)
    {
        name = in.readString();
        checked = in.readInt() == 1;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(name);
        parcel.writeInt(checked ? 1 : 0);
    }

    public Boolean getChecked()
    {
        return checked;
    }

    public void setChecked(Boolean checked)
    {
        this.checked = checked;
    }
}
