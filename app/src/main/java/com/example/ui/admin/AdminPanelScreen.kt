package com.example.ui.admin

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.EditingTutorial
import com.example.data.model.FAQ
import com.example.data.model.FeedbackReport
import com.example.data.model.Post
import com.example.data.model.ProSpotlight
import com.example.data.model.Registration
import com.example.data.model.SpotlightQa
import com.example.data.model.Tip
import com.example.data.model.Tournament
import com.example.data.repository.OgRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    repository: OgRepository,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Posts", "Tips", "Tournaments", "Editing", "Spotlights", "Registrations", "FAQs", "Feedback", "Analytics")
    var statusMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val registrations by repository.observeRegistrations().collectAsStateWithLifecycle(initialValue = emptyList())
    val posts by repository.observePosts().collectAsStateWithLifecycle(initialValue = emptyList())
    val tips by repository.observeTips().collectAsStateWithLifecycle(initialValue = emptyList())
    val tournaments by repository.observeTournaments().collectAsStateWithLifecycle(initialValue = emptyList())
    val editingTutorials by repository.observeEditingTutorials().collectAsStateWithLifecycle(initialValue = emptyList())
    val spotlights by repository.observeSpotlights().collectAsStateWithLifecycle(initialValue = emptyList())
    val faqs by repository.observeFAQs().collectAsStateWithLifecycle(initialValue = emptyList())
    val teams by repository.observeTeams().collectAsStateWithLifecycle(initialValue = emptyList())
    val feedbacks by repository.observeFeedback().collectAsStateWithLifecycle(initialValue = emptyList())

    // Form states
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("News") }
    var imageUrl by remember { mutableStateOf("") }

    // Spotlight form states
    var spotlightPlayerName by remember { mutableStateOf("") }
    var spotlightRealName by remember { mutableStateOf("") }
    var spotlightTeamName by remember { mutableStateOf("GodLike Esports") }
    var spotlightRole by remember { mutableStateOf("Assaulter") }
    var spotlightBio by remember { mutableStateOf("") }
    var spotlightInterviewTitle by remember { mutableStateOf("") }
    var spotlightInterviewSummary by remember { mutableStateOf("") }
    var spotlightKeyAdvice by remember { mutableStateOf("") }
    var spotlightDeviceAndSens by remember { mutableStateOf("") }
    var spotlightAchievements by remember { mutableStateOf("") }
    var spotlightQ1 by remember { mutableStateOf("") }
    var spotlightA1 by remember { mutableStateOf("") }
    var spotlightQ2 by remember { mutableStateOf("") }
    var spotlightA2 by remember { mutableStateOf("") }

    // Tip form
    var tipCategory by remember { mutableStateOf("Headshot") }
    var difficulty by remember { mutableStateOf("Intermediate") }

    // Tournament form
    var prizePool by remember { mutableStateOf("$1,000") }
    var game by remember { mutableStateOf("BGMI / Free Fire") }
    var rules by remember { mutableStateOf("No emulators allowed.") }
    var mapName by remember { mutableStateOf("Erangel Tactical Grid") }
    var tacticalHotspots by remember { mutableStateOf("Pochinki, Military Base, School, Georgopol") }
    var zoneStrategy by remember { mutableStateOf("Phase 1: Outer perimeter looting. Phase 2-3: Early central rotation. Phase 4: Choke-point gatekeeping.") }

    // Editing form
    var software by remember { mutableStateOf("CapCut") }

    // FAQ form
    var faqQuestion by remember { mutableStateOf("") }
    var faqAnswer by remember { mutableStateOf("") }
    var faqCategory by remember { mutableStateOf("Tournament") }

    // Edit FAQ Dialog state
    var editingFaq by remember { mutableStateOf<FAQ?>(null) }
    var editQuestion by remember { mutableStateOf("") }
    var editAnswer by remember { mutableStateOf("") }
    var editCategory by remember { mutableStateOf("Tournament") }

    val faqCategories = listOf("Tournament", "Registration", "Rules", "Schedule", "Prizes")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Super Admin Dashboard (Owner)") },
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
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, titleText ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index; statusMessage = null },
                        text = { Text(titleText) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                if (selectedTab < 5 || selectedTab == 6) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = when (selectedTab) {
                                        6 -> "Add Tournament FAQ"
                                        4 -> "Create Pro Player Spotlight Interview"
                                        else -> "Create / Publish ${tabs[selectedTab]}"
                                    },
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                if (selectedTab == 6) {
                                    OutlinedTextField(
                                        value = faqQuestion,
                                        onValueChange = { faqQuestion = it },
                                        label = { Text("Question") },
                                        placeholder = { Text("e.g. How do I register my squad?") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = faqAnswer,
                                        onValueChange = { faqAnswer = it },
                                        label = { Text("Answer") },
                                        placeholder = { Text("Detailed explanation for tournament players...") },
                                        modifier = Modifier.fillMaxWidth(),
                                        minLines = 3
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "FAQ Category:",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        faqCategories.forEach { cat ->
                                            FilterChip(
                                                selected = faqCategory == cat,
                                                onClick = { faqCategory = cat },
                                                label = { Text(cat) }
                                            )
                                        }
                                    }
                                } else if (selectedTab == 4) {
                                    OutlinedTextField(
                                        value = spotlightPlayerName,
                                        onValueChange = { spotlightPlayerName = it },
                                        label = { Text("Player In-Game Name (IGN) *") },
                                        placeholder = { Text("e.g. Jonathan, Mortal, ScoutOP") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = spotlightRealName,
                                        onValueChange = { spotlightRealName = it },
                                        label = { Text("Real Name") },
                                        placeholder = { Text("e.g. Jonathan Amaral") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = spotlightTeamName,
                                            onValueChange = { spotlightTeamName = it },
                                            label = { Text("Team Name") },
                                            modifier = Modifier.weight(1f)
                                        )
                                        OutlinedTextField(
                                            value = spotlightRole,
                                            onValueChange = { spotlightRole = it },
                                            label = { Text("Role") },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = spotlightBio,
                                        onValueChange = { spotlightBio = it },
                                        label = { Text("Player Bio & Overview") },
                                        modifier = Modifier.fillMaxWidth(),
                                        minLines = 2
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = spotlightInterviewTitle,
                                        onValueChange = { spotlightInterviewTitle = it },
                                        label = { Text("Interview Topic / Headline *") },
                                        placeholder = { Text("e.g. Mastering 4-Finger Claw & Clutch Plays") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = spotlightKeyAdvice,
                                        onValueChange = { spotlightKeyAdvice = it },
                                        label = { Text("Featured Pro Advice Quote") },
                                        placeholder = { Text("Key insight from the pro player...") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = spotlightDeviceAndSens,
                                        onValueChange = { spotlightDeviceAndSens = it },
                                        label = { Text("Hardware, Grip & Sensitivity") },
                                        placeholder = { Text("e.g. iPhone 15 Pro Max | 4-Finger Claw | Gyro Always On") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = spotlightAchievements,
                                        onValueChange = { spotlightAchievements = it },
                                        label = { Text("Achievements (Comma-separated)") },
                                        placeholder = { Text("e.g. BGIS MVP, PMCO South Asia Champion") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "Interview Q&A Pair 1:",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    OutlinedTextField(
                                        value = spotlightQ1,
                                        onValueChange = { spotlightQ1 = it },
                                        label = { Text("Question 1") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    OutlinedTextField(
                                        value = spotlightA1,
                                        onValueChange = { spotlightA1 = it },
                                        label = { Text("Answer 1") },
                                        modifier = Modifier.fillMaxWidth(),
                                        minLines = 2
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Interview Q&A Pair 2:",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    OutlinedTextField(
                                        value = spotlightQ2,
                                        onValueChange = { spotlightQ2 = it },
                                        label = { Text("Question 2") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    OutlinedTextField(
                                        value = spotlightA2,
                                        onValueChange = { spotlightA2 = it },
                                        label = { Text("Answer 2") },
                                        modifier = Modifier.fillMaxWidth(),
                                        minLines = 2
                                    )
                                } else {
                                    OutlinedTextField(
                                        value = title,
                                        onValueChange = { title = it },
                                        label = { Text("Title / Name") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    when (selectedTab) {
                                        0 -> {
                                            OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Announcement Content") }, modifier = Modifier.fillMaxWidth())
                                            Spacer(modifier = Modifier.height(8.dp))
                                            OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("Image URL (Optional)") }, modifier = Modifier.fillMaxWidth())
                                        }
                                        1 -> {
                                            OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Tip Guide") }, modifier = Modifier.fillMaxWidth())
                                        }
                                        2 -> {
                                            OutlinedTextField(value = prizePool, onValueChange = { prizePool = it }, label = { Text("Prize Pool") }, modifier = Modifier.fillMaxWidth())
                                            Spacer(modifier = Modifier.height(8.dp))
                                            OutlinedTextField(value = rules, onValueChange = { rules = it }, label = { Text("Rules") }, modifier = Modifier.fillMaxWidth())
                                            Spacer(modifier = Modifier.height(8.dp))
                                            OutlinedTextField(value = mapName, onValueChange = { mapName = it }, label = { Text("Map / Tactical Layout Name") }, modifier = Modifier.fillMaxWidth())
                                            Spacer(modifier = Modifier.height(8.dp))
                                            OutlinedTextField(value = tacticalHotspots, onValueChange = { tacticalHotspots = it }, label = { Text("Tactical Hotspots (Comma separated)") }, modifier = Modifier.fillMaxWidth())
                                            Spacer(modifier = Modifier.height(8.dp))
                                            OutlinedTextField(value = zoneStrategy, onValueChange = { zoneStrategy = it }, label = { Text("Zone Rotation Strategy Guide") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                                        }
                                        3 -> {
                                            OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = {
                                        scope.launch {
                                            try {
                                                if (selectedTab == 6) {
                                                    if (faqQuestion.isNotBlank() && faqAnswer.isNotBlank()) {
                                                        repository.addFAQ(faqQuestion, faqAnswer, faqCategory)
                                                        statusMessage = "Successfully added FAQ!"
                                                        faqQuestion = ""
                                                        faqAnswer = ""
                                                    }
                                                } else if (selectedTab == 4) {
                                                    if (spotlightPlayerName.isNotBlank() && spotlightInterviewTitle.isNotBlank()) {
                                                        val qaList = mutableListOf<SpotlightQa>()
                                                        if (spotlightQ1.isNotBlank() && spotlightA1.isNotBlank()) {
                                                            qaList.add(SpotlightQa(spotlightQ1, spotlightA1))
                                                        }
                                                        if (spotlightQ2.isNotBlank() && spotlightA2.isNotBlank()) {
                                                            qaList.add(SpotlightQa(spotlightQ2, spotlightA2))
                                                        }
                                                        val achList = spotlightAchievements.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                                                        val spot = ProSpotlight(
                                                            playerName = spotlightPlayerName,
                                                            realName = spotlightRealName,
                                                            teamName = spotlightTeamName,
                                                            role = spotlightRole,
                                                            game = "BGMI / Free Fire",
                                                            bio = spotlightBio,
                                                            interviewTitle = spotlightInterviewTitle,
                                                            interviewSummary = spotlightInterviewSummary,
                                                            keyAdvice = spotlightKeyAdvice,
                                                            deviceAndSens = spotlightDeviceAndSens,
                                                            achievements = achList,
                                                            qaList = qaList,
                                                            rating = 5.0
                                                        )
                                                        repository.addSpotlight(spot)
                                                        statusMessage = "Pro Spotlight for $spotlightPlayerName published!"
                                                        spotlightPlayerName = ""
                                                        spotlightRealName = ""
                                                        spotlightBio = ""
                                                        spotlightInterviewTitle = ""
                                                        spotlightInterviewSummary = ""
                                                        spotlightKeyAdvice = ""
                                                        spotlightDeviceAndSens = ""
                                                        spotlightAchievements = ""
                                                        spotlightQ1 = ""
                                                        spotlightA1 = ""
                                                        spotlightQ2 = ""
                                                        spotlightA2 = ""
                                                    } else {
                                                        statusMessage = "Please enter Player Name and Interview Headline."
                                                    }
                                                } else if (title.isNotBlank()) {
                                                    when (selectedTab) {
                                                        0 -> repository.addPost(Post(title = title, content = content, category = category, imageUrl = imageUrl))
                                                        1 -> repository.addTip(Tip(title = title, content = content, category = tipCategory, difficulty = difficulty))
                                                        2 -> repository.addTournament(
                                                            Tournament(
                                                                name = title,
                                                                prizePool = prizePool,
                                                                game = game,
                                                                rules = rules,
                                                                status = "Upcoming",
                                                                mapName = mapName.ifBlank { "Erangel Tactical Grid" },
                                                                tacticalHotspots = tacticalHotspots.ifBlank { "Pochinki, Military Base, School, Georgopol" },
                                                                zoneStrategy = zoneStrategy.ifBlank { "Phase 1: Outer perimeter looting. Phase 2-3: Early central rotation. Phase 4: Choke-point gatekeeping." }
                                                            )
                                                        )
                                                        3 -> repository.addEditingTutorial(EditingTutorial(title = title, description = content, software = software, category = "Montage"))
                                                    }
                                                    statusMessage = "Successfully published!"
                                                    title = ""
                                                    content = ""
                                                }
                                            } catch (e: Exception) {
                                                statusMessage = "Error: ${e.localizedMessage}"
                                            }
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        when (selectedTab) {
                                            6 -> "Add FAQ Question"
                                            4 -> "Publish Pro Spotlight"
                                            else -> "Publish Now"
                                        }
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = when (selectedTab) {
                                6 -> "Manage Tournament FAQs (${faqs.size})"
                                4 -> "Manage Pro Spotlights (${spotlights.size})"
                                else -> "Manage Existing ${tabs[selectedTab]}"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                when (selectedTab) {
                    0 -> items(posts) { post -> AdminItemCard(title = post.title, subtitle = post.content) { scope.launch { repository.deletePost(post.id) } } }
                    1 -> items(tips) { tip -> AdminItemCard(title = tip.title, subtitle = "Category: ${tip.category}") { scope.launch { repository.deleteTip(tip.id) } } }
                    2 -> items(tournaments) { tour -> AdminItemCard(title = tour.name, subtitle = "Prize: ${tour.prizePool}") { scope.launch { repository.deleteTournament(tour.id) } } }
                    3 -> items(editingTutorials) { tut -> AdminItemCard(title = tut.title, subtitle = "Software: ${tut.software}") { scope.launch { repository.deleteEditingTutorial(tut.id) } } }
                    4 -> {
                        if (spotlights.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                    Text("No spotlights found.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        } else {
                            items(spotlights) { spot ->
                                AdminItemCard(
                                    title = "${spot.playerName} • ${spot.teamName}",
                                    subtitle = "[${spot.role}] ${spot.interviewTitle}",
                                    onDelete = {
                                        scope.launch {
                                            try {
                                                repository.deleteSpotlight(spot.id)
                                                statusMessage = "Spotlight deleted successfully."
                                            } catch (e: Exception) {
                                                statusMessage = "Delete failed: ${e.localizedMessage}"
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                    5 -> {
                        item { Text(text = "Tournament Registrations", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground) }
                        items(registrations) { reg ->
                            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(text = "Team: ${reg.teamName}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Players: ${reg.members}", color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }
                    }
                    6 -> {
                        if (faqs.isEmpty()) {
                            item {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "No custom FAQs in Firestore yet. Default built-in tournament rules are active for users.",
                                        modifier = Modifier.padding(16.dp),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            items(faqs) { faq ->
                                AdminItemCard(
                                    title = faq.question,
                                    subtitle = "[${faq.category}] ${faq.answer}",
                                    onEdit = {
                                        editingFaq = faq
                                        editQuestion = faq.question
                                        editAnswer = faq.answer
                                        editCategory = faq.category.ifBlank { "Tournament" }
                                    },
                                    onDelete = {
                                        scope.launch {
                                            try {
                                                repository.deleteFAQ(faq.id)
                                                statusMessage = "FAQ deleted successfully."
                                            } catch (e: Exception) {
                                                statusMessage = "Delete failed: ${e.localizedMessage}"
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                    7 -> {
                        // User Feedback & Bug Reports
                        if (feedbacks.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                    Text("No user feedback or bug reports submitted yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        } else {
                            items(feedbacks) { report ->
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                                                Text(report.status, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                            }
                                            Text(report.userEmail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(report.subject, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(report.message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            TextButton(onClick = {
                                                scope.launch {
                                                    try {
                                                        repository.updateFeedbackStatus(report.id, "In Review")
                                                        statusMessage = "Feedback marked as In Review"
                                                    } catch (_: Exception) {}
                                                }
                                            }) {
                                                Text("Mark In Review")
                                            }
                                            TextButton(onClick = {
                                                scope.launch {
                                                    try {
                                                        repository.updateFeedbackStatus(report.id, "Resolved")
                                                        statusMessage = "Feedback marked as Resolved"
                                                    } catch (_: Exception) {}
                                                }
                                            }) {
                                                Text("Mark Resolved")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    8 -> {
                        // Analytics Dashboard
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text(text = "ESPORTS PLATFORM ANALYTICS", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    AnalyticsStatRow(label = "Total News Posts", count = posts.size)
                                    AnalyticsStatRow(label = "Total Gaming Tips", count = tips.size)
                                    AnalyticsStatRow(label = "Total Tournaments", count = tournaments.size)
                                    AnalyticsStatRow(label = "Tournament Registrations", count = registrations.size)
                                    AnalyticsStatRow(label = "Editing Tutorials", count = editingTutorials.size)
                                    AnalyticsStatRow(label = "Pro Player Spotlights", count = spotlights.size)
                                    AnalyticsStatRow(label = "Active Esports Teams", count = teams.size)
                                    AnalyticsStatRow(label = "Published FAQs", count = faqs.size)
                                    AnalyticsStatRow(label = "User Feedback & Reports", count = feedbacks.size)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Edit FAQ Dialog
    if (editingFaq != null) {
        AlertDialog(
            onDismissRequest = { editingFaq = null },
            title = { Text("Edit Tournament FAQ", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editQuestion,
                        onValueChange = { editQuestion = it },
                        label = { Text("Question") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editAnswer,
                        onValueChange = { editAnswer = it },
                        label = { Text("Answer") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                    Text(
                        text = "Category:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        faqCategories.take(3).forEach { cat ->
                            FilterChip(
                                selected = editCategory == cat,
                                onClick = { editCategory = cat },
                                label = { Text(cat, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        faqCategories.drop(3).forEach { cat ->
                            FilterChip(
                                selected = editCategory == cat,
                                onClick = { editCategory = cat },
                                label = { Text(cat, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val target = editingFaq ?: return@Button
                        if (editQuestion.isNotBlank() && editAnswer.isNotBlank()) {
                            scope.launch {
                                try {
                                    repository.updateFAQ(target.id, editQuestion, editAnswer, editCategory)
                                    statusMessage = "Successfully updated FAQ!"
                                    editingFaq = null
                                } catch (e: Exception) {
                                    statusMessage = "Update failed: ${e.localizedMessage}"
                                }
                            }
                        }
                    }
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingFaq = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AnalyticsStatRow(label: String, count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
        Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
            Text(text = "$count", modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontWeight = FontWeight.Bold, fontSize = MaterialTheme.typography.bodyLarge.fontSize)
        }
    }
}

@Composable
fun AdminItemCard(
    title: String,
    subtitle: String,
    onEdit: (() -> Unit)? = null,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (onEdit != null) {
                    IconButton(onClick = onEdit) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                    }
                }
                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
