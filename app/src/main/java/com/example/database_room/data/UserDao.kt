package com.example.database_room.data

import androidx.room.*

@Dao
interface UserDao {

    // ➕ Create
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    // 📄 Read by ID
    @Query("SELECT * FROM user_table WHERE id = :id")
    suspend fun getUserById(id: Int): User?

    // 📄 Read all
    @Query("SELECT * FROM user_table ORDER BY id ASC")
    suspend fun getAllUsers(): List<User>

    // ✏️ Update (BEST PRACTICE)
    @Update
    suspend fun updateUser(user: User): Int   // rows updated

    // ✏️ Update using query (no object needed)
    @Query("UPDATE user_table SET name = :name, age = :age WHERE id = :id")
    suspend fun updateUserById(id: Int, name: String, age: Int): Int

    // ✏️ Update using name (no ID)
    @Query("UPDATE user_table SET name = :newName, age = :newAge WHERE name = :oldName")
    suspend fun updateByName(oldName: String, newName: String, newAge: Int): Int

    // ❌ Delete using object
    @Delete
    suspend fun deleteUser(user: User): Int   // rows deleted

    // ❌ Delete by ID
    @Query("DELETE FROM user_table WHERE id = :id")
    suspend fun deleteById(id: Int): Int

    // ❌ Delete by Name
    @Query("DELETE FROM user_table WHERE name = :name")
    suspend fun deleteByName(name: String): Int

    // ❌ Delete all users
    @Query("DELETE FROM user_table")
    suspend fun deleteAllUsers()

    // 🔄 Reset Primary Key
    @Query("DELETE FROM sqlite_sequence WHERE name = 'user_table'")
    suspend fun resetPrimaryKey()

    // 🔥 Combined operation (BEST PRACTICE)
    @Transaction
    suspend fun deleteAllAndReset() {
        deleteAllUsers()
        resetPrimaryKey()
    }
}