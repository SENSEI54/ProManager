package com.example.promanager.activities


import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.promanager.Adapters.MembersAdapter
import com.example.promanager.R
import com.example.promanager.databinding.ActivityMembersBinding
import com.example.promanager.firebase.fireStore
import com.example.promanager.module.Board
import com.example.promanager.module.User
import com.example.promanager.utils.Constants

class MembersActivity : BaseActivity() {
    private lateinit var mBoardDetails: Board
    private lateinit var mAssignedMembersList: ArrayList<User>
    private var anyChangesDone: Boolean = false

    private lateinit var binding: ActivityMembersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMembersBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        setUpActionBar()
        if(intent.hasExtra(Constants.BOARD_DETAIL))
        {
            mBoardDetails = intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!!
        }

        showProgressDialog(resources.getString(R.string.please_wait))
        fireStore().getMemberList(this,mBoardDetails.assignedTo)
    }

    fun setUpActionBar(){
        setSupportActionBar(binding.toolbarMembersActivity)
        val actionBar=supportActionBar

        if(actionBar!=null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_back_24dp)
            actionBar.title=resources.getString(R.string.members)
        }

        binding.toolbarMembersActivity.setNavigationOnClickListener{onBackPressed()}
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_members, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_members-> {
                dialogSearchMember()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun setupMembersList(list: ArrayList<User>) {
        mAssignedMembersList = list

        hideProgressDialog()
        binding.rvMembersList.layoutManager = LinearLayoutManager(this)
        binding.rvMembersList.setHasFixedSize(true)
        val adapter = MembersAdapter(this, list)
        binding.rvMembersList.adapter = adapter
    }

    fun membersDetails(user:User)
    {
        mBoardDetails.assignedTo.add(user.id)

        fireStore().assignMemberToBoard(this, mBoardDetails, user)
    }



    fun memberAssignSuccess(user: User) {
        hideProgressDialog()
        mAssignedMembersList.add(user)
        anyChangesDone = true
        setUpMemberListItem(mAssignedMembersList)
    }



    private fun dialogSearchMember() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_search_member)

        dialog.findViewById<TextView>(R.id.tv_add).setOnClickListener {
            val email = dialog.findViewById<EditText>(R.id.et_email_search_member).text.toString()

            if (email.isNotEmpty()) {
                dialog.dismiss()
                showProgressDialog(resources.getString(R.string.please_wait))
                fireStore().getMemberDetails(this, email)
            } else {
                showErrorSnackBar("Please enter members email address.")
            }
        }

        dialog.findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    fun setUpMemberListItem(list:ArrayList<User>){
        hideProgressDialog()
        mAssignedMembersList=list
        binding.rvMembersList.layoutManager= LinearLayoutManager(this)
        binding.rvMembersList.setHasFixedSize(true)

        val adapter= MembersAdapter(this,list)
        binding.rvMembersList.adapter=adapter
    }
}