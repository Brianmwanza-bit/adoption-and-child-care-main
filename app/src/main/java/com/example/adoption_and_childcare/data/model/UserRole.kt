package com.example.adoption_and_childcare.data.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.adoption_and_childcare.R

/**
 * Enum representing the different user roles in the system.
 *
 * Using an enum ensures role names are stable (e.g. "Admin") even
 * when the UI display name is localized.
 *
 * @property roleName The stable string identifier for the role used in DB and API.
 * @property labelResId The string resource ID for the localized display name.
 */
enum class UserRole(val roleName: String, val labelResId: Int) {
    /** Administrator role with full system access. */
    ADMIN("Admin", R.string.role_admin),
    /** Caseworker role responsible for specific children/families. */
    CASE_WORKER("Caseworker", R.string.role_case_worker),
    /** Guardian or foster parent role with access to assigned child records. */
    GUARDIAN("Guardian", R.string.role_guardian),
    /** Social worker role for assessments and field work. */
    SOCIAL_WORKER("Social Worker", R.string.role_social_worker),
    /** Supervisor role for monitoring and approvals. */
    SUPERVISOR("Supervisor", R.string.role_supervisor);

    /**
     * Gets the localized label for the role.
     */
    @Composable
    fun getLabel(): String = stringResource(labelResId)

    companion object {
        /**
         * Finds a UserRole by its stable role name.
         * Returns [ADMIN] by default if not found.
         * 
         * @param name The stable role name to search for.
         */
        fun fromRoleName(name: String?): UserRole {
            return entries.find { it.roleName.equals(name, ignoreCase = true) } ?: ADMIN
        }

        /**
         * Finds a UserRole by its localized label.
         * This is useful when handling selections from localized UI components.
         * 
         * @param label The localized label text.
         */
        @Composable
        fun fromLabel(label: String): UserRole {
            return entries.find { it.getLabel() == label } ?: ADMIN
        }
    }
}
