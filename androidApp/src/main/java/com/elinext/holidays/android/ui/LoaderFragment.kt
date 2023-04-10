package com.elinext.holidays.android.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import com.elinext.holidays.Greeting
import com.elinext.holidays.android.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class LoaderFragment : BaseFragment() {


    companion object {
        //animation
        const val ANIMATED_IS_NEEDED = "animation_is_needed"
        const val NOTIFICATION_APPEAR_ANIMATION_MILLIS = 600
        const val NOTIFICATION_APPEAR_DELAY_MILLIS = 100L
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("system.out", "----->${this.javaClass.name}")
        view.findViewById<ComposeView>(R.id.compose_view).setContent {
            GreetingView()
        }
    }

    @SuppressLint("CoroutineCreationDuringComposition")
    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    override fun GreetingView() {
        var preLoading by remember { mutableStateOf(false) }
        val isLoading = remember { mutableStateOf(false) }
        val isShown = remember { mutableStateOf(false) }
        val layerAppeared = remember { mutableStateOf(false) }
        LaunchedEffect(layerAppeared) {
            delay(NOTIFICATION_APPEAR_DELAY_MILLIS)
            isShown.value = true
            layerAppeared.value = true
        }

        MaterialTheme(content = {
            AnimatedVisibility(
                visible = isShown.value,
                enter = scaleIn(
                    animationSpec = tween(
                        durationMillis = NOTIFICATION_APPEAR_ANIMATION_MILLIS,
                        easing = FastOutSlowInEasing
                    ),
                    initialScale = 3f
                ),
                exit = slideOutVertically(
                    animationSpec = tween(
                        durationMillis = NOTIFICATION_APPEAR_ANIMATION_MILLIS,
                        easing = FastOutSlowInEasing
                    ),
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .animateContentSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Text(text = "Elinext", color = Color.Red, fontSize = 30.sp, fontWeight = FontWeight.Bold)
//                    Image(
//                        alignment = Alignment.Center,
//                        painter = painterResource(id = R.drawable.logo_loader),
//                        contentDescription = null,
//                        colorFilter = ColorFilter.tint(color = Color.Black)
//                    )

                    if (preLoading) {
                        Text(text = "Holidays", fontSize = 16.sp, color = Color.Gray)
                    }

                }
                rememberCoroutineScope().launch() {
                    delay(600)
                    preLoading = true
                    delay(400)
                    isLoading.value = true
                }
                if (isLoading.value) {
                    findNavController().navigate(
                        R.id.action_loaderFragment_to_monthFragment,
                        bundleOf()
                    )
                }
            }
        })
    }
}