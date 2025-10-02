package com.nishant.tapnshare.data

class UserProfileRepository(private val dao: UserProfileDao) {

    suspend fun getProfile(): UserProfile? = dao.getProfile()

    suspend fun saveProfile(profile: UserProfile) = dao.saveProfile(profile)

    suspend fun clearProfile() = dao.clearProfile()
}
