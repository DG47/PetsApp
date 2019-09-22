package com.example.android.pets

import android.app.SearchManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.forEach
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.pets.room.Pet
import com.example.android.pets.room.PetDao
import com.example.android.pets.room.PetDatabase
import com.example.android.pets.room.PetListAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_catalog.*
import kotlin.concurrent.thread


class CatalogActivity : AppCompatActivity(), SearchView.OnQueryTextListener, ActionMode.Callback {

    private var petNumber = 1
    private lateinit var database: PetDatabase
    private lateinit var petListAdapter: PetListAdapter
    private var isEmpty = false
    private lateinit var petDao: PetDao
    private var petCount = -1

    private var actionMode: ActionMode? = null
    private var isMultiSelect = false
    private val selectedIds = ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog)
        setSupportActionBar(toolbar)

        database = PetDatabase.getInstance(this)
        petDao = database.petDao()

        val floatingActionButton = findViewById<FloatingActionButton>(R.id.fab)

        floatingActionButton.setOnClickListener {
            val intent = Intent(this@CatalogActivity, EditorActivity::class.java)
            startActivity(intent)
        }

        val petListView = list
        petListAdapter = PetListAdapter(this)
        petListView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        petListView.layoutManager = LinearLayoutManager(this)
        petListView.adapter = petListAdapter

        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val pet = petListAdapter.getPetAt(position)
                val name = pet.name
                thread { petDao.delete(pet) }
                Snackbar.make(findViewById(R.id.catalog_layout), "$name is deleted.", Snackbar.LENGTH_LONG)
                    .setAction("UNDO") {
                        thread { petDao.insert(pet) }
                    }.show()
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(petListView)

        val emptyView = findViewById<View>(R.id.empty_view)

//        emptyView.visibility = View.GONE

        petDao.getAll().observe(this, Observer<List<Pet>> {
            petListAdapter.submitList(it)
            petCount = it.count()
            invalidateOptionsMenu()

//            Log.e("CatalogActivityTEST", "Inside observer")


            if (it.count() == 0) {
//                Log.e("CatalogActivityTEST", "Inside if")
                isEmpty = true
                petListView.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
            } else if (isEmpty) {
//                Log.e("CatalogActivityTEST", "Inside else if")
                petListView.visibility = View.VISIBLE
                emptyView.visibility = View.GONE
                isEmpty = false
            }
        })

        petListView.addOnItemTouchListener(
            RecyclerItemClickListener(this, petListView,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        if (isMultiSelect) {
                            multiSelect(position, view)
                        } else {
                            val pet = petListAdapter.getPetAt(position)
                            val id = pet.id
                            intent = Intent(this@CatalogActivity, EditorActivity::class.java)
                            intent.putExtra("id", id)
                            val transitionName = getString(R.string.transition_string)
                            val viewStart = findViewById<LinearLayout>(R.id.list_item_linear_layout)
                            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this@CatalogActivity,
                                view,
                                transitionName)
                            ActivityCompat.startActivity(this@CatalogActivity,intent,options.toBundle())
                        }
                    }

                    override fun onItemLongClick(view: View?, position: Int) {
                        if (!isMultiSelect) {
                            selectedIds.clear()
                            isMultiSelect = true

                            if (actionMode == null) {
                                actionMode = startActionMode(this@CatalogActivity)
                            }
                        }
                        multiSelect(position, view)
                    }

                })
        )

//        @Override
//        public boolean onTouchEvent(MotionEvent event) {
//            InputMethodManager imm = (InputMethodManager)getSystemService(Context.
//                INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
//            return true;
//        }



    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (currentFocus != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_catalog, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu?.findItem(R.id.action_search_all_entries)?.actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = "Try dog,cat,fish,bird,other..."
        searchView.isSubmitButtonEnabled = false
        searchView.setOnQueryTextListener(this)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            // Respond to a click on the "Insert dummy data" menu option
            R.id.action_insert_dummy_data -> {
                val pet = Pet(
                    name = "Pet$petNumber",
                    type = (petNumber % 5),
                    breed = "Breed$petNumber",
                    gender = (petNumber % 3),
                    weight = petNumber
                )
                thread { petDao.insert(pet) }
                petNumber++
                return true
            }
            // Respond to a click on the "Delete all entries" menu option
            R.id.action_delete_all_entries -> {
                showDeleteAllConfirmationDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        super.onPrepareOptionsMenu(menu)

        if (petCount < 1)
            menu?.findItem(R.id.action_delete_all_entries)?.isVisible = false

        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        petListAdapter.notifyDataSetChanged()
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        petListAdapter.filter.filter(newText)
        return false
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_delete_selected) {
            val dialogClickListener = DialogInterface.OnClickListener { _, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        val size = selectedIds.size
                        selectedIds.forEach { thread { petDao.deletePet(it) } }
                        Snackbar.make(catalog_layout, "$size pets were removed.", Snackbar.LENGTH_LONG).show()
                        selectedIds.clear()
                        actionMode!!.finish()
                        Log.e("TEST", "${selectedIds.size}")
                    }

                    DialogInterface.BUTTON_NEGATIVE -> {
                        selectedIds.clear()
                        actionMode!!.finish()
                        Snackbar.make(
                            findViewById(R.id.catalog_layout),
                            "No pets were removed :)",
                            Snackbar.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            }

            val builder = AlertDialog.Builder(this)
            builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show()
            return true
        }
        return false
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        val inflater = mode!!.menuInflater
        inflater.inflate(R.menu.contextual_menu, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        actionMode = null
        isMultiSelect = false
        list.forEach {
            if (it.isSelected) {
                it.isSelected = false
//                it.setBackgroundColor(Color.parseColor("#FFFFFF"))
            }
        }
        selectedIds.clear()
        Log.e("TEST", "${selectedIds.size}")
    }

    private fun showDeleteAllConfirmationDialog() {
        val dialogClickListener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> deleteAllPets() //Delete all the pets and make snackbar showing number of deleted pets

                DialogInterface.BUTTON_NEGATIVE -> {
                    Snackbar.make(findViewById(R.id.catalog_layout), "No pets were removed :)", Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        }

        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
            .setNegativeButton("No", dialogClickListener).show()
    }

    private fun deleteAllPets() {
        val count = petListAdapter.itemCount
        thread { petDao.deleteAll() }
        Snackbar.make(catalog_layout, "$count pets were removed.", Snackbar.LENGTH_SHORT).show()
    }

    private fun multiSelect(position: Int, view: View?) {
        val pet = petListAdapter.getPetAt(position)
        view!!
        if (actionMode != null) {
            if (selectedIds.contains(pet.id)) {
                selectedIds.remove(Integer.valueOf(pet.id))
                view.isSelected = false
//                view.setBackgroundColor(Color.parseColor("#FFFFFF"))
            } else {
                selectedIds.add(pet.id)
                view.isSelected = true
//                view.setBackgroundColor(Color.parseColor("#E0E0E0"))
            }

            if (selectedIds.size > 0)
                actionMode!!.title = "${selectedIds.size} pets selected" //show selected item count on action mode.
            else {

                actionMode!!.title = "" //remove item count from action mode.
                actionMode!!.finish() //hide action mode.
            }
        }

    }
}
