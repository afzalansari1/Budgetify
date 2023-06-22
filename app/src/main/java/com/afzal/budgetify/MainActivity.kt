package com.afzal.budgetify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afzal.budgetify.database.Item
import com.google.firebase.auth.FirebaseAuth
import javax.security.auth.Subject

class MainActivity : AppCompatActivity(), IItemDataAdapter{
    private lateinit var mAuth: FirebaseAuth
    lateinit var viewModel : SubjectViewModel
    private val totalData = ArrayList<Item>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = ItemDataAdapter(this, this)
        recyclerView.adapter = adapter

        viewModel = ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application))[SubjectViewModel::class.java]

        viewModel.allItem.observe(this, Observer {list->
            list?.let{
                adapter.updateList(it)
                updateTotal(list)
            }
        })
        mAuth = FirebaseAuth.getInstance()
        val logout = findViewById<Button>(R.id.logoutBtn)
        logout.setOnClickListener{
            logOut()
        }

    }
    private fun logOut(){
        val auth = FirebaseAuth.getInstance()
        auth.signOut()
        auth.addAuthStateListener {
            if(auth.currentUser == null){
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onItemClicked(item: Item) {
        viewModel.deleteNode(item)
//        finalTransaction -= subject.price
//        val final = findViewById<TextView>(R.id.total)
//        final.text = finalTransaction.toString()
    }

    fun submitData(view: View) {
        addItemDialog()
    }

    private fun updateTotal(list : List<Item>) {
        var total = 0.00;
        for(i in list.indices) {
            total += list[i].price
        }
        val totalTextView = findViewById<TextView>(R.id.total)
        totalTextView.text = "₹"+total.toBigDecimal().toPlainString()
    }
    private fun addItemDialog() {
        val inflater = this.layoutInflater
        val view: View = inflater.inflate(R.layout.dialog_add,null)
        val item = view.findViewById<EditText>(R.id.item_name)
        val price = view.findViewById<EditText>(R.id.price)

        setErrorListener(item)
        setErrorListener(price)
        setPriceError(price)

        val builder = AlertDialog.Builder(this)
        with(builder) {
            setTitle("Add Item")
            setMessage("Enter the details of purchased item")
            setView(view)

            //sets positive button
            setPositiveButton("Add") { _, _ ->
                val itemName = item.text.toString()
                val itemPrice: Float? = price.text.toString().toFloatOrNull()

//                val itemP: Long = itemPrice.toLong()

                if(itemName.isNotEmpty() && (itemPrice != null) && (itemPrice <= 9999999999)) {
                    viewModel.insertNode(Item(itemName, itemPrice!!))
                    totalData.add(Item(itemName, itemPrice!!))
//                    finalTransaction += itemPrice
//                    val final = findViewById<TextView>(R.id.total)
//                    final.text = finalTransaction.toString()
                }
                else if (itemPrice != null) {
                    if(itemPrice >= 100000000000){
                        Toast.makeText(this@MainActivity, "Invalid Price", Toast.LENGTH_SHORT).show()
                    }
                }
                else
                    Toast.makeText(this@MainActivity,"Invalid Input", Toast.LENGTH_SHORT).show()
            }
            setNegativeButton("Cancel") { _, _ ->
            }
            show()
        }
    }
    private fun setErrorListener(editText: EditText) {
        editText.error = if(editText.text.toString().isNotEmpty()) null else "Field Cannot be Empty"
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                editText.error = if(editText.text.toString().isNotEmpty()) null else "Field Cannot be Empty"

            }
        })
    }
    private fun setPriceError(editText: EditText) {
        editText.error = if(editText.length() in 1..10) null else "Field Cannot be Empty"
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                editText.error = if(editText.length() in 1..10) null else "Invalid Price"

            }
        })
    }

    inner class MinMaxFilter() : InputFilter {
        private var intMin: Int = 0
        private var intMax: Long = 0

        constructor(minValue: Int, maxValue: Long) : this() {
            this.intMin = minValue
            this.intMax = maxValue
        }

        override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dStart: Int, dEnd: Int): CharSequence? {
            try {
                val input = Integer.parseInt(dest.toString() + source.toString())
                if (isInRange(intMin, intMax, input)) {
                    return null
                }
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }
            return ""
        }
        private fun isInRange(a: Int, b: Long, c: Int): Boolean {
            return if (b > a) c in a..b else c in b..a
        }
    }




}