import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "permissions")
data class PermissionsEntity(
    @PrimaryKey(autoGenerate = true)
    val permission_id: Int = 0,
    val name: String,
    val description: String?,
    val category: String?
) 