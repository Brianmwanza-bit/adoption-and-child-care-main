package com.example.adoption_and_childcare.ui.compose

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.adoption_and_childcare.R
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.adoption_and_childcare.viewmodel.DashboardViewModel

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ModernLandingPagePreview() {
    MaterialTheme {
        ModernLandingPage(onGetStarted = {}, onLogin = {})
    }
}

/**
 * Modern, high-conversion landing page structure.
 * 
 * @param onGetStarted Callback for "Get Started" button.
 * @param onLogin Callback for "Login" button.
 * @param hasLoggedInBefore Whether the user has previously logged in.
 * @param viewModel ViewModel for dashboard data.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernLandingPage(
    onGetStarted: () -> Unit,
    onLogin: () -> Unit,
    hasLoggedInBefore: Boolean = false,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    var isVisible by remember { mutableStateOf(false) }
    
    val metrics by viewModel.metrics.collectAsState()
    val settings by viewModel.settings.collectAsState()

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Scaffold(
        bottomBar = {
            BottomStickyCTA(onGetStarted, hasLoggedInBefore)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .background(MaterialTheme.colorScheme.background)
        ) {
            HeroSection(isVisible, onLogin, hasLoggedInBefore)

            ImpactStatsSection(metrics)

            FeaturesSection(settings.filter { it.category == "Features" })

            QuoteSection(settings.filter { it.category == "Testimonials" })

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun HeroSection(isVisible: Boolean, onLogin: () -> Unit, hasLoggedInBefore: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(450.dp)
            .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.White.copy(alpha = 0.05f),
                radius = 400f,
                center = androidx.compose.ui.geometry.Offset(size.width * 0.9f, 0f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { -50 })
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    modifier = Modifier.size(100.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.app_logo),
                        contentDescription = stringResource(R.string.app_name),
                        modifier = Modifier.size(100.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = if (hasLoggedInBefore) stringResource(R.string.landing_welcome_back) else stringResource(R.string.landing_headline),
                style = MaterialTheme.typography.displaySmall,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                lineHeight = 42.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (hasLoggedInBefore) stringResource(R.string.landing_signin_desc) else stringResource(R.string.landing_hero_desc),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            if (!hasLoggedInBefore) {
                Spacer(modifier = Modifier.height(32.dp))

                TextButton(onClick = onLogin) {
                    Text(
                        stringResource(R.string.landing_already_account),
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                    )
                }
            }
        }
    }
}

@Composable
fun ImpactStatsSection(metrics: List<com.example.adoption_and_childcare.data.db.entities.DashboardMetricEntity>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .offset(y = (-30).dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (metrics.isEmpty()) {
            StatCard(stringResource(R.string.landing_stat_children), stringResource(R.string.landing_stat_children_label), Modifier.weight(1f))
            StatCard(stringResource(R.string.landing_stat_families), stringResource(R.string.landing_stat_families_label), Modifier.weight(1f))
            StatCard(stringResource(R.string.landing_stat_success), stringResource(R.string.landing_stat_success_label), Modifier.weight(1f))
        } else {
            metrics.take(3).forEach { metric ->
                val displayValue = when (metric.metricName) {
                    "success_rate" -> "${metric.metricValue.toInt()}%"
                    else -> String.format("%,d+", metric.metricValue.toInt())
                }
                StatCard(displayValue, metric.metricLabel ?: "", Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun StatCard(value: String, label: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(value, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 18.sp)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun FeaturesSection(featureSettings: List<com.example.adoption_and_childcare.data.db.entities.SystemSettingEntity>) {
    val features = if (featureSettings.isEmpty()) {
        listOf(
            FeatureData(Icons.Default.Speed, stringResource(R.string.landing_feature_realtime_title), stringResource(R.string.landing_feature_realtime_desc)),
            FeatureData(Icons.Default.Lock, stringResource(R.string.landing_feature_secure_title), stringResource(R.string.landing_feature_secure_desc)),
            FeatureData(Icons.Default.Groups, stringResource(R.string.landing_feature_matching_title), stringResource(R.string.landing_feature_matching_desc))
        )
    } else {
        featureSettings.map { setting ->
            val (icon, title) = when (setting.settingKey) {
                "feature_real_time" -> Icons.Default.Speed to stringResource(R.string.landing_feature_realtime_title)
                "feature_secure_docs" -> Icons.Default.Lock to stringResource(R.string.landing_feature_secure_title)
                "feature_family_match" -> Icons.Default.Groups to stringResource(R.string.landing_feature_matching_title)
                else -> Icons.Default.Star to setting.settingKey
            }
            FeatureData(icon, title, setting.settingValue ?: "")
        }
    }

    Column(modifier = Modifier.padding(vertical = 24.dp)) {
        Text(
            text = stringResource(R.string.landing_features_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(features) { feature ->
                GlassmorphicFeatureCard(feature)
            }
        }
    }
}

data class FeatureData(val icon: ImageVector, val title: String, val desc: String)

@Composable
fun GlassmorphicFeatureCard(feature: FeatureData) {
    Card(
        modifier = Modifier
            .width(260.dp)
            .height(180.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    feature.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Column {
                Text(feature.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(feature.desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun QuoteSection(testimonialSettings: List<com.example.adoption_and_childcare.data.db.entities.SystemSettingEntity>) {
    val quote = testimonialSettings.find { it.settingKey == "testimonial_quote" }?.settingValue
        ?: stringResource(R.string.landing_testimonial_quote)
    val author = testimonialSettings.find { it.settingKey == "testimonial_author" }?.settingValue
        ?: stringResource(R.string.landing_testimonial_author)

    Card(
        modifier = Modifier.padding(24.dp).fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.FormatQuote, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(48.dp))
            Text(
                text = "\"$quote\"",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("— $author", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun BottomStickyCTA(onGetStarted: () -> Unit, hasLoggedInBefore: Boolean) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 16.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp).navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(if (hasLoggedInBefore) stringResource(R.string.landing_access_portal) else stringResource(R.string.landing_ready_title), fontWeight = FontWeight.Bold)
                Text(if (hasLoggedInBefore) stringResource(R.string.landing_manage_cases) else stringResource(R.string.landing_ready_desc), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Button(
                onClick = onGetStarted,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(if (hasLoggedInBefore) stringResource(R.string.landing_go_dashboard) else stringResource(R.string.landing_get_started), fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    if (hasLoggedInBefore) Icons.Default.Dashboard else Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
