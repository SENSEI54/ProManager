package com.example.promanager.module

import android.os.Parcel
import android.os.Parcelable

data class Card (
    val name: String = "",
    val createdby: String = "",
    val AssignedTo: ArrayList<String> = ArrayList()
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!
    ) {
    }

    override fun writeToParcel(dest: Parcel, flags: Int)=with(dest) {
        dest.writeString(name)
        dest.writeString(createdby)
        dest.writeStringList(AssignedTo)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Card> {
        override fun createFromParcel(parcel: Parcel): Card {
            return Card(parcel)
        }

        override fun newArray(size: Int): Array<Card?> {
            return arrayOfNulls(size)
        }
    }
}