package com.example.maze.data.repository

import com.example.maze.data.model.User
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document

/**
 * Handles communication with mongo.
 */
class AuthRepository{

    //Static initialization. We need this once across all.
    //Not very secure. Exposed URL
    companion object {
        private val client = MongoClients.create("mongodb+srv://jay:jayjayjay@users.xjxxl.mongodb.net/?retryWrites=true&w=majority&appName=users")
        private val database: MongoDatabase = client.getDatabase("appgame")
        private val collection: MongoCollection<Document> = database.getCollection("Users")
    }

    private fun documentToUser(document: Document): User {
        return User(
            id = document.getObjectId("_id").toString(),
            username = document.getString("username"),
            avatarColor = document.getInteger("avatarColor")
        )
    }

    private fun userToDocument(user: User): Document {
        return Document()
            .append("username",user.username)
            .append("avatarColor",user.avatarColor)
    }

    fun getUserByName(username: String): User? {
        val document = collection.find(Document("username",username)).firstOrNull()
        return document?.let { documentToUser(it) }
    }

    fun getUserById(id: String): User? {
        val document = collection.find(Document("_id",id)).firstOrNull()
        return document?.let { documentToUser(it) }
    }

    fun createUser(username: String, avatarColor: Int): User {
        val user = User(id = null, username = username, avatarColor = avatarColor)
        val document = userToDocument(user)
        collection.insertOne(document)
        val id = document.getObjectId("_id").toString()
        return user.copy(id = id)
    }

    fun updateUser(user: User): Boolean {
        val filter = Document("_id", org.bson.types.ObjectId(user.id))
        val update = Document("\$set", Document()
            .append("username",user.username)
            .append("avatarColor",user.avatarColor)
        )

        val res = collection.updateOne(filter,update)
        return res.modifiedCount > 0 //maybe ==1 ?
    }
}