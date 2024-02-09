class IntMatrix(val rows: Int, val columns: Int) {
    private val matrix: IntArray

    init {
        require(rows > 0) { "Rows amount should be positive" }
        require(columns > 0) { "Rows amount should be positive" }
        this.matrix = IntArray(rows * columns)
    }

    private fun calcCell(row: Int, column: Int): Int {
        require(row in 0..<rows) { "Row value should be positive" }
        require(column in 0..<columns) { "Row value should be positive" }
        return columns * row + column
    }

    operator fun get(row: Int, column: Int): Int {
        return matrix[calcCell(row, column)]
    }

    operator fun set(row: Int, column: Int, value: Int) {
        matrix[calcCell(row, column)] = value
    }
}
