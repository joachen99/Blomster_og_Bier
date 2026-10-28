    package in2000.gruppe8.domain

    import in2000.gruppe8.data.plants.PlantEntry
    import in2000.gruppe8.data.plants.getPlantsForSignals
    import in2000.gruppe8.data.climate.WeatherRepository
    import in2000.gruppe8.model.plants.ClimateSignal
    import in2000.gruppe8.regressionLine

    class ClimateProfileRepository(
        private val weatherRepository: WeatherRepository
    ) {
        companion object {
            private const val UPPER_THRESHOLD = 1.05
            private const val LOWER_THRESHOLD = 0.95
    }

        //Normal precipitation june-august 1961-1990
        suspend fun getEarlySummerPrecipitation(longitude: Double, latitude: Double): Int{
             return weatherRepository.getSummerPrecipitation(longitude, latitude, "1961/1990", "sum(precipitation_amount P1M)")
        }

        //Normal precipitation june-august 1991-2020
        suspend fun getLateSummerPrecipitation(longitude: Double, latitude: Double): Int{
            return weatherRepository.getSummerPrecipitation(longitude, latitude, "1991/2020", "sum(precipitation_amount P1M)")
        }

        //list of hours heavy rain pr year List<Pair<year, hours>>
        suspend fun getHeavyRain(longitude: Double, latitude: Double): List<Pair<Int, Double>>{
            return weatherRepository.getYearlyHeavyRain(longitude, latitude, "2023-01-01/2025-12-31", "sum(precipitation_amount PT1H)")
        }

        //list of yearly precipitation
        suspend fun getYearlyPrecipitation(longitude: Double, latitude: Double): List<Pair<Int, Double>>{
            return weatherRepository.getYearlyPrecipitation(longitude, latitude, "2000-01-01/2025-12-31", "sum(precipitation_amount P1Y)")
        }

        //regression on the list of yearly precipitation
        suspend fun getYearlyPrecipitationRegression(longitude: Double, latitude: Double): List<Pair<Int, Double>> {
            val yearly = getYearlyPrecipitation(longitude, latitude)
            return regressionLine(yearly)
        }

        //regression on the heavy rain list
        suspend fun getHeavyRainRegression(longitude: Double, latitude: Double): List<Pair<Int, Double>> {
            val yearly = getHeavyRain(longitude, latitude)
            return regressionLine(yearly)
        }

        //returns climate trend signal on heavy rain
        suspend fun calculateHeavyRainTrend(longitude: Double, latitude: Double): ClimateSignal{
            val rain = getHeavyRain(longitude, latitude)
            if(rain.size < 2){
                return ClimateSignal.STABLE
            }else{
                val score = (rain.last().second/rain.first().second)
                return when{
                    score >= UPPER_THRESHOLD -> ClimateSignal.HEAVY_RAIN
                    else -> ClimateSignal.STABLE
                }
            }
        }

        //returns climate trend signal on climate normals
        suspend fun calculateSummerPrecipitationTrend(longitude: Double, latitude: Double): ClimateSignal{
            val early = getEarlySummerPrecipitation(longitude, latitude)
            val late = getLateSummerPrecipitation(longitude, latitude)
            if(early == 0 || late == 0){
                return ClimateSignal.STABLE
            }

            val score = early.toDouble() / late.toDouble()
            return when{
                score <= LOWER_THRESHOLD -> ClimateSignal.WET_SUMMER
                score >= UPPER_THRESHOLD ->  ClimateSignal.DRY_SUMMER
                else -> ClimateSignal.STABLE
            }
        }

        suspend fun fetchPlants(longitude: Double, latitude: Double): Pair<ClimateSignal, List<PlantEntry>> {
            val signal = calculateSummerPrecipitationTrend(longitude, latitude)
            return Pair(signal, getPlantsForSignals(signal))
        }
    }