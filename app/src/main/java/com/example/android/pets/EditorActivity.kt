package com.example.android.pets

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NavUtils
import com.example.android.pets.room.Pet
import com.example.android.pets.room.PetDao
import com.example.android.pets.room.PetDatabase
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_editor.*
import kotlin.concurrent.thread

class EditorActivity : AppCompatActivity() {

    private lateinit var mNameEditText: EditText
    private lateinit var mTypeRadioGroup: RadioGroup
    private lateinit var mTypeRadioButton: RadioButton
    private lateinit var mTypeOtherButton: RadioButton
    private var mType = 4
    private lateinit var mBreedEditText: EditText
    private lateinit var mBreedLinearLayout: LinearLayout
    private lateinit var mGenderSpinner: Spinner
    private var mGender = 0
    private lateinit var mWeightEditText: EditText
    private var mPetHasChanged = false
    private val mTouchListener = View.OnTouchListener { _, _ ->
        mPetHasChanged = true
        false
    }


    private lateinit var database: PetDatabase
    private var mCurrentPetId: Int = -1
    private lateinit var petDao: PetDao


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        database = PetDatabase.getInstance(this)
        petDao = database.petDao()

        val intent = intent
        mCurrentPetId = intent.getIntExtra("id", -1)

        mNameEditText = edit_pet_name
        mTypeRadioGroup = pet_type_group
        mBreedEditText = edit_pet_breed
        mBreedLinearLayout = breed_linear_layout
        mWeightEditText = edit_pet_weight
        mGenderSpinner = spinner_gender
        mTypeOtherButton = pet_type_other

        setupSpinner()

        if (mCurrentPetId == -1) {
            title = getString(R.string.editor_activity_title_new_pet)
            invalidateOptionsMenu()
        } else {
            thread {
                val pet = petDao.getPet(mCurrentPetId)

                runOnUiThread {
                    mNameEditText.setText(pet.name)
                    checkRadioButton(pet.type)
                    mBreedEditText.setText(pet.breed)
                    mGenderSpinner.setSelection(pet.gender)
                    mWeightEditText.setText(pet.weight.toString())
                    title = "Edit ${pet.name}"
                }
            }

        }

        mNameEditText.setOnTouchListener(mTouchListener)
        mBreedEditText.setOnTouchListener(mTouchListener)
        mWeightEditText.setOnTouchListener(mTouchListener)
        mGenderSpinner.setOnTouchListener(mTouchListener)

