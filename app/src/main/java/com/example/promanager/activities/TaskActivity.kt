package com.example.promanager.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.promanager.Adapters.TaskListAdapter
import com.example.promanager.R
import com.example.promanager.databinding.ActivityTaskBinding
import com.example.promanager.firebase.fireStore
import com.example.promanager.module.Board
import com.example.promanager.module.Card
import com.example.promanager.module.Task
import com.example.promanager.module.User
import com.example.promanager.utils.Constants

class TaskActivity : BaseActivity() {
    private lateinit var binding: ActivityTaskBinding
    private lateinit var mBoardDetails:Board
    private lateinit var mBoardDocumentId: String
    lateinit var mAssignedMembersDetailList: ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityTaskBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        var boardDocumentId=""
        if (intent.hasExtra(Constants.DOCUMENT_ID)) {
            mBoardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID)!!
        }

        showProgressDialog(resources.getString(R.string.please_wait))
        fireStore().getBoardDetails(this,mBoardDocumentId)
    }

    fun setUpActionBar(){
        setSupportActionBar(binding.toolbarTaskListActivity)
        val actionBar=supportActionBar
        if(actionBar!=null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_back_24dp)
            actionBar.title=mBoardDetails.name
        }
        binding.toolbarTaskListActivity.setNavigationOnClickListener{onBackPressed()}
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_members, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.action_member->{
                val intent=Intent(this,MembersActivity::class.java)
                intent.putExtra(Constants.BOARD_DETAIL,mBoardDetails)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
            && (requestCode == MEMBERS_REQUEST_CODE || requestCode == CARD_DETAILS_REQUEST_CODE)
        ) {
            showProgressDialog(resources.getString(R.string.please_wait))
            fireStore().getBoardDetails(this, mBoardDocumentId)
        } else {
            Log.e("Cancelled", "Cancelled")
        }
    }

    fun BoardDetails(board: Board){
        mBoardDetails=board
        hideProgressDialog()
        setUpActionBar()


        showProgressDialog(resources.getString(R.string.please_wait))
        fireStore().getAssignedMembersListDetails(
            this,
            mBoardDetails.assignedTo
        )
    }

    fun createTaskLists(TaskListName:String)
    {
        val task =Task(TaskListName, fireStore().getCurrentUserUid())
        mBoardDetails.taskList.add(0,task)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        showProgressDialog(resources.getString(R.string.please_wait))
        fireStore().addUpdateTaskList(this,mBoardDetails)
    }

    fun UpdateTaskList(position:Int,listName:String, model:Task){
        val task=Task(listName,model.createdBy)

        mBoardDetails.taskList[position]=task
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        showProgressDialog(resources.getString(R.string.please_wait))
        fireStore().addUpdateTaskList(this,mBoardDetails)
    }

    fun deleteTaskList(position: Int){
        mBoardDetails.taskList.removeAt(position)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        showProgressDialog(resources.getString(R.string.please_wait))
        fireStore().addUpdateTaskList(this,mBoardDetails)
    }

    fun addUpdateTaskListSuccess(){
        hideProgressDialog()
        showProgressDialog(resources.getString(R.string.please_wait))
        fireStore().getBoardDetails(this, mBoardDetails.documentID)
    }


    fun addCardToTaskList(position:Int,CardName:String){
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        val cardAssignedUsersList:ArrayList<String> = ArrayList()

        val card = Card(CardName,fireStore().getCurrentUserUid(),cardAssignedUsersList)

        val cardsList=mBoardDetails.taskList[position].cards
        cardsList.add(card)

        val task=Task(mBoardDetails.taskList[position].taskName,
            mBoardDetails.taskList[position].createdBy,
            cardsList
        )

        mBoardDetails.taskList[position]=task

        showProgressDialog(resources.getString(R.string.please_wait))
        fireStore().addUpdateTaskList(this,mBoardDetails)
    }

    fun cardDetails(taskListPosition: Int, cardPosition: Int) {
        val intent = Intent(this, CardDetailsActivity::class.java)
        intent.putExtra(Constants.BOARD_DETAIL, mBoardDetails)
        intent.putExtra(Constants.TASK_LIST_ITEM_POSITION, taskListPosition)
        intent.putExtra(Constants.CARD_LIST_ITEM_POSITION, cardPosition)
        intent.putExtra(Constants.BOARD_MEMBERS_LIST, mAssignedMembersDetailList)
        startActivityForResult(intent, CARD_DETAILS_REQUEST_CODE)
    }

    fun boardMembersDetailList(list: ArrayList<User>) {

        mAssignedMembersDetailList = list

        hideProgressDialog()

        val addTaskList = Task(resources.getString(R.string.add_list))
        mBoardDetails.taskList.add(addTaskList)

        binding.rvTaskList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvTaskList.setHasFixedSize(true)

        val adapter = TaskListAdapter(this, mBoardDetails.taskList)
        binding.rvTaskList.adapter = adapter
    }

//    fun updateCardsInTaskList(taskListPosition: Int, cards: ArrayList<Card>) {
//        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)
//        mBoardDetails.taskList[taskListPosition].cards = cards
//
//        showProgressDialog(resources.getString(R.string.please_wait))
//        fireStore().addUpdateTaskList(this, mBoardDetails)
//    }

    companion object {
        const val MEMBERS_REQUEST_CODE: Int = 13
        const val CARD_DETAILS_REQUEST_CODE: Int = 14
    }
}