package com.example.foodiefling.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foddieflingmessaging.MessageAdapter
import com.example.foodiefling.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mDbRef: DatabaseReference
    private lateinit var sendingUser: User

    //private rooms
    var receiverRoom: String? = null
    var senderRoom: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val intent = intent
        val name = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("uid") // check this as well
        val senderUid = intent.getStringExtra("senderUid") // replace with my id
        mDbRef = FirebaseDatabase.getInstance().getReference()

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        supportActionBar?.title = name

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageBox = findViewById(R.id.messageBox)
        sendButton = findViewById(R.id.sentButton)
        messageList = ArrayList()
        messageAdapter = senderUid?.let { MessageAdapter(this,messageList, it) }!!
        chatRecyclerView.adapter = messageAdapter
        chatRecyclerView.layoutManager = LinearLayoutManager(this)

        //adds the message to the recycler view
        mDbRef.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()//clears the list before adding new messages
                    for (postSnapshot in snapshot.children){
                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

        val db = FirebaseDatabase.getInstance().getReference("Users")
        db.child(senderUid).get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                // Extract user data
                sendingUser = dataSnapshot.getValue(User::class.java)!!
                // Check if user is not null
                sendingUser.let {
                    Log.d("User Retrieval", "User Data: $it")
                    Log.d("User Retrieval", "User Data: $sendingUser")
                }
            } else {
                // Handle the case where the user does not exist
                Log.e("User Retrieval", "User  not found")
            }
        }.addOnFailureListener { error ->
            // Handle any errors that occur during the retrieval
            Log.e("User Retrieval", "Error retrieving user data: ${error.message}")
        }

        val receiver_name = findViewById<TextView>(R.id.receiver_name)
        receiver_name.text = name

        //adding the message to database
        sendButton.setOnClickListener{
            val message = messageBox.text.toString()
            //val messageObject = Message(message, sendButton.toString())
            val messageObject = Message(message, senderUid)

            //updates sender room and then receiver room
            mDbRef.child("chats").child(senderRoom!!).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    mDbRef.child("chats").child(receiverRoom!!).child("messages")
                        .push().setValue(messageObject)
                        // Scrolls to the bottom after sending a message
                        scrollToBottom()
                }
            // Clears the message box after sending
            messageBox.setText("")
        }
        val btnLeft = findViewById<ImageView>(R.id.btn_left)
        btnLeft.setOnClickListener {
            val intent1 = Intent(this, RestaurantListing::class.java)
            intent1.putExtra("current_user", senderUid)
            intent1.putExtra("other_user", receiverUid)
            startActivity(intent1)
        }
    }

    // Function to scroll to the bottom of the RecyclerView
    private fun scrollToBottom() {
        chatRecyclerView.scrollToPosition(messageList.size - 1)
    }
}