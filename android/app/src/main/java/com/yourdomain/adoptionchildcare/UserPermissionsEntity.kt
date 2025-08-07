import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "user_permissions", primaryKeys = ["user_id", "permission_id"])
data class UserPermissionsEntity(
    val user_id: Int,
    val permission_id: Int,
    val granted_at: String?,
    val granted_by: Int?
) 