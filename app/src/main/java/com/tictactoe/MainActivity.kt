package com.tictactoe

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tictactoe.ui.theme.TicTacToeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TicTacToeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    RootView(clickEvent={
                        startActivity(Intent(this@MainActivity,MainActivity::class.java))
                        finish()
                    })
                }
            }
        }
    }
}

interface ClickEvent{
    fun onResetClicked()
}

private val oBackgroundColor = mutableStateOf(R.color.voilet)
private val xBackgroundColor = mutableStateOf(R.color.light_grey)

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RootView(clickEvent: ()->Unit) {
    val oTextColor = remember {
        mutableStateOf(R.color.white)
    }
    val xTextColor = remember {
        mutableStateOf(R.color.black)
    }
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Tic Tac Toe Game",
            textDecoration = TextDecoration.None,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            fontSize = 32.sp,
            color = colorResource(id = R.color.voilet)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Surface(
                color = colorResource(
                    id = oBackgroundColor.value
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
            ) {
                Text(
                    text = "O",
                    fontSize = 35.sp,
                    color = colorResource(id = oTextColor.value),
                    modifier = Modifier.padding(
                        start = 20.dp,
                        end = 20.dp,
                        bottom = 10.dp,
                        top = 5.dp
                    )
                )
            }
            Surface(
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp)
                    .clip(RoundedCornerShape(10.dp)),
                color = colorResource(id = xBackgroundColor.value)
            ) {
                Text(
                    text = "X",
                    fontSize = 35.sp,
                    color = colorResource(id = xTextColor.value),
                    modifier = Modifier.padding(
                        start = 20.dp,
                        end = 20.dp,
                        bottom = 10.dp,
                        top = 5.dp
                    )
                )
            }

        }
        Spacer(modifier = Modifier.height(20.dp))
        InputBoxForGame()
        Spacer(modifier = Modifier.height(15.dp))
        if (!winnerName.value.isEmpty()) {
            Text(
                text = "${winnerName.value}",
                fontSize = 20.sp,
                color = colorResource(id = R.color.voilet),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            OutlinedButton(
                onClick = {
                    oBackgroundColor.value = R.color.voilet
                    xBackgroundColor.value = R.color.light_grey
                    winnerName.value = ""
                    isTrunOfO = true
                    points.clear()
                    clickEvent()
                },
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Reset",
                    color = colorResource(id = R.color.black),
                )
            }
        }
    }
}

data class GameDataModel(
    var pointName: String = "",
    var backgroundColor: Int,
    var point: Int,
    var pointIndex: Int
)

private var isTrunOfO = true

@ExperimentalFoundationApi
@Composable
fun InputBoxForGame() {
    val context = LocalContext.current
    val gameDataList = remember {
        mutableStateListOf<GameDataModel>()
    }

    var itsXTurn = false

    for (i in 0..8) {
        gameDataList.add(GameDataModel("", R.color.white, -1, i))
    }

    for (i in 0..8) {
        points.add(-1)
    }

    Log.d(ContentValues.TAG, "InputBoxForGame: ${gameDataList}, $points")

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 20.dp)
        ) {
            LazyVerticalGrid(cells = GridCells.Fixed(3) , content = {
                items(gameDataList.size) { index ->
                    val gameModel = gameDataList[index]
                    Box(
                        modifier = Modifier
                            .background(
                                color = colorResource(id = gameModel.backgroundColor)
                            )
                            .height(120.dp)
                            .border(
                                width = 1.dp,
                                color = colorResource(id = R.color.grey)
                            )
                            .clickable {
                                if (gameModel.pointName.isEmpty() || gameModel.pointName == "") {
                                    if (!checkWinner(context, gameDataList)) {
                                        if (!points.contains(-1)) {
                                            itsXTurn = false
                                            winnerName.value = "Game draw, Reset & play again."
                                        } else {
                                            if (itsXTurn) {
                                                val game =
                                                    GameDataModel(
                                                        "X",
                                                        R.color.light_voilet,
                                                        1,
                                                        index
                                                    )
                                                gameDataList.set(index, game)
                                            } else {
                                                val game =
                                                    GameDataModel(
                                                        "O",
                                                        R.color.light_grey,
                                                        0,
                                                        index
                                                    )
                                                gameDataList.set(index, game)
                                            }
                                            itsXTurn = !itsXTurn

                                        }
                                        if (!checkWinner(context, gameDataList)) {
                                            if (!points.contains(-1)) {
                                                itsXTurn = false
                                                winnerName.value = "Game dram, Reset & play again."
                                            }
                                        }
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = gameModel.pointName,
                            modifier = Modifier
                                .align(alignment = Alignment.Center),
                            fontSize = 50.sp,
                        )
                    }
                }
            })
        }
    }
}

fun checkWinner(context: Context, list: SnapshotStateList<GameDataModel>): Boolean {
    val xWin = 1
    val oWin = 0
    var result = false

    list.forEach {
        points.set(it.pointIndex, it.point)
    }
    if (list.size >= 9) {
        if (xWin == points[0] && xWin == points[1] && xWin == points[2]) {
            showWinner("X")
            result = true
        } else if (xWin == points[3] && xWin == points[4] && xWin == points[5]) {
            showWinner("X")
            result = true
        } else if (xWin == points[6] && xWin == points[7] && xWin == points[8]) {
            showWinner("X")
            result = true
        } else if (xWin == points[0] && xWin == points[4] && xWin == points[8]) {
            showWinner("X")
            result = true
        } else if (xWin == points[2] && xWin == points[4] && xWin == points[6]) {
            showWinner("X")
            result = true
        } else if (xWin == points[0] && xWin == points[3] && xWin == points[6]) {
            showWinner("X")
            result = true
        } else if (xWin == points[1] && xWin == points[4] && xWin == points[7]) {
            showWinner("X")
            result = true
        } else if (xWin == points[2] && xWin == points[5] && xWin == points[8]) {
            showWinner("X")
            result = true
        }

        if (oWin == points[0] && oWin == points[1] && oWin == points[2]) {
            showWinner("O")
            result = true
        } else if (oWin == points[3] && oWin == points[4] && oWin == points[5]) {
            showWinner("O")
            result = true
        } else if (oWin == points[6] && oWin == points[7] && oWin == points[8]) {
            showWinner("O")
            result = true
        } else if (oWin == points[0] && oWin == points[4] && oWin == points[8]) {
            showWinner("O")
            result = true
        } else if (oWin == points[2] && oWin == points[4] && oWin == points[6]) {
            showWinner("O")
            result = true
        } else if (oWin == points[0] && oWin == points[3] && oWin == points[6]) {
            showWinner("O")
            result = true
        } else if (oWin == points[1] && oWin == points[4] && oWin == points[7]) {
            showWinner("O")
            result = true
        } else if (oWin == points[2] && oWin == points[5] && oWin == points[8]) {
            showWinner("O")
            result = true
        }
    }
    return result
}

private var points = ArrayList<Int>()

private var winnerName = mutableStateOf("")

fun showWinner(winner: String) {
    winnerName.value = "The winner is " + winner
    Log.d(ContentValues.TAG, "showWinner: is $winner")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreviewTicTacToe() {
    TicTacToeTheme {
        RootView(clickEvent={

        })
    }
}