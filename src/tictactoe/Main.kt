package tictactoe

fun main() {
    val game = TicTacToeGame()
    game.printGameGrid()

    while (!game.gameFinished) {

        val userCoords = readln().split(" ")
        game.playCoords(userCoords)
    }
}


enum class PLAYERS(val id: Char) {
    NONE('_'),
    X('X'),
    O('O')
}

enum class GAMEVENTS(val message: String) {
    X_WIN("X wins"),
    O_WIN("O wins"),
    DRAW("Draw"),
    OCCUPIED_COORDS("This cell is occupied! Choose another one!"),
    RANGE_COORDS("Coordinates should be from 1 to 3!"),
    ERROR_COORDS("You should enter numbers!")
}

class TicTacToeGame(
    var grid: List<List<Char>> = List(3) { List(3) { '_' } },
    var currentPlayer: PLAYERS = PLAYERS.X,
    var gameFinished: Boolean = false
) {

    fun playCoords(userCoords: List<String>) {
        if (isUserCoordsValid(userCoords)) {
            val coords = userCoords.map { it.toInt() }
            if (isCellCoordsEmpty(coords)) {
                grid = grid.run {
                    val tempGrid = this.map { it.toMutableList() }
                    tempGrid[coords.first() - 1][coords.last() - 1] = currentPlayer.id
                    tempGrid
                }
                printGameGrid()
                nextPlayer()
                checkGameState()
            }
        }
    }

    private fun isCellCoordsEmpty(coords: List<Int>): Boolean {
        return if (grid[coords.first() - 1][coords.last() - 1] == '_')
            true
        else {
            triggerEvent(GAMEVENTS.OCCUPIED_COORDS)
            false
        }
    }

    private fun isUserCoordsValid(userCoords: List<String>): Boolean {
        return if (userCoords.count { it.matches(Regex("[0-9]")) } == 2) {
            if (userCoords.count { it.matches(Regex("[1-3]")) } == 2) {
                true
            } else {
                triggerEvent(GAMEVENTS.RANGE_COORDS)
                false
            }
        } else {
            triggerEvent(GAMEVENTS.ERROR_COORDS)
            false
        }
    }

    // TODO need refactoring maybe better way to do it
    private fun checkGameState() {
        when (checkLinesWin(grid)) {
            PLAYERS.X -> {
                triggerEvent(GAMEVENTS.X_WIN)
                gameFinished = true
            }
            PLAYERS.O -> {
                triggerEvent(GAMEVENTS.O_WIN)
                gameFinished = true
            }
            PLAYERS.NONE ->
                when (checkDiagWin(grid)) {
                    PLAYERS.X -> {
                        triggerEvent(GAMEVENTS.X_WIN)
                        gameFinished = true
                    }
                    PLAYERS.O -> {
                        triggerEvent(GAMEVENTS.O_WIN)
                        gameFinished = true
                    }
                    PLAYERS.NONE ->
                        when (checkColsWin(grid)) {
                            PLAYERS.X -> {
                                triggerEvent(GAMEVENTS.X_WIN)
                                gameFinished = true
                            }
                            PLAYERS.O -> {
                                triggerEvent(GAMEVENTS.O_WIN)
                                gameFinished = true
                            }
                            PLAYERS.NONE -> {
                                if (isGameFinished(grid)) {
                                    triggerEvent(GAMEVENTS.DRAW)
                                    gameFinished = true
                                }
                            }
                        }
                }
        }
    }

    private fun checkLinesWin(grid: List<List<Char>>): PLAYERS {
        val linesWin = grid.map { it.toSet().size == 1 && it[0] != '_' }
        return if (linesWin.count { it } == 1) {
            if (grid[linesWin.indexOf(true)][0] == 'X') PLAYERS.X else PLAYERS.O
        } else
            PLAYERS.NONE
    }

    private fun checkColsWin(grid: List<List<Char>>): PLAYERS {
        val colsWin = mutableListOf<Int>()
        for (i in grid.indices) {
            if (grid[0][i] != '_' && grid[0][i] == grid[1][i] && grid[0][i] == grid[2][i])
                colsWin.add(i)
        }
        return if (colsWin.size != 0)
            if (grid[0][colsWin[0]] == 'X') PLAYERS.X else PLAYERS.O
        else
            PLAYERS.NONE
    }

    private fun checkDiagWin(grid: List<List<Char>>): PLAYERS {
        if (grid[1][1] != '_') {
            if ((grid[0][0] == grid[1][1] && grid[0][0] == grid[2][2])
                || (grid[0][2] == grid[1][1] && grid[0][2] == grid[2][0])
            )
                return if (grid[1][1] == 'X') PLAYERS.X else PLAYERS.O
        }
        return PLAYERS.NONE
    }

    private fun isGameFinished(grid: List<List<Char>>): Boolean {
        val nbEmptyCells = grid.filter { it.contains('_') }.count()
        return nbEmptyCells == 0
    }

    private fun nextPlayer() {
        currentPlayer = if (currentPlayer == PLAYERS.X)
            PLAYERS.O
        else
            PLAYERS.X
    }

    private fun triggerEvent(event: GAMEVENTS) = println(event.message)

    fun printGameGrid() {
        println("---------")
        grid.map { it.joinToString(" ", "| ", " |") }.forEach(::println)
        println("---------")
    }
}