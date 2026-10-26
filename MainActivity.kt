package com.neonaim.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

private val Bg = Color(0xFF050817)
private val Panel = Color(0xFF0B1530)
private val Cyan = Color(0xFF00E5FF)
private val Pink = Color(0xFFE040FB)
private val Muted = Color(0xFF9AB0D0)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { NeonAimApp() }
    }
}

@Composable
fun NeonAimApp() {
    var page by remember { mutableStateOf("Início") }
    var sensitivity by remember { mutableFloatStateOf(70f) }
    var targetSize by remember { mutableFloatStateOf(48f) }
    var sound by remember { mutableStateOf(true) }
    var showCrosshair by remember { mutableStateOf(true) }
    var hits by remember { mutableIntStateOf(0) }
    var misses by remember { mutableIntStateOf(0) }
    var targetX by remember { mutableFloatStateOf(.5f) }
    var targetY by remember { mutableFloatStateOf(.45f) }
    val pages = listOf("Início", "Treino", "Ajustes", "Informações")

    MaterialTheme(colorScheme = darkColorScheme(
        primary = Cyan, secondary = Pink, background = Bg, surface = Panel
    )) {
        Column(Modifier.fillMaxSize().background(Bg)) {
            Row(
                Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(Color(0xFF081A38), Color(0xFF16082B))))
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.GpsFixed, null, tint = Cyan, modifier = Modifier.size(30.dp))
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("NEON AIM", color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.Black)
                    Text("CENTRO DE TREINO • ANDROID", color = Cyan, fontSize = 9.sp, letterSpacing = 2.sp)
                }
                Text("● ONLINE", color = Color(0xFF63FFB1), fontSize = 10.sp)
            }
            Row(Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                pages.forEach { p ->
                    val selected = page == p
                    Text(p, modifier = Modifier.weight(1f).background(if (selected) Color(0xFF10334B) else Panel, RoundedCornerShape(12.dp))
                        .clickable { page = p }.padding(vertical = 12.dp), color = if (selected) Cyan else Muted,
                        fontSize = 10.sp, fontWeight = FontWeight.Bold, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            }
            when (page) {
                "Início" -> HomePage(onTrain = { page = "Treino" }, hits = hits, misses = misses)
                "Treino" -> TrainingPage(
                    sensitivity, { sensitivity = it }, targetSize, { targetSize = it },
                    showCrosshair, { showCrosshair = it }, targetX, targetY,
                    { x, y -> targetX = x; targetY = y },
                    onHit = { hits++ }, onMiss = { misses++ }
                )
                "Ajustes" -> SettingsPage(sensitivity, { sensitivity = it }, targetSize, { targetSize = it }, sound, { sound = it }, showCrosshair, { showCrosshair = it })
                else -> InfoPage(hits, misses)
            }
        }
    }
}

@Composable
fun NeonCard(content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 7.dp)
        .background(Panel, RoundedCornerShape(18.dp))
        .padding(16.dp), content = content)
}

@Composable
fun HomePage(onTrain: () -> Unit, hits: Int, misses: Int) {
    Column(Modifier.fillMaxSize()) {
        NeonCard {
            Text("MIRA EM FOCO", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
            Text("Treine reflexos e precisão no seu próprio espaço.", color = Muted, fontSize = 13.sp)
            Spacer(Modifier.height(14.dp))
            Box(Modifier.fillMaxWidth().height(150.dp).background(Color(0xFF050B1B), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
                Canvas(Modifier.fillMaxSize()) {
                    val c = Offset(size.width / 2, size.height / 2)
                    drawCircle(Cyan.copy(alpha = .16f), 54f, c)
                    drawCircle(Cyan, 2f, c)
                    drawCircle(Cyan, 42f, c, style = androidx.compose.ui.graphics.drawscope.Stroke(2f))
                    drawCircle(Pink, 25f, c, style = androidx.compose.ui.graphics.drawscope.Stroke(2f))
                    drawLine(Cyan, Offset(c.x - 68, c.y), Offset(c.x + 68, c.y), 1.5f)
                    drawLine(Cyan, Offset(c.x, c.y - 68), Offset(c.x, c.y + 68), 1.5f)
                }
            }
            Spacer(Modifier.height(16.dp))
            Button(onClick = onTrain, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF073B56))) {
                Icon(Icons.Default.PlayArrow, null, tint = Cyan)
                Spacer(Modifier.width(8.dp))
                Text("INICIAR TREINO", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
        Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard("ACERTOS", hits.toString(), Cyan, Modifier.weight(1f))
            StatCard("ERROS", misses.toString(), Pink, Modifier.weight(1f))
            StatCard("PRECISÃO", if (hits + misses == 0) "—" else "${hits * 100 / (hits + misses)}%", Color(0xFF63FFB1), Modifier.weight(1f))
        }
        NeonCard {
            Text("RECURSOS", color = Cyan, fontWeight = FontWeight.Bold)
            FeatureRow(Icons.Default.GpsFixed, "Alvos interativos", "Pratique sua velocidade de reação")
            FeatureRow(Icons.Default.Tune, "Sensibilidade", "Ajuste os controles para seu estilo")
            FeatureRow(Icons.Default.Visibility, "Mira holográfica", "Personalize a aparência da mira")
        }
    }
}

@Composable
fun StatCard(label: String, value: String, color: Color, modifier: Modifier) {
    Column(modifier.background(Panel, RoundedCornerShape(14.dp)).padding(12.dp)) {
        Text(label, color = Muted, fontSize = 9.sp)
        Text(value, color = color, fontSize = 22.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
fun FeatureRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, desc: String) {
    Row(Modifier.fillMaxWidth().padding(top = 13.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Pink, modifier = Modifier.size(25.dp))
        Spacer(Modifier.width(12.dp))
        Column {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(desc, color = Muted, fontSize = 11.sp)
        }
    }
}

@Composable
fun TrainingPage(
    sensitivity: Float, onSensitivity: (Float) -> Unit,
    targetSize: Float, onTargetSize: (Float) -> Unit,
    crosshair: Boolean, onCrosshair: (Boolean) -> Unit,
    targetX: Float, targetY: Float, moveTarget: (Float, Float) -> Unit,
    onHit: () -> Unit, onMiss: () -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        NeonCard {
            Text("CAMPO DE TREINO", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Black)
            Text("Toque no alvo para marcar um acerto.", color = Muted, fontSize = 12.sp)
        }
        BoxWithConstraints(Modifier.fillMaxWidth().weight(1f).padding(horizontal = 14.dp).background(Color(0xFF071126), RoundedCornerShape(18.dp))) {
            val w = constraints.maxWidth.toFloat().coerceAtLeast(1f)
            val h = constraints.maxHeight.toFloat().coerceAtLeast(1f)
            Box(Modifier.offset((maxWidth * targetX) - (targetSize.dp / 2), (maxHeight * targetY) - (targetSize.dp / 2))
                .size(targetSize.dp).background(Color(0x3320DFFF), RoundedCornerShape(50)).clickable {
                    onHit()
                    moveTarget(Random.nextFloat() * .78f + .11f, Random.nextFloat() * .72f + .14f)
                }, contentAlignment = Alignment.Center) {
                Canvas(Modifier.fillMaxSize()) {
                    drawCircle(Cyan, style = androidx.compose.ui.graphics.drawscope.Stroke(2.dp.toPx()))
                    drawCircle(Pink, radius = size.minDimension * .23f, style = androidx.compose.ui.graphics.drawscope.Stroke(2.dp.toPx()))
                    drawCircle(Cyan, radius = 3.dp.toPx())
                }
            }
            if (crosshair) Canvas(Modifier.fillMaxSize()) {
                val c = Offset(size.width / 2, size.height / 2)
                drawLine(Cyan.copy(alpha = .35f), Offset(c.x - 14, c.y), Offset(c.x + 14, c.y), 1.dp.toPx())
                drawLine(Cyan.copy(alpha = .35f), Offset(c.x, c.y - 14), Offset(c.x, c.y + 14), 1.dp.toPx())
            }
        }
        NeonCard {
            Text("SENSIBILIDADE  ${sensitivity.toInt()}%", color = Cyan, fontWeight = FontWeight.Bold)
            Slider(value = sensitivity, onValueChange = onSensitivity, valueRange = 0f..100f)
            Text("TAMANHO DO ALVO  ${targetSize.toInt()} dp", color = Cyan, fontWeight = FontWeight.Bold)
            Slider(value = targetSize, onValueChange = onTargetSize, valueRange = 28f..80f)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Mira central", color = Color.White, modifier = Modifier.weight(1f))
                Switch(checked = crosshair, onCheckedChange = onCrosshair)
            }
        }
    }
}

@Composable
fun SettingsPage(sensitivity: Float, onSensitivity: (Float) -> Unit, targetSize: Float, onTargetSize: (Float) -> Unit,
                 sound: Boolean, onSound: (Boolean) -> Unit, crosshair: Boolean, onCrosshair: (Boolean) -> Unit) {
    Column {
        NeonCard {
            Text("AJUSTES DO SISTEMA", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
            Text("Personalize seu ambiente de treino.", color = Muted, fontSize = 12.sp)
        }
        NeonCard {
            Text("Sensibilidade: ${sensitivity.toInt()}%", color = Cyan, fontWeight = FontWeight.Bold)
            Slider(value = sensitivity, onValueChange = onSensitivity, valueRange = 0f..100f)
            Text("Tamanho do alvo: ${targetSize.toInt()} dp", color = Cyan, fontWeight = FontWeight.Bold)
            Slider(value = targetSize, onValueChange = onTargetSize, valueRange = 28f..80f)
            SettingSwitch("Mira central", crosshair, onCrosshair)
            SettingSwitch("Efeitos sonoros (visual)", sound, onSound)
        }
        NeonCard {
            Text("SEGURANÇA", color = Color(0xFF63FFB1), fontWeight = FontWeight.Bold)
            Text("Este app funciona como simulador independente. Não lê nem altera outros jogos.", color = Muted, fontSize = 12.sp)
        }
    }
}

@Composable
fun SettingSwitch(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = Color.White, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onChange)
    }
}

@Composable
fun InfoPage(hits: Int, misses: Int) {
    Column {
        NeonCard {
            Text("INFORMAÇÕES", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
            Text("Neon Aim • versão 1.0.0", color = Cyan)
            Spacer(Modifier.height(12.dp))
            Text("Aplicativo demonstrativo de treino de mira para Android, com interface neon, alvo interativo e ajustes locais.", color = Muted)
        }
        NeonCard {
            Text("SESSÃO ATUAL", color = Cyan, fontWeight = FontWeight.Bold)
            Text("Acertos: $hits", color = Color.White)
            Text("Erros: $misses", color = Color.White)
            Text("Precisão: ${if (hits + misses == 0) "—" else "${hits * 100 / (hits + misses)}%"}", color = Color.White)
        }
        NeonCard {
            Text("NOTA", color = Pink, fontWeight = FontWeight.Bold)
            Text("A mira e as estatísticas funcionam dentro deste aplicativo. Não há integração com Free Fire, ESP real, aimbot, anti-ban ou bypass.", color = Muted, fontSize = 12.sp)
        }
    }
}
