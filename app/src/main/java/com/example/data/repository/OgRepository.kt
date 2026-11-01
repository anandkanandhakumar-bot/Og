package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.Achievement
import com.example.data.model.Bookmark
import com.example.data.model.ChatMessage
import com.example.data.model.Comment
import com.example.data.model.EditingTutorial
import com.example.data.model.FAQ
import com.example.data.model.FeedbackReport
import com.example.data.model.Post
import com.example.data.model.ProSpotlight
import com.example.data.model.Registration
import com.example.data.model.SpotlightQa
import com.example.data.model.Team
import com.example.data.model.Tip
import com.example.data.model.Tournament
import com.example.data.model.TutorialProgress
import com.example.data.model.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.PersistentCacheSettings
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class OgRepository(private val db: FirebaseFirestore) {
    init {
        try {
            val settings = FirebaseFirestoreSettings.Builder()
                .setLocalCacheSettings(
                    PersistentCacheSettings.newBuilder().build()
                )
                .build()
            db.firestoreSettings = settings
        } catch (_: Exception) {}
    }

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    fun isSuperAdmin(): Boolean {
        val user = auth.currentUser
        return user != null && user.email == "anandkanandhakumar@gmail.com"
    }

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in.")
    }

    // --- Posts ---
    fun observePosts(): Flow<List<Post>> =
        db.collection("posts")
            .snapshots()
            .map { snapshot -> snapshot.toObjects(Post::class.java) }
            .catch { e -> Log.e("OgRepo", "Error observing posts", e); emit(emptyList()) }

    suspend fun addPost(post: Post) {
        if (!isSuperAdmin()) throw SecurityException("Unauthorized")
        db.collection("posts").add(post.copy(createdAt = null, updatedAt = null)).await()
    }

    suspend fun updatePost(post: Post) {
        if (!isSuperAdmin()) throw SecurityException("Unauthorized")
        if (post.id.isBlank()) return
        db.collection("posts").document(post.id).set(post.copy(updatedAt = null)).await()
    }

    suspend fun deletePost(postId: String) {
        if (!isSuperAdmin()) throw SecurityException("Unauthorized")
        db.collection("posts").document(postId).delete().await()
    }

    // --- Tips ---
    fun observeTips(): Flow<List<Tip>> =
        db.collection("tips")
            .snapshots()
            .map { snapshot -> snapshot.toObjects(Tip::class.java) }
            .catch { e -> Log.e("OgRepo", "Error observing tips", e); emit(emptyList()) }

    suspend fun addTip(tip: Tip) {
        if (!isSuperAdmin()) throw SecurityException("Unauthorized")
        db.collection("tips").add(tip.copy(createdAt = null)).await()
    }

    suspend fun deleteTip(tipId: String) {
        if (!isSuperAdmin()) throw SecurityException("Unauthorized")
        db.collection("tips").document(tipId).delete().await()
    }

    // --- Tournaments ---
    fun observeTournaments(): Flow<List<Tournament>> =
        db.collection("tournaments")
            .snapshots()
            .map { snapshot -> snapshot.toObjects(Tournament::class.java) }
            .catch { e -> Log.e("OgRepo", "Error observing tournaments", e); emit(emptyList()) }

    suspend fun addTournament(tournament: Tournament) {
        if (!isSuperAdmin()) throw SecurityException("Unauthorized")
        db.collection("tournaments").add(tournament.copy(createdAt = null)).await()
    }

    suspend fun updateTournament(tournament: Tournament) {
        if (!isSuperAdmin()) throw SecurityException("Unauthorized")
        if (tournament.id.isBlank()) return
        db.collection("tournaments").document(tournament.id).set(tournament).await()
    }

    suspend fun deleteTournament(tournamentId: String) {
        if (!isSuperAdmin()) throw SecurityException("Unauthorized")
        db.collection("tournaments").document(tournamentId).delete().await()
    }

    // --- Registrations ---
    fun observeRegistrations(): Flow<List<Registration>> =
        db.collection("registrations")
            .snapshots()
            .map { snapshot -> snapshot.toObjects(Registration::class.java) }
            .catch { e -> Log.e("OgRepo", "Error observing registrations", e); emit(emptyList()) }

    fun observeUserRegistrations(): Flow<List<Registration>> {
        val uid = try { requireUserId() } catch (_: Exception) { return flowOf(emptyList()) }
        return db.collection("registrations")
            .whereEqualTo("userId", uid)
            .snapshots()
            .map { snapshot -> snapshot.toObjects(Registration::class.java) }
            .catch { e -> Log.e("OgRepo", "Error observing user registrations", e); emit(emptyList()) }
    }

    suspend fun registerTournament(tournamentId: String, teamName: String, members: String) {
        val uid = requireUserId()
        val reg = Registration(
            tournamentId = tournamentId,
            userId = uid,
            teamName = teamName,
            members = members,
            status = "Confirmed"
        )
        db.collection("registrations").add(reg).await()
        try {
            unlockAchievement("Tournament Veteran", "Registered for an esports tournament", "Tournament")
        } catch (_: Exception) {}
    }

    // --- FAQs ---
    fun observeFAQs(): Flow<List<FAQ>> =
        db.collection("faqs")
            .snapshots()
            .map { snapshot -> snapshot.toObjects(FAQ::class.java) }
            .catch { e -> Log.e("OgRepo", "Error observing FAQs", e); emit(emptyList()) }

    suspend fun addFAQ(question: String, answer: String, category: String = "Tournament") {
        if (!isSuperAdmin()) throw SecurityException("Unauthorized")
        val faq = FAQ(question = question, answer = answer, category = category)
        db.collection("faqs").add(faq).await()
    }

    suspend fun updateFAQ(faqId: String, question: String, answer: String, category: String = "Tournament") {
        if (!isSuperAdmin()) throw SecurityException("Unauthorized")
        if (faqId.isBlank()) return
        val faq = FAQ(id = faqId, question = question, answer = answer, category = category)
        db.collection("faqs").document(faqId).set(faq).await()
    }

    suspend fun deleteFAQ(faqId: String) {
        if (!isSuperAdmin()) throw SecurityException("Unauthorized")
        db.collection("faqs").document(faqId).delete().await()
    }

    // --- Comments ---
    fun observeComments(targetId: String): Flow<List<Comment>> =
        db.collection("comments")
            .whereEqualTo("targetId", targetId)
            .snapshots()
            .map { snapshot -> snapshot.toObjects(Comment::class.java) }
            .catch { e -> Log.e("OgRepo", "Error observing comments", e); emit(emptyList()) }

    suspend fun addComment(targetId: String, text: String) {
        val user = auth.currentUser ?: throw IllegalStateException("Must be signed in to comment")
        val comment = Comment(
            targetId = targetId,
            userId = user.uid,
            userName = user.displayName ?: "OG Warrior",
            userPhoto = user.photoUrl?.toString() ?: "",
            text = text
        )
        db.collection("comments").add(comment).await()
    }

    suspend fun deleteComment(commentId: String, commentUserId: String) {
        val user = auth.currentUser ?: throw IllegalStateException("Must be signed in")
        if (user.uid != commentUserId && !isSuperAdmin()) {
            throw SecurityException("Unauthorized to delete comment")
        }
        db.collection("comments").document(commentId).delete().await()
    }

    // --- Bookmarks ---
    fun observeBookmarks(): Flow<List<Bookmark>> {
        val uid = try { requireUserId() } catch (_: Exception) { return flowOf(emptyList()) }
        return db.collection("users").document(uid).collection("bookmarks")
            .snapshots()
            .map { snapshot -> snapshot.toObjects(Bookmark::class.java) }
            .catch { e -> Log.e("OgRepo", "Error observing bookmarks", e); emit(emptyList()) }
    }

    suspend fun toggleBookmark(itemId: String, itemType: String, title: String) {
        val uid = requireUserId()
        val ref = db.collection("users").document(uid).collection("bookmarks")
        val existing = ref.whereEqualTo("itemId", itemId).get().await()
        if (!existing.isEmpty) {
            for (doc in existing.documents) {
                doc.reference.delete().await()
            }
        } else {
            val bookmark = Bookmark(itemId = itemId, itemType = itemType, title = title)
            ref.add(bookmark).await()
        }
    }

    // --- Tutorial Progress ---
    fun observeProgress(): Flow<List<TutorialProgress>> {
        val uid = try { requireUserId() } catch (_: Exception) { return flowOf(emptyList()) }
        return db.collection("users").document(uid).collection("progress")
            .snapshots()
            .map { snapshot -> snapshot.toObjects(TutorialProgress::class.java) }
            .catch { e -> Log.e("OgRepo", "Error observing progress", e); emit(emptyList()) }
    }

    suspend fun updateProgress(tutorialId: String, tutorialTitle: String, progressPercent: Int, isCompleted: Boolean) {
        val uid = requireUserId()
        val ref = db.collection("users").document(uid).collection("progress")
        val existing = ref.whereEqualTo("tutorialId", tutorialId).get().await()
        val progress = TutorialProgress(
            tutorialId = tutorialId,
            tutorialTitle = tutorialTitle,
            progressPercent = progressPercent,
            isCompleted = isCompleted
        )
        if (!existing.isEmpty) {
            val docId = existing.documents[0].id
            ref.document(docId).set(progress).await()
        } else {
            ref.add(progress).await()
        }
        if (isCompleted) {
            try {
                unlockAchievement("Tutorial Master", "Completed a pro gaming tutorial", "Tutorial")
            } catch (_: Exception) {}
        }
    }

    // --- Achievements ---
    fun observeAchievements(): Flow<List<Achievement>> {
        val uid = try { requireUserId() } catch (_: Exception) { return flowOf(emptyList()) }
        return db.collection("users").document(uid).collection("achievements")
            .snapshots()
            .map { snapshot -> snapshot.toObjects(Achievement::class.java) }
            .catch { e -> Log.e("OgRepo", "Error observing achievements", e); emit(emptyList()) }
    }

    suspend fun unlockAchievement(title: String, description: String, category: String) {
        val uid = requireUserId()
        val ref = db.collection("users").document(uid).collection("achievements")
        val existing = ref.whereEqualTo("title", title).get().await()
        if (existing.isEmpty) {
            val ach = Achievement(title = title, description = description, category = category)
            ref.add(ach).await()
        }
    }

    // --- Teams ---
    fun observeTeams(): Flow<List<Team>> =
        db.collection("teams")
            .snapshots()
            .map { snapshot -> snapshot.toObjects(Team::class.java) }
            .catch { e -> Log.e("OgRepo", "Error observing teams", e); emit(emptyList()) }

    suspend fun createTeam(name: String) {
        val user = auth.currentUser ?: throw IllegalStateException("Must be signed in")
        val team = Team(
            name = name,
            captainUid = user.uid,
            captainName = user.displayName ?: "Captain",
            members = listOf(user.uid)
        )
        db.collection("teams").add(team).await()
    }

    // --- Chat Messages ---
    fun observeMessages(chatId: String): Flow<List<ChatMessage>> =
        db.collection("chats").document(chatId).collection("messages")
            .snapshots()
            .map { snapshot -> snapshot.toObjects(ChatMessage::class.java) }
            .catch { e -> Log.e("OgRepo", "Error observing messages", e); emit(emptyList()) }

    suspend fun sendMessage(chatId: String, text: String) {
        val user = auth.currentUser ?: throw IllegalStateException("Must be signed in")
        val msg = ChatMessage(
            chatId = chatId,
            senderId = user.uid,
            senderName = user.displayName ?: "Warrior",
            text = text
        )
        db.collection("chats").document(chatId).collection("messages").add(msg).await()
    }

    // --- Editing Tutorials ---
    fun observeEditingTutorials(): Flow<List<EditingTutorial>> =
        db.collection("editing_tutorials")
            .snapshots()
            .map { snapshot ->
                val list = snapshot.toObjects(EditingTutorial::class.java)
                if (list.isEmpty()) getDefaultEditingTutorials() else list
            }
            .catch { e ->
                Log.e("OgRepo", "Error observing editing tutorials", e)
                emit(getDefaultEditingTutorials())
            }

    fun getDefaultEditingTutorials(): List<EditingTutorial> = listOf(
        EditingTutorial(
            id = "tutorial_capcut_1",
            title = "CapCut Velocity & Speed Ramping Masterclass",
            software = "CapCut",
            category = "Velocity",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            description = "Learn how to sync keyframes to heavy bass drops for professional PUBG/BGMI montages."
        ),
        EditingTutorial(
            id = "tutorial_premiere_2",
            title = "Premiere Pro 4K Cinematic Color Grading",
            software = "Premiere",
            category = "Color Grade",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
            description = "Step-by-step LUT grading, S-curves, and glow effects for immersive cinematic gameplay clips."
        ),
        EditingTutorial(
            id = "tutorial_alight_3",
            title = "Alight Motion Smooth 60fps Shaking Cam & Optics",
            software = "Alight Motion",
            category = "Effects",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
            description = "Master directional blur, tile reflection, and camera shake expressions for smooth mobile edits."
        )
    )

    suspend fun addEditingTutorial(tutorial: EditingTutorial) {
        if (!isSuperAdmin()) throw SecurityException("Unauthorized")
        db.collection("editing_tutorials").add(tutorial.copy(createdAt = null)).await()
    }

    suspend fun deleteEditingTutorial(tutorialId: String) {
        if (!isSuperAdmin()) throw SecurityException("Unauthorized")
        db.collection("editing_tutorials").document(tutorialId).delete().await()
    }

    // --- User Profile ---
    suspend fun syncUserProfile() {
        val user = auth.currentUser ?: return
        val profile = UserProfile(
            uid = user.uid,
            displayName = user.displayName ?: "OG Warrior",
            email = user.email ?: "",
            photoUrl = user.photoUrl?.toString() ?: "",
            role = if (isSuperAdmin()) "Super Admin" else "Member"
        )
        db.collection("users").document(user.uid).set(profile).await()
        try {
            unlockAchievement("Arena Rookie", "Joined OG Warriors esports arena", "General")
        } catch (_: Exception) {}
    }

    // --- Feedback & Bug Reports ---
    fun observeFeedback(): Flow<List<FeedbackReport>> {
        if (!isSuperAdmin()) return flowOf(emptyList())
        return db.collection("feedback")
            .snapshots()
            .map { snapshot -> snapshot.toObjects(FeedbackReport::class.java) }
            .catch { e -> Log.e("OgRepo", "Error observing feedback", e); emit(emptyList()) }
    }

    suspend fun submitFeedback(subject: String, message: String) {
        val user = auth.currentUser ?: throw IllegalStateException("Must be signed in to submit feedback")
        val report = FeedbackReport(
            userId = user.uid,
            userEmail = user.email ?: "",
            userName = user.displayName ?: "OG Warrior",
            subject = subject,
            message = message,
            status = "Received"
        )
        db.collection("feedback").add(report).await()
    }

    suspend fun updateFeedbackStatus(feedbackId: String, status: String) {
        if (!isSuperAdmin()) throw SecurityException("Unauthorized")
        db.collection("feedback").document(feedbackId).update("status", status).await()
    }

    // --- Pro Player Spotlights ---
    fun observeSpotlights(): Flow<List<ProSpotlight>> =
        db.collection("spotlights")
            .snapshots()
            .map { snapshot ->
                val list = snapshot.toObjects(ProSpotlight::class.java)
                if (list.isEmpty()) getDefaultSpotlights() else list
            }
            .catch { e ->
                Log.e("OgRepo", "Error observing spotlights", e)
                emit(getDefaultSpotlights())
            }

    suspend fun addSpotlight(spotlight: ProSpotlight) {
        if (!isSuperAdmin()) throw SecurityException("Unauthorized")
        db.collection("spotlights").add(spotlight.copy(createdAt = null)).await()
    }

    suspend fun updateSpotlight(spotlight: ProSpotlight) {
        if (!isSuperAdmin()) throw SecurityException("Unauthorized")
        if (spotlight.id.isBlank()) return
        db.collection("spotlights").document(spotlight.id).set(spotlight).await()
    }

    suspend fun deleteSpotlight(spotlightId: String) {
        if (!isSuperAdmin()) throw SecurityException("Unauthorized")
        db.collection("spotlights").document(spotlightId).delete().await()
    }

    fun getDefaultSpotlights(): List<ProSpotlight> = listOf(
        ProSpotlight(
            id = "jonathan_godlike",
            playerName = "Jonathan",
            realName = "Jonathan Amaral",
            teamName = "GodLike Esports",
            role = "Assaulter / Entry Fragger",
            game = "BGMI / PUBG Mobile",
            bio = "Widely regarded as one of the fiercest assaulters and spray-transfer masters in mobile esports history, leading GodLike across global and national circuits.",
            interviewTitle = "Mastering Close-Range Reflexes, 4-Finger Claw & Aggressive Crosshair Placement",
            interviewSummary = "In this deep-dive interview, Jonathan breaks down how he converted from thumb player to 4-finger claw, his gyro sensitivity secrets, and psychological discipline during 1v4 clutches.",
            keyAdvice = "Never rely on luck in close combat. Keep your crosshair at head level before you ever see the enemy peek, and keep your gyro movement fluid.",
            deviceAndSens = "iPhone 15 Pro Max | 4-Finger Claw + Full Gyro Always On | Red Dot ADS: 58%, Camera: 100%, Gyroscope: 300%",
            achievements = listOf("PMCO Fall Split South Asia Champion", "BGIS Most Valuable Player (MVP)", "Highest Fragger - Skyesports Championship"),
            rating = 4.98,
            qaList = listOf(
                SpotlightQa(
                    question = "What is your daily training routine before entering high-stakes tournaments?",
                    answer = "I spend at least 45 minutes in the training ground: 15 minutes of moving target spray transfers, 15 minutes of jiggle-peek drills with iron sights, and 15 minutes in Team Deathmatch warming up hip-fire accuracy."
                ),
                SpotlightQa(
                    question = "How do you maintain calm when your team is down 3 players in late circles?",
                    answer = "Tunnel vision on high-percentage isolation plays. I don't think about taking on a 1v3 directly—I position myself so each opponent is forced into a 1v1 duel one after another."
                ),
                SpotlightQa(
                    question = "What advice do you have for aspiring competitive players struggling with recoil control?",
                    answer = "Lower your ADS sensitivity and master gyro tilt. Your fingers should control trigger timing and jiggle, while gyro handles micro-adjustments on the target's helmet."
                )
            )
        ),
        ProSpotlight(
            id = "mortal_soul",
            playerName = "Mortal",
            realName = "Naman Mathur",
            teamName = "S8UL Esports",
            role = "IGL (In-Game Leader)",
            game = "BGMI / Free Fire",
            bio = "Iconic esports pioneer, founder of S8UL, and world-renowned in-game leader celebrated for legendary zone rotations and composure under extreme championship pressure.",
            interviewTitle = "Zone Rotations, Splitting Compounds & Leading a Team Through Heavy Fire",
            interviewSummary = "Mortal shares the tactical decision tree behind surviving dynamic blue zone phases, communication protocols, and recovering from bad drop RNG.",
            keyAdvice = "A great IGL doesn't look for kills; a great IGL creates scenarios where their assaulters can't fail.",
            deviceAndSens = "iPad Pro & iPhone 15 Pro | 4-Finger Claw (Thumb + Index) | Gyroscope: Off / Semi | Tactical HUD setup",
            achievements = listOf("PMIS Champion & PMCO India Winner", "Esports Awards Community Personality of the Year", "Global Invitational Qualifier Leader"),
            rating = 5.0,
            qaList = listOf(
                SpotlightQa(
                    question = "How do you decide whether to take early zone center or play the edge?",
                    answer = "It depends entirely on vehicle count and compound density. If we have 3 four-wheelers and hard cover available, center control wins games. If edge has terrain dips and low threat, edge gatekeeping guarantees placement points."
                ),
                SpotlightQa(
                    question = "How should a team handle communication when chaos breaks out in circle 5?",
                    answer = "One voice rule. Only the IGL calls rotations, while assaulters call exact degrees and enemy HP tags. No emotional venting until the match finishes."
                ),
                SpotlightQa(
                    question = "What separates tier-1 teams from amateur tournament squads?",
                    answer = "Patience and utility discipline. Tier-1 teams don't push until 3 utility grenades or molotovs have disrupted enemy cover."
                )
            )
        ),
        ProSpotlight(
            id = "scout_xspark",
            playerName = "ScoutOP",
            realName = "Tanmay Singh",
            teamName = "Team XSpark",
            role = "Scout & DMR Marksman",
            game = "BGMI / Free Fire",
            bio = "Pioneer of long-range M416+6x sprays and high-tempo scouting. Known for fearless entry angles and international tournament representation.",
            interviewTitle = "Long-Range DMR Dominance, Gyro Sensitivity Tuning & Compound Breaches",
            interviewSummary = "Scout gives a masterclass on reading topography, vehicular breaching, and using DMRs (Mini-14/SLR) like full-auto lasers.",
            keyAdvice = "Positioning without information is suicide. The scout's job is to secure the next hill 30 seconds before the rest of the team rolls in.",
            deviceAndSens = "ROG Phone / iPhone Pro | 4-Finger Claw + High Gyroscope | 6x down to 3x: 230% Gyro, M416 ADS: 52%",
            achievements = listOf("BGIS Champion 2024 (Team XSpark)", "Peacekeeper Elite Championship Participant", "Red Bull Player of the Match"),
            rating = 4.95,
            qaList = listOf(
                SpotlightQa(
                    question = "What is your secret to laser-accurate M416 + 6x scope sprays at 150+ meters?",
                    answer = "Always crouch before pulling the trigger—crouching cuts horizontal bounce by nearly 50%. Then smoothly pull down with gyro while letting your thumb stabilize horizontal drift."
                ),
                SpotlightQa(
                    question = "How do you approach scouting a compound when you suspect a squad is camping inside?",
                    answer = "Never drive straight up to the door. Circle from 80 meters away, listen for footstep audio cues or reloading clicks, and pre-fire common window angles."
                ),
                SpotlightQa(
                    question = "What mindset is required to transition from casual ranked to professional tournaments?",
                    answer = "Stop playing for K/D ratio. In tournaments, survival, positioning, and trade-kills are the only metrics that matter."
                )
            )
        )
    )
}
