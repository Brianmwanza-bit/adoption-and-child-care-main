import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audit_logs")
data class AuditLogsEntity(
    @PrimaryKey(autoGenerate = true)
    val log_id: Int = 0,
    val table_name: String,
    val record_id: Int,
    val action: String,
    val changed_by: Int?,
    val changed_at: String?,
    val ip_address: String?,
    val user_agent: String?,
    val old_data: String?, // JSON as String
    val new_data: String?  // JSON as String
) 