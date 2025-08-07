import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "foster_tasks")
data class FosterTasksEntity(
    @PrimaryKey(autoGenerate = true) val task_id: Int = 0,
    val family_id: Int,
    val case_worker_id: Int?,
    val description: String?,
    val status: String?,
    val created_at: String?,
    val due_date: String?
) 