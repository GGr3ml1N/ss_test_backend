package com.ggr3ml1n.domain.repositories

import com.ggr3ml1n.data.db.FakeDb
import com.ggr3ml1n.data.models.User

class AuthRepository(private val fakeDb: FakeDb) {
    val users: List<User> = fakeDb.users

    fun addUser(user: User): Unit {
        if (user.isNullOrEmpty()) {
            throw IllegalArgumentException("Login or/and password can't be null or empty")
        }
        if (isUserExist(user)) {
            throw IllegalArgumentException("User already exists")
        }
        fakeDb.users.add(user)
    }

    fun isUserExist(user: User): Boolean = fakeDb.users.contains(user)

    fun compareUser(user: User): Boolean = fakeDb.users.any { it == user }
}