package it.unibo.acdingnet.protelis.model

import io.kotlintest.matchers.doubles.shouldBeExactly
import io.kotlintest.matchers.doubles.shouldBeGreaterThan
import io.kotlintest.specs.StringSpec

class LatLongPositionTest: StringSpec() {

    init {
        "distance between the same position should be 0" {
            LatLongPosition.zero().distanceTo(LatLongPosition.zero()) shouldBeExactly (0.0)
        }

        "distance between two positions should be symmetric" {
            val position1 = LatLongPosition(1.0, 5.0)
            val position2 = LatLongPosition(3.0, 5.0)

            position1.distanceTo(position2) shouldBeExactly position2.distanceTo(position1)
        }

        "distance between two different positions should be greater then 0" {
            val position1 = LatLongPosition(1.0, 5.0)
            val position2 = LatLongPosition(3.0, 5.0)

            position1.distanceTo(position2) shouldBeGreaterThan 0.0
            position2.distanceTo(position1) shouldBeGreaterThan 0.0
        }
    }
}