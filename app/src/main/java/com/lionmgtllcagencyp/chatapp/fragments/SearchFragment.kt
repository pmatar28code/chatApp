package com.lionmgtllcagencyp.chatapp.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lionmgtllcagencyp.chatapp.R
import com.lionmgtllcagencyp.chatapp.adapters.UserAdapter
import com.lionmgtllcagencyp.chatapp.modelClasses.Users
import java.util.*
import kotlin.collections.ArrayList

class SearchFragment : Fragment() {
    private var userAdapter:UserAdapter ?= null
    private var mUsersList:List<Users> ?= null
    private var recyclerView:RecyclerView ?= null
    private var searchEditText:EditText ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       val view = inflater.inflate(R.layout.fragment_search, container, false)

        recyclerView = view.findViewById(R.id.search_list_recycler_view)
        recyclerView?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
        }
        searchEditText = view.findViewById(R.id.search_users_edit_text)

        mUsersList = ArrayList()
        retrieveAllUsers()

        searchEditText?.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchForUsers(s.toString().lowercase(Locale.getDefault()))
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        return view
    }

    private fun retrieveAllUsers() {
        val firebaseUserId = FirebaseAuth.getInstance().currentUser?.uid
        val refUsers = FirebaseDatabase.getInstance().reference.child("Users")

        refUsers.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                (mUsersList as ArrayList<Users>).clear()

                if(searchEditText?.text.toString() == ""){
                    for(item in p0.children){
                        val user = item.getValue(Users::class.java)
                        if(user?.getUID() != firebaseUserId) {
                            if (user != null) {
                                (mUsersList as ArrayList<Users>).add(user)
                            }
                        }
                    }
                    userAdapter =  UserAdapter(requireContext(),mUsersList!!,false)
                    recyclerView?.adapter = userAdapter
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }

        })


    }

    private fun searchForUsers(user:String){
        val firebaseUserId = FirebaseAuth.getInstance().currentUser?.uid
        val queryUsers = FirebaseDatabase.getInstance().reference
            .child("Users").orderByChild("search")
            .startAt(user)
            .endAt(user + "\uf8ff")

        queryUsers.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                (mUsersList as ArrayList<Users>).clear()
                for(item in p0.children){
                    val user: Users? = item.getValue(Users::class.java)
                    if(user?.getUID() != firebaseUserId) {
                        if (user != null) {
                            (mUsersList as ArrayList<Users>).add(user)
                        }
                    }
                }
                userAdapter = UserAdapter(requireContext(),mUsersList!!,false)
                recyclerView?.adapter = userAdapter
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })
    }

}