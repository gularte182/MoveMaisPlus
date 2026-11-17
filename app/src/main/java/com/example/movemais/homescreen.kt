package com.example.movemais

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val PinkSoft = Color(0xFFFFC2DA)
private val BlueSoft = Color(0xFFB9DDFF)
private val PinkStrong = Color(0xFFD81B78)   // texto branco sobre ele tem bom contraste
private val BlueStrong = Color(0xFF1E6FD9)
private val TextBlack = Color(0xFF111111)
private val TextGray = Color(0xFF444444)
private val Surface = Color(0xFFFFFFFF)
private val Background = Color(0xFFFAF8FF)

private val SoftGradient = Brush.linearGradient(listOf(PinkSoft, BlueSoft))
private val StrongGradient = Brush.horizontalGradient(listOf(PinkStrong, BlueStrong))


enum class SportType(val label: String, val subtitle: String, val icon: ImageVector) {
    RUN("Corrida", "Ritmo e distância", Icons.AutoMirrored.Filled.DirectionsRun),
    WALK("Caminhada", "Passos e bem-estar", Icons.AutoMirrored.Filled.DirectionsWalk),
    GYM("Musculação", "Treinos e séries", Icons.Filled.FitnessCenter),
    BIKE("Ciclismo", "Pedal e percurso", Icons.AutoMirrored.Filled.DirectionsBike)
}

data class RecentActivity(
    val sport: SportType,
    val title: String,
    val date: String,
    val summary: String
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userName: String = "Atleta",
    onStartActivity: (SportType?) -> Unit = {},
    onNavigate: (Int) -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val recents = remember {
        listOf(
            RecentActivity(SportType.RUN, "Corrida matinal", "Hoje, 06:40", "5,2 km • 28 min"),
            RecentActivity(SportType.GYM, "Treino de peito e tríceps", "Ontem", "55 min • 420 kcal"),
            RecentActivity(SportType.BIKE, "Pedal no parque", "Sáb, 16:10", "18,4 km • 1h 02min"),
            RecentActivity(SportType.WALK, "Caminhada leve", "Sex, 18:30", "3,1 km • 40 min")
        )
    }

    Scaffold(
        containerColor = Background,
        bottomBar = {
            NavigationBar(containerColor = Surface) {
                val items = listOf(
                    "Início" to Icons.Filled.Home,
                    "Atividades" to Icons.Filled.BarChart,
                    "Comunidade" to Icons.Filled.Groups,
                    "Perfil" to Icons.Filled.Person
                )
                items.forEachIndexed { index, (label, icon) ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                            onNavigate(index)
                        },
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label, color = TextBlack) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            indicatorColor = PinkStrong,
                            unselectedIconColor = TextGray
                        )
                    )
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { Header(userName) }
            item {
                StartButton(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    onClick = { onStartActivity(null) }
                )
            }
            item { SectionTitle("Escolha seu esporte") }
            item {
                SportGrid(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    onSelect = { onStartActivity(it) }
                )
            }
            item { WeeklyGoalCard(Modifier.padding(horizontal = 20.dp)) }
            item { SectionTitle("Atividades recentes") }
            items(recents) { activity ->
                RecentActivityItem(activity, Modifier.padding(horizontal = 20.dp))
            }
        }
    }
}
@Composable
private fun Header(userName: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(SoftGradient)
            .padding(horizontal = 20.dp)
            .padding(top = 48.dp, bottom = 24.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Olá, $userName 👋", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                    Text("Vamos se mexer hoje?", fontSize = 16.sp, color = TextBlack)
                }
                IconButton(
                    onClick = { /* abrir notificações */ },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Surface, CircleShape)
                ) {
                    Icon(Icons.Filled.Notifications, contentDescription = "Notificações", tint = TextBlack)
                }
            }

            Spacer(Modifier.height(20.dp))

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Resumo da semana", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = TextBlack)
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatItem(Icons.Filled.Route, "26,7 km", "Distância")
                        StatItem(Icons.Filled.Timer, "3h 45min", "Tempo")
                        StatItem(Icons.Filled.LocalFireDepartment, "1.980", "Calorias")
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(icon: ImageVector, value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.semantics(mergeDescendants = true) {
            contentDescription = "$label: $value"
        }
    ) {
        Icon(icon, contentDescription = null, tint = PinkStrong)
        Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextBlack)
        Text(label, fontSize = 14.sp, color = TextGray)
    }
}
@Composable
private fun StartButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 60.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(StrongGradient)
            .clickable(onClick = onClick)
            .semantics { contentDescription = "Iniciar uma nova atividade" },
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(8.dp))
            Text("Iniciar atividade", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}
@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        modifier = Modifier.padding(horizontal = 20.dp),
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = TextBlack
    )
}
@Composable
private fun SportGrid(modifier: Modifier = Modifier, onSelect: (SportType) -> Unit) {
    val sports = SportType.entries
    Column(modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        sports.chunked(2).forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                rowItems.forEach { sport ->
                    SportCard(sport, Modifier.weight(1f)) { onSelect(sport) }
                }
            }
        }
    }
}

@Composable
private fun SportCard(sport: SportType, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = modifier.semantics { contentDescription = "Iniciar ${sport.label}" },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, PinkSoft),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(SoftGradient),
                contentAlignment = Alignment.Center
            ) {
                Icon(sport.icon, contentDescription = null, tint = TextBlack, modifier = Modifier.size(28.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text(sport.label, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextBlack)
            Text(sport.subtitle, fontSize = 14.sp, color = TextGray)
        }
    }
}
@Composable
private fun WeeklyGoalCard(modifier: Modifier = Modifier) {
    val progress = 0.67f
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Meta semanal", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextBlack)
                Text("4 de 6 treinos", fontSize = 16.sp, color = TextBlack)
            }
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFE9E4F0))
                    .semantics { contentDescription = "Progresso da meta: 67 por cento" }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(6.dp))
                        .background(StrongGradient)
                )
            }
            Spacer(Modifier.height(8.dp))
            Text("Faltam 2 treinos para bater sua meta!", fontSize = 14.sp, color = TextGray)
        }
    }
}
@Composable
private fun RecentActivityItem(activity: RecentActivity, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(SoftGradient),
                contentAlignment = Alignment.Center
            ) {
                Icon(activity.sport.icon, contentDescription = activity.sport.label, tint = TextBlack)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(activity.title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = TextBlack)
                Text(activity.summary, fontSize = 14.sp, color = TextGray)
            }
            Text(activity.date, fontSize = 12.sp, color = TextGray)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    MaterialTheme { HomeScreen(userName = "Maria") }
}