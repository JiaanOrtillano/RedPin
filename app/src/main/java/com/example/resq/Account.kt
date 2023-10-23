package com.example.resq

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.auth.User
import com.google.firebase.ktx.Firebase

class Account : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var uid: String
    private lateinit var txtName: TextView
    private lateinit var txtBirthday: TextView
    private lateinit var txtAddress: TextView
    private lateinit var txtEmail: TextView
    private lateinit var txtGender: TextView
    private lateinit var txtCivilStatus: TextView
    private  lateinit var txtPhone: TextView
    private lateinit var user: com.example.resq.User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()



        databaseReference = FirebaseDatabase.getInstance().getReference("users")

        if(uid.isNotEmpty()){
            retrive_data()

        }


        }

    private fun retrive_data() {

        txtName = findViewById(R.id.full_name_user)
        txtBirthday = findViewById(R.id.bday_user)
        txtAddress = findViewById(R.id.address_user)
        txtEmail = findViewById(R.id.email_user)
        txtGender = findViewById(R.id.gender_user)
        txtCivilStatus = findViewById(R.id.civilstat_user)
        txtPhone = findViewById(R.id.phone_num_user)


        databaseReference.child(uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(com.example.resq.User::class.java)
                if (user != null) {
                    txtName.text = user.name
                    txtBirthday.text = user.birthday
                    txtAddress.text = user.address
                    txtEmail.text = user.email
                    txtGender.text = user.gender
                    txtCivilStatus.text = user.civil_status
                    txtPhone.text = user.contacts

                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException())
            }
        })






    }


}
