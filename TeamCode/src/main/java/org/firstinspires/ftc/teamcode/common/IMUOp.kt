package org.firstinspires.ftc.teamcode.common

import com.qualcomm.hardware.bosch.BNO055IMU
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference
import org.firstinspires.ftc.robotcore.external.navigation.Orientation
import org.firstinspires.ftc.robotcore.external.navigation.Position
import org.firstinspires.ftc.robotcore.external.navigation.Velocity

/**
 * IMUOperation custom common class for internal BNO055IMUs
 * @author Lucas Bubner - FTC 15215 Captain; Oct 2022 - Murray Bridge Bunyips
 */
class IMUOp(opMode: BunyipsOpMode?, private val imu: BNO055IMU?) : BunyipsComponent(opMode) {
    @Volatile
    var currentAngles: Orientation? = null
    private var previousHeading = 0.0
    var capture: Double? = null
        private set

    // Offset used for Field-centric navigation, defined in FieldPositioning.kt
    // May not be used, but is here for any potential use cases.
    var offset = 0.0
        set(value) {
            if (value >= 0 && value < 360) field = value
        }

    /**
     * Get the current heading reading from the internal IMU, with support for 360 degree readings
     * Instead of using Euler readings, this will return a number within 0 to 360
     * @return Z value of Orientation axes in human-friendly reading range [0, 360]
     */
    var heading: Double? = 0.0
        get() {
            val currentHeading = currentAngles?.thirdAngle?.toDouble()?.plus(offset)
            var delta = currentHeading?.minus(previousHeading)

            // Detects if there is a sudden 180 turn which means we have turned more than the 180
            // degree threshold. Adds 360 to additively inverse the value and give us a proper delta
            if (delta != null) {
                if (delta < -180) {
                    delta += 360.0
                } else if (delta >= 180) {
                    delta -= 360.0
                }
            }
            field = delta?.let { field?.plus(it) }

            if (currentHeading != null) {
                previousHeading = currentHeading
            }

            // Limit heading readings to only be in a (0, 360) index
            if (field!! >= 360.0) {
                field = field!! - 360.0
            } else if (field!! < 0.0) {
                field = field!! + 360.0
            }
            return field
        }
        private set

    /**
     * Update the latest state in the IMU to current data
     */
    fun tick() {
        opMode!!.telemetry.addLine(this.currentAngles.toString())
        this.currentAngles = imu?.getAngularOrientation(
            AxesReference.INTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES
        )
    }

    /**
     * Get the current roll reading from the internal IMU
     * @return Y value of Orientation axes
     */
    val roll: Double?
        get() = this.currentAngles?.secondAngle?.toDouble()

    /**
     * Get the current pitch reading from the internal IMU
     * @return X value of Orientation axes
     */
    val pitch: Double?
        get() = this.currentAngles?.firstAngle?.toDouble()

    /**
     * Start PrecisionDrive IMU alignment algorithm and capture the original angle
     */
    fun startCapture() {
        imu?.startAccelerationIntegration(Position(), Velocity(), 50)
        this.tick()
        this.capture = this.heading
    }

    /**
     * Run a self-test of the heading capture system and return the result
     */
    fun selfTest(): Boolean {
        this.startCapture()
        this.tick()
        return if (this.currentAngles == null || this.capture == null) {
            // Yikes
            false
        } else {
            this.resetCapture()
            // Ready to go
            true
        }
    }

    /**
     * Stop and reset PrecisionDrive IMU alignment algorithm
     */
    fun resetCapture() {
        imu?.stopAccelerationIntegration()
        this.capture = null
    }

    /**
     * Query motor alignment speed for ROTATIONAL speed through PrecisionDrive
     * @param original_speed supply the intended speed for the R SPEED value
     * @param tolerance supply the tolerance in degrees for the IMU to be within to stop adjusting
     * @return queried speed based on parameters given, returns the unaltered speed if PrecisionDrive is not online
     */
    fun getRPrecisionSpeed(original_speed: Double, tolerance: Int): Double {
        // If we're not capturing, return the original speed
        if (this.capture == null) return original_speed

        // If something goes wrong in getting current IMU data, try to tick the IMU first, as the user may not have
        // reached that hardware cycle yet or has not called tick() in the main loop
        var current = this.heading
        if (current == null) {
            this.tick()
            // If still nothing, bail out.
            current = this.heading ?: return original_speed
        }

        // If we're at the minimum tolerance, increase turn rate
        if (current < this.capture!! - tolerance) return Math.abs(original_speed + 0.1)

        // If we're at maximum tolerance, decrease turn rate
        return if (current > this.capture!! + tolerance) Math.abs(original_speed - 0.1) else original_speed
    }
}