package in2000.gruppe8

import org.junit.Assert.assertEquals
import org.junit.Test

class RegressionUnitTest {

    @Test
    fun perfectLine() {
        // y = 2x + 1
        val points = listOf(
            0 to 1.0,
            1 to 3.0,
            2 to 5.0,
            3 to 7.0
        )

        val result = linearRegression(points)

        //Checking a and b in line
        assertEquals(2.0, result.a, 1e-9)
        assertEquals(1.0, result.b, 1e-9)

        //Checking prediction
        assertEquals(9.0, result.predict(4.0), 1e-9)
    }

    @Test
    fun straightLine() {
        // y = 5 , no trend
        val points = listOf(
            2019 to 5.0,
            2020 to 5.0,
            2021 to 5.0,
            2022 to 5.0
        )

        val result = linearRegression(points)
        //Checking line
        assertEquals(0.0, result.a, 1e-9)
        assertEquals(5.0, result.b, 1e-9)
    }

    @Test
    fun slightlyPositiveLine() {
        // y = 1/2 x + 1
        val points = listOf(
            0 to 1.0,
            1 to 1.6,
            2 to 2.2,
            3 to 2.7
        )

        val result = linearRegression(points)

        //Checking a and b in line
        assertEquals(0.5, result.a, 0.1)
        assertEquals(1.0, result.b, 0.2)
    }

    @Test
    fun wrongOrder() {
        // y = 2x + 1
        //data in wrong order
        val points = listOf(
            3 to 7.0,
            0 to 1.0,
            2 to 5.0,
            1 to 3.0
        )

        val line = regressionLine(points)

        //Order should be sorted
        val expected = listOf(
            0 to 1.0,
            1 to 3.0,
            2 to 5.0,
            3 to 7.0
        )

        //Checking size and order is as expected
        assertEquals(expected.size, line.size)
        expected.zip(line).forEach { (exp, actual) ->
            assertEquals(exp.first, actual.first)
            assertEquals(exp.second, actual.second, 1e-9)
        }
    }

    @Test
    fun realisticExample() {
        //Realistic set of data
        val points = listOf(
            2019 to 800.0,
            2020 to 810.0,
            2021 to 830.0,
            2022 to 840.0
        )

        val line = regressionLine(points)

        //Checking correct x-axis
        val years = line.map { it.first }
        assertEquals(listOf(2019, 2020, 2021, 2022), years)

        //Checking that the line is somewhat realistic
        line.zip(points).forEach { (reg, original) ->
            assertEquals(original.second, reg.second, 50.0)
        }
    }
}