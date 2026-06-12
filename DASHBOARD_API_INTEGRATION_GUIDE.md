# Dashboard to API Integration Guide

## ✅ What's Been Completed

### 1. Database Schema Mapping
- ✅ Created comprehensive mapping of all 42 MySQL tables to Room entities
- ✅ All Room entities already exist and match MySQL schema
- ✅ Documented in `DATABASE_SCHEMA_MAPPING.md`

### 2. Repository Layer
- ✅ Created `BaseRepository<T>` interface with API integration
- ✅ Updated `ChildRepository` to extend BaseRepository
- ✅ Implemented `ChildRepositoryImpl` with:
  - Local Room database operations
  - Remote API synchronization
  - Offline-first architecture
  - Error handling

### 3. Children Screen (Template)
- ✅ Updated `ChildrenListScreen.kt` with:
  - API integration via ChildRepositoryImpl
  - Loading states
  - Error handling
  - Pull-to-refresh capability
  - Real-time sync with backend

## 📋 Implementation Pattern for All Screens

### Step 1: Update Repository (if not done)

Create a repository implementation for each entity following the ChildRepository pattern:

```kotlin
@Singleton
class [Entity]RepositoryImpl @Inject constructor(
    private val [entity]Dao: [Entity]Dao,
    private val apiService: ApiService
) : [Entity]Repository {
    
    override fun observeAll(): Flow<List<[Entity]Entity>> {
        return [entity]Dao.observeAll()
    }
    
    override suspend fun fetchFromApi(token: String): Result<List<[Entity]Entity>> {
        return try {
            val authHeader = "Bearer $token"
            val response = apiService.get[Entity]s(authHeader)
            
            if (response.isSuccessful && response.body() != null) {
                val entities = response.body()!!
                
                // Update local database
                for (entity in entities) {
                    val existing = [entity]Dao.findById(entity.id)
                    if (existing != null) {
                        [entity]Dao.update(entity)
                    } else {
                        [entity]Dao.insert(entity)
                    }
                }
                
                Result.success(entities)
            } else {
                Result.failure(Exception("Failed to fetch: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ... implement insert, update, delete with API sync
}
```

### Step 2: Update Screen UI

Add these components to each screen:

```kotlin
@Composable
fun [Entity]ListScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val apiService = remember { RetrofitClient.getInstance(context).apiService }
    val repository = remember { [Entity]RepositoryImpl(db.[entity]Dao(), apiService) }
    
    // UI State
    var isLoading by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Load from local DB
    LaunchedEffect(Unit) {
        db.[entity]Dao().observeAll().collectLatest { list ->
            entities = list
        }
    }
    
    // Fetch from API
    LaunchedEffect(Unit) {
        fetchFromApi(repository, scope) { loading, error ->
            isLoading = loading
            errorMessage = error
        }
    }
    
    Scaffold(
        topBar = { /* ... */ },
        floatingActionButton = { /* ... */ }
    ) { padding ->
        // Show loading indicator
        if (isLoading && entities.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        // Show error message
        else if (errorMessage != null && entities.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Error, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Red)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Error: $errorMessage", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { /* Retry */ }) { Text("Retry") }
                }
            }
        }
        // Show empty state
        else if (entities.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.[Icon], contentDescription = null, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No [entities] yet", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
        // Show list
        else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(entities) { entity ->
                    // Entity card
                }
            }
        }
    }
}
```

### Step 3: Add Helper Function

```kotlin
private fun fetchFromApi(
    repository: [Entity]RepositoryImpl,
    scope: CoroutineScope,
    onLoading: (Boolean, String?) -> Unit
) {
    scope.launch {
        onLoading(true, null)
        try {
            val token = "" // TODO: Get actual auth token
            if (token.isNotEmpty()) {
                val result = repository.fetchFromApi(token)
                if (result.isFailure) {
                    onLoading(false, result.exceptionOrNull()?.message)
                } else {
                    onLoading(false, null)
                }
            } else {
                onLoading(false, "No authentication token available")
            }
        } catch (e: Exception) {
            onLoading(false, "Failed to fetch: ${e.message}")
        }
    }
}
```

## 🎯 Screens Priority List

### High Priority (Main Dashboard Items)
1. ✅ **ChildrenListScreen** - Template created
2. ⏳ **FamiliesScreen** - Use FamiliesScreen.kt
3. ⏳ **PlacementsScreen** - Use PlacementsScreen.kt
4. ⏳ **AdoptionApplicationsScreen** - Use AdoptionApplicationsScreen.kt
5. ⏳ **MedicalScreen** - Use MedicalScreen.kt
6. ⏳ **EducationScreen** - Use EducationScreen.kt
7. ⏳ **FinanceScreen** - Use FinanceScreen.kt
8. ⏳ **DocumentsScreen** - Use DocumentsScreen.kt

