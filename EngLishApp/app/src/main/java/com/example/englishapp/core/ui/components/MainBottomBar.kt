package com.example.englishapp.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

data class NavItem(
    val icon: ImageVector,
    val label: String
)

@Composable
fun MainBottomBar(
    navItems: List<NavItem>,
    selectedIndex: Int,
    onItemClick: (Int) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 12.dp), // Bỏ padding horizontal để weight tự căn đều
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEachIndexed { index, item ->
                val isSelected = index == selectedIndex
                
                Column(
                    modifier = Modifier
                        .weight(1f) // QUAN TRỌNG: Giúp mỗi item chiếm không gian bằng nhau, không bị lệch
                        .clip(RoundedCornerShape(12.dp))
                        .then(
                            if (isSelected) Modifier.background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f))
                            else Modifier
                        )
                        .clickable { onItemClick(index) }
                        .padding(vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
