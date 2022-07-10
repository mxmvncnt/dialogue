package io.github.zohrevand.dialogue.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun ConversationsRoute(
    modifier: Modifier = Modifier,
    navigateToChat: (String) -> Unit,
) {
    ConversationsScreen(
        navigateToChat = navigateToChat,
        modifier = modifier
    )
}

@Composable
fun ConversationsScreen(
    modifier: Modifier = Modifier,
    navigateToChat: (String) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Blue)
    ) {
        Text(text = "This is Conversations Screen")
        Button(onClick = { navigateToChat("{chat_id}")}) {
            Text(text = "Navigate to chat with id {chat_id}")
        }
    }
}