### Medium Priority (Secondary Features)
9. ⏳ **GuardiansScreen**
10. ⏳ **CourtCasesScreen**
11. ⏳ **HomeStudiesScreen**
12. ⏳ **FosterTasksScreen**
13. ⏳ **FosterMatchesScreen**
14. ⏳ **BackgroundChecksScreen**
15. ⏳ **UserManagementScreen**
16. ⏳ **AnalyticsScreen**

### Lower Priority (Enhancement Features)
17. ⏳ **TasksScreen**
18. ⏳ **ActionItemsScreen**
19. ⏳ **RiskAssessmentsScreen**
20. ⏳ **PermanencyPlansScreen**
21. ⏳ **CaseActivitiesScreen** (if exists)
22. ⏳ **CaseDeadlinesScreen**
23. ⏳ **CaseApprovalsScreen**
24. ⏳ **CaseUrgencyFlagsScreen**
25. ⏳ **CriticalDatesScreen**
26. ⏳ **WorkloadDashboardScreen**
27. ⏳ **WorkerMessagesScreen**
28. ⏳ **PlacementCompatibilityScreen**
29. ⏳ **DashboardPreferencesScreen**
30. ⏳ **NotificationsScreen**
31. ⏳ **AuditLogsScreen**
32. ⏳ **UserRolesScreen**
33. ⏳ **SearchScreen**
34. ⏳ **SettingsScreen**

## 🔑 Authentication Token Management

You need to implement token storage and retrieval. Create a helper:

```kotlin
object AuthManager {
    private const val PREF_NAME = "auth_prefs"
    private const val KEY_TOKEN = "auth_token"
    
    fun saveToken(context: Context, token: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }
    
    fun getToken(context: Context): String? {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_TOKEN, null)
    }
    
    fun clearToken(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_TOKEN).apply()
    }
}
```

Then use it in all screens:
```kotlin
val token = AuthManager.getToken(context) ?: ""
```

## 🎨 UI Enhancement Checklist

For each screen, add:

- [ ] Loading indicator (CircularProgressIndicator)
- [ ] Error state with retry button
- [ ] Empty state with icon and message
- [ ] Pull-to-refresh (SwipeRefresh)
- [ ] Sync status indicator
- [ ] Toast/Snackbar for success/error messages
- [ ] Confirmation dialogs for delete operations
- [ ] Form validation for create/edit dialogs

## 📊 Dashboard Metrics Update

Update `DashboardViewModel.kt` to fetch real-time counts from API:

```kotlin
fun refreshDashboardData(token: String) {
    viewModelScope.launch {
        try {
            val authHeader = "Bearer $token"
            
            // Fetch counts from API
            val childrenResult = apiService.getChildren(authHeader)
            val familiesResult = apiService.getFamilies(authHeader)
            val placementsResult = apiService.getPlacements(authHeader)
            val appsResult = apiService.getAdoptionApplications(authHeader)
            
            // Update counts
            if (childrenResult.isSuccessful) {
                _childCount.value = childrenResult.body()?.size ?: 0
            }
            // ... repeat for others
            
        } catch (e: Exception) {
            // Fallback to local data
            refreshData()
        }
    }
}
```

## 🧪 Testing Checklist

For each updated screen:

- [ ] Screen loads with local data immediately
- [ ] Screen fetches from API in background
- [ ] Loading spinner shows during API call
- [ ] Error message shows on failure
- [ ] Pull-to-refresh works
- [ ] Create operation syncs with API
- [ ] Update operation syncs with API
- [ ] Delete operation syncs with API
- [ ] Works offline with local data
- [ ] Re-syncs when connection restored

## 🚀 Quick Start Commands

To update a specific screen:

1. Find the screen file: `app/src/main/java/com/example/adoption_and_childcare/ui/compose/[ScreenName].kt`
2. Add repository initialization
3. Add loading/error states
4. Add API fetch in LaunchedEffect
5. Update UI to show states
6. Test with backend running

## 📝 Notes

- All 42 MySQL tables already have corresponding Room entities
- Backend API endpoints are already defined in `ApiService.kt`
- Sync mechanism exists via `SyncWorker` and `SyncQueueDao`
- The pattern shown in ChildrenListScreen should be replicated for all screens
- Consider creating a BaseScreen composable to reduce code duplication

## 🔧 Next Steps

1. Implement AuthManager for token storage
2. Update remaining 33 screens following the template
3. Add pull-to-refresh to all list screens
4. Update DashboardViewModel to fetch real-time metrics
5. Test all CRUD operations with backend
6. Add sync status indicator to dashboard header
7. Implement offline mode UI indicators
