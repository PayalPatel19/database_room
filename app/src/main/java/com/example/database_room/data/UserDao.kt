package com.example.database_room.data

import androidx.room.*
@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: User): Long

    @Query("SELECT * FROM user_table WHERE id = :id")
    suspend fun getUserById(id: Int): User?

    @Query("SELECT * FROM user_table ORDER BY id ASC")
    suspend fun getAllUsers(): List<User>


    @Update
    suspend fun updateUser(user: User): Int

    @Query("UPDATE user_table SET name = :name, age = :age WHERE  id = :id")
    suspend fun updateUserById(id: Int, name: String, age: Int): Int


    @Query("UPDATE user_table SET name = :newName, age = :newAge WHERE name = :oldName")
    suspend fun updateByName(oldName: String, newName: String, newAge: Int): Int

    @Delete
    suspend fun deleteUser(user: User): Int

    @Query("DELETE FROM user_table WHERE id = :id")
    suspend fun deleteById(id: Int): Int

    @Query("DELETE FROM user_table WHERE name  = :name")
    suspend fun deleteByName(name: String): Int

    @Query("DELETE FROM user_table")
    suspend fun deleteAllUsers()

    @Query("DELETE FROM sqlite_sequence WHERE name = 'user_table'")
    suspend fun resetPrimaryKey()

    @Transaction
    suspend fun deleteAllAndReset() {
        deleteAllUsers()
        resetPrimaryKey()
    }


//    @Query("SELECT * FROM user_table WHERE id = :id")
//    suspend fun getUserById(id: Int): User
}