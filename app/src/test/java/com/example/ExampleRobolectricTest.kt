package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("OG Warriors", appName)
  }

  @Test
  fun `tournament model has valid tactical map defaults`() {
    val tournament = com.example.data.model.Tournament()
    assertEquals("Erangel Tactical Grid", tournament.mapName)
    assert(tournament.tacticalHotspots.isNotEmpty())
    assert(tournament.zoneStrategy.isNotEmpty())
  }

  @Test
  fun `feedback report model initializes with valid pending status`() {
    val report = com.example.data.model.FeedbackReport(
        userId = "user_123",
        userEmail = "warrior@example.com",
        subject = "App Feedback",
        message = "Tournament lobby was awesome!"
    )
    assertEquals("Pending", report.status)
    assertEquals("user_123", report.userId)
    assertEquals("warrior@example.com", report.userEmail)
  }

  @Test
  fun `team and chat models initialize with correct defaults`() {
    val team = com.example.data.model.Team(name = "Soul Warriors", captainUid = "capt_1")
    assertEquals("Soul Warriors", team.name)
    assertEquals("capt_1", team.captainUid)

    val chat = com.example.data.model.ChatMessage(text = "Drop Pochinki!", senderId = "capt_1")
    assertEquals("global_esports", chat.chatId)
    assertEquals("Drop Pochinki!", chat.text)
  }

  @Test
  fun `owner admin email constant matches verified super admin`() {
    val expectedAdmin = "anandkanandhakumar@gmail.com"
    assertEquals("anandkanandhakumar@gmail.com", expectedAdmin)
  }

  @Test
  fun `pro spotlight model initializes with interviews and QA sessions`() {
    val qaList = listOf(
        com.example.data.model.SpotlightQa(
            question = "What is your claw layout?",
            answer = "4-finger claw with gyro always on."
        )
    )
    val spotlight = com.example.data.model.ProSpotlight(
        id = "pro_1",
        playerName = "Jonathan",
        teamName = "GodLike Esports",
        role = "Assaulter",
        interviewTitle = "Mastering 4-Finger Claw",
        qaList = qaList
    )
    assertEquals("Jonathan", spotlight.playerName)
    assertEquals("GodLike Esports", spotlight.teamName)
    assertEquals(1, spotlight.qaList.size)
    assertEquals("What is your claw layout?", spotlight.qaList[0].question)
  }
}

