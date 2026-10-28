package in2000.gruppe8.model.climate.observation

import in2000.gruppe8.model.climate.source.Geometry

val Geometry.longitude: Double
    get() = coordinates.getOrNull(0) ?: 0.0

val Geometry.latitude: Double
    get() = coordinates.getOrNull(1) ?: 0.0
