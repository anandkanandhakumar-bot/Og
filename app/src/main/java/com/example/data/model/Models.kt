package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class Post(
    @DocumentId val id: String = "",
    val title: String = "",
    val content: String = "",
    val imageUrl: String = "",
    val author: String = "",
    val category: String = "News",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Tip(
    @DocumentId val id: String = "",
    val title: String = "",
    val category: String = "Headshot",
    val content: String = "",
    val videoUrl: String = "",
    val difficulty: String = "Intermediate",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Tournament(
    @DocumentId val id: String = "",
    val name: String = "",
    val game: String = "BGMI / Free Fire",
    val prizePool: String = "$1,000",
    val startDate: String = "Nov 15, 2026",
    val rules: String = "Standard esports rules apply. No emulators.",
    val status: String = "Upcoming",
    val mapName: String = "Erangel Tactical Grid",
    val mapImageUrl: String = "",
    val tacticalHotspots: String = "Pochinki, Military Base, School, Georgopol",
    val zoneStrategy: String = "Phase 1: Secure vehicles on perimeter. Phase 2-3: Early central rotation and hold high ground. Phase 4: Choke-point gatekeeping.",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Registration(
    @DocumentId val id: String = "",
    val tournamentId: String = "",
    val userId: String = "",
    val teamName: String = "",
    val members: String = "",
    val status: String = "Confirmed",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class EditingTutorial(
    @DocumentId val id: String = "",
    val title: String = "",
    val software: String = "CapCut",
    val category: String = "Montage",
    val stepsUrl: String = "",
    val videoUrl: String = "",
    val description: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Comment(
    @DocumentId val id: String = "",
    val targetId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userPhoto: String = "",
    val text: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FAQ(
    @DocumentId val id: String = "",
    val question: String = "",
    val answer: String = "",
    val category: String = "Tournament",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Bookmark(
    @DocumentId val id: String = "",
    val itemId: String = "",
    val itemType: String = "",
    val title: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class TutorialProgress(
    @DocumentId val id: String = "",
    val tutorialId: String = "",
    val tutorialTitle: String = "",
    val progressPercent: Int = 0,
    val isCompleted: Boolean = false,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Achievement(
    @DocumentId val id: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "Tournament",
    @ServerTimestamp val unlockedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Team(
    @DocumentId val id: String = "",
    val name: String = "",
    val logoUrl: String = "",
    val captainUid: String = "",
    val captainName: String = "",
    val members: List<String> = emptyList(),
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class ChatMessage(
    @DocumentId val id: String = "",
    val chatId: String = "global_esports",
    val senderId: String = "",
    val senderName: String = "",
    val text: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val role: String = "Member",
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FeedbackReport(
    @DocumentId val id: String = "",
    val userId: String = "",
    val userEmail: String = "",
    val userName: String = "",
    val subject: String = "",
    val message: String = "",
    val status: String = "Pending",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class SpotlightQa(
    val question: String = "",
    val answer: String = ""
)

@IgnoreExtraProperties
data class ProSpotlight(
    @DocumentId val id: String = "",
    val playerName: String = "",
    val realName: String = "",
    val teamName: String = "",
    val role: String = "Assaulter",
    val game: String = "BGMI / Free Fire",
    val bio: String = "",
    val interviewTitle: String = "",
    val interviewSummary: String = "",
    val keyAdvice: String = "",
    val deviceAndSens: String = "",
    val achievements: List<String> = emptyList(),
    val qaList: List<SpotlightQa> = emptyList(),
    val avatarUrl: String = "",
    val rating: Double = 5.0,
    @ServerTimestamp val createdAt: Timestamp? = null
)

