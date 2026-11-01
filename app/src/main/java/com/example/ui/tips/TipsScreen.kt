package com.example.ui.tips

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.repository.OgRepository
import com.example.ui.comments.CommentsBottomSheet
import kotlinx.coroutines.launch

@Composable
fun TipsScreen(repository: OgRepository) {
    val tips by repository.observeTips().collectAsStateWithLifecycle(initialValue = emptyList())
    val tutorials by repository.observeEditingTutorials().collectAsStateWithLifecycle(initialValue = emptyList())
    val spotlights by repository.observeSpotlights().collectAsStateWithLifecycle(initialValue = emptyList())
    val bookmarks by repository.observeBookmarks().collectAsStateWithLifecycle(initialValue = emptyList())
    val progressList by repository.observeProgress().collectAsStateWithLifecycle(initialValue = emptyList())

    var selectedMainTab by remember { mutableStateOf(0) } // 0: Tips, 1: Editing Tutorials, 2: Pro Spotlight
    var selectedTipCategory by remember { mutableStateOf("All") }
    var selectedSoftwareCategory by remember { mutableStateOf("All") }
    var selectedSpotlightRole by remember { mutableStateOf("All") }

    var activeCommentTargetId by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val tipCategories = listOf("All", "Headshot", "Sensitivity", "HUD", "Movement")
    val softwareCategories = listOf("All", "CapCut", "Premiere", "After Effects", "Alight Motion")
    val spotlightRoles = listOf("All", "Assaulter", "IGL", "Scout")

    val bookmarkedIds = remember(bookmarks) { bookmarks.map { it.itemId }.toSet() }
    val completedTutorialIds = remember(progressList) {
        progressList.filter { it.isCompleted }.map { it.tutorialId }.toSet()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ESPORTS ACADEMY",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                color = MaterialTheme.colorScheme.onBackground
            )
            Icon(
                imageVector = when (selectedMainTab) {
                    0 -> Icons.Default.Psychology
                    1 -> Icons.Default.VideoLibrary
                    else -> Icons.Default.Star
                },
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        TabRow(selectedTabIndex = selectedMainTab) {
            Tab(
                selected = selectedMainTab == 0,
                onClick = { selectedMainTab = 0 },
                text = { Text("Gaming Tips") }
            )
            Tab(
                selected = selectedMainTab == 1,
                onClick = { selectedMainTab = 1 },
                text = { Text("Editing") }
            )
            Tab(
                selected = selectedMainTab == 2,
                onClick = { selectedMainTab = 2 },
                text = { Text("Pro Spotlight") }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (selectedMainTab == 0) {
            // Category Filter Chips for Tips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tipCategories.forEach { cat ->
                    FilterChip(
                        selected = selectedTipCategory == cat,
                        onClick = { selectedTipCategory = cat },
                        label = { Text(cat) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val filteredTips = if (selectedTipCategory == "All") tips else tips.filter { it.category == selectedTipCategory }

            if (filteredTips.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No gaming tips available in this category yet.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredTips) { tip ->
                        val isBookmarked = bookmarkedIds.contains(tip.id)
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Badge(
                                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                        ) {
                                            Text(tip.category, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                        Text(
                                            text = tip.difficulty,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(onClick = { activeCommentTargetId = tip.id }) {
                                            Icon(
                                                imageVector = Icons.Default.Comment,
                                                contentDescription = "Comments",
                                                tint = MaterialTheme.colorScheme.secondary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        IconButton(onClick = {
                                            scope.launch {
                                                try {
                                                    repository.toggleBookmark(tip.id, "Tip", tip.title)
                                                } catch (_: Exception) {}
                                            }
                                        }) {
                                            Icon(
                                                imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                                contentDescription = "Bookmark",
                                                tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = tip.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = tip.content,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        } else if (selectedMainTab == 1) {
            // Editing Tutorials Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                softwareCategories.take(3).forEach { cat ->
                    FilterChip(
                        selected = selectedSoftwareCategory == cat,
                        onClick = { selectedSoftwareCategory = cat },
                        label = { Text(cat) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val filteredTutorials = if (selectedSoftwareCategory == "All") tutorials else tutorials.filter { it.software.equals(selectedSoftwareCategory, ignoreCase = true) }

            if (filteredTutorials.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No montage or editing tutorials uploaded yet.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredTutorials) { tutorial ->
                        val isBookmarked = bookmarkedIds.contains(tutorial.id)
                        val isCompleted = completedTutorialIds.contains(tutorial.id)
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Badge(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                        ) {
                                            Text(tutorial.software, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                        Text(
                                            text = tutorial.category,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(onClick = { activeCommentTargetId = tutorial.id }) {
                                            Icon(
                                                imageVector = Icons.Default.Comment,
                                                contentDescription = "Comments",
                                                tint = MaterialTheme.colorScheme.secondary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        IconButton(onClick = {
                                            scope.launch {
                                                try {
                                                    repository.toggleBookmark(tutorial.id, "Editing", tutorial.title)
                                                } catch (_: Exception) {}
                                            }
                                        }) {
                                            Icon(
                                                imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                                contentDescription = "Bookmark",
                                                tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = tutorial.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = tutorial.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                VideoPlayerComponent(videoUrl = tutorial.videoUrl)
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            scope.launch {
                                                try {
                                                    val newStatus = !isCompleted
                                                    val progress = if (newStatus) 100 else 50
                                                    repository.updateProgress(tutorial.id, tutorial.title, progress, newStatus)
                                                } catch (_: Exception) {}
                                            }
                                        },
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = if (isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(if (isCompleted) "Completed" else "Mark Progress")
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                                        Text("4.9 (Pro Guide)", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Pro Player Spotlight Section (selectedMainTab == 2)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                spotlightRoles.forEach { role ->
                    FilterChip(
                        selected = selectedSpotlightRole == role,
                        onClick = { selectedSpotlightRole = role },
                        label = { Text(role) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val filteredSpotlights = if (selectedSpotlightRole == "All") {
                spotlights
            } else {
                spotlights.filter { it.role.contains(selectedSpotlightRole, ignoreCase = true) }
            }

            if (filteredSpotlights.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No pro player spotlights found for this role.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredSpotlights) { spotlight ->
                        val isBookmarked = bookmarkedIds.contains(spotlight.id)
                        SpotlightCard(
                            spotlight = spotlight,
                            isBookmarked = isBookmarked,
                            onToggleBookmark = {
                                scope.launch {
                                    try {
                                        repository.toggleBookmark(spotlight.id, "Spotlight", spotlight.playerName)
                                    } catch (_: Exception) {}
                                }
                            },
                            onOpenComments = {
                                activeCommentTargetId = spotlight.id
                            }
                        )
                    }
                }
            }
        }
    }

    if (activeCommentTargetId != null) {
        CommentsBottomSheet(
            targetId = activeCommentTargetId!!,
            repository = repository,
            onDismiss = { activeCommentTargetId = null }
        )
    }
}
