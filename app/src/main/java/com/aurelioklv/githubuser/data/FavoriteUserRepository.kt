package com.aurelioklv.githubuser.data

import androidx.lifecycle.LiveData
import com.aurelioklv.githubuser.data.local.FavoriteUser
import com.aurelioklv.githubuser.data.local.FavoriteUserDao

class FavoriteUserRepository(private val dao: FavoriteUserDao) {
    fun getFavoriteUser(): LiveData<List<FavoriteUser>> {
        return dao.getFavoriteUser()
    }

    suspend fun insertFavoriteUser(favoriteUser: FavoriteUser) {
        dao.insertFavUser(favoriteUser)
    }

    suspend fun deleteFavoriteUser(favoriteUser: FavoriteUser) {
        dao.deleteFavUser(favoriteUser)
    }

    suspend fun isFavorite(username: String): Boolean {
        return dao.isFavorite(username)
    }

    companion object {
        @Volatile
        private var instance: FavoriteUserRepository? = null

        fun getInstance(
            dao: FavoriteUserDao,
        ): FavoriteUserRepository =
            instance ?: synchronized(this) {
                instance ?: FavoriteUserRepository(dao)

            }.also { instance = it }
    }
}