package it.unibo.acdingnet.protelis.model

data class LatLongPosition(val latitude: Double, val longitude: Double) {

    private val latR by lazy { Math.toRadians(latitude) }
    private val longR by lazy { Math.toRadians(longitude) }

    fun distanceTo(position: LatLongPosition): Double {
       val posLatR = Math.toRadians(position.latitude)
       val posLongR = Math.toRadians(position.longitude)

        val x = (posLongR - longR) * Math.cos((latR + posLatR) / 2)
        val y = (posLatR - latR)
        return Math.sqrt(x * x + y * y)
    }
}