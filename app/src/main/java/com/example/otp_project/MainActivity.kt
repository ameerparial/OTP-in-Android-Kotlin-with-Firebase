package com.example.otp_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var myAuth:FirebaseAuth
    var verificationCode:String = ""
    private var mCallBacks = object: PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
        override fun onVerificationCompleted(phone: PhoneAuthCredential) {
            val code:String? = phone.smsCode
            if(code!=null){
                findViewById<EditText>(R.id.otpET).setText(code)
                validatePIN(code)
            }
        }


        override fun onVerificationFailed(p0: FirebaseException) {
            Toast.makeText(applicationContext, p0.message, Toast.LENGTH_LONG).show()
        }

        override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(p0, p1)
            verificationCode = p0
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        myAuth = FirebaseAuth.getInstance()
    }

    fun generateOTP(v: View){
        val phoneNo = findViewById<EditText>(R.id.phoneNoID).text.toString()
        if(TextUtils.isEmpty(phoneNo)){
            Toast.makeText(this, "Please enter your phone number.", Toast.LENGTH_SHORT).show()
            return

        }
        sendVerificationCode("+92$phoneNo")
    }

    private fun sendVerificationCode(phoneNo:String){
        val options = PhoneAuthOptions.newBuilder(myAuth)
            .setPhoneNumber(phoneNo)
            .setTimeout(60, TimeUnit.SECONDS)
            .setActivity(this).setCallbacks(mCallBacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
    fun verifyPIN(v:View){
        val userCode:String = findViewById<EditText>(R.id.otpET).text.toString()
        if(TextUtils.isEmpty(userCode)){
            Toast.makeText(this, "Please Enter PIN.", Toast.LENGTH_SHORT).show()
            return
        }
        validatePIN(userCode)
    }

    private fun validatePIN(code:String){
        val credential:PhoneAuthCredential = PhoneAuthProvider
            .getCredential(verificationCode, code)
        signInWithCredential(credential)
    }
    private fun signInWithCredential(credential: PhoneAuthCredential){
        myAuth.signInWithCredential(credential)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    Toast.makeText(applicationContext, "Right OTP", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(applicationContext, "Wrong OTP", Toast.LENGTH_SHORT).show()
                }

            }

    }





}