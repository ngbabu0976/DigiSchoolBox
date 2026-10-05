package com.ganesh.eduscribe.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ganesh.eduscribe.domain.model.DigitalResource

@Composable
fun DocumentReaderView(
    resource: DigitalResource,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentPage by remember { mutableStateOf(1) }
    var isBookmarked by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF263238))
    ) {
        // Reader Header Bar
        Surface(
            color = Color(0xFF37474F),
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onClose) {
                        Text("✖", color = Color.White, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = resource.title,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "${resource.type.label} • ${resource.className} • Chapter ${resource.chapterNumber}",
                            color = Color(0xFFB0BEC5),
                            fontSize = 12.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { isBookmarked = !isBookmarked }) {
                        Text(if (isBookmarked) "🔖 Bookmarked" else "🏷️ Bookmark", color = if (isBookmarked) Color(0xFFFFD54F) else Color.White, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF455A64))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        IconButton(
                            onClick = { if (currentPage > 1) currentPage-- },
                            enabled = currentPage > 1
                        ) {
                            Text("◀", color = Color.White, fontSize = 12.sp)
                        }

                        Text(
                            text = "Page $currentPage / ${resource.totalPages}",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )

                        IconButton(
                            onClick = { if (currentPage < resource.totalPages) currentPage++ },
                            enabled = currentPage < resource.totalPages
                        ) {
                            Text("▶", color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Reader Document View Content Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(24.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(32.dp)
            ) {
                Text(
                    text = resource.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A237E)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    color = Color(0xFFE8EAF6),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "DOCUMENT READER - PAGE $currentPage",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF303F9F)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "High-definition rendered digital textbook page content. Optimally rendered for 10-inch tablets with fast stroke and page reading capabilities.",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Chapter Summary & Key Learning Concepts:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("1. Fundamental properties and step-by-step mathematical proofs.", fontSize = 12.sp)
                        Text("2. Solved examples with step-wise guidance.", fontSize = 12.sp)
                        Text("3. Self-assessment exercises and interactive notebook practice.", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
