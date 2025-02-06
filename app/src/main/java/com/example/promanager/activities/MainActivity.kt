package com.example.promanager.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.promanager.Adapters.BoardItemsAdapter
import com.example.promanager.R
import com.example.promanager.databinding.ActivityMainBinding
import com.example.promanager.firebase.fireStore
import com.example.promanager.module.Board
import com.example.promanager.module.User
import com.example.promanager.utils.Constants
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity :BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object{
        const val Profile_Request_Code:Int=11
        const val Create_Board_Request_Code:Int=12
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var mUserName:String
    private lateinit var userName:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        binding.navView.setNavigationItemSelectedListener(this)
        setUpActionBar()
        fireStore().loadUserData(this,true)

        val flt_btn=findViewById<FloatingActionButton>(R.id.add_btn)
        flt_btn.setOnClickListener{
            val intent=Intent(this,CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME,mUserName)
            startActivityForResult(intent,Create_Board_Request_Code)
        }
    }

    fun populateBoardItem(boardList:ArrayList<Board>){
        val rv_board=findViewById<RecyclerView>(R.id.rv_board_list)
        val rv_Tv=findViewById<TextView>(R.id.tv_no_boards_available)
        hideProgressDialog()
        if(boardList.size>0)
        {
            rv_board.visibility= View.VISIBLE
            rv_Tv.visibility=View.GONE

            rv_board.layoutManager=LinearLayoutManager(this)
            rv_board.setHasFixedSize(true)

            val adapter=BoardItemsAdapter(this,boardList)
            rv_board.adapter=adapter

            adapter.setOnClickListener(object : BoardItemsAdapter.OnClickListener{
                override fun onClick(position: Int, model: Board) {
                    val intent=Intent(this@MainActivity,TaskActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID,model.documentID)
                   startActivity(intent)
                }
            })
        }else{
            rv_board.visibility= View.GONE
            rv_Tv.visibility=View.VISIBLE
        }

    }

    fun setUpActionBar(){
        val toolBarMain=findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_main_activity)
        toolBarMain.setNavigationIcon(R.drawable.ic_navigation_menu)
        toolBarMain.setNavigationOnClickListener{
           toogleToolbar()
        }

    }


    fun toogleToolbar(){
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }else
        {
            binding.drawerLayout.openDrawer((GravityCompat.START))
        }
    }

    override fun onBackPressed() {
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }else
        {
            doublePressBack()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==Activity.RESULT_OK && requestCode== Profile_Request_Code ){
            fireStore().loadUserData(this)
        }else if(resultCode==Activity.RESULT_OK && requestCode== Create_Board_Request_Code){
            fireStore().getBoardData(this)
        }
        else{
            Log.e("Cancelled","cancelled")
        }
    }

    fun NavigationUpdate(user:User,readBoardList:Boolean){
        val navUserImage=findViewById<CircleImageView>(R.id.circular_View)
        val tv_name=findViewById<TextView>(R.id.nav_tv_name)
        mUserName=user.name
        Glide
            .with(this)
            .load(user.image)
            .circleCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(navUserImage)

        tv_name.text=user.name

        if(readBoardList)
        {
            showProgressDialog(resources.getString(R.string.please_wait))
            fireStore().getBoardData(this)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean
    {
        when(item.itemId){
            R.id.nav_my_profile->{
                startActivityForResult(Intent(this,MyProfileActivity::class.java),
                    Profile_Request_Code)
            }
            R.id.nav_sign_out->{
                FirebaseAuth.getInstance().signOut()
                val intent= Intent(this,Intro_Activity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
