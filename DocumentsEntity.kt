import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "documents")
data class DocumentsEntity(
    @PrimaryKey(autoGenerate = true)
    val document_id: Int = 0,
    val child_id: Int,
    val document_type: String,
    val file_name: String,
    val file_type: String?,
    val file_size: Int?,
    val file_path: String,
    val description: String?,
    val uploaded_at: String?,
    val uploaded_by: Int?
) 