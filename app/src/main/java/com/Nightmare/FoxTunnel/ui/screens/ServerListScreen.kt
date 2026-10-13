package com.Nightmare.FoxTunnel.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Nightmare.FoxTunnel.data.FavoritesStore
import com.Nightmare.FoxTunnel.data.ProxyStateStore
import com.Nightmare.FoxTunnel.data.RECOMMENDED_COUNTRY_CODE
import com.Nightmare.FoxTunnel.data.ServerListClient
import com.Nightmare.FoxTunnel.data.model.ProxyCandidate
import com.Nightmare.FoxTunnel.data.model.VpnCountry
import com.Nightmare.FoxTunnel.vpn.PingUtil

private sealed class LocationRow {
    data class RecommendedRow(val country: VpnCountry) : LocationRow()
    data class SectionHeader(val title: String) : LocationRow()
    data class FavoriteRow(val country: VpnCountry, val cityIndex: Int) : LocationRow()
    data class CountryHeader(val country: VpnCountry) : LocationRow()
    data class CityRow(val country: VpnCountry, val cityIndex: Int) : LocationRow()
}

private enum class PingState { PENDING, DONE, FAILED }

@Composable
fun ServerListScreen(
    serverListClient: ServerListClient,
    proxyStateStore: ProxyStateStore,
    onServerSelected: () -> Unit,
    onBack: () -> Unit,
) {
    var countries by remember { mutableStateOf<List<VpnCountry>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val selectedState by proxyStateStore.selectedProxyFlow.collectAsState()

    val context = LocalContext.current
    val favoritesStore = remember { FavoritesStore(context) }
    var favorites by remember { mutableStateOf(favoritesStore.load()) }

    val pingResults = remember { mutableStateMapOf<String, Int>() }
    val pingStates = remember { mutableStateMapOf<String, PingState>() }

    LaunchedEffect(Unit) {
        runCatching { serverListClient.fetchCountries() }
            .onSuccess { countries = it }
            .onFailure { errorMessage = it.message ?: "Failed to load server list" }
        isLoading = false
    }

    val ordered = remember(countries) {
        countries.sortedWith(compareBy({ it.code != RECOMMENDED_COUNTRY_CODE }, { it.name }))
    }

    val rows = remember(ordered, favorites) {
        buildList<LocationRow> {
            ordered.firstOrNull { it.code == RECOMMENDED_COUNTRY_CODE }
                ?.let { add(LocationRow.RecommendedRow(it)) }

            val favoriteRows = buildList<LocationRow> {
                for (country in ordered) {
                    if (country.code == RECOMMENDED_COUNTRY_CODE) continue
                    country.cities.forEachIndexed { index, city ->
                        if (FavoritesStore.key(country.code, city.code) in favorites) {
                            add(LocationRow.FavoriteRow(country, index))
                        }
                    }
                }
            }
            if (favoriteRows.isNotEmpty()) {
                add(LocationRow.SectionHeader("Favorites"))
                addAll(favoriteRows)
                add(LocationRow.SectionHeader("All locations"))
            }

            for (country in ordered) {
                if (country.code == RECOMMENDED_COUNTRY_CODE) continue
                add(LocationRow.CountryHeader(country))
                country.cities.indices.forEach { index -> add(LocationRow.CityRow(country, index)) }
            }
        }
    }

    fun selectCity(country: VpnCountry, cityCode: String) {
        val candidates = ServerListClient.candidatesForCity(countries, country.code, cityCode)
        val chosen = candidates.randomOrNull()
        if (chosen != null) {
            proxyStateStore.save(chosen)
            onServerSelected()
        } else {
            errorMessage = "No available servers in $cityCode"
        }
    }

    fun toggleFavorite(country: VpnCountry, cityCode: String) {
        val key = FavoritesStore.key(country.code, cityCode)
        favorites = if (key in favorites) favorites - key else favorites + key
        favoritesStore.save(favorites)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Choose location") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                errorMessage != null && countries.isEmpty() -> Text(
                    errorMessage.orEmpty(),
                    modifier = Modifier.align(Alignment.Center).padding(24.dp),
                )
                else -> LazyColumn {
                    items(
                        rows,
                        key = { row ->
                            when (row) {
                                is LocationRow.RecommendedRow -> "recommended:${row.country.code}"
                                is LocationRow.SectionHeader -> "section:${row.title}"
                                is LocationRow.FavoriteRow ->
                                    "fav:${row.country.code}:${row.country.cities[row.cityIndex].code}"
                                is LocationRow.CountryHeader -> "country:${row.country.code}"
                                is LocationRow.CityRow ->
                                    "city:${row.country.code}:${row.country.cities[row.cityIndex].code}"
                            }
                        },
                    ) { row ->

                        val selected = selectedState
                        when (row) {
                            is LocationRow.RecommendedRow -> Column {
                            ListItem(
                                headlineContent = { Text("Recommended", fontWeight = FontWeight.SemiBold) },
                                supportingContent = { Text("Automatically chosen for the best performance") },
                                trailingContent = {
                                    if (row.country.code == selected?.countryCode) {
                                        Icon(Icons.Filled.CheckCircle, contentDescription = "Selected")
                                    }
                                },
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .clickable {
                                        val candidates = ServerListClient.candidatesForCountry(countries, row.country.code)
                                        val chosen = candidates.randomOrNull()
                                        if (chosen != null) {
                                            proxyStateStore.save(chosen)
                                            onServerSelected()
                                        } else {
                                            errorMessage = "No available servers for ${row.country.name}"
                                        }
                                    },
                            )
                            HorizontalDivider(Modifier.padding(vertical = 8.dp))
                            }
                            is LocationRow.SectionHeader -> Text(
                                row.title,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(start = 24.dp, end = 16.dp, top = 16.dp, bottom = 4.dp),
                            )
                            is LocationRow.FavoriteRow -> CityItem(
                                country = row.country,
                                cityIndex = row.cityIndex,
                                inFavorites = true,
                                selected = selected,
                                isFavorite = true,
                                pingResults = pingResults,
                                pingStates = pingStates,
                                onSelect = { selectCity(row.country, row.country.cities[row.cityIndex].code) },
                                onToggleFavorite = { toggleFavorite(row.country, row.country.cities[row.cityIndex].code) },
                            )
                            is LocationRow.CountryHeader -> {
                                val flag = flagEmoji(row.country.code)
                                ListItem(
                                    headlineContent = { Text(row.country.name, fontWeight = FontWeight.SemiBold) },
                                    leadingContent = if (flag.isNotEmpty()) {
                                        { Text(flag, fontSize = 26.sp) }
                                    } else {
                                        null
                                    },
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                )
                            }
                            is LocationRow.CityRow -> {
                                val city = row.country.cities[row.cityIndex]
                                CityItem(
                                    country = row.country,
                                    cityIndex = row.cityIndex,
                                    inFavorites = false,
                                    selected = selected,
                                    isFavorite = FavoritesStore.key(row.country.code, city.code) in favorites,
                                    pingResults = pingResults,
                                    pingStates = pingStates,
                                    onSelect = { selectCity(row.country, city.code) },
                                    onToggleFavorite = { toggleFavorite(row.country, city.code) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CityItem(
    country: VpnCountry,
    cityIndex: Int,
    inFavorites: Boolean,
    selected: ProxyCandidate?,
    isFavorite: Boolean,
    pingResults: MutableMap<String, Int>,
    pingStates: MutableMap<String, PingState>,
    onSelect: () -> Unit,
    onToggleFavorite: () -> Unit,
) {
    val city = country.cities[cityIndex]
    val serverCount = city.servers.size

    val pingKey = "${country.code}:${city.code}"
    val pingMs = pingResults[pingKey]
    val pingState = pingStates[pingKey] ?: PingState.PENDING

    LaunchedEffect(pingKey) {
        if (pingStates.containsKey(pingKey)) return@LaunchedEffect
        val target = city.servers.firstOrNull { !it.quarantined }
            ?.let { ServerListClient.defaultConnectTarget(it) }
        if (target == null) {
            pingStates[pingKey] = PingState.FAILED
            return@LaunchedEffect
        }
        val result = PingUtil.measureTcpLatencyMs(target.first, target.second)
        if (result != null) {
            pingResults[pingKey] = result
            pingStates[pingKey] = PingState.DONE
        } else {
            pingStates[pingKey] = PingState.FAILED
        }
    }

    val flag = if (inFavorites) flagEmoji(country.code) else ""

    Column {
    ListItem(
        headlineContent = { Text(city.code) },
        supportingContent = {
            val primaryHostname = city.servers.firstOrNull()?.hostname
            val serverLabel = when {
                primaryHostname == null -> "No servers available"
                serverCount > 1 -> "$primaryHostname (+${serverCount - 1} more)"
                else -> primaryHostname
            }
            val pingLabel = when {
                pingMs != null -> "$pingMs ms"
                pingState == PingState.FAILED -> "ping unavailable"
                else -> "pinging\u2026"
            }
            Text(
                if (inFavorites) "$pingLabel \u2022 ${country.name}" else "$pingLabel \u2022 $serverLabel",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        leadingContent = if (flag.isNotEmpty()) {
            { Text(flag, fontSize = 26.sp) }
        } else {
            null
        },
        trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (country.code == selected?.countryCode && city.code == selected?.cityCode) {
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = "Selected",
                        modifier = Modifier.size(22.dp),
                    )
                }
                IconButton(onClick = onToggleFavorite, modifier = Modifier.size(40.dp)) {
                    Icon(
                        modifier = Modifier.size(22.dp),
                        imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = if (isFavorite) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                }
            }
        },
        modifier = Modifier
            .padding(start = if (inFavorites) 8.dp else 24.dp, end = 8.dp)
            .clickable(onClick = onSelect),
    )
    HorizontalDivider(Modifier.padding(start = if (inFavorites) 16.dp else 32.dp, end = 16.dp))
    }
}
