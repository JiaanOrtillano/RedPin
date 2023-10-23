package com.example.resq

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class Registration : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etBirthday: EditText
    private lateinit var etAddress: EditText
    private lateinit var etPassword: EditText
    private lateinit var etEmail: EditText
    private lateinit var spGender: Spinner
    private lateinit var spCivilStatus: Spinner
    private lateinit var auth: FirebaseAuth
    private lateinit var siginup: Button
    private  lateinit var etPhone: EditText




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        // Initialize Firebase Auth
        auth = Firebase.auth
        etName = findViewById(R.id.full_name)
        etBirthday = findViewById(R.id.bday)
        etAddress = findViewById(R.id.address)
        etPassword = findViewById(R.id.confirm_pass)
        etEmail = findViewById(R.id.email)
        spGender = findViewById(R.id.gender)
        spCivilStatus = findViewById(R.id.civilstat)
        etPhone = findViewById(R.id.phone_num)

        siginup = findViewById(R.id.sign_up)

        siginup.setOnClickListener {
            signup()
        }



    }
    @SuppressLint("MissingInflatedId")
    private fun signup(){
        val name = etName.text.toString()
        val birthday = etBirthday.text.toString()
        val address = etAddress.text.toString()
        val password = etPassword.text.toString()
        val email = etEmail.text.toString()
        val gender = spGender.selectedItem.toString()
        val civilStatus = spCivilStatus.selectedItem.toString()
        val contact_number = etPhone.text.toString()

        if (name.isEmpty() || birthday.isEmpty() || address.isEmpty() ||
            password.isEmpty() || email.isEmpty() || gender.isEmpty() || civilStatus.isEmpty() ||contact_number.isEmpty() ) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    saveUserToDatabase(user, name, birthday, address, gender, civilStatus,contact_number,email)
                    val dialogboxBinding_confirm = layoutInflater.inflate(R.layout.dialogbox_account_created,null)
                    val mydialog = Dialog(this)
                    mydialog.setContentView(dialogboxBinding_confirm)
                    mydialog.setCancelable(true)
                    mydialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
                    mydialog.show()
                    val ok_btn = dialogboxBinding_confirm.findViewById<Button>(R.id.dialog_button_ok_create)
                    ok_btn.setOnClickListener {
                        mydialog.dismiss()
                        home()
                    }


                } else {
                    Toast.makeText(this, "Error creating user", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserToDatabase(
        user: FirebaseUser?, name: String, birthday: String,
        address: String, gender: String, civilStatus: String, contact_number: String, email: String
    ) {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")

        val userMap = HashMap<String, Any>()
        userMap["name"] = name
        userMap["birthday"] = birthday
        userMap["address"] = address
        userMap["gender"] = gender
        userMap["civil_status"] = civilStatus
        userMap ["contacts"] = contact_number
        userMap ["email"] = email
        userMap ["user_type"] = "Resident"
        userMap ["status_account"] = "Pending"

        user?.let {
            usersRef.child(it.uid).setValue(userMap)
        }
    }
    private fun home(){
        val intent = Intent(this,LogIn::class.java)
        startActivity(intent)
        finish()
    }


}