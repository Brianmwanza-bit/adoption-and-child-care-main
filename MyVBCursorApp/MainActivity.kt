import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController, startDestination = "admin_dashboard") {
                composable("admin_dashboard") { AdminDashboardScreen() }
            }
        }
    }
}

@Composable
fun AdminDashboardScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var roleStats by remember { mutableStateOf(listOf<Pair<String, Int>>()) }
    var recentActivity by remember { mutableStateOf(listOf<String>()) }
    var pendingChecks by remember { mutableStateOf(0) }
    var unreadNotifications by remember { mutableStateOf(0) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val token = TokenManager.getToken(context)
        if (token == null) {
            error = "Not authenticated."
            return@LaunchedEffect
        }
        try {
            val api = RetrofitClient.apiService
            val roles = withContext(Dispatchers.IO) { api.getRoleBreakdown("Bearer $token") }
            roleStats = roles.map { it.role to it.count }
            val activity = withContext(Dispatchers.IO) { api.getRecentActivity("Bearer $token") }
            recentActivity = activity.map { it.action + " on " + it.table_name }
            val pending = withContext(Dispatchers.IO) { api.getPendingBackgroundChecks("Bearer $token") }
            pendingChecks = pending.size
            val unread = withContext(Dispatchers.IO) { api.getUnreadNotificationCount("Bearer $token") }
            unreadNotifications = unread.unread
        } catch (e: HttpException) {
            error = "API error: ${e.code()} ${e.message()}"
        } catch (e: IOException) {
            error = "Network error: ${e.localizedMessage}" 
        } catch (e: Exception) {
            error = "Unknown error: ${e.localizedMessage}"
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Admin Dashboard", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        if (error != null) {
            Text("Error: $error", color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(16.dp))
        }
        Text("User Roles:")
        roleStats.forEach { (role, count) ->
            Text("$role: $count")
        }
        Spacer(Modifier.height(16.dp))
        Text("Recent Activity:")
        recentActivity.forEach { activity ->
            Text("- $activity")
        }
        Spacer(Modifier.height(16.dp))
        Text("Pending Background Checks: $pendingChecks")
        Spacer(Modifier.height(8.dp))
        Text("Unread Notifications: $unreadNotifications")
    }
} 