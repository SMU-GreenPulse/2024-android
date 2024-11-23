data class ChatRequest(
    val messages: List<Message>,
    val model: String = "gpt-3.5-turbo",
    val temperature: Double = 0.7
)

data class ChatResponse(
    val id: String,
    val choices: List<Choice>,
    val created: Long,
    val model: String,
    val usage: Usage
)

data class Choice(
    val index: Int,
    val message: Message,
    val finishReason: String
)

data class Message(
    val role: String,
    val content: String
)

data class Usage(
    val promptTokens: Int,
    val completionTokens: Int,
    val totalTokens: Int
)