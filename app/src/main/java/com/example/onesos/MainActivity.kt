package com.example.onesos

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onesos.databinding.ActivityMainBinding
import java.io.FileNotFoundException
import java.util.Scanner

class MainActivity : ComponentActivity() {
    lateinit var binding: ActivityMainBinding
    val contacts:ArrayList<Contact> = ArrayList()
    lateinit var adapter: ContactAdapter

//    val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
//        if(it.resultCode== Activity.RESULT_OK) {
//            @Suppress("DEPRECATION")
//            val contact = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
//                it.contact?.getSerializableExtra("contact", Contact::class.java)
//            else
//                it.contact?.getSerializableExtra("contact") as Contact
//            Toast.makeText(this, contact?.name + " Added", Toast.LENGTH_SHORT).show()
//            initRecyclerView()
//        }
//
//    }

//    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            @Suppress("DEPRECATION")
            val intent = result.data
            val contact = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    intent?.getSerializableExtra("contact", Contact::class.java)
                else
                    intent?.getSerializableExtra("contact") as Contact
            Toast.makeText(this, contact?.name + " added", Toast.LENGTH_SHORT).show()
            refreshList()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initData()
        initRecyclerView()
        initButton()
    }

    fun refreshList() {
        contacts.clear()
        initData()
        initRecyclerView()
    }

    fun readFileScan(scan:Scanner) {
        while(scan.hasNextLine()){
            val name = scan.nextLine()
            val number = scan.nextLine()
            contacts.add(Contact(name, number))
        }
    }

    fun initData(){
        try {
            val scan2 = Scanner(openFileInput("contacts.txt"))
            readFileScan(scan2)
        } catch (e: FileNotFoundException) {

        }
        val scan = Scanner(resources.openRawResource(R.raw.contacts))
        readFileScan(scan)
    }

    fun initRecyclerView(){

        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
            false)
        adapter = ContactAdapter(contacts)
//        adapter.itemClickListener = object : MyDataAdapter.OnItemClickListener{
//            override fun OnItemClick(data: MyData) {
//                if(isTtsReady)
//                    tts.speak(data.word, TextToSpeech.QUEUE_ADD, null, null)
//                Toast.makeText(this@MainActivity, data.meaning, Toast.LENGTH_SHORT).show()
//            }
//        }
        binding.recyclerView.adapter = adapter
//        val simpleCallback = object: ItemTouchHelper.SimpleCallback(
//            ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT
//        ){
//            override fun onMove(
//                recyclerView: RecyclerView,
//                viewHolder: RecyclerView.ViewHolder,
//                target: RecyclerView.ViewHolder
//            ): Boolean {
//                adapter.moveItem(viewHolder.adapterPosition, target.adapterPosition)
//                return true
//            }
//
//            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                adapter.removeItem(viewHolder.adapterPosition)
//            }
//
//        }
//        val itemTouchHelper = ItemTouchHelper(simpleCallback)
//        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    fun initButton() {
        binding.addBtn.setOnClickListener {
            val intent = Intent(this, AddContactActivity::class.java)
            launcher.launch(intent)
        }
    }
}