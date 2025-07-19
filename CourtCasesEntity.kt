import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "court_cases")
data class CourtCasesEntity(
    @PrimaryKey(autoGenerate = true)
    val case_id: Int = 0,
    val child_id: Int?,
    val case_number: String?,
    val status: String?
) 