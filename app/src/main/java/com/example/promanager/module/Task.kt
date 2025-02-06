package com.example.promanager.module

import android.os.Parcel
import android.os.Parcelable

data class Task (
    val taskName: String="",
    val createdBy: String="",
    val cards: ArrayList<Card> = ArrayList()
    ):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(Card.CREATOR)!!
    ) {
    }

    override fun writeToParcel(dest: Parcel, flag: Int)=with(dest) {
        dest.writeString(taskName)
        dest.writeString(createdBy)
        dest.writeTypedList(cards)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Task> {
        override fun createFromParcel(parcel: Parcel): Task {
            return Task(parcel)
        }

        override fun newArray(size: Int): Array<Task?> {
            return arrayOfNulls(size)
        }
    }
}