package com.example.promanager.activities

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.promanager.R
import com.example.promanager.databinding.ActivityMyProfileBinding
import com.example.promanager.firebase.fireStore
import com.example.promanager.module.User
import com.example.promanager.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class MyProfileActivity : BaseActivity() {



    var selectedImageUri:Uri?=null
    var profileImageUrl:String=""
    private lateinit var mUserDetail:User
    private lateinit var binding:ActivityMyProfileBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMyProfileBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        setUpActionBar()
        fireStore().loadUserData(this)

        binding.myProfileCv.setOnClickListener{
            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                Constants.showImagePicker(this)
            }
            else{
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.Read_Storage_Permission_Code)
            }
        }

        binding.updateBtn.setOnClickListener {
            if(selectedImageUri!=null){
                uploadImageOnFirebaseStorage()
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))
                updateUserProfileInfo()
            }
        }
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
                .placeholder(R.drawable.ic_user_place_holder)
                .into(binding.myProfileCv)
        }catch (e:IOException){
            Toast.makeText(this,"$e",Toast.LENGTH_LONG).show()
        }
    }

    fun setUpActionBar(){
       setSupportActionBar(binding.myProfileToolBar)
        val actionBar=supportActionBar

        if(actionBar!=null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_back_24dp)
            actionBar.title=resources.getString(R.string.my_profile)
        }

        binding.myProfileToolBar.setNavigationOnClickListener{onBackPressed()}
    }


    fun populateUserData(user:User)
    {
        mUserDetail=user
        Glide
            .with(this)
            .load(user.image)
            .circleCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(binding.myProfileCv)

        binding.etProfileName.setText(user.name)
        binding.etProfileEmail.setText(user.email)
        if(user.mobile!=0L)
        {
            binding.etProfileMobile.setText(user.mobile.toString())
        }
    }

    fun uploadImageOnFirebaseStorage(){
        showProgressDialog(resources.getString(R.string.please_wait))
        if(selectedImageUri!=null){
            val storeRef:StorageReference=FirebaseStorage.getInstance().reference.child(
                "User_image"+System.currentTimeMillis()+"."+Constants.getFileExtension(this,selectedImageUri)
            )
            storeRef.putFile(selectedImageUri!!).addOnSuccessListener{
                taskSnapshot->
                Log.i("Firebase Image Url"
                    ,taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri->
                    Log.i("downloadImageUri", uri.toString())
                    profileImageUrl=uri.toString()
                    updateUserProfileInfo()
                }
            }.addOnFailureListener{
                exception->
                Toast.makeText(this@MyProfileActivity,exception.message,Toast.LENGTH_LONG )
                hideProgressDialog()
            }
        }
    }
    fun updateUserProfileInfo(){
        val userHashMap=HashMap<String,Any>()

        if(profileImageUrl.isNotEmpty()&&profileImageUrl!=mUserDetail.image){
            userHashMap[Constants.IMAGE]=profileImageUrl
        }
        if(binding.etProfileName.text.toString()!=mUserDetail.name){
            userHashMap[Constants.NAME]=binding.etProfileName.text.toString()
        }

        if(binding.etProfileMobile.text.toString()!=mUserDetail.mobile.toString()){
            userHashMap[Constants.MOBILE]=binding.etProfileMobile.text.toString().toLong()
        }


        fireStore().updateUserProfileInfo(this, userHashMap)
    }

    fun profileUpdateSuccess(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }



}