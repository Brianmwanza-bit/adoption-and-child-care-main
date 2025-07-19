import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "money_records")
data class MoneyRecordsEntity(
    @PrimaryKey(autoGenerate = true)
    val money_id: Int = 0,
    val child_id: Int?,
    val amount: Double?,
    val date: String?,
    val description: String?
) 