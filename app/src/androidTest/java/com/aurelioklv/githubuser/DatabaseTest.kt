package com.aurelioklv.githubuser

import android.content.Context
import androidx.lifecycle.asFlow
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aurelioklv.githubuser.data.local.FavoriteUser
import com.aurelioklv.githubuser.data.local.FavoriteUserDao
import com.aurelioklv.githubuser.data.local.FavoriteUserDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    private lateinit var db: FavoriteUserDatabase
    private lateinit var dao: FavoriteUserDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        db = Room.inMemoryDatabaseBuilder(context, FavoriteUserDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        dao = db.favUserDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun emptyDb() = runBlocking {
        val favoriteUsers = dao.getFavoriteUser().asFlow().first()

        assertEquals(0, favoriteUsers.size)
    }

    @Test
    fun insertFavUsers() = runBlocking {
        dao.insertFavUser(FavoriteUser("johndoe", "johndoe_url"))
        val favoriteUsers = dao.getFavoriteUser().asFlow().first()

        assertEquals(1, favoriteUsers.size)
        assertEquals("johndoe", favoriteUsers.first().username)
    }

    @Test
    fun insertMultipleFavUsers() = runBlocking {
        dao.insertFavUser(FavoriteUser("johndoe", "johndoe_url"))
        dao.insertFavUser(FavoriteUser("janedoe", "janedoe_url"))
        val favoriteUsers = dao.getFavoriteUser().asFlow().first()

        assertEquals(2, favoriteUsers.size)
    }

    @Test
    fun deleteFavoriteUsers() = runBlocking {
        dao.insertFavUser(FavoriteUser("johndoe", "johndoe_url"))
        dao.insertFavUser(FavoriteUser("janedoe", "janedoe_url"))

        dao.deleteFavUser(FavoriteUser("johndoe", "johndoe_url"))

        val favoriteUsers = dao.getFavoriteUser().asFlow().first()
        assertEquals(1, favoriteUsers.size)
        assertEquals("janedoe", favoriteUsers.first().username)
    }
}