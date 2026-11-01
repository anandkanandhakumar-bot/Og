package com.example.ui.tournaments

import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Tournament

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentTacticalDetailModal(
    tournament: Tournament,
    onDismiss: () -> Unit,
    onRegisterClicked: () -> Unit
) {
    val context = LocalContext.current
    var selectedZonePhase by remember { mutableStateOf(1) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.background,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Badge(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                            Text(tournament.game, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontWeight = FontWeight.Bold)
                        }
                        Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                            Text(tournament.status, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = tournament.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurface)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tournament Overview Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).padding(end = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "Prize Pool", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = tournament.prizePool, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).padding(start = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "Match Date", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = tournament.startDate, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tactical Map & Zone Layout Section
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(imageVector = Icons.Default.Map, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                            Text(
                                text = "TACTICAL MAP & ZONE RADAR",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = tournament.mapName.ifBlank { "Battleground Grid" },
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Zone Phase Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        (1..4).forEach { phase ->
                            FilterChip(
                                selected = selectedZonePhase == phase,
                                onClick = { selectedZonePhase = phase },
                                label = { Text("Phase $phase", fontSize = 12.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Tactical Radar Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(230.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF0F141C))
                            .border(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (tournament.mapImageUrl.isNotBlank()) {
                            AsyncImage(
                                model = tournament.mapImageUrl,
                                contentDescription = "Tournament Tactical Map",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        // Esports Tactical Radar Canvas overlay
                        TacticalRadarCanvas(phase = selectedZonePhase)

                        // Tactical HUD watermark
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(10.dp)
                                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "ZONE PHASE $selectedZonePhase • SHRINK INTERVAL 180s",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Map Legend
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        LegendItem(color = Color(0xFF00E5FF), label = "Safe Zone")
                        LegendItem(color = Color(0xFFFF1744), label = "Hot Drops")
                        LegendItem(color = Color(0xFFFF9100), label = "Blue Zone Ring")
                        LegendItem(color = Color.White, label = "Flight Path")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tactical Drop Hotspots & Zone Strategy Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(text = "High Loot Hotspots", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = tournament.tacticalHotspots.ifBlank { "Central Compound, Military Hub, School, Coastal Docks" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        Text(text = "Zone Rotation Strategy", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = tournament.zoneStrategy.ifBlank {
                            "Phase 1: Land safely, secure transport. Phase 2: Scout perimeter and establish ridge vision. Phase 3-4: Gatekeep entry corridors and deploy utility smokes."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val shareText = "🎯 *OG WARRIORS TOURNAMENT TACTICAL BRIEFING* 🎯\n\n" +
                                "Tournament: ${tournament.name}\n" +
                                "Game: ${tournament.game}\n" +
                                "Map: ${tournament.mapName}\n" +
                                "Hotspots: ${tournament.tacticalHotspots}\n" +
                                "Prize Pool: ${tournament.prizePool}\n" +
                                "Schedule: ${tournament.startDate}\n\n" +
                                "Prepare your squad now on OG WARRIORS!"
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "Tactical Map: ${tournament.name}")
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share Tactical Briefing"))
                    },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share Map")
                }

                Button(
                    onClick = {
                        onDismiss()
                        onRegisterClicked()
                    },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(imageVector = Icons.Default.GroupAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Register Now", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun TacticalRadarCanvas(phase: Int) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val center = Offset(width / 2f, height / 2f)

        // Draw Tactical Grid
        val gridCount = 6
        for (i in 1..gridCount) {
            val x = (width / gridCount) * i
            val y = (height / gridCount) * i
            drawLine(
                color = Color.Cyan.copy(alpha = 0.15f),
                start = Offset(x, 0f),
                end = Offset(x, height),
                strokeWidth = 1f
            )
            drawLine(
                color = Color.Cyan.copy(alpha = 0.15f),
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1f
            )
        }

        // Flight Path (White dashed line with arrow)
        val flightStart = Offset(width * 0.1f, height * 0.85f)
        val flightEnd = Offset(width * 0.9f, height * 0.15f)
        drawLine(
            color = Color.White.copy(alpha = 0.7f),
            start = flightStart,
            end = flightEnd,
            strokeWidth = 2.5f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
        )

        // Blue Zone Hazard Outer Ring
        val blueRadius = when (phase) {
            1 -> width * 0.45f
            2 -> width * 0.38f
            3 -> width * 0.30f
            else -> width * 0.22f
        }
        drawCircle(
            color = Color(0xFFFF9100).copy(alpha = 0.6f),
            radius = blueRadius,
            center = center,
            style = Stroke(width = 3.5f)
        )

        // Safe Zone (White/Electric Cyan circle)
        val safeRadius = when (phase) {
            1 -> width * 0.35f
            2 -> width * 0.26f
            3 -> width * 0.18f
            else -> width * 0.11f
        }
        val safeOffset = when (phase) {
            1 -> center
            2 -> Offset(center.x + width * 0.05f, center.y - height * 0.05f)
            3 -> Offset(center.x + width * 0.08f, center.y - height * 0.08f)
            else -> Offset(center.x + width * 0.10f, center.y - height * 0.10f)
        }
        drawCircle(
            color = Color(0xFF00E5FF).copy(alpha = 0.85f),
            radius = safeRadius,
            center = safeOffset,
            style = Stroke(width = 3f)
        )

        // Hot Drop Tactical Markers (Red glowing dots)
        val hotDrops = listOf(
            Offset(width * 0.32f, height * 0.45f),
            Offset(width * 0.58f, height * 0.52f),
            Offset(width * 0.70f, height * 0.35f),
            Offset(width * 0.45f, height * 0.72f)
        )
        hotDrops.forEach { drop ->
            drawCircle(color = Color(0xFFFF1744).copy(alpha = 0.35f), radius = 12f, center = drop)
            drawCircle(color = Color(0xFFFF1744), radius = 6f, center = drop)
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
