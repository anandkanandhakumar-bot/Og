package com.example.ui.search

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.repository.OgRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    repository: OgRepository,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Tips", "Spotlights", "Tournaments", "Editing", "Announcements")

    val posts by repository.observePosts().collectAsStateWithLifecycle(initialValue = emptyList())
    val tips by repository.observeTips().collectAsStateWithLifecycle(initialValue = emptyList())
    val spotlights by repository.observeSpotlights().collectAsStateWithLifecycle(initialValue = emptyList())
    val tournaments by repository.observeTournaments().collectAsStateWithLifecycle(initialValue = emptyList())
    val editing by repository.observeEditingTutorials().collectAsStateWithLifecycle(initialValue = emptyList())

    val query = searchQuery.trim().lowercase()

    val filteredPosts = if (selectedFilter == "All" || selectedFilter == "Announcements") {
        posts.filter { it.title.lowercase().contains(query) || it.content.lowercase().contains(query) }
    } else emptyList()

    val filteredTips = if (selectedFilter == "All" || selectedFilter == "Tips") {
        tips.filter { it.title.lowercase().contains(query) || it.content.lowercase().contains(query) || it.category.lowercase().contains(query) }
    } else emptyList()

    val filteredSpotlights = if (selectedFilter == "All" || selectedFilter == "Spotlights") {
        spotlights.filter {
            it.playerName.lowercase().contains(query) ||
            it.realName.lowercase().contains(query) ||
            it.teamName.lowercase().contains(query) ||
            it.role.lowercase().contains(query) ||
            it.interviewTitle.lowercase().contains(query) ||
            it.keyAdvice.lowercase().contains(query)
        }
    } else emptyList()

    val filteredTournaments = if (selectedFilter == "All" || selectedFilter == "Tournaments") {
        tournaments.filter { it.name.lowercase().contains(query) || it.game.lowercase().contains(query) }
    } else emptyList()

    val filteredEditing = if (selectedFilter == "All" || selectedFilter == "Editing") {
        editing.filter { it.title.lowercase().contains(query) || it.software.lowercase().contains(query) || it.description.lowercase().contains(query) }
    } else emptyList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search tips, tournaments, news...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().padding(end = 12.dp),
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        shape = RoundedCornerShape(12.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(16.dp)
        ) {
            ScrollableTabRow(
                selectedTabIndex = filters.indexOf(selectedFilter),
                modifier = Modifier.fillMaxWidth()
            ) {
                filters.forEach { filter ->
                    Tab(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        text = { Text(filter) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val totalResults = filteredPosts.size + filteredTips.size + filteredSpotlights.size + filteredTournaments.size + filteredEditing.size

            if (query.isBlank()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Type keywords to search across OG Warriors arena.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else if (totalResults == 0) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No matching esports results found.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredSpotlights) { spot ->
                        SearchResultCard(
                            title = "${spot.playerName} (${spot.teamName}) - ${spot.interviewTitle}",
                            subtitle = "${spot.role}: \"${spot.keyAdvice}\"",
                            category = "Pro Spotlight"
                        )
                    }
                    items(filteredPosts) { post ->
                        SearchResultCard(title = post.title, subtitle = post.content, category = "Announcement")
                    }
                    items(filteredTips) { tip ->
                        SearchResultCard(title = tip.title, subtitle = tip.content, category = "Tip: ${tip.category}")
                    }
                    items(filteredTournaments) { tour ->
                        SearchResultCard(title = tour.name, subtitle = "Game: ${tour.game} | Prize: ${tour.prizePool}", category = "Tournament")
                    }
                    items(filteredEditing) { tut ->
                        SearchResultCard(title = tut.title, subtitle = tut.description, category = "Editing: ${tut.software}")
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultCard(title: String, subtitle: String, category: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Badge(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Text(category, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
