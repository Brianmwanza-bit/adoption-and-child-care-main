import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "family_profile")
data class FamilyProfileEntity(
    @PrimaryKey(autoGenerate = true) val family_id: Int = 0,
    val user_id: Int,
    val address: String?,
    val household_size: Int?,
    val notes: String?
) 