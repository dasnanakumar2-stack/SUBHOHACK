package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Component
import com.example.model.ComponentDatabase
import com.example.model.PinInfo
import com.example.ui.components.ComponentIllustration
import com.example.ui.components.PinoutDiagramWidget
import com.example.ui.components.ResistorOnCanvas
import com.example.ui.components.SmdResistorOnCanvas
import com.example.ui.theme.MyApplicationTheme
import com.example.util.ResistorCalculators
import com.example.util.ResistorColorBand
import com.example.util.SmdParseResult

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ComponentDatabaseDashboard(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ComponentDatabaseDashboard(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isWideScreen = configuration.screenWidthDp >= 760

    // App State
    var selectedCategory by remember { mutableStateOf("All Categories") }
    var searchQuery by remember { mutableStateOf("") }
    var activeCalculatorTab by remember { mutableIntStateOf(0) } // 0 = Color Bands, 1 = SMD Resistor
    var selectedComponentForDetail by remember { mutableStateOf<Component?>(null) }
    
    // Resistor Calculator State
    var bandCount by remember { mutableIntStateOf(4) }
    val allDigits = ResistorCalculators.getAllBandsForDigit()
    val allMultipliers = ResistorCalculators.getAllBandsForMultiplier()
    val allTolerances = ResistorCalculators.getAllBandsForTolerance()
    val allTempCos = ResistorCalculators.getAllBandsForTempCoeff()

    var b1 by remember { mutableStateOf(allDigits[1]) } // Brown (1)
    var b2 by remember { mutableStateOf(allDigits[0]) } // Black (0)
    var b3 by remember { mutableStateOf(allDigits[1]) } // Brown (multiplier 10 for 4-band, or digit 1 for 5/6 band)
    var b4 by remember { mutableStateOf(allMultipliers[10]) } // Gold (multi 0.1)
    var b5 by remember { mutableStateOf(allTolerances[0]) } // Brown (1%)
    var b6 by remember { mutableStateOf(allTempCos[1]) } // Brown (100 ppm)

    // Calculate real-time color values
    val resistorCalculationResult = remember(bandCount, b1, b2, b3, b4, b5, b6) {
        ResistorCalculators.calculateResistor(
            bandsCount = bandCount,
            b1 = b1,
            b2 = b2,
            b3 = b3,
            b4 = b4,
            b5 = b5,
            b6 = b6
        )
    }

    // SMD Calculator State
    var smdCodeInput by remember { mutableStateOf("103") }
    val smdParseResult = remember(smdCodeInput) {
        ResistorCalculators.parseSmdCode(smdCodeInput)
    }

    // Filter database
    val filteredComponents = remember(selectedCategory, searchQuery) {
        ComponentDatabase.components.filter { comp ->
            val matchesCategory = selectedCategory == "All Categories" || comp.category == selectedCategory
            val matchesSearch = comp.name.contains(searchQuery, ignoreCase = true) ||
                    comp.packageType.contains(searchQuery, ignoreCase = true) ||
                    comp.subcategory.contains(searchQuery, ignoreCase = true) ||
                    comp.description.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    // AI Studio custom space colors
    val darkSlateBg = Color(0xFF0F1015)
    val panelBg = Color(0xFF161920)
    val accentTeal = Color(0xFF00E5FF)
    val accentCyan = Color(0xFF2979FF)
    val textMuted = Color(0xFF90A4AE)

    Row(
        modifier = modifier
            .fillMaxSize()
            .background(darkSlateBg)
    ) {
        // 1. PERSISTENT SIDEBAR NAVIGATION (Wide screens only)
        if (isWideScreen) {
            SidebarPanel(
                categories = listOf("All Categories") + ComponentDatabase.categories,
                selectedCategory = selectedCategory,
                onSelectCategory = { selectedCategory = it },
                modifier = Modifier
                    .width(260.dp)
                    .fillMaxHeight()
                    .background(panelBg)
            )
            VerticalDivider(
                color = Color(0xFF232B35),
                thickness = 1.dp,
                modifier = Modifier.fillMaxHeight()
            )
        }

        // Main content area containing calculators and list grid
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Title Bar inspired by Google AI Studio header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(accentTeal, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ELECTRO-LABS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentTeal,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.5.sp
                        )
                    }
                    Text(
                        text = "Component DB & Assistant",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }

                // AI Studio styled active indicator or compact category toggle
                if (!isWideScreen) {
                    var showCompactCategoryMenu by remember { mutableStateOf(false) }
                    IconButton(
                        onClick = { showCompactCategoryMenu = true },
                        modifier = Modifier
                            .background(Color(0xFF1E2833), RoundedCornerShape(8.dp))
                            .testTag("compact_category_button")
                    ) {
                        Icon(Icons.Default.MenuOpen, contentDescription = "Select Category", tint = accentTeal)
                    }

                    // Mobile Category BottomSheet/Dialog drop down
                    if (showCompactCategoryMenu) {
                        AlertDialog(
                            onDismissRequest = { showCompactCategoryMenu = false },
                            confirmButton = {
                                TextButton(onClick = { showCompactCategoryMenu = false }) {
                                    Text("Done", color = accentTeal)
                                }
                            },
                            title = { Text("Filter Categories", color = Color.White, fontWeight = FontWeight.Bold) },
                            containerColor = panelBg,
                            text = {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .verticalScroll(rememberScrollState()),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    (listOf("All Categories") + ComponentDatabase.categories).forEach { cat ->
                                        val isSel = cat == selectedCategory
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSel) Color(0xFF1A354C) else Color.Transparent)
                                                .clickable {
                                                    selectedCategory = cat
                                                    showCompactCategoryMenu = false
                                                }
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = getCategoryIcon(cat),
                                                contentDescription = null,
                                                tint = if (isSel) accentTeal else textMuted,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(
                                                text = cat,
                                                color = if (isSel) Color.White else textMuted,
                                                fontSize = 14.sp,
                                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }

            // 2. DEDICATED INTERACTIVE RESISTOR & SMD CALCULATORS PANEL
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = panelBg),
                border = BorderStroke(1.dp, Color(0xFF2C3241))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Calculator Tab Selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F1015), RoundedCornerShape(8.dp))
                            .padding(4.dp)
                    ) {
                        TabButton(
                            title = "Axial Color Codes",
                            isActive = activeCalculatorTab == 0,
                            onClick = { activeCalculatorTab = 0 },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("tab_color_codes")
                        )
                        TabButton(
                            title = "SMD Chip Codes",
                            isActive = activeCalculatorTab == 1,
                            onClick = { activeCalculatorTab = 1 },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("tab_smd_codes")
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (activeCalculatorTab == 0) {
                        // COLOR BANDS CALCULATOR PANEL
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "AXIAL RESISTOR COLOR CODE",
                                fontSize = 11.sp,
                                color = textMuted,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            
                            // Band Count Switcher (4, 5, 6 bands)
                            Row(
                                modifier = Modifier
                                    .background(Color(0xFF0F1015), RoundedCornerShape(6.dp))
                                    .padding(2.dp)
                            ) {
                                listOf(4, 5, 6).forEach { num ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (bandCount == num) Color(0xFF263238) else Color.Transparent)
                                            .clickable { bandCount = num }
                                            .padding(horizontal = 10.dp, vertical = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${num}B",
                                            color = if (bandCount == num) accentTeal else Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Resistor visualization on Canvas
                        val bandsToDraw = remember(bandCount, b1, b2, b3, b4, b5, b6) {
                            when (bandCount) {
                                4 -> listOf(b1.color, b2.color, b3.color, b4.color)
                                5 -> listOf(b1.color, b2.color, b3.color, b4.color, b5.color)
                                else -> listOf(b1.color, b2.color, b3.color, b4.color, b5.color, b6.color)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .background(Color(0xFF0D0F13), RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFF1E2430), RoundedCornerShape(12.dp))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            ResistorOnCanvas(
                                bandColors = bandsToDraw,
                                modifier = Modifier
                                    .width(260.dp)
                                    .height(60.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Standard result display
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF1A1D26), RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFF2D3545), RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("CALCULATED VALUE", fontSize = 9.sp, color = textMuted, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                Text(
                                    text = resistorCalculationResult.formattedValue,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("TOLERANCE / TEMPCO", fontSize = 9.sp, color = textMuted, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                Text(
                                    text = "${resistorCalculationResult.tolerance}  ${resistorCalculationResult.tempCoeff}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = accentTeal
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Interactive Band Color Dropdown selects
                        Text(
                            text = "CHOOSE COLOR PER BAND",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = textMuted,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Band 1 selection
                            BandColorPickerDropdown(
                                label = "Band 1 (1st Digit)",
                                selected = b1,
                                options = allDigits,
                                onSelected = { b1 = it }
                            )

                            // Band 2 selection
                            BandColorPickerDropdown(
                                label = "Band 2 (2nd Digit)",
                                selected = b2,
                                options = allDigits,
                                onSelected = { b2 = it }
                            )

                            if (bandCount >= 5) {
                                // Band 3 selection (digit 3)
                                BandColorPickerDropdown(
                                    label = "Band 3 (3rd Digit)",
                                    selected = b3,
                                    options = allDigits,
                                    onSelected = { b3 = it }
                                )
                            }

                            // Multiplier Band selection
                            // For 4-band, b3 represents Multiplier. For 5/6, b4 does.
                            if (bandCount == 4) {
                                BandColorPickerDropdown(
                                    label = "Band 3 (Multiplier)",
                                    selected = b3,
                                    options = allMultipliers,
                                    onSelected = { b3 = it }
                                )
                            } else {
                                BandColorPickerDropdown(
                                    label = "Band 4 (Multiplier)",
                                    selected = b4,
                                    options = allMultipliers,
                                    onSelected = { b4 = it }
                                )
                            }

                            // Tolerance Band selection
                            // For 4-band, b4 represents Tolerance. For 5/6, b5 does.
                            if (bandCount == 4) {
                                BandColorPickerDropdown(
                                    label = "Band 4 (Tolerance)",
                                    selected = b4,
                                    options = allTolerances,
                                    onSelected = { b4 = it }
                                )
                            } else {
                                BandColorPickerDropdown(
                                    label = "Band 5 (Tolerance)",
                                    selected = b5,
                                    options = allTolerances,
                                    onSelected = { b5 = it }
                                )
                            }

                            if (bandCount == 6) {
                                // Temp Coefficient Band selection
                                BandColorPickerDropdown(
                                    label = "Band 6 (TempCo)",
                                    selected = b6,
                                    options = allTempCos,
                                    onSelected = { b6 = it }
                                )
                            }
                        }

                    } else {
                        // SMD CALCULATOR PANEL
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "SMD COMPONENT CODE DECODER",
                                fontSize = 11.sp,
                                color = textMuted,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // SMD Chip Drawing matching code live
                            SmdResistorOnCanvas(
                                code = if (smdCodeInput.isEmpty()) "?" else smdCodeInput,
                                modifier = Modifier
                                    .size(width = 120.dp, height = 75.dp)
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            // Input Box and Results display side-by-side
                            Column(modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = smdCodeInput,
                                    onValueChange = { if (it.length <= 6) smdCodeInput = it },
                                    label = { Text("SMD Resistor Code", color = textMuted) },
                                    placeholder = { Text("E.g., 103, 4R7, 01B", color = textMuted.copy(alpha = 0.5f)) },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = accentTeal,
                                        unfocusedBorderColor = Color(0xFF37474F),
                                        focusedContainerColor = Color(0xFF0F1015),
                                        unfocusedContainerColor = Color(0xFF0F1015)
                                    ),
                                    textStyle = androidx.compose.ui.text.TextStyle(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("smd_code_input")
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            if (smdParseResult.success) Color(0xFF14241B) else Color(
                                                0xFF241517
                                            ), RoundedCornerShape(6.dp)
                                        )
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (smdParseResult.success) Icons.Default.CheckCircle else Icons.Default.Info,
                                        contentDescription = null,
                                        tint = if (smdParseResult.success) Color(0xFF4CAF50) else Color(
                                            0xFFFF5252
                                        ),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (smdParseResult.success) "Value: ${smdParseResult.value}" else "Invalid format",
                                        color = if (smdParseResult.success) Color.White else Color(
                                            0xFFFF8A80
                                        ),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Description parsing summary block
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0D0F13), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = smdParseResult.explanation,
                                color = textMuted,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // 3. MAIN COMPONENT GRID SECTION
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = getCategoryIcon(selectedCategory),
                        contentDescription = "Category Theme",
                        tint = accentTeal,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$selectedCategory (${filteredComponents.size})",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Tiny Google AI Studio search input
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Filter components...", fontSize = 13.sp, color = textMuted) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search icon", tint = textMuted) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.White)
                            }
                        }
                    },
                    modifier = Modifier
                        .width(220.dp)
                        .height(50.dp)
                        .testTag("search_field"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = accentTeal,
                        unfocusedBorderColor = Color(0xFF2C3241),
                        focusedContainerColor = Color(0xFF0F1015),
                        unfocusedContainerColor = Color(0xFF0F1015)
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp)
                )
            }

            // Empty search state
            if (filteredComponents.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp)
                        .background(panelBg, RoundedCornerShape(12.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = "No database results",
                            tint = textMuted,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No components match your search filter",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Try searching by package name, like 'TO-220', '0805', or general terms, or change categories.",
                            color = textMuted,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                // Responsive Component Grid
                val gridColumns = if (configuration.screenWidthDp >= 1000) 3 else if (configuration.screenWidthDp >= 600) 2 else 1
                
                // Since this column is within verticalScroll, we can either use high-performance FlowRow or define simulated rows.
                // Or we can programmatically chunk items and render standard Composable Rows! Let's do modular rows, which works perfectly with scrolling.
                val chunkedComponents = filteredComponents.chunked(gridColumns)
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    chunkedComponents.forEach { rowItems ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            rowItems.forEach { comp ->
                                ComponentItemCard(
                                    component = comp,
                                    onClick = { selectedComponentForDetail = comp },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("component_card_${comp.id}")
                                )
                            }
                            // Fill remaining empty cells in row to keep layout balanced
                            val emptyCells = gridColumns - rowItems.size
                            if (emptyCells > 0) {
                                Spacer(modifier = Modifier.weight(emptyCells.toFloat()))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }

    // 4. INTERACTIVE PINOUT BREAKDOWN OVERLAY DIALOG / POPUP
    selectedComponentForDetail?.let { comp ->
        AlertDialog(
            onDismissRequest = { selectedComponentForDetail = null },
            confirmButton = {},
            dismissButton = {
                TextButton(
                    onClick = { selectedComponentForDetail = null },
                    modifier = Modifier.testTag("modal_close")
                ) {
                    Text("Close", color = accentTeal)
                }
            },
            containerColor = panelBg,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .widthIn(max = 620.dp)
                .border(1.dp, Color(0xFF37474F), RoundedCornerShape(16.dp)),
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = comp.packageType.uppercase(),
                            color = accentTeal,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = comp.name,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Category & sub-classification indicator
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ComponentBadge(text = comp.category, color = Color(0xFF0D47A1))
                        ComponentBadge(text = comp.packageType, color = Color(0xFF006064))
                    }

                    // Practical schematic description
                    Column {
                        Text(
                            text = "COMPREHENSIVE DESCRIPTION",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = textMuted,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = comp.description,
                            color = Color.White,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }

                    // Typical use cases
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F1015), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "TYPICAL APPLICATION USAGE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentTeal,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = comp.typicalApplication,
                            color = Color.White,
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Dynamic Schematic Pinout allocation widget
                    PinoutDiagramWidget(
                        symbolType = comp.internalSymbolType,
                        pinList = comp.pinouts,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Datasheet action lookups (Intent triggering alldatasheet)
                    Button(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(comp.datasheetUrl))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Could not open web link", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = accentCyan),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("datasheet_btn")
                    ) {
                        Icon(Icons.Default.CloudDownload, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (comp.name.contains("Timer")) "Search Official NE555 Datasheet" else "Search Reference Datasheet",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun SidebarPanel(
    categories: List<String>,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val accentTeal = Color(0xFF00E5FF)
    val textMuted = Color(0xFF90A4AE)

    Column(
        modifier = modifier
            .padding(16.dp)
    ) {
        // Logo block mimicking Google AI Studio branding panel
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        Brush.linearGradient(listOf(accentTeal, Color(0xFF2979FF))),
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Memory,
                    contentDescription = "Electrolab chip",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "LAB ASSISTANT",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "v1.2 // Live Database",
                    color = accentTeal,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Divider(color = Color(0xFF232B35), thickness = 1.dp, modifier = Modifier.padding(bottom = 16.dp))

        Text(
            text = "COMPONENT CATEGORIES",
            color = textMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(start = 8.dp, bottom = 12.dp)
        )

        // List Scroll of categorizations
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            categories.forEach { cat ->
                val isSelected = cat == selectedCategory
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) Color(0xFF1E2430) else Color.Transparent)
                        .clickable { onSelectCategory(cat) }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = getCategoryIcon(cat),
                        contentDescription = null,
                        tint = if (isSelected) accentTeal else textMuted,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = cat,
                        color = if (isSelected) Color.White else textMuted,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Divider(color = Color(0xFF232B35), thickness = 1.dp, modifier = Modifier.padding(vertical = 16.dp))

        // AI Studio type credits sidebar block
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0F1015), RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Column {
                Text(
                    text = "ACTIVE SIMULATOR",
                    fontSize = 9.sp,
                    color = accentTeal,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Supports 4/5/6 Band coding, SMD resistor codes, and interactive pin placement mappings.",
                    fontSize = 11.sp,
                    color = textMuted,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
fun ComponentItemCard(
    component: Component,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentTeal = Color(0xFF00E5FF)
    val textMuted = Color(0xFF90A4AE)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161920)),
        border = BorderStroke(1.dp, Color(0xFF2C3241))
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Package vector drawing top container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(105.dp)
                    .background(Color(0xFF0D0F13), RoundedCornerShape(8.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                ComponentIllustration(symbolType = component.internalSymbolType, modifier = Modifier.fillMaxSize())
                
                // Package miniature tag on top right
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(Color(0xFF263238), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = component.packageType,
                        color = accentTeal,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Text Header name
            Text(
                text = component.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Subclassification
            Text(
                text = "${component.category} • ${component.subcategory}",
                fontSize = 11.sp,
                color = textMuted,
                modifier = Modifier.padding(vertical = 2.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Truncated Description snippet
            Text(
                text = component.description,
                fontSize = 11.sp,
                color = textMuted.copy(alpha = 0.8f),
                lineHeight = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Action line trigger indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${component.pinouts.size} Pinout Mappings",
                    fontSize = 10.sp,
                    color = accentTeal,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "View Pins", fontSize = 11.sp, color = Color.White)
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TabButton(
    title: String,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentTeal = Color(0xFF00E5FF)
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isActive) Color(0xFF1E2833) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (isActive) accentTeal else Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}

@Composable
fun BandColorPickerDropdown(
    label: String,
    selected: ResistorColorBand,
    options: List<ResistorColorBand>,
    onSelected: (ResistorColorBand) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .background(Color(0xFF1E2430), RoundedCornerShape(6.dp))
            .border(1.dp, Color(0xFF2C3241), RoundedCornerShape(6.dp))
            .clickable { isExpanded = true }
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Visual Color pill
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(selected.color)
                    .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "${selected.name} (${selected.digitValue ?: selected.multiplierValue?.let { if (it<1) "$it" else if (it>=1000) "${it.toInt()}" else "${it.toInt()}" } ?: selected.toleranceValue?.let { "${it}%" } ?: selected.tempCoeffValue ?: ""})",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
        }

        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            modifier = Modifier.background(Color(0xFF1E2430))
        ) {
            options.forEach { opt ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(opt.color)
                                    .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            val detailSuffix = when {
                                opt.digitValue != null -> " = ${opt.digitValue}"
                                opt.multiplierValue != null -> " = x${opt.multiplierValue}"
                                opt.toleranceValue != null -> " = ±${opt.toleranceValue}%"
                                opt.tempCoeffValue != null -> " = ${opt.tempCoeffValue} ppm"
                                else -> ""
                            }
                            Text(text = "${opt.name}$detailSuffix", color = Color.White, fontSize = 12.sp)
                        }
                    },
                    onClick = {
                        onSelected(opt)
                        isExpanded = false
                    },
                    modifier = Modifier.background(Color(0xFF161920))
                )
            }
        }
    }
}

@Composable
fun ComponentBadge(text: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.25f), RoundedCornerShape(4.dp))
            .border(1.dp, color, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}

fun getCategoryIcon(category: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (category) {
        "Resistors" -> Icons.Default.LinearScale
        "Capacitors" -> Icons.Default.PausePresentation
        "Transistors & MOSFETs" -> Icons.Default.SwitchRight
        "Diodes" -> Icons.Default.DoubleArrow
        "Integrated Circuits" -> Icons.Default.Memory
        "Inductors & Transformers" -> Icons.Default.AllInclusive
        "Switches & Relays" -> Icons.Default.ToggleOn
        else -> Icons.Default.Widgets
    }
}
