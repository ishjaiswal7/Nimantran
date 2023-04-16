package com.example.nimantran

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.nimantran.databinding.ActivityCheckoutBinding
import com.example.nimantran.ui.main.clientGifts.MyGiftsViewModel.Companion.COLL_ORDERS
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult

class CheckoutActivity : AppCompatActivity() {

    private lateinit var paymentSheet: PaymentSheet
    private lateinit var customerConfig: PaymentSheet.CustomerConfiguration
    private lateinit var paymentIntentClientSecret: String
    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var orderId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = Firebase.firestore

        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)
        paymentIntentClientSecret = getString(R.string.pk)
        val amount = intent.getFloatExtra("amount", 0f)
        orderId = intent.getStringExtra("orderId") ?: ""
        binding.tvTotalAmount.text = amount.toString()

        binding.button.setOnClickListener {
            presentPaymentSheet()
        }
        binding.button.isEnabled = false
        validateFromServer(amount)
    }

    private fun validateFromServer(amount: Float) {
        val url = getString(R.string.api_url)
        val params = listOf(
            "amount" to amount * 100,
            "currency" to "inr",
            "phone" to auth.currentUser?.phoneNumber,
            "name" to "Dummy User",
        )
        url.httpPost(
            params
        ).responseJson { req, res, result ->
            Log.d("Request", req.toString())
            Log.d("Response", res.toString())
            Log.d("Result", result.toString())
            if (result is Result.Success) {
                val responseJson = result.get().obj()
                paymentIntentClientSecret = responseJson.getString("paymentIntent")
                val customerId = responseJson.getString("customer")
                val ephemeralKeySecret = responseJson.getString("ephemeralKey")
                customerConfig = PaymentSheet.CustomerConfiguration(customerId, ephemeralKeySecret)
                val publishableKey = responseJson.getString("publishableKey")
                PaymentConfiguration.init(this, publishableKey)
                presentPaymentSheet()
                binding.button.isEnabled = true
            } else {
                showAlert("Error validating from server, make sure server is running on the network and update the API URL in strings.xml")
            }
        }

    }

    private fun presentPaymentSheet() {

        paymentSheet.presentWithPaymentIntent(
            paymentIntentClientSecret, PaymentSheet.Configuration(
                merchantDisplayName = "My Business Merch",
                customer = customerConfig,
                allowsDelayedPaymentMethods = true
            )
        )
    }

    fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        when (paymentSheetResult) {
            is PaymentSheetResult.Canceled -> {
                showAlert("Payment cancelled")
            }
            is PaymentSheetResult.Failed -> {
                showAlert("Payment failed ${paymentSheetResult.error.message}")
            }
            is PaymentSheetResult.Completed -> {
                showAlert("Payment completed successfully")
                db.collection(COLL_ORDERS).document(orderId).update("orderStatus", "confirmed")
                    .addOnSuccessListener {
                        Log.d("CheckoutActivity", "Order status updated to placed")
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                    }.addOnFailureListener {
                        Log.d("CheckoutActivity", "Order status update failed")
                    }
            }
        }
    }

    fun showAlert(message: String) {
        AlertDialog.Builder(this).setTitle("Alert").setMessage(message)
            .setPositiveButton("OK", null).show()

    }

}