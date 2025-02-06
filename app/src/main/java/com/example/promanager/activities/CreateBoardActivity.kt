package com.example.promanager.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.promanager.R
import com.example.promanager.databinding.ActivityCreateBoardBinding
import com.example.promanager.firebase.fireStore
import com.example.promanager.module.Board
import com.example.promanager.module.User
import com.example.promanager.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class CreateBoardActivity : BaseActivity() {

    private lateinit var binding:ActivityCreateBoardBinding
    var selectedImageUri: Uri?=null
    private lateinit var mUserName:String
    private var boardImageUrl:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCreateBoardBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        setUpActionBar()

        if(intent.hasExtra(Constants.NAME))
        {
            mUserName = intent.getStringExtra(Constants.NAME).toString()
        }

        binding.createBoardCv.setOnClickListener{
            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                Constants.showImagePicker(this)
            }
            else{
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.Read_Storage_Permission_Code)
            }
        }

        binding.createBoardBtn.setOnClickListener {
            if(selectedImageUri!=null)
            {
                uploadImage()
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))
                createBoard()
            }
        }
    }
    private fun setUpActionBar(){
        val toolBar=findViewById<Toolbar>(R.id.create_board_toolbar)
        setSupportActionBar(toolBar)
        val actionBar=supportActionBar
        if(actionBar!=null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_back_24dp)
        }
        binding.createBoardToolbar.setNavigationOnClickListener{onBackPressed()}
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== Constants.Read_Storage_Permission_Code){
            if(grantResults.isNotEmpty()&&grantResults[0]== Constants.Read_Storage_Permission_Code){
                Constants.showImagePicker(this)
            }
        }else{
            Toast.makeText(this,"Oops! you denied the permission for storage, You can turn on from settings",Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK && requestCode== Constants.Pick_imagePermission_code && data!!.data!=null)
        {
            selectedImageUri=data.data
        }
        try {
            Glide
                .with(this)
                .load(selectedImageUri)
                .centerCrop()
                .placeholder(R.drawable.ic_board_place_holder)
                .into(binding.createBoardCv)
        }catch (e: IOException){
            Toast.makeText(this,"$e", Toast.LENGTH_LONG).show()
        }
    }

    fun createBoard(){
        val assignedUsersArrayList:ArrayList<String> = ArrayList()
        assignedUsersArrayList.add(getCurrentUserID())

        var board=Board(
            binding.boardName.text.toString(),
            boardImageUrl,
            mUserName,
            assignedUsersArrayList
        )
        fireStore().createBoard(this,board)
    }

    private fun uploadImage(){
        showProgressDialog(resources.getString(R.string.please_wait))
        if(selectedImageUri!=null){
            val storeRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                "Board_image"+System.currentTimeMillis()+"."+Constants.getFileExtension(this,selectedImageUri)
            )
            storeRef.putFile(selectedImageUri!!).addOnSuccessListener{
                    taskSnapshot->
                Log.i("Firebase Image Url"
                    ,taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        uri->
                    Log.i("downloadImageUri", uri.toString())
                    boardImageUrl=uri.toString()
                    createBoard()
                }
            }.addOnFailureListener{
                    exception->
                Toast.makeText(this,exception.message,Toast.LENGTH_LONG )
                hideProgressDialog()
            }
        }
    }

    fun BoaedCreatedSuccesfully(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }
}