package in2000.gruppe8

//A simplified model for determining a trend through linear regression

data class LinearRegressionResult(
    val a: Double,
    val b: Double
) {
    //Simple function for a line: y = ax + b
    fun predict(x: Double): Double = a * x + b
}

fun linearRegression(points: List<Pair<Int, Double>>): LinearRegressionResult {
    // Making elements for regression using least squeares method
    val n = points.size
    val xs = points.map { it.first.toDouble() } //years list, x
    val ys = points.map { it.second } //precipitation list, y

    // Needs sum(x), sum(y), sum(xy) and sum(square(X))
    val sumX = xs.sum() //sum of year list
    val sumY = ys.sum() //sum of precipitation list
    val sumXY = xs.zip(ys).sumOf { (x, y) -> x * y } //
    val sumX2 = xs.sumOf { x -> x * x } //X squared
    val denominator = n * sumX2 - sumX * sumX

    //Calculation a and b
    val a = (n * sumXY - sumX * sumY) / denominator
    val b = (sumY - a * sumX) / n

    return LinearRegressionResult(
        a = a,
        b = b
    )
}

fun regressionLine(points: List<Pair<Int, Double>>): List<Pair<Int, Double>> {
    val regressionPoints = linearRegression(points)
    return points
        .sortedBy { it.first }
        .map { (year, _) ->
            year to regressionPoints.predict(year.toDouble())
        }
}