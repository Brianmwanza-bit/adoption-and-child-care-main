import retrofit2.http.*
import retrofit2.Response

data class UnreadCountResponse(val unread: Int)

@GET("analytics/summary")
suspend fun getAnalyticsSummary(@Header("Authorization") token: String): Response<AnalyticsSummaryResponse>

@GET("analytics/placements-over-time")
suspend fun getPlacementsOverTime(@Header("Authorization") token: String): Response<List<PlacementOverTimeResponse>>

@GET("analytics/children-by-status")
suspend fun getChildrenByStatus(@Header("Authorization") token: String): Response<Map<String, Int>>

data class AnalyticsSummaryResponse(val users: Int, val children: Int, val placements: Int)
data class PlacementOverTimeResponse(val month: String, val count: Int)

data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val success: Boolean, val token: String?, val user: User?)

data class RegisterRequest(val username: String, val password: String, val email: String, val role: String)
data class RegisterResponse(val success: Boolean, val user: User?)

@POST("login")
suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

@POST("register")
suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse> 