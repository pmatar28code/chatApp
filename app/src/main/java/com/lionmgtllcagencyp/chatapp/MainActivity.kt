package com.lionmgtllcagencyp.chatapp

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.lionmgtllcagencyp.chatapp.databinding.ActivityMainBinding
import com.lionmgtllcagencyp.chatapp.fragments.ChatsFragment
import com.lionmgtllcagencyp.chatapp.fragments.SearchFragment
import com.lionmgtllcagencyp.chatapp.fragments.SettingsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarMain)

        val toolBar = binding.toolbarMain
        setSupportActionBar(toolBar)
        supportActionBar!!.title = ""

        val tableLayout = binding.tabLayout
        val viewPager = binding.viewPager

        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPagerAdapter.addFragment(ChatsFragment(),"Chats")
        viewPagerAdapter.addFragment(SearchFragment(),"Search")
        viewPagerAdapter.addFragment(SettingsFragment(),"Settings")

        viewPager.adapter = viewPagerAdapter
        tableLayout.setupWithViewPager(viewPager)





        //val navController = findNavController(R.id.nav_host_fragment_content_main)
        //appBarConfiguration = AppBarConfiguration(navController.graph)
        //setupActionBarWithNavController(navController, appBarConfiguration)


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    //override fun onSupportNavigateUp(): Boolean {
      //  val navController = findNavController(R.id.nav_host_fragment_content_main)
        //return navController.navigateUp(appBarConfiguration)
          //      || super.onSupportNavigateUp()
    //}

    internal class ViewPagerAdapter(fragmentManager: FragmentManager):
        FragmentPagerAdapter(fragmentManager){

        private val fragments: ArrayList<Fragment> = ArrayList<Fragment>()
        private val titles: ArrayList<String> = ArrayList<String>()

        override fun getCount(): Int {
            return fragments.size
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        fun addFragment(fragment:Fragment,title:String){
            fragments.add(fragment)
            titles.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titles[position]
        }

    }
}