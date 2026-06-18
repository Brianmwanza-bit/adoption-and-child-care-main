package com.example.adoption_and_childcare.data.db.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

/**
 * Data class representing a user with all their assigned permissions.
 * 
 * This class is used for Room relations to fetch a user and their permissions
 * in a single database operation.
 */
data class UserWithPermissions(
    @Embedded val user: UserEntity,
    @Relation(
        parentColumn = "user_id",
        entityColumn = "permission_id",
        associateBy = Junction(
            value = UserPermissionEntity::class,
            parentColumn = "user_id",
            entityColumn = "permission_id"
        )
    )
    val permissions: List<PermissionEntity>
)
