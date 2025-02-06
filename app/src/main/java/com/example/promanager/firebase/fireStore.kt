package com.example.promanager.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.promanager.activities.*
import com.example.promanager.module.Board
import com.example.promanager.module.User
import com.example.promanager.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class fireStore {
    private val mFireStore=FirebaseFirestore.getInstance()

    fun updateUserProfileInfo(activity:MyProfileActivity,userHashMap: HashMap<String,Any>){
        mFireStore.collection(Constants.Users).document(getCurrentUserUid()).update(userHashMap).addOnSuccessListener {
            Log.i(activity.javaClass.simpleName,"Data Uploaded at user Profile")
            Toast.makeText(activity,"profile data has been updated",Toast.LENGTH_SHORT).show()
            activity.profileUpdateSuccess()
        }.addOnFailureListener{
            e->
            activity.hideProgressDialog()
            Log.i(activity.javaClass.simpleName,"Error while creating board",e)
            Toast.makeText(activity,"Error while updating the data",Toast.LENGTH_SHORT).show()
        }
    }

    fun createBoard(activity: CreateBoardActivity,board:Board){
        mFireStore.collection(Constants.Boards).document().set(board, SetOptions.merge()).addOnSuccessListener {
            Log.e(activity.javaClass.simpleName,"Board Created SuccessFully")

            Toast.makeText(activity,"Board created Successfully",Toast.LENGTH_LONG).show()
            activity.BoaedCreatedSuccesfully()
        }.addOnFailureListener{
            exception->
            activity.hideProgressDialog()
            Log.e(
                activity.javaClass.simpleName,"Board creation Failed",exception
            )
        }
    }

    fun getBoardDetails(activity: TaskActivity,documentID:String){
        mFireStore.collection(Constants.Boards)
            .document(documentID)
            .get()
            .addOnSuccessListener {
                    document->
                Log.i(activity.javaClass.simpleName,document.toString())
                val board=document.toObject(Board::class.java)!!
                board.documentID=document.id
                activity.BoardDetails(board)
            }.addOnFailureListener{
                    e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Cant load the data",e)
            }
    }

    fun getBoardData(activity: MainActivity){
        mFireStore.collection(Constants.Boards)
            .whereArrayContains(Constants.ASSIGNED_TO,getCurrentUserUid())
            .get()
            .addOnSuccessListener {
                document->
                Log.i(activity.javaClass.simpleName,document.documents.toString())

                val boardList: ArrayList<Board> = ArrayList()
                for(i in document.documents)
                {
                    val board=i.toObject(Board::class.java)!!
                    board.documentID=i.id
                    boardList.add(board)
                }
                activity.populateBoardItem(boardList)
            }.addOnFailureListener{
                e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Cant load the data",e)
            }
    }
    fun addUpdateTaskList(activity: TaskActivity,board:Board){
        val TaskListHashMap=HashMap<String,Any>()
        TaskListHashMap[Constants.TASK_LIST] = board.taskList

        mFireStore.collection(Constants.Boards).document(board.documentID).update(TaskListHashMap).addOnSuccessListener {
            Log.i(activity.javaClass.simpleName,"TaskListUpdation was succesdfull")
            activity.addUpdateBoardTaskList()
        }.addOnFailureListener{
            e->Log.e(activity.javaClass.simpleName,"Error Occurred",e)
        }
    }

    fun loadUserData(activity:Activity, readBoardList: Boolean = false)
    {
        mFireStore.collection(Constants.Users).document(getCurrentUserUid()).get().addOnSuccessListener {
            document->

            val LoggedInUser=document.toObject(User::class.java)!!
            when(activity)
            {
                is Sign_in_Activity->{
                    activity.SigninSuccess(LoggedInUser)
                }
                is MainActivity->{
                    activity.NavigationUpdate(LoggedInUser,readBoardList)
                }
                is MyProfileActivity->{
                    activity.populateUserData(LoggedInUser)
                }
            }

        }.addOnFailureListener{ e ->
            when(activity)
            {
                is Sign_in_Activity->{
                    activity.hideProgressDialog()
                }
                is MainActivity->{
                    activity.hideProgressDialog()
                }
            }
            Log.e("Sign In failed","data fetching Failed")
        }
    }

    fun registerUser(activity:Sign_up_activity,UserInfo: User){
        mFireStore.collection(Constants.Users).document(getCurrentUserUid()).set(UserInfo,
            SetOptions.merge()).addOnSuccessListener {
            activity.RegisteredUserSuccessfull(UserInfo.name,UserInfo.email)
        }.addOnFailureListener(){
            e->
            Log.e(activity.javaClass.simpleName,"Error writing document")
        }
    }

    fun getCurrentUserUid():String{
        val currentUser= FirebaseAuth.getInstance().currentUser
        var currentUserId=""
        if(currentUser!=null)
        {
            currentUserId=currentUser.uid
        }
        return currentUserId
    }

    fun getMemberList(activity: MembersActivity,assignedTo: ArrayList<String>){
        mFireStore.collection(Constants.Users).whereIn(Constants.ID,assignedTo).get().addOnSuccessListener {
            document->
            Log.e(activity.javaClass.simpleName,document.documents.toString())

            val userList:ArrayList<User> = ArrayList()
            for (i in document.documents){
                val user=i.toObject(User::class.java)!!
                userList.add(user)
            }
            activity.setUpMemberListItem(userList)
        }.addOnFailureListener{e->
            activity.hideProgressDialog()
            Log.e(activity.javaClass.simpleName,"Can't fetch the members",e)
        }
    }

    fun getMemberDetails(activity: MembersActivity,email:String){
        mFireStore.collection(Constants.Users)
            .whereEqualTo(Constants.EMAIL,email)
            .get().addOnSuccessListener{
                document->
                if(document.documents.size>0)
                {
                   val user=document.documents[0].toObject(User::class.java)
                   activity.membersDetails(user!!)
                }
                else{
                   activity.hideProgressDialog()
                   activity.showErrorSnackBar("No User Found")
                }
        }.addOnFailureListener {
            activity.hideProgressDialog()
            Log.e(activity.javaClass.simpleName,"Error while fetching",)
        }
    }
}