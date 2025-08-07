import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "case_reports")
data class CaseReportsEntity(
    @PrimaryKey(autoGenerate = true)
    val report_id: Int = 0,
    val case_id: Int?,
    val report_text: String?,
    val date: String?
) 