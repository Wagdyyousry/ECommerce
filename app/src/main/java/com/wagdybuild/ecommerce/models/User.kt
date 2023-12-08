package  com.wagdybuild.ecommerce.models

data class User(
    var name: String = "",
    var phone_number: String = "",
    var password: String = "",
    var id: String = "",
    var profileImageUri: String? = null,
    var user_type: String = "user",
)