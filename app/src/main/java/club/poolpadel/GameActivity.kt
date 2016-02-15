package club.poolpadel

data class GameActivity(
        var dateTime: Int = timestamp(),
        var action: String = "",
        var description: String = ""
)
