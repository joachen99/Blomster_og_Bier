package in2000.gruppe8.data.plants

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import in2000.gruppe8.R
import in2000.gruppe8.model.plants.ClimateSignal
import in2000.gruppe8.model.plants.WateringNeed

data class PlantEntry(
    val id: String,
    @param:StringRes val nameRes: Int,
    @param:StringRes val latinNameRes: Int,
    @param:StringRes val descriptionRes: Int,
    val bloomMonths: List<Int>,
    @param:StringRes val soilTypeRes: Int,
    val watering: WateringNeed,
    val hardinessZone: Int,
    val goodForBees: Boolean,
    val climateSignals: List<ClimateSignal>,
    @param:StringRes val tipsRes: Int,
    @param:DrawableRes val imageRes: Int? = null
)

val plantDatabase: List<PlantEntry> = listOf(

    // --- DRY_SUMMER ---

    PlantEntry(
        id = "lavendel",
        nameRes = R.string.plant_lavendel_name,
        latinNameRes = R.string.plant_lavendel_latin_name,
        descriptionRes = R.string.plant_lavendel_description,
        bloomMonths = listOf(6, 7, 8),
        soilTypeRes = R.string.plant_lavendel_soil_type,
        watering = WateringNeed.LOW,
        hardinessZone = 5,
        goodForBees = true,
        climateSignals = listOf(ClimateSignal.DRY_SUMMER),
        tipsRes = R.string.plant_lavendel_tips,
        imageRes = R.drawable.lavendel_lavandula_angustifolia
    ),

    PlantEntry(
        id = "kattemynte",
        nameRes = R.string.plant_kattemynte_name,
        latinNameRes = R.string.plant_kattemynte_latin_name,
        descriptionRes = R.string.plant_kattemynte_description,
        bloomMonths = listOf(6, 7, 8, 9),
        soilTypeRes = R.string.plant_kattemynte_soil_type,
        watering = WateringNeed.LOW,
        hardinessZone = 6,
        goodForBees = true,
        climateSignals = listOf(ClimateSignal.DRY_SUMMER),
        tipsRes = R.string.plant_kattemynte_tips,
        imageRes = R.drawable.prydkattemynte_nepeta_faassenii
    ),

    // --- WET_SUMMER ---

    PlantEntry(
        id = "sverdlilje",
        nameRes = R.string.plant_sverdlilje_name,
        latinNameRes = R.string.plant_sverdlilje_latin_name,
        descriptionRes = R.string.plant_sverdlilje_description,
        bloomMonths = listOf(6, 7),
        soilTypeRes = R.string.plant_sverdlilje_soil_type,
        watering = WateringNeed.MEDIUM,
        hardinessZone = 7,
        goodForBees = true,
        climateSignals = listOf(ClimateSignal.WET_SUMMER, ClimateSignal.HEAVY_RAIN),
        tipsRes = R.string.plant_sverdlilje_tips,
        imageRes = R.drawable.sverdlilje_iris_pseudacorus
    ),

    PlantEntry(
        id = "mynte",
        nameRes = R.string.plant_mynte_name,
        latinNameRes = R.string.plant_mynte_latin_name,
        descriptionRes = R.string.plant_mynte_description,
        bloomMonths = listOf(7, 8, 9),
        soilTypeRes = R.string.plant_mynte_soil_type,
        watering = WateringNeed.MEDIUM,
        hardinessZone = 7,
        goodForBees = true,
        climateSignals = listOf(ClimateSignal.WET_SUMMER),
        tipsRes = R.string.plant_mynte_tips,
        imageRes = R.drawable.mynte_mentha_spicata
    ),

    // --- HEAVY_RAIN ---

    PlantEntry(
        id = "daglilje",
        nameRes = R.string.plant_daglilje_name,
        latinNameRes = R.string.plant_daglilje_latin_name,
        descriptionRes = R.string.plant_daglilje_description,
        bloomMonths = listOf(6, 7, 8),
        soilTypeRes = R.string.plant_daglilje_soil_type,
        watering = WateringNeed.LOW,
        hardinessZone = 7,
        goodForBees = true,
        climateSignals = listOf(ClimateSignal.HEAVY_RAIN, ClimateSignal.WET_SUMMER, ClimateSignal.DRY_SUMMER),
        tipsRes = R.string.plant_daglilje_tips,
        imageRes = R.drawable.daglilje_hemerocallis
    ),

    PlantEntry(
        id = "blasvingel",
        nameRes = R.string.plant_blasvingel_name,
        latinNameRes = R.string.plant_blasvingel_latin_name,
        descriptionRes = R.string.plant_blasvingel_description,
        bloomMonths = listOf(6, 7),
        soilTypeRes = R.string.plant_blasvingel_soil_type,
        watering = WateringNeed.LOW,
        hardinessZone = 7,
        goodForBees = false,
        climateSignals = listOf(ClimateSignal.HEAVY_RAIN, ClimateSignal.DRY_SUMMER),
        tipsRes = R.string.plant_blasvingel_tips,
        imageRes = R.drawable.blasvingel_festuca_glauca
    ),

    // --- DRY_SUMMER ---

    PlantEntry(
        id = "oregano",
        nameRes = R.string.plant_oregano_name,
        latinNameRes = R.string.plant_oregano_latin_name,
        descriptionRes = R.string.plant_oregano_description,
        bloomMonths = listOf(7, 8, 9),
        soilTypeRes = R.string.plant_oregano_soil_type,
        watering = WateringNeed.LOW,
        hardinessZone = 6,
        goodForBees = true,
        climateSignals = listOf(ClimateSignal.DRY_SUMMER),
        tipsRes = R.string.plant_oregano_tips,
        imageRes = R.drawable.bergmynte_origanum_vulgare
    ),

    // --- STABLE ---

    PlantEntry(
        id = "ryllik",
        nameRes = R.string.plant_ryllik_name,
        latinNameRes = R.string.plant_ryllik_latin_name,
        descriptionRes = R.string.plant_ryllik_description,
        bloomMonths = listOf(6, 7, 8, 9),
        soilTypeRes = R.string.plant_ryllik_soil_type,
        watering = WateringNeed.LOW,
        hardinessZone = 7,
        goodForBees = true,
        climateSignals = listOf(ClimateSignal.STABLE),
        tipsRes = R.string.plant_ryllik_tips,
        imageRes = R.drawable.ryllik_achillea_millefolium
    ),

    PlantEntry(
        id = "akeleie",
        nameRes = R.string.plant_akeleie_name,
        latinNameRes = R.string.plant_akeleie_latin_name,
        descriptionRes = R.string.plant_akeleie_description,
        bloomMonths = listOf(5, 6, 7),
        soilTypeRes = R.string.plant_akeleie_soil_type,
        watering = WateringNeed.MEDIUM,
        hardinessZone = 7,
        goodForBees = true,
        climateSignals = listOf(ClimateSignal.STABLE),
        tipsRes = R.string.plant_akeleie_tips,
        imageRes = R.drawable.akeleie_aquilegia_vulgaris
    ),

    PlantEntry(
        id = "stjerneskjerm",
        nameRes = R.string.plant_stjerneskjerm_name,
        latinNameRes = R.string.plant_stjerneskjerm_latin_name,
        descriptionRes = R.string.plant_stjerneskjerm_description,
        bloomMonths = listOf(6, 7, 8),
        soilTypeRes = R.string.plant_stjerneskjerm_soil_type,
        watering = WateringNeed.MEDIUM,
        hardinessZone = 6,
        goodForBees = true,
        climateSignals = listOf(ClimateSignal.STABLE),
        tipsRes = R.string.plant_stjerneskjerm_tips,
        imageRes = R.drawable.stjerneskjerm_astrantia_major
    )
)

fun getPlantsForSignals(signal: ClimateSignal): List<PlantEntry> {
    return plantDatabase.filter { plant ->
        plant.climateSignals.any { it == signal }
    }.distinctBy { it.id }
}
