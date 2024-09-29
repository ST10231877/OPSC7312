import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

// Request body for login
data class LoginRequest(
    val username: String,
    val password: String
)

// Response from login
data class LoginResponse(
    val message: String,
    val userId: String // Capture userId
)

// Registration request body
data class RegisterRequest(
    val username: String,
    val password: String, // This will be the hashed password
    val email: String
)

// Registration response body
data class RegisterResponse(
    val message: String
)

interface ApiService {
    @POST("/api/user/register")
    fun registerUser(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("/api/user/login")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>


}
