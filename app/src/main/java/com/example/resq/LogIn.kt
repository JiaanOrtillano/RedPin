package com.example.resq

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class LogIn : AppCompatActivity() {
    @SuppressLint("MissingInflatedId", "WrongViewCast")
    private lateinit var auth: FirebaseAuth
    private lateinit var email: EditText
    private lateinit var pass: EditText
    private lateinit var StatusAcct: String
    private lateinit var uid: String
    private lateinit var databaseReference: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
        auth = Firebase.auth
        StatusAcct = " "
        val login = findViewById<ImageView>(R.id.log_in_btn)
        val signup = findViewById<TextView>(R.id.signupmain)
        email = findViewById(R.id.email_login)
        pass = findViewById(R.id.pass_login)





        login.setOnClickListener{

            Login()


        }

        signup.setOnClickListener {
            val intent = Intent(this, Registration::class.java)
            startActivity(intent)
        }


    }

    private fun Login() {
        val email_login = email.text.toString()
        val password_login = pass.text.toString()

        if(email_login.isEmpty() || password_login.isEmpty()){
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email_login, password_login)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    verification()
                    if(StatusAcct == "Pending"){
                        val dialogboxBinding_Pending = layoutInflater.inflate(R.layout.dialogbox_pending, null)
                        val mydialog = Dialog(this)
                        mydialog.setContentView(dialogboxBinding_Pending)
                        mydialog.setCancelable(true)
                        mydialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
                        mydialog.show()
                        val ok_btn = dialogboxBinding_Pending.findViewById<Button>(R.id.dialog_button_ok_ped)
                        ok_btn.setOnClickListener {
                            mydialog.dismiss()
                            auth.signOut()
                        }
                        auth.signOut()
                    }
                    else if(StatusAcct.isEmpty()){
                        Toast.makeText(this, "No Status Account", Toast.LENGTH_SHORT).show()
                        auth.signOut()
                    }
                    else if(StatusAcct == "Rejected"){
                        val dialogboxBinding_Rejected = layoutInflater.inflate(R.layout.dialogbox_rejected, null)
                        val mydialog = Dialog(this)
                        mydialog.setContentView(dialogboxBinding_Rejected)
                        mydialog.setCancelable(true)
                        mydialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
                        mydialog.show()
                        val ok_btn = dialogboxBinding_Rejected.findViewById<Button>(R.id.dialog_button_ok_rej)
                        ok_btn.setOnClickListener {
                            mydialog.dismiss()
                            auth.signOut()
                        }

                    }
                    else if(StatusAcct == "Approved"){
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()

                }
            }


    }



    private fun verification(){
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        databaseReference = FirebaseDatabase.getInstance().getReference("users")


        databaseReference.child(uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(com.example.resq.User::class.java)
                if (user != null) {
                    StatusAcct = user.status_account
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException())
            }
        })




    }
}