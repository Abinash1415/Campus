package fr.esgi.campus

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.esgi.campus.ui.theme.CampusTheme
import kotlinx.coroutines.launch

private const val TAG = "Campus"

/*
* 19/11/2026 — Démarrage de l’application Compose
* */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { CampusApp() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// 19/11/2026 — Thème et actions globales du catalogue.
fun CampusApp() {
    var darkTheme by rememberSaveable { mutableStateOf(false) }
    var selectedCategory by rememberSaveable { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    CampusTheme(darkTheme = darkTheme, dynamicColor = false) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        // Bonus : toucher Campus réinitialise le filtre et revient à toute la liste.
                        TextButton(onClick = {
                            selectedCategory = null
                        }) {
                            Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineSmall)
                        }
                    },
                    actions = {
                        Text(stringResource(R.string.dark_theme_label), style = MaterialTheme.typography.labelMedium)
                        // Bonus : interrupteur de thème clair/sombre dans l’application.
                        Switch(
                            checked = darkTheme,
                            onCheckedChange = { darkTheme = it },
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            EventListScreen(
                events = sampleEvents,
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it },
                onEventClick = { event ->
                    Log.d(TAG, "Carte ouverte : ${event.id} - ${event.title}")
                },
                onBook = { event ->
                    Log.d(TAG, "Réservation demandée : ${event.id} - ${event.title}")
                    // Bonus : confirmation visuelle, sans enregistrer de vraie réservation.
                    // TODO: remplacer la confirmation de démonstration par une réservation persistée.
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            "${event.title} : demande enregistrée (démo)"
                        )
                    }
                },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
// 19/11/2026 — Carte d’événement et actions transmises au parent.
fun EventCard(
    event: Event,
    onClick: () -> Unit,
    onBook: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                EventHeader(
                    title = event.title,
                    category = event.category,
                    location = event.location,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = onClick) { Text(stringResource(R.string.event_details)) }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = pluralStringResource(R.plurals.available_seats, event.capacity, event.capacity),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                Button(onClick = onBook, enabled = event.capacity > 0) {
                    Text(stringResource(R.string.book_event))
                }
            }
        }
    }
}

@Composable
// 19/11/2026 — Titre, catégorie et lieu nullable.
fun EventHeader(title: String, category: String, location: String?, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(title, style = MaterialTheme.typography.titleMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
        Text(category, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        Text(
            text = location ?: stringResource(R.string.location_to_confirm),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
// 19/11/2026 — Filtres de catégories et liste défilante.
fun EventListScreen(
    events: List<Event>,
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit,
    onEventClick: (Event) -> Unit,
    onBook: (Event) -> Unit,
    modifier: Modifier = Modifier
) {
    // Bonus : filtrage interactif des événements par catégorie.
    val categories = events.map(Event::category).distinct()
    val visibleEvents = if (selectedCategory == null) events else events.filter { it.category == selectedCategory }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(key = "category_header") {
            Column {
                Text(stringResource(R.string.categories_heading), style = MaterialTheme.typography.titleMedium)
                LazyRow(
                    contentPadding = PaddingValues(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item(key = "all_categories") {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { onCategorySelected(null) },
                            label = { Text(stringResource(R.string.all_categories)) }
                        )
                    }
                    items(categories, key = { it }) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = {
                                onCategorySelected(if (selectedCategory == category) null else category)
                            },
                            label = { Text(category) }
                        )
                    }
                }
            }
        }
        items(visibleEvents, key = { it.id }) { event ->
            EventCard(
                event = event,
                onClick = { onEventClick(event) },
                onBook = { onBook(event) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
// 19/11/2026 — Détail autonome avec description défilante.
fun EventDetailScreen(
    event: Event,
    onBook: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.event_banner),
            contentDescription = stringResource(R.string.event_image_description, event.title),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().height(210.dp)
        )
        Text(event.title, style = MaterialTheme.typography.headlineMedium)
        Text(event.category, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        Text(event.location ?: stringResource(R.string.location_to_confirm), style = MaterialTheme.typography.bodyLarge)
        Text(event.description, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(pluralStringResource(R.plurals.available_seats, event.capacity, event.capacity), style = MaterialTheme.typography.bodyMedium)
        Button(onClick = onBook, enabled = event.capacity > 0, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.book_event))
        }
    }
}

// 19/11/2026 — Previews des cas principaux en clair et en sombre.
@Preview(showBackground = true, name = "Carte complète")
@Composable
private fun FullEventCardPreview() {
    CampusTheme {
        EventCard(sampleEvents.last(), onClick = {}, onBook = {}, modifier = Modifier.padding(16.dp))
    }
}

@Preview(showBackground = true, name = "En-tête sans lieu")
@Composable
private fun EventHeaderPreview() {
    CampusTheme {
        EventHeader("Rencontre des associations", "Campus", null, Modifier.padding(16.dp))
    }
}

@Preview(showBackground = true, name = "Catalogue clair")
@Composable
private fun EventListLightPreview() {
    CampusTheme(darkTheme = false) {
        EventListScreen(sampleEvents, null, {}, {}, {}, Modifier)
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Catalogue sombre")
@Composable
private fun EventListDarkPreview() {
    CampusTheme(darkTheme = true) {
        EventListScreen(sampleEvents, null, {}, {}, {}, Modifier)
    }
}

@Preview(showBackground = true, name = "Détail clair")
@Composable
private fun EventDetailLightPreview() {
    CampusTheme(darkTheme = false) { EventDetailScreen(sampleEvents.first(), {}) }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Détail sombre")
@Composable
private fun EventDetailDarkPreview() {
    CampusTheme(darkTheme = true) { EventDetailScreen(sampleEvents.first(), {}) }
}
