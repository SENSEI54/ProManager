package com.example.promanager.activities


import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.promanager.Adapters.MembersAdapter
import com.example.promanager.R
import com.example.promanager.databinding.ActivityMembersBinding
import com.example.promanager.firebase.fireStore
import com.example.promanager.module.Board
import com.example.promanager.module.User
import com.example.promanager.utils.Constants

class MembersActivity : BaseActivity() {
    private lateinit var mBoardDetails:Board
    private lateinit var mAddMembers:ArrayList<User>
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

    fun membersDetails(user:User)
    {

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

    fun setUpMemberListItem(list:ArrayList<User>){
        hideProgressDialog()
        mAddMembers=list
        binding.rvMembersList.layoutManager= LinearLayoutManager(this)
        binding.rvMembersList.setHasFixedSize(true)

        val adapter= MembersAdapter(this,list)
        binding.rvMembersList.adapter=adapter
    }
}