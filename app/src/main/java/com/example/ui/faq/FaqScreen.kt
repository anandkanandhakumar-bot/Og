package com.example.ui.faq

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.FAQ
import com.example.data.repository.OgRepository

// Built-in standard tournament FAQs as defaults if Firestore is empty
private val DefaultTournamentFaqs = listOf(
    FAQ(
        id = "default_reg",
        question = "How do I register my squad for a tournament?",
        answer = "Navigate to the Tournaments tab, select the upcoming tournament of your choice, and tap 'Register Squad'. Provide your Team/Clan name and player roster IDs. Your registration will sync immediately to the arena roster.",
        category = "Registration"
    ),
    FAQ(
        id = "default_fee",
        question = "Are there any entry fees for OG WARRIORS tournaments?",
        answer = "Community cups and academy qualifiers are 100% free to enter. High-stakes invitationals and major sponsored pro leagues will display their entry tier and prize pool directly on the tournament card.",
        category = "Registration"
    ),
    FAQ(
        id = "default_team_size",
        question = "What are the squad and team size requirements?",
        answer = "Standard Battle Royale tournaments require a 4-player squad (plus optional 1 substitute). For 2v2 or solo clash showdowns, team size requirements will be specified in the match rules header.",
        category = "Rules"
    ),
    FAQ(
        id = "default_rules",
        question = "What are the primary tournament rules and anti-cheat policies?",
        answer = "Emulators, third-party recoil scripts, modified APKs, and iPad view modifiers are strictly forbidden and result in permanent device and clan bans. All matches are monitored by verified arena referees.",
        category = "Rules"
    ),
    FAQ(
        id = "default_schedule",
        question = "Where and when are match schedules published?",
        answer = "Match schedules, custom room IDs, and passwords are distributed 15 minutes prior to match kickoff in the Global Squad Chat and on the Tournament announcement ticker.",
        category = "Schedule"
    ),
    FAQ(
        id = "default_prizes",
        question = "How are cash prizes and trophies distributed?",
        answer = "Prize pools are disbursed to the verified Team Captain within 24–48 hours after final result verification via UPI, Bank Transfer, or PayPal. Winners also unlock the prestigious 'Tournament Winner' in-app badge.",
        category = "Prizes"
    ),
    FAQ(
        id = "default_results",
        question = "How are match scores and points calculated on the Leaderboard?",
        answer = "Rankings are updated live: 1st Place (15 pts), 2nd Place (12 pts), 3rd Place (10 pts), plus 1 point per confirmed kill. Final tournament leaderboards are posted directly on the Tournaments tab.",
        category = "Schedule"
    )
)

@Composable
fun FaqScreen(
    repository: OgRepository,
    onBack: (() -> Unit)? = null
) {
    if (onBack != null) {
        BackHandler(onBack = onBack)
    }

    val firestoreFaqs by repository.observeFAQs().collectAsStateWithLifecycle(initialValue = emptyList())
    var expandedId by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "Registration", "Rules", "Schedule", "Prizes")

    // Use Firestore FAQs when available; fall back to default tournament FAQs if database has none
    val displayedFaqs = if (firestoreFaqs.isNotEmpty()) {
        firestoreFaqs
    } else {
        DefaultTournamentFaqs
    }

    val filteredFaqs = displayedFaqs.filter { faq ->
        val matchesCategory = selectedCategory == "All" || faq.category.equals(selectedCategory, ignoreCase = true)
        val query = searchQuery.trim().lowercase()
        val matchesSearch = query.isBlank() ||
                faq.question.lowercase().contains(query) ||
                faq.answer.lowercase().contains(query) ||
                faq.category.lowercase().contains(query)
        matchesCategory && matchesSearch
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (onBack != null) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
                Column {
                    Text(
                        text = "TOURNAMENT FAQ & RULES",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Everything you need to know about competing",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.Help,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search FAQ (e.g. entry fee, prizes, rules)...") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Category Filter Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { selectedCategory = cat },
                    label = { Text(cat) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredFaqs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No matching FAQ items found.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredFaqs) { faq ->
                    val isExpanded = expandedId == faq.id
                    val rotationState by animateFloatAsState(
                        targetValue = if (isExpanded) 180f else 0f,
                        label = "chevron_rotation"
                    )

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isExpanded) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedId = if (isExpanded) null else faq.id }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    ) {
                                        Text(
                                            text = faq.category.ifBlank { "Tournament" },
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = faq.question,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                IconButton(onClick = { expandedId = if (isExpanded) null else faq.id }) {
                                    Icon(
                                        imageVector = Icons.Default.ExpandMore,
                                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.rotate(rotationState)
                                    )
                                }
                            }
                            AnimatedVisibility(visible = isExpanded) {
                                Column {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = faq.answer,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
