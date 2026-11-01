package com.example.ui.tournaments

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Tournament
import com.example.data.repository.OgRepository
import com.example.ui.comments.CommentsBottomSheet
import com.example.ui.faq.FaqScreen
import kotlinx.coroutines.launch

@Composable
fun TournamentsScreen(repository: OgRepository) {
    val tournaments by repository.observeTournaments().collectAsStateWithLifecycle(initialValue = emptyList())
    val registrations by repository.observeRegistrations().collectAsStateWithLifecycle(initialValue = emptyList())

    var selectedTab by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var statusFilter by remember { mutableStateOf("All") }
    var sortByNewest by remember { mutableStateOf(true) }

    var showRegisterDialog by remember { mutableStateOf(false) }
    var selectedTournament by remember { mutableStateOf<Tournament?>(null) }
    var tacticalDetailTournament by remember { mutableStateOf<Tournament?>(null) }
    var activeCommentTournamentId by remember { mutableStateOf<String?>(null) }
    var teamName by remember { mutableStateOf("") }
    var members by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val statuses = listOf("All", "Upcoming", "Ongoing", "Completed")

    // Filter and sort tournaments
    val filteredTournaments = tournaments.filter { tour ->
        val matchesQuery = tour.name.lowercase().contains(searchQuery.trim().lowercase()) ||
                tour.game.lowercase().contains(searchQuery.trim().lowercase())
        val matchesStatus = statusFilter == "All" || tour.status.equals(statusFilter, ignoreCase = true)
        matchesQuery && matchesStatus
    }.let { list ->
        if (sortByNewest) list.sortedByDescending { it.startDate } else list.sortedBy { it.startDate }
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
                text = "ESPORTS TOURNAMENTS",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                color = MaterialTheme.colorScheme.onBackground
            )
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Tournaments") })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Leaderboard") })
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("FAQ & Rules") })
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (selectedTab == 0) {
            // Search & Filter Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search tournaments by name or game...") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                statuses.forEach { status ->
                    FilterChip(
                        selected = statusFilter == status,
                        onClick = { statusFilter = status },
                        label = { Text(status) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = {
                    searchQuery = ""
                    statusFilter = "All"
                    sortByNewest = true
                }) {
                    Text("Clear Filters")
                }
                TextButton(onClick = { sortByNewest = !sortByNewest }) {
                    Text(if (sortByNewest) "Sort: Newest" else "Sort: Oldest")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (statusMessage != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = statusMessage ?: "",
                    modifier = Modifier.padding(12.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        when (selectedTab) {
            0 -> {
                if (filteredTournaments.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No matching tournaments found.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredTournaments) { tournament ->
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = tournament.game,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.secondary,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            IconButton(
                                                onClick = { tacticalDetailTournament = tournament },
                                                modifier = Modifier.size(36.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Map,
                                                    contentDescription = "Tactical Map & Zone Layout",
                                                    tint = MaterialTheme.colorScheme.tertiary,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                            IconButton(
                                                onClick = { activeCommentTournamentId = tournament.id },
                                                modifier = Modifier.size(36.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Comment,
                                                    contentDescription = "Comments",
                                                    tint = MaterialTheme.colorScheme.secondary,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                            IconButton(
                                                onClick = {
                                                    val shareText = "🏆 *OG WARRIORS TOURNAMENT ARENA* 🏆\n\n" +
                                                            "Tournament: ${tournament.name}\n" +
                                                            "Game: ${tournament.game}\n" +
                                                            "Prize Pool: ${tournament.prizePool}\n" +
                                                            "Date: ${tournament.startDate}\n" +
                                                            "Rules: ${tournament.rules}\n\n" +
                                                            "🔥 Join the battle now in OG WARRIORS!"
                                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                                        type = "text/plain"
                                                        putExtra(Intent.EXTRA_SUBJECT, "Tournament: ${tournament.name}")
                                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                                    }
                                                    context.startActivity(Intent.createChooser(intent, "Share Tournament via"))
                                                },
                                                modifier = Modifier.size(36.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Share,
                                                    contentDescription = "Share Tournament",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                            Badge(
                                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                                            ) {
                                                Text(tournament.status, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = tournament.name,
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Prize Pool: ${tournament.prizePool}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Date: ${tournament.startDate}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Rules: ${tournament.rules}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { tacticalDetailTournament = tournament },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Map, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Tactical Map")
                                        }
                                        Button(
                                            onClick = {
                                                selectedTournament = tournament
                                                showRegisterDialog = true
                                            },
                                            modifier = Modifier.weight(1.2f),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.GroupAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Register")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            1 -> {
                if (registrations.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No registered squads on the leaderboard yet.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(registrations.indices.toList()) { index ->
                            val reg = registrations[index]
                            val rank = index + 1
                            val rankColor = when (rank) {
                                1 -> MaterialTheme.colorScheme.primary
                                2 -> MaterialTheme.colorScheme.secondary
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }

                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = rankColor.copy(alpha = 0.2f),
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = "#$rank",
                                                    fontWeight = FontWeight.Black,
                                                    color = rankColor
                                                )
                                            }
                                        }
                                        Column {
                                            Text(
                                                text = reg.teamName,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "Players: ${reg.members}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    ) {
                                        Text(
                                            text = "${(10 - index) * 150} PTS",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else -> {
                FaqScreen(repository)
            }
        }
    }

    if (activeCommentTournamentId != null) {
        CommentsBottomSheet(
            targetId = activeCommentTournamentId!!,
            repository = repository,
            onDismiss = { activeCommentTournamentId = null }
        )
    }

    if (showRegisterDialog && selectedTournament != null) {
        AlertDialog(
            onDismissRequest = { showRegisterDialog = false },
            title = { Text("Register for ${selectedTournament?.name}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = teamName,
                        onValueChange = { teamName = it },
                        label = { Text("Team / Clan Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = members,
                        onValueChange = { members = it },
                        label = { Text("Player Names (Comma separated)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (teamName.isNotBlank() && members.isNotBlank()) {
                            scope.launch {
                                try {
                                    repository.registerTournament(
                                        selectedTournament!!.id,
                                        teamName,
                                        members
                                    )
                                    statusMessage = "Successfully registered team $teamName!"
                                    showRegisterDialog = false
                                    teamName = ""
                                    members = ""
                                } catch (e: Exception) {
                                    statusMessage = "Registration failed: ${e.localizedMessage}"
                                    showRegisterDialog = false
                                }
                            }
                        }
                    }
                ) {
                    Text("Confirm Registration")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRegisterDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (tacticalDetailTournament != null) {
        TournamentTacticalDetailModal(
            tournament = tacticalDetailTournament!!,
            onDismiss = { tacticalDetailTournament = null },
            onRegisterClicked = {
                val target = tacticalDetailTournament
                tacticalDetailTournament = null
                selectedTournament = target
                showRegisterDialog = true
            }
        )
    }
}
