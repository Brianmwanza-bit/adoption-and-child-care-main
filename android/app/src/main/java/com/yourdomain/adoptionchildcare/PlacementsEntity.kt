import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "placements")
data class PlacementsEntity(
    @PrimaryKey(autoGenerate = true)
    val placement_id: Int = 0,
    val child_id: Int?,
    val guardian_id: Int?,
    val start_date: String?,
    val end_date: String?
) 