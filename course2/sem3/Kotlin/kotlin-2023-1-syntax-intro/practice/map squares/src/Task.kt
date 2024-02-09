fun mapSquares(values: IntArray): IntArray {
    return IntArray(values.size) {values[it] * values[it]}
}