        mTypeRadioGroup.setOnCheckedChangeListener { _, _ ->
            setmType()
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (currentFocus != null)
            inputMethodManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_editor, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        super.onPrepareOptionsMenu(menu)
        if (mCurrentPetId == -1) {
            val menuItem = menu?.findItem(R.id.action_delete)
            menuItem?.isVisible = false
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_save -> {
                if (savePet())
                    finish()
                return true
            }
            R.id.action_delete -> {
                showDeleteConfirmationDialog()
                return true
            }

            android.R.id.home -> {
                // Navigate back to parent activity (CatalogActivity)
                if (!mPetHasChanged) {
                    NavUtils.navigateUpFromSameTask(this)
                    return true
                }

                val discardButtonClickListener = DialogInterface.OnClickListener { _, _ ->
                    NavUtils.navigateUpFromSameTask(this@EditorActivity)
                }

                showUnsavedChangesDialog(discardButtonClickListener)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showUnsavedChangesDialog(
        discardButtonClickListener: DialogInterface.OnClickListener
    ) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.unsaved_changes_dialog_msg)
        builder.setPositiveButton(R.string.discard, discardButtonClickListener)
        builder.setNegativeButton(R.string.keep_editing) { dialogInterface, _ ->
            dialogInterface?.dismiss()
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    override fun onBackPressed() {
        if (!mPetHasChanged) {
            super.onBackPressed()
            return
        }

        val discardButtonClickListener = DialogInterface.OnClickListener { _, _ -> finish() }

        showUnsavedChangesDialog(discardButtonClickListener)
    }

    private fun showDeleteConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.delete_dialog_msg)
        builder.setPositiveButton(R.string.delete) { _, _ ->
            thread { petDao.deletePet(mCurrentPetId) }
            Toast.makeText(this, "Pet Deleted.", Toast.LENGTH_SHORT).show()
            finish()
        }
        builder.setNegativeButton(R.string.cancel) { dialog, _ ->
            dialog?.dismiss()
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun setupSpinner() {
        val genderSpinnerAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.array_gender_options, android.R.layout.simple_spinner_item
        )

        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)

        mGenderSpinner.adapter = genderSpinnerAdapter

        mGenderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selection = parent?.getItemAtPosition(position) as String
                if (!selection.isEmpty()) {
                    mGender = when (selection) {
                        getString(R.string.gender_male) -> 1
                        getString(R.string.gender_female) -> 2
                        else -> 0
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                mGender = 0
            }
        }
    }

    private fun savePet(): Boolean {

        val nameString = mNameEditText.text.toString().trim()
        val breedString = mBreedEditText.text.toString().trim()
        val weightString = mWeightEditText.text.toString().trim()
        setmType()

        when {
            nameString.isEmpty() -> Snackbar.make(
                editor_layout,
                "Please give your pet a name!",
                Snackbar.LENGTH_SHORT
            ).show()
            weightString.isEmpty() -> Snackbar.make(
                editor_layout,
                "Please enter the weight of the pet!",
                Snackbar.LENGTH_SHORT
            ).show()
            weightString.toInt() == 0 -> Snackbar.make(
                editor_layout,
                "Feed your pet, and weight again!",
                Snackbar.LENGTH_SHORT
            ).show()
            else -> {
                val weight = weightString.toInt()

                if (mCurrentPetId == -1) {

                    val pet =
                        Pet(name = nameString, type = mType, breed = breedString, gender = mGender, weight = weight)

                    thread { petDao.insert(pet) }
                    Toast.makeText(
                        this,
                        "$nameString is added.",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    val pet = Pet(
                        id = mCurrentPetId,
                        name = nameString,
                        type = mType,
                        breed = breedString,
                        gender = mGender,
                        weight = weight
                    )

                    thread { petDao.update(pet) }
                    Toast.makeText(
                        this,
                        "$nameString is updated.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return true
            }
        }

        return false
    }

    private fun setmType() {
        val selectedTypeId = mTypeRadioGroup.checkedRadioButtonId
        mTypeRadioButton = findViewById(selectedTypeId)

        if (mTypeRadioButton === mTypeOtherButton) {
            mType = 4
            mBreedLinearLayout.visibility = View.GONE
        } else {
            mBreedLinearLayout.visibility = View.VISIBLE
            mBreedEditText.text = null
            mType = when {
                mTypeRadioButton === findViewById<View>(R.id.pet_type_dog) -> 0
                mTypeRadioButton === findViewById<View>(R.id.pet_type_cat) -> 1
                mTypeRadioButton === findViewById<View>(R.id.pet_type_fish) -> 2
                mTypeRadioButton === findViewById<View>(R.id.pet_type_bird) -> 3
                else -> 4
            }
        }
    }

    private fun checkRadioButton(type: Int) {

        when (type) {
            0 -> {
                mTypeRadioGroup.check(R.id.pet_type_dog)
                mBreedLinearLayout.visibility = View.VISIBLE
            }
            1 -> {
                mTypeRadioGroup.check(R.id.pet_type_cat)
                mBreedLinearLayout.visibility = View.VISIBLE
            }
            2 -> {
                mTypeRadioGroup.check(R.id.pet_type_fish)
                mBreedLinearLayout.visibility = View.VISIBLE
            }
            3 -> {
                mTypeRadioGroup.check(R.id.pet_type_bird)
                mBreedLinearLayout.visibility = View.VISIBLE
            }
            4 -> {
                mTypeRadioGroup.check(R.id.pet_type_other)
                mBreedEditText.text = null
            }
            else -> {
                mTypeRadioGroup.check(R.id.pet_type_other)
                mBreedEditText.text = null
            }
        }
    }
}
