package com.example.promanager.activities

import android.content.Intent
import android.os.Bundle
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
import com.example.promanager.utils.Constants

class TaskActivity : BaseActivity() {
    private lateinit var binding: ActivityTaskBinding
    private lateinit var mBoardDetails:Board

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityTaskBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        var boardDocumentId=""
        if(intent.hasExtra(Constants.DOCUMENT_ID))
        {
            boardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }

        showProgressDialog(resources.getString(R.string.please_wait))
        fireStore().getBoardDetails(this,boardDocumentId)
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

    fun BoardDetails(board: Board){
        mBoardDetails=board
        hideProgressDialog()
        setUpActionBar()

        val addTaskList = Task(resources.getString(R.string.add_list))
        board.taskList.add(addTaskList)

        binding.rvTaskList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
        binding.rvTaskList.setHasFixedSize(true)

        val adapter = TaskListAdapter(this , board.taskList)
        binding.rvTaskList.adapter=adapter
    }

    fun addUpdateBoardTaskList(){
        hideProgressDialog()
        showProgressDialog(resources.getString(R.string.please_wait))
        fireStore().getBoardDetails(this, mBoardDetails.documentID)
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
}