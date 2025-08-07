import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medical_records")
data class MedicalRecordsEntity(
    @PrimaryKey(autoGenerate = true)
    val record_id: Int = 0,
    val child_id: Int?,
    val description: String?,
    val date: String?
) 