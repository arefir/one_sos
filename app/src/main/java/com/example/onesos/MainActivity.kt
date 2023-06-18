package com.example.onesos

import android.app.Activity
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onesos.databinding.ActivityMainBinding
import java.io.FileNotFoundException
import java.io.Serializable
import java.util.Scanner
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class MainActivity : ComponentActivity() {
    private val RECORD_REQUEST_CODE = 101
    lateinit var binding: ActivityMainBinding
    var contacts:ArrayList<Contact> = ArrayList()
    lateinit var adapter: ContactAdapter
    var service:MyService = MyService()

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
//            @Suppress("DEPRECATION")
            val intent = result.data
            val contact = intent?.getSerializableExtra("contact", Contact::class.java)
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
        initPermissions()
        initService()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        println("OnNewIntent")
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
    
    fun initPermissions() {
         val TAG = "PermissionDemo"

        val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to record denied")
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.SEND_SMS),
                RECORD_REQUEST_CODE)

        }
    }
//
    fun initService() {
        val intent = Intent(this, MyService::class.java)
        val args = Bundle()
//        args.putSerializable("ARRAYLIST", contacts as Serializable?)
//        intent.putExtra("anjai", args)
        intent.putExtra("ARRAYLIST", contacts as Serializable?)
        startService(intent)
//        bindService(intent, connection, Context)
    }

//    val serviceConnection = ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName componentName) {
//        }
//    };
//
//    ArrayList<String> stringList = new ArrayList<>();
//    stringList.add("a");
//    stringList.add("b");
//
//    Intent i = new Intent(this, BLEDiscoveryService.class);
//    i.putStringArrayListExtra("list", stringList);
//
//    bindService(new Intent(this, MyService.class), serviceConnection, BIND_AUTO_CREATE);
//
//    suspend inline fun <reified S : Service, B : IBinder> Context.connectService(
//        crossinline onDisconnect: () -> Unit = {}
//    ): Pair<B, ServiceConnection> = suspendCoroutine {
//        val connection = object : ServiceConnection {
//            override fun onServiceDisconnected(name: ComponentName?) {
//                onDisconnect()
//            }
//
//            override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
//                it.resume(binder as B to this)
//            }
//        }
//
//        val intent = Intent(this, MyService::class.java)
//        intent.putExtra("ARRAYLIST", contacts as Serializable?)
//
//        applicationContext.bindService(
//            intent,
//            connection,
//            Context.BIND_AUTO_CREATE
//        )
//
//        startService(intent)
//    }
//
}