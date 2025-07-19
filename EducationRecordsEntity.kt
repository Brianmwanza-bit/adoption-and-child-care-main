import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "education_records")
data class EducationRecordsEntity(
    @PrimaryKey(autoGenerate = true)
    val record_id: Int = 0,
    val child_id: Int?,
    val school: String?,
    val grade: String?,
    val year: String?
) 
 