import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "guardians")
data class GuardiansEntity(
    @PrimaryKey(autoGenerate = true)
    val guardian_id: Int = 0,
    val name: String,
    val phone: String?,
    val address: String?
) 