import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "foster_matches")
data class FosterMatchesEntity(
    @PrimaryKey(autoGenerate = true) val match_id: Int = 0,
    val family_id: Int,
    val case_worker_id: Int?,
    val task_id: Int?,
    val status: String?,
    val created_at: String?
) 