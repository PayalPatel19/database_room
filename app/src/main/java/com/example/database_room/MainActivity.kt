package com.example.database_room

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.database_room.data.AppDatabase
import com.example.database_room.data.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "user_database"
        ).build()

        val etId = findViewById<EditText>(R.id.etId)
        val etName = findViewById<EditText>(R.id.etName)
        val etAge = findViewById<EditText>(R.id.etAge)

        val btnAdd = findViewById<Button>(R.id.btnAdd)
        val btnUpdate = findViewById<Button>(R.id.btnUpdate)
        val btnShow = findViewById<Button>(R.id.btnShow)
        val btnDelete = findViewById<Button>(R.id.btnDelete)
        val btnDeleteAll = findViewById<Button>(R.id.btnDeleteAll)

        val tvResult = findViewById<TextView>(R.id.tvResult)

        // ➕ Add User
        btnAdd.setOnClickListener {
            val name = etName.text.toString().trim()
            val age = etAge.text.toString().toIntOrNull()

            if (name.isEmpty() || age == null) {
                Toast.makeText(this, "Enter valid Name & Age", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO) {
                db.userDao().insertUser(User(name = name, age = age))
                val users = db.userDao().getAllUsers()

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "User Added", Toast.LENGTH_SHORT).show()
                    tvResult.text = formatUsers(users)

                    etName.text.clear()
                    etAge.text.clear()
                }
            }
        }

        // ✏️ Update User (by ID)
        btnUpdate.setOnClickListener {
            val id = etId.text.toString().toIntOrNull()
            val name = etName.text.toString().trim()
            val age = etAge.text.toString().toIntOrNull()

            if (id == null || name.isEmpty() || age == null) {
                Toast.makeText(this, "Enter ID, Name & Age", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO) {
                val existingUser = db.userDao().getUserById(id)

                val rowsUpdated = if (existingUser != null) {
                    db.userDao().updateUser(User(id = id, name = name, age = age))
                } else {
                    0
                }

                val users = db.userDao().getAllUsers()

                withContext(Dispatchers.Main) {
                    if (rowsUpdated > 0) {
                        Toast.makeText(this@MainActivity, "User Updated", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@MainActivity, "User Not Found", Toast.LENGTH_SHORT).show()
                    }

                    tvResult.text = formatUsers(users)

                    etId.text.clear()
                    etName.text.clear()
                    etAge.text.clear()
                }
            }
        }

        // ❌ Delete by ID
        btnDelete.setOnClickListener {
            val id = etId.text.toString().toIntOrNull()

            if (id == null) {
                Toast.makeText(this, "Enter valid ID", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO) {
                val rowsDeleted = db.userDao().deleteById(id)
                val users = db.userDao().getAllUsers()

                withContext(Dispatchers.Main) {
                    if (rowsDeleted > 0) {
                        Toast.makeText(this@MainActivity, "User Deleted", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@MainActivity, "User Not Found", Toast.LENGTH_SHORT).show()
                    }

                    tvResult.text = formatUsers(users)
                    etId.text.clear()
                }
            }
        }

        // ❌ Delete All Users
        btnDeleteAll.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                db.userDao().deleteAllAndReset()

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "All Users Deleted", Toast.LENGTH_SHORT).show()
                    tvResult.text = "No Users Found"
                }
            }
        }

        // 📄 Show Users
        btnShow.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val users = db.userDao().getAllUsers()

                withContext(Dispatchers.Main) {
                    tvResult.text = formatUsers(users)
                }
            }
        }
    }

    // 📄 Format Output
    private fun formatUsers(users: List<User>): String {
        return if (users.isEmpty()) {
            "No Users Found"
        } else {
            users.joinToString("\n") {
                "ID: ${it.id}, Name: ${it.name}, Age: ${it.age}"
            }
        }
    }
}