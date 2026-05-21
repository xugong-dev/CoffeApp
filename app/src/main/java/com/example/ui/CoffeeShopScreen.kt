package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.local.FavoriteCoffeeShop

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoffeeShopScreen(
    viewModel: CoffeeShopViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Search, 1 = Saved
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val searchUiState by viewModel.searchUiState.collectAsStateWithLifecycle()
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current

    val popularCities = listOf(
        "Seattle, WA",
        "Portland, OR",
        "Austin, TX",
        "San Francisco, CA",
        "New York, NY",
        "Chicago, IL"
    )

    val filters = listOf("All", "Patio", "Garden", "Rooftop", "Sidewalk", "Deck", "Balcony")

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Header Brand Section
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocalCafe,
                            contentDescription = "Search",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Outdoor Coffee Finder",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D1B20),
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "Discover patios, gardens & green rooftops",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF49454F)
                        )
                    }
                    
                    // User initials avatar "XG" based on xu.gong@gmail.com
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF6750A4)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "XG",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // AI Search input card styled exactly like design html search bar
                if (selectedTab == 0) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.updateSearchQuery(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .testTag("search_text_input"),
                        placeholder = {
                            Text(
                                "Search coffee shops...",
                                color = Color(0xFF49454F),
                                fontSize = 15.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Search",
                                tint = Color(0xFF49454F)
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { viewModel.updateSearchQuery("") },
                                    modifier = Modifier.testTag("clear_search_button")
                                ) {
                                    Icon(Icons.Filled.Close, contentDescription = "Clear", tint = Color(0xFF49454F))
                                }
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = {
                            keyboardController?.hide()
                            viewModel.performSearch()
                        }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF1D1B20),
                            unfocusedTextColor = Color(0xFF1D1B20),
                            focusedContainerColor = Color(0xFFF3EDF7),
                            unfocusedContainerColor = Color(0xFFF3EDF7),
                            focusedBorderColor = Color(0xFFE7E0EC),
                            unfocusedBorderColor = Color(0xFFE7E0EC),
                            cursorColor = Color(0xFF6750A4)
                        ),
                        shape = CircleShape
                    )

                    // Popular cities sliders
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp, bottom = 6.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        popularCities.forEach { city ->
                            val isSelected = searchQuery.equals(city, ignoreCase = true)
                            SleekChip(
                                text = city,
                                selected = isSelected,
                                onClick = {
                                    viewModel.updateSearchQuery(city)
                                    viewModel.performSearch(city)
                                    keyboardController?.hide()
                                },
                                modifier = Modifier.testTag("city_chip_${city.replace(" ", "_").replace(",", "")}")
                            )
                        }
                    }

                    // Seating layout environment selections
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FilterList,
                            contentDescription = "Filter",
                            tint = Color(0xFF6750A4),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        filters.forEach { filter ->
                            val isSelected = selectedFilter.equals(filter, ignoreCase = true)
                            SleekChip(
                                text = filter,
                                selected = isSelected,
                                onClick = { viewModel.updateSelectedFilter(filter) },
                                modifier = Modifier.testTag("filter_chip_$filter"),
                                leadingIcon = if (isSelected) {
                                    {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                } else null
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            // Elegant M3 Bottom Navigation styled after design html <nav>
            NavigationBar(
                containerColor = Color(0xFFF3EDF7),
                tonalElevation = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search"
                        )
                    },
                    label = { Text("Search", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color(0xFFE8DEF8),
                        selectedIconColor = Color(0xFF1D1B20),
                        selectedTextColor = Color(0xFF1D1B20),
                        unselectedIconColor = Color(0xFF49454F),
                        unselectedTextColor = Color(0xFF49454F)
                    ),
                    modifier = Modifier.testTag("tab_search")
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Saved"
                        )
                    },
                    label = { Text("Saved", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color(0xFFE8DEF8),
                        selectedIconColor = Color(0xFF1D1B20),
                        selectedTextColor = Color(0xFF1D1B20),
                        unselectedIconColor = Color(0xFF49454F),
                        unselectedTextColor = Color(0xFF49454F)
                    ),
                    modifier = Modifier.testTag("tab_favorites")
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFFEF7FF))
        ) {
            if (selectedTab == 0) {
                // AI Search mode results
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    when (val state = searchUiState) {
                        is SearchUiState.Idle -> {
                            EmptyStateView(
                                title = "Let's Find Your Patio!",
                                subtitle = "Search for a location or click one of the quick tabs above to find local coffee shops with outdoor seats.",
                                icon = Icons.Filled.LocalCafe
                            )
                        }
                        is SearchUiState.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.padding(32.dp)
                                ) {
                                    CircularProgressIndicator(
                                        color = Color(0xFF6750A4),
                                        strokeWidth = 3.dp,
                                        modifier = Modifier.size(40.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "Firing up the espresso machine...",
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF6750A4),
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Querying Gemini AI for local coffee patios, botanical gardens, and scenic rooftops.",
                                        color = Color(0xFF49454F),
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                        is SearchUiState.Error -> {
                            ErrorStateView(
                                message = state.message,
                                onRetry = { viewModel.performSearch() }
                            )
                        }
                        is SearchUiState.Success -> {
                            val filteredShops = if (selectedFilter.equals("All", ignoreCase = true)) {
                                state.shops
                            } else {
                                state.shops.filter { it.outdoorSeatingType.equals(selectedFilter, ignoreCase = true) }
                            }

                            if (filteredShops.isEmpty()) {
                                EmptyStateView(
                                    title = "No Match Found",
                                    subtitle = "There are no local coffee shops in this list matches the seating filter \"$selectedFilter\". Try another filter!",
                                    icon = Icons.Filled.Info
                                )
                            } else {
                                CoffeeList(
                                    shops = filteredShops,
                                    favorites = favorites,
                                    onFavoriteClick = { viewModel.toggleFavorite(it) }
                                )
                            }
                        }
                    }
                }
            } else {
                // Favorites Mode Screen Layout
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    if (favorites.isEmpty()) {
                        EmptyStateView(
                            title = "No Saved Patios Yet",
                            subtitle = "Click the heart icon on any coffee shop card while searching to save your favorite outdoor coffee spots here for offline tracking!",
                            icon = Icons.Filled.FavoriteBorder
                        )
                    } else {
                        CoffeeList(
                            shops = favorites,
                            favorites = favorites,
                            onFavoriteClick = { viewModel.toggleFavorite(it) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SleekChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    Surface(
        selected = selected,
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) Color(0xFF6750A4) else Color(0xFF79747E)
        ),
        color = if (selected) Color(0xFFE8DEF8) else Color.White,
        contentColor = if (selected) Color(0xFF1D192B) else Color(0xFF49454F),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun CoffeeList(
    shops: List<FavoriteCoffeeShop>,
    favorites: List<FavoriteCoffeeShop>,
    onFavoriteClick: (FavoriteCoffeeShop) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(shops, key = { it.id }) { shop ->
            val isFavorited = favorites.any { it.id == shop.id }
            CoffeeShopCard(
                shop = shop,
                isFavorited = isFavorited,
                onFavoriteClick = { onFavoriteClick(shop) }
            )
        }
    }
}

@Composable
fun CoffeeShopCard(
    shop: FavoriteCoffeeShop,
    isFavorited: Boolean,
    onFavoriteClick: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    
    // Deterministic realistic distance (e.g. 0.4 mi) and hours built from shop attributes
    val distance = remember(shop.id) {
        val hash = Math.abs(shop.id.hashCode())
        val rounded = ((hash % 16) + 3) / 10.0
        "$rounded mi"
    }
    val closesText = remember(shop.id) {
        val hash = Math.abs(shop.id.hashCode())
        val hour = (hash % 4) + 6
        "Closes $hour PM"
    }

    // Design layout card: rounded-[28px], white/light purple container, border-[#CAC4D0]
    Card(
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
        colors = CardDefaults.cardColors(
            containerColor = if (isFavorited) Color(0xFFF7F2FA) else Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
            .testTag("coffee_shop_card_${shop.id}")
            .animateContentSize()
    ) {
        Column {
            // Card cover photo or fallback color container (h-32 bg-[#EADDFF])
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Color(0xFFEADDFF))
            ) {
                AsyncImage(
                    model = shop.imageUrl,
                    contentDescription = "Cover of ${shop.name}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // High-contrast translucent hearts action button
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.85f))
                        .clickable(onClick = onFavoriteClick)
                        .testTag("favorite_button_${shop.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isFavorited) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Save favorite",
                        tint = if (isFavorited) Color(0xFF6750A4) else Color(0xFF1D1B20),
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Top-Left Seating Badge overlay
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart)
                        .background(
                            color = Color.White.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    val icon = when (shop.outdoorSeatingType.lowercase()) {
                        "patio" -> "⛱️"
                        "garden" -> "🌳"
                        "rooftop" -> "🌇"
                        "sidewalk" -> "🚶"
                        "deck" -> "⛵"
                        "balcony" -> "🌌"
                        else -> "☕"
                    }
                    Text(
                        text = "$icon ${shop.outdoorSeatingType}",
                        color = Color(0xFF1D1B20),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Description block content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header with name and rating
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = shop.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D1B20),
                        modifier = Modifier.weight(1f)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFE6BE2E),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${shop.rating}",
                            color = Color(0xFF1D1B20),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = " (${if (shop.reviewCount >= 1000) "${(shop.reviewCount/1000.0).let { "%.1fk".format(it) }}" else shop.reviewCount})",
                            color = Color(0xFF49454F),
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                
                // Location label
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "Address",
                        tint = Color(0xFF49454F),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = shop.address,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF49454F),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Short patio description text matching "The Garden Roast" paragraph style
                Text(
                    text = shop.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF49454F),
                    maxLines = if (isExpanded) 8 else 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )

                // Interactive Expandable detail block show on click
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Column(modifier = Modifier.padding(top = 10.dp)) {
                        Divider(color = Color(0xFFE7E0EC), thickness = 0.5.dp)
                        Spacer(modifier = Modifier.height(10.dp))
                        
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Filled.Deck,
                                contentDescription = "Seating details",
                                tint = Color(0xFF6750A4),
                                modifier = Modifier.size(16.dp).padding(top = 1.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Outdoor Atmosphere Details",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1D1B20)
                                )
                                Text(
                                    text = shop.seatingDescription,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF49454F),
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                // Hours, Distances & Actions Bar styled in Color(0xFF6750A4)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Distance label
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.NearMe,
                                contentDescription = "Near me",
                                tint = Color(0xFF6750A4),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = distance,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6750A4)
                            )
                        }

                        // Hours
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Schedule,
                                contentDescription = "Hours",
                                tint = Color(0xFF6750A4),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = closesText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6750A4)
                            )
                        }
                    }

                    // Expand tap hint link
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = if (isExpanded) "Less" else "Detail",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6750A4)
                        )
                        Icon(
                            imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Color(0xFF6750A4),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateView(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF3EDF7)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF6750A4),
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1D1B20),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF49454F),
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun ErrorStateView(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(28.dp),
            border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Filled.Error,
                    contentDescription = "Error",
                    tint = Color(0xFFB3261E),
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "AI Connection Notice",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1D1B20)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (message.contains("GEMINI_API_KEY", ignoreCase = true) || message.contains("placeholder", ignoreCase = true)) {
                        "Your GEMINI_API_KEY is not configured yet!\n\nTo make this AI Coffee Finder operational, please click on the Secrets Panel in the bottom-left of visual AI Studio, and add a secret named:\nGEMINI_API_KEY\nwith your official Google AI Studio key."
                    } else {
                        message
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF49454F),
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6750A4),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(100.dp),
                    modifier = Modifier.testTag("retry_search_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Retry Connection")
                    }
                }
            }
        }
    }
}
