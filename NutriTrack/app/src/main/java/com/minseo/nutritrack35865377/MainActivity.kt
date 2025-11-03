package com.minseo.nutritrack35865377

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.*
import androidx.compose.material3.TextField
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import android.content.Context
import com.minseo.nutritrack35865377.ui.theme.NutriTrackTheme
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import android.content.Intent
import android.graphics.Paint.Align
import android.net.Uri
import android.util.TimingLogger
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.annotation.DrawableRes
import androidx.navigation.compose.currentBackStackEntryAsState



data class UserData(
    val userId: String,
    val phoneNumber: String,
    val sex: String,
    val totalScore: String
)



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NutriTrackTheme {
                NutriNavHost()
            }
        }
    }
}

@Composable
fun WelcomeScreen(onLoginClick: () -> Unit) {
        val context = LocalContext.current

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(100.dp))
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "NutriTrack Logo",
                    modifier = Modifier
                        .height(250.dp)
                        .width(250.dp)
                        .padding(bottom = 10.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))
                Text("This app provides general health and nutrition information for educational purposes only. It is not intended as medical advice, diagnosis, or treatment. Always consult a qualified healthcare professional before making any changes to your diet, exercise, or health regimen.\n" +
                        "Use this app at your own risk.\n" +
                        "If you’d like to an Accredited Practicing Dietitian (APD), please visit the",
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Monash Nutrition/Dietetics Clinic (discounted rates for students)",
                    color = Color.Blue,
                    modifier = Modifier.clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.monash.edu/medicine/scs/nutrition/clinics/nutrition"))
                        context.startActivity(intent)
                    }
                )
                Spacer(modifier = Modifier.height(30.dp))
                Button(onClick = { onLoginClick() }) {
                    Text("Login")
                }
            }
            Text(
                text = "Shin Minseo (35865377)",
                modifier = Modifier.padding(bottom = 16.dp),
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    NutriTrackTheme {
        WelcomeScreen(onLoginClick = {})
    }
}

@Composable
fun NutriNavHost() {
    val navController = rememberNavController()

    val userIdState = remember { mutableStateOf("")}
    val scoreState = remember { mutableStateOf("")}


    Scaffold(
        bottomBar = {
            NavButtonBar(navController = navController)
        }
    ) { innerPadding ->

        NavHost(navController = navController, startDestination = "Welcome") {
            composable("Welcome") {
                WelcomeScreen(onLoginClick = {
                    navController.navigate("login")
                })
            }
            composable("login") {
                LoginScreen(navController = navController)
            }
            composable("home/{userId}/{score}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                val score = backStackEntry.arguments?.getString("score") ?: ""
                HomeScreen(userId = userId, score = score, onEditClick = {
                    navController.navigate("questionnaire")
                })
            }
            composable("questionnaire") {
                QuestionnaireScreen(onSaveClick = {
                    navController.navigate("home/1/999")
                })
            }
            composable(NavButtonItem.Home.route){
                HomeScreen(userId = userIdState.value, score = scoreState.value, onEditClick = {
                    navController.navigate("questionnaire")
                })
            }
            composable(NavButtonItem.Insights.route){
                InsightsScreen(userId = userIdState.value, score = scoreState.value)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavHostController){

    val context = LocalContext.current

    var selectedUserId by remember {mutableStateOf("")}
    val userList = loadUserDataFromCSV(context)
    val userIds = userList.map { it.userId}

    var phoneNumber by remember{ mutableStateOf("")}
    var showError by remember { mutableStateOf(false)}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Login", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(20.dp))

        var expanded by remember { mutableStateOf(false)}

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {expanded = !expanded}
        ) {
            TextField(
                value = selectedUserId,
                onValueChange = {},
                readOnly = true,
                label = {Text("My ID (Provided by your Clinician)")},
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {expanded = false}
            ) {
                userIds.forEach { id ->
                    DropdownMenuItem(
                        text = {Text(id)},
                        onClick = {
                            selectedUserId = id
                            expanded = false
                        }
                    )

                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        //phone num
        TextField(
            value = phoneNumber,
            onValueChange = {phoneNumber = it},
            label = {Text("Phone Number")}
        )
        Spacer(modifier=Modifier.height(20.dp))

        Button(onClick = {
            val matchedUser = userList.find { it.userId == selectedUserId && it.phoneNumber == phoneNumber }
            if (matchedUser != null) {
                showError = false

                val score = matchedUser.totalScore
                navController.navigate("home/${matchedUser?.userId}/${score}")

            } else {
                showError = true
            }

        }) {
            Text("Continue")
        }

        if (showError){
            Spacer(modifier = Modifier.height(8.dp))
            Text("Invalid input. Please Try Again.", color = Color.Red)
        }
    }
}

fun loadUserDataFromCSV(context: Context): List<UserData>{
    val userList = mutableListOf<UserData>()
    try{
        val inputStream = context.assets.open("users.csv")
        val reader = inputStream.bufferedReader()
        val lines = reader.readLines().drop(1)

        for(line in lines){
            val tokens = line.split(",")
            if (tokens.size >= 5) {

                val phone = tokens[0].trim()
                val userId = tokens[1].trim()
                val sex = tokens[2].trim()

                val totalScore = if (sex == "Male") {
                    tokens[3].trim()
                } else {
                    tokens[4].trim()
                }
                userList.add(
                    UserData(
                        userId = userId,
                        phoneNumber = phone,
                        sex = sex,
                        totalScore = totalScore)
                )
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return userList
}

@Composable
fun HomeScreen(userId: String, score: String, onEditClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Hello, $userId", fontSize = 22.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Your Food Quality Score: ",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = score,
            fontSize = 40.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF4CAF50)
        )
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "What is the Food Quality Score?\n" +
                    "Your Food Quality Score provides a snapshot of how well your eating patterns align with established food guidelines, helping you identify both strengths and opportunities for improvement in your diet.\n" +
                    "This personalized measurement considers various food groups including vegetables, fruits, whole grains, and proteins to give you practical insights for making healthier food choices.",
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onEditClick) {
            Text("Edit My Answers")
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionnaireScreen(onSaveClick: () -> Unit) {

    val personaDescriptions = mapOf(
        "Health Devotee" to "I’m passionate about healthy eating & health plays a big part in my life. I use social media to follow active lifestyle personalities or get new recipes/exercise ideas. I may even buy superfoods or follow a particular type of diet. I like to think I am super healthy.",
        "Mindful Eater" to "I’m health-conscious and being healthy and eating healthy is important to me. Although health means different things to different people, I make conscious lifestyle decisions about eating based on what I believe healthy means. I look for new recipes and healthy eating information on social media.",
        "Wellness Striver" to "I aspire to be healthy (but struggle sometimes). Healthy eating is hard work! I’ve tried to improve my diet, but always find things that make it difficult to stick with the changes. Sometimes I notice recipe ideas or healthy eating hacks, and if it seems easy enough, I’ll give it a go.",
        "Balance Seeker" to "I try and live a balanced lifestyle, and I think that all foods are okay in moderation. I shouldn’t have to feel guilty about eating a piece of cake now and again. I get all sorts of inspiration from social media like finding out about new restaurants, fun recipes and sometimes healthy eating tips.",
        "Health Procrastinator" to "I’m contemplating healthy eating but it’s not a priority for me right now. I know the basics about what it means to be healthy, but it doesn’t seem relevant to me right now. I have taken a few steps to be healthier but I am not motivated to make it a high priority because I have too many other things going on in my life.",
        "Food Carefree" to "I’m not bothered about healthy eating. I don’t really see the point and I don’t think about it. I don’t really notice healthy eating tips or recipes and I don’t care what I eat."
    )

    val personaNames = listOf(
        "Health Devotee", "Mindful Eater", "Wellness Striver",
        "Balance Seeker", "Health Procrastinator", "Food Carefree"
    )

    var selectedDialogPersona by remember{mutableStateOf<String?>(null)}

    val foodCategories = listOf(
        "Fruits", "Vegetables", "Grains", "Red Meat", "Seafood", "Poultry", "Fish", "Eggs", "Nuts/Seeds"
    )

    val foodSelections = remember {
        mutableStateMapOf<String, Boolean>().apply {
            foodCategories.forEach { put(it, false)}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text("Food Intake Questionnaire",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

    Text(
        "Tick all the food categories you can eat",
        fontWeight = FontWeight.SemiBold)

    Spacer(modifier = Modifier.height(16.dp))

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        userScrollEnabled = false,
        content = {
            items(foodCategories.size) { index ->
                val category = foodCategories[index]
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(4.dp)
                ) {
                    Checkbox(
                        checked = foodSelections[category] ?: false,
                        onCheckedChange = { isChecked ->
                            foodSelections[category] = isChecked
                        }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(category)
                }
            }
        }

    )
        Spacer(modifier = Modifier.height(32.dp))

        Text("Your Persona", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))

        Text("People can be broadly classified into 6 different types based on their eating preferences. Click on each button below to find out the different types, and select the type that best fits you!")

        Spacer(modifier = Modifier.height(16.dp))

        Column{
            personaNames.chunked(3).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    row.forEach { persona ->
                        Button(
                            onClick = { selectedDialogPersona = persona},
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 4.dp)
                        ) {
                            Text(persona, fontSize = 12.sp)
                        }
                    }
                }
            }
        }


        selectedDialogPersona?.let { persona ->

            val personaImages = mapOf(
                "Health Devotee" to R.drawable.persona_1,
                "Mindful Eater" to R.drawable.persona_2,
                "Wellness Striver" to R.drawable.persona_3,
                "Balance Seeker" to R.drawable.persona_4,
                "Health Procrastinator" to R.drawable.persona_5,
                "Food Carefree" to R.drawable.persona_6,
            )

            val description = personaDescriptions[persona]?: "No description"
            val imageRes = personaImages[persona]

            AlertDialog(
                onDismissRequest = { selectedDialogPersona = null},
                confirmButton = {
                    Button(onClick = {selectedDialogPersona = null}) {
                        Text("Dismiss")
                    }
                },
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        imageRes?.let {
                            Image(
                                painter = painterResource(id = it),
                                contentDescription = "$persona image",
                                modifier = Modifier
                                    .height(120.dp)
                                    .padding(bottom = 12.dp)
                            )
                        }
                        Text(persona, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                },
                text = { Text(description, fontSize = 14.sp)
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Which persona best fits you?", fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))

        var selectedPersona by remember { mutableStateOf("") }
        var expanded by remember { mutableStateOf(false)}

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {expanded = !expanded}
        ) {
            TextField(
                value = selectedPersona,
                onValueChange = {},
                readOnly = true,
                label = {Text("Select option")},
                trailingIcon = {ExposedDropdownMenuDefaults.TrailingIcon(expanded)},
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {expanded = false}
            ) {
                personaNames.forEach { option ->
                    DropdownMenuItem(
                        text = {Text(option)},
                        onClick = {
                            selectedPersona = option
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        val context = LocalContext.current

        var biggestmeal by remember { mutableStateOf("00:00")}
        var sleeptime by remember { mutableStateOf("00:00")}
        var wakeuptime by remember { mutableStateOf("oo:oo") }

        fun TimePicker(initialTime: String, onTimeSelected: (String) -> Unit) {
            val parts = initialTime.split(":")
            val hour = parts.getOrNull(0)?.toIntOrNull() ?: 0
            val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0

            val timePickerDialog = android.app.TimePickerDialog(
                context,
                { _, selectedHour, selectedMinute ->
                    val formatted = String.format("%02d:%02d", selectedHour, selectedMinute)
                    onTimeSelected(formatted)
                },
                hour,
                minute,
                true
            )
            timePickerDialog.show()
        }

        Text("Timings", fontWeight=FontWeight.Bold, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))

        TimingRow(
            label = "What time of day do you normally eat your biggest meal",
            time = biggestmeal,
            onTimeClick = {TimePicker(biggestmeal){biggestmeal = it} }
        )

        TimingRow(
            label = "What time of day do you go to sleep at night",
            time = sleeptime,
            onTimeClick = {TimePicker(sleeptime){sleeptime = it} }
        )

        TimingRow(
            label = "What time of day do you wake up in the morning",
            time = wakeuptime,
            onTimeClick = {TimePicker(wakeuptime){wakeuptime = it} }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ){
            Button(onClick = {onSaveClick() }){
                Text("Save (Stub)")
            }
        }
    }
}


@Composable
fun TimingRow(label: String, time: String, onTimeClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            fontSize = 14.sp
        )
        Button(onClick = onTimeClick) {
            Text(time)
        }
    }
}

data class InsightItem(
    val name: String,
    val score: Float,
    val maxScore: Int
)

@Composable
fun InsightsScreen(userId: String, score: String) {

    val insights = listOf(
        InsightItem("Vegetables", 10f, 10),
        InsightItem("Fruits", 10f, 10),
        InsightItem("Grains & Cereals", 10f, 10),
        InsightItem("Whole Grains", 10f, 10),
        InsightItem("Meat & Alternatives", 10f, 10),
        InsightItem("Dairy", 10f, 10),
        InsightItem("Water", 2f, 5),
        InsightItem("Unsaturated Fats", 10f, 10),
        InsightItem("Sodium", 10f, 10),
        InsightItem("Sugar", 10f, 10),
        InsightItem("Alcohol", 2f, 5),
        InsightItem("Discretionary Foods", 8f, 10)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text("Insights: Food Score", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(24.dp))

        insights.forEach { item ->
            Text(text = item.name, fontWeight = FontWeight.Medium)

            LinearProgressIndicator(
                progress = { item.score / item.maxScore },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
            )
            Text(
                "${item.score.toInt()}/${item.maxScore}",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.End)
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("Total Food Quality Score", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(20.dp))

        LinearProgressIndicator(
            progress = { score.toFloatOrNull() ?: 0f / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp),
        )
        Text(
            text = "$score/100",
            fontSize = 12.sp
        )


        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = { }) {
                Text("Share with someone")
            }
            Button(onClick = { }) {
                Text("Improve my diet")
            }

        }
    }
}

sealed class NavButtonItem(val route: String, val label: String, @DrawableRes val iconRes: Int) {
    object Home : NavButtonItem("home", "Home", R.drawable.homeicon)
    object Insights : NavButtonItem("insights", "Insights", R.drawable.barcharticon)
    object NutriCoach : NavButtonItem("coach", "NutriCoach", R.drawable.coachicon)
    object Settings : NavButtonItem("settings", "Settings", R.drawable.settingsicon)
}


@Composable
fun NavButtonBar(navController: NavHostController){
    val items = listOf(
        NavButtonItem.Home,
        NavButtonItem.Insights,
        NavButtonItem.NutriCoach,
        NavButtonItem.Settings
    )
    NavigationBar {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { navController.navigate(item.route) },
                icon = {
                    Image(
                        painter = painterResource(id = item.iconRes),
                        contentDescription = item.label,
                        modifier = Modifier.height(24.dp)
                    )
                },
                label = { Text(item.label) }
            )
        }
    }
}
