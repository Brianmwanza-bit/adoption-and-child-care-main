import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "children")
data class ChildrenEntity(
    @PrimaryKey(autoGenerate = true)
    val child_id: Int = 0,
    val name: String,
    val dob: String?,
    val gender: String?,
    val guardian_id: Int?,
    val photoBlob: ByteArray? = null
) 