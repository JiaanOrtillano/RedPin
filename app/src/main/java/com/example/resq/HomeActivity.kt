package com.example.resq

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class HomeActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")

    private lateinit var auth: FirebaseAuth
    private lateinit var uid: String
    private lateinit var name_user: String
    private lateinit var address_user: String
    private lateinit var time_report: String
    private lateinit var contact_num: String
    private lateinit var status: String
    private lateinit var coor: String
    private lateinit var databaseReference: DatabaseReference
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val REQUEST_LOCATION_PERMISSION = 1

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()


        databaseReference = FirebaseDatabase.getInstance().getReference("users")
        databaseReference.child(uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(com.example.resq.User::class.java)
                if (user != null) {
                    name_user = user.name
                    address_user = user.address
                    contact_num = user.contacts


                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(ContentValues.TAG, "onCancelled", databaseError.toException())
            }
        })


        val mapfrag = MapsFragment()

        supportFragmentManager.beginTransaction().replace(R.id.maps, mapfrag).commit()

        val myaccount = findViewById<Button>(R.id.btn_acct)
        val sos = findViewById<ImageView>(R.id.sos_btn)




        myaccount.setOnClickListener {
            val intent = Intent(this, Account::class.java)
            startActivity(intent)
        }

        sos.setOnClickListener {
            val dialogboxBinding = layoutInflater.inflate(R.layout.dialogbox_confirmation, null)

            val mydialog = Dialog(this)
            mydialog.setContentView(dialogboxBinding)

            mydialog.setCancelable(true)
            mydialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
            mydialog.show()
            val no_btn = dialogboxBinding.findViewById<Button>(R.id.dialog_button_no)
            val yes_btn = dialogboxBinding.findViewById<Button>(R.id.dialog_button_yes)

            yes_btn.setOnClickListener {
                mydialog.dismiss()
                val currentDate = LocalDateTime.now().toLocalDate()
                val currentTime = LocalDateTime.now().toLocalTime()
                val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val timeFormatter = DateTimeFormatter.ofPattern("HH:mm a")
                val formattedDate = currentDate.format(dateFormatter)
                val formattedTime = currentTime.format(timeFormatter)
                time_report = "$formattedDate $formattedTime"
                send_coor()


            }
            no_btn.setOnClickListener {
                mydialog.dismiss()

            }


        }

    }

    private fun send_coor() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
            return
        }

        val task = fusedLocationProviderClient!!.lastLocation

        task.addOnSuccessListener { lt ->
            if (lt != null) {
                val long_coor = lt.longitude
                val lat_coor = lt.latitude
                coor = "$lat_coor $long_coor"
            }

            status = "Pending";
            val randomString = generateRandomString(26)
            val database = FirebaseDatabase.getInstance().getReference("Report")
            val report = Report(name_user,address_user,coor,contact_num,time_report,status)
            database.child(randomString).setValue(report).addOnSuccessListener {
                val dialogbox_Binding = layoutInflater.inflate(R.layout.dialogbox_finish, null)
                val mydialog1 = Dialog(this)
                mydialog1.setContentView(dialogbox_Binding)
                mydialog1.setCancelable(true)
                mydialog1.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))

                mydialog1.show()
                val ok_btn = dialogbox_Binding.findViewById<Button>(R.id.dialog_button_ok)
                ok_btn.setOnClickListener {
                    mydialog1.dismiss()
                }
            }


        }


    }


    private fun generateRandomString(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")

    }
}














