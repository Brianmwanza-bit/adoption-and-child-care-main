# AuthManager Integration Status

## ✅ **Completed**
- **AuthManager** created at `utils/AuthManager.kt`
- **ChildRepositoryImpl** fully integrated with AuthManager (serves as template)
- **FamilyRepositoryImpl** AuthManager added to constructor

## ⏳ **Remaining Repositories to Update**

All repositories follow the same pattern. Here's what needs to be changed in each:

### **Pattern to Apply:**

```kotlin
// 1. Add import
import com.example.adoption_and_childcare.utils.AuthManager

// 2. Add to constructor
class [RepositoryName] @Inject constructor(
    private val [dao]: [DaoType],
    private val apiService: ApiService,
    private val authManager: AuthManager  // ← ADD THIS
) {

// 3. Replace all instances of:
val authHeader = "Bearer $token"
// WITH:
val authHeader = authManager.getAuthHeader()

// 4. In fetchFromApi, add check:
val authHeader = authManager.getAuthHeader()
if (authHeader == null) {
    return Result.failure(Exception("Not authenticated. Please log in."))
}
```

### **Repositories to Update (13 remaining):**

1. ✅ ChildRepositoryImpl - **DONE**
2. ✅ FamilyRepositoryImpl - **Constructor updated**
3. ⏳ PlacementRepositoryImpl - Needs full update
4. ⏳ DocumentRepositoryImpl - Needs full update
5. ⏳ HomeStudyRepositoryImpl - Needs full update
6. ⏳ GuardianRepositoryImpl - Needs full update
7. ⏳ CourtCaseRepositoryImpl - Needs full update
8. ⏳ FosterTaskRepositoryImpl - Needs full update
9. ⏳ FosterMatchRepositoryImpl - Needs full update
10. ⏳ BackgroundCheckRepositoryImpl - Needs full update
11. ⏳ UserRepositoryImpl - Needs full update
12. ⏳ MedicalRecordRepositoryImpl - **File doesn't exist yet**
13. ⏳ EducationRecordRepositoryImpl - **File doesn't exist yet**
14. ⏳ MoneyRecordRepositoryImpl - **File doesn't exist yet**
15. ⏳ AdoptionApplicationRepositoryImpl - Uses different pattern (BaseSyncRepository)

**Note:** Medical, Education, and Money repositories were rejected by user earlier and need to be recreated.

---

## 🚀 **Quick Update Script**

To update any repository, apply these 3 changes:

1. **Import AuthManager**
2. **Add to constructor**
3. **Replace "Bearer $token" with authManager.getAuthHeader()**

Each repository takes ~2 minutes to update manually.
