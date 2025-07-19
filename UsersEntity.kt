import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UsersEntity(
    @PrimaryKey(autoGenerate = true)
    val user_id: Int = 0,
    val username: String,
    val password_hash: String,
    val phone: String?,
    val id_number: String?,
    val role: String,
    val email: String,
    val photo: String?
) 