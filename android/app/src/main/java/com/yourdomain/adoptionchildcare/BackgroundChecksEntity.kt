import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "background_checks")
data class BackgroundChecksEntity(
    @PrimaryKey(autoGenerate = true) val check_id: Int = 0,
    val user_id: Int,
    val status: String?,
    val result: String?,
    val requested_at: String?,
    val completed_at: String?
) 