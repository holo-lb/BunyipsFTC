package org.firstinspires.ftc.teamcode.jerry.components

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.util.Range
import org.firstinspires.ftc.teamcode.common.BunyipsComponent
import org.firstinspires.ftc.teamcode.common.BunyipsOpMode
import java.util.*
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

class JerryDrive(
    opMode: BunyipsOpMode,
    private val bl: DcMotorEx?, private val br: DcMotorEx?,
    private val fl: DcMotorEx?, private val fr: DcMotorEx?
) : BunyipsComponent(opMode) {
    private val opmode: BunyipsOpMode? = null
    private var speedX = 0.0
    private var speedY = 0.0
    private var speedR = 0.0

    // Drive mode functionality to change between normal and rotational modes
    enum class MecanumDriveMode {
        NORMALIZED, ROTATION_PRIORITY_NORMALIZED
    }

    private var driveMode = MecanumDriveMode.NORMALIZED
    fun setDriveMode(mode: MecanumDriveMode) {
        driveMode = mode
    }

    init {
        // Encoders are not controlled by JerryDrive, but by their own configuration as stated in the Deadwheel class.
        assert(bl != null && br != null && fl != null && fr != null)
    }

    fun setToFloat() {
        bl?.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT
        br?.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT
        fl?.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT
        fr?.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT
    }

    fun setToBrake() {
        bl?.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        br?.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        fl?.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        fr?.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
    }

    /**
     * Call to update motor speeds through the selected drivemode.
     * Rotation Priority will calculate rotation speed before translation speed, while normalised
     * will do the opposite, calculating translation before rotati
     */
    fun update() {
        if (driveMode == MecanumDriveMode.ROTATION_PRIORITY_NORMALIZED) rotationalPriority()

        // Calculate motor powers
        var frontLeftPower = speedX + speedY - speedR
        var frontRightPower = speedX - speedY + speedR
        var backLeftPower = speedX - speedY - speedR
        var backRightPower = speedX + speedY + speedR

        // Get the maximum power from all four powers
        val maxPower = abs(frontLeftPower).coerceAtLeast(
            abs(frontRightPower).coerceAtLeast(
                abs(backLeftPower).coerceAtLeast(abs(backRightPower))
            )
        )

        // If the maximum number is greater than 1.0, then normalise by that number
        if (maxPower > 1.0) {
            frontLeftPower /= maxPower
            frontRightPower /= maxPower
            backLeftPower /= maxPower
            backRightPower /= maxPower
        }

        // Update motor powers with the calculated values
        fl?.power = frontLeftPower
        fr?.power = frontRightPower
        bl?.power = backLeftPower
        br?.power = backRightPower

        opMode!!.telemetry.addLine(
            String.format(
                Locale.getDefault(),
                "Mecanum Drive: X: %.2f, Y: %.2f, R: %.2f",
                -speedY,
                -speedX,
                speedR
            )
        )
    }

    /**
     * This method is automatically called when required from the driveMode.
     * Calculate rotational speed first, and use remaining headway for translation.
     */
    private fun rotationalPriority() {
        // Calculate motor powers
        val translationValues = doubleArrayOf(
            speedX + speedY,
            speedX - speedY,
            speedX - speedY,
            speedX + speedY
        )
        val rotationValues = doubleArrayOf(
            -speedR,
            speedR,
            -speedR,
            speedR
        )
        var scaleFactor = 1.0
        var tmpScale = 1.0

        // Solve this equation backwards:
        // MotorX = TranslationX * scaleFactor + RotationX
        // to find scaleFactor that ensures -1 <= MotorX <= 1 and 0 < scaleFactor <= 1
        for (i in 0..3) {
            if (abs(translationValues[i] + rotationValues[i]) > 1) {
                tmpScale = (1 - rotationValues[i]) / translationValues[i]
            } else if (translationValues[i] + rotationValues[i] < -1) {
                tmpScale = (rotationValues[i] - 1) / translationValues[i]
            }
            if (tmpScale < scaleFactor) {
                scaleFactor = tmpScale
            }
        }
        val frontLeftPower = translationValues[0] * scaleFactor + rotationValues[0]
        val frontRightPower = translationValues[1] * scaleFactor + rotationValues[1]
        val backLeftPower = translationValues[2] * scaleFactor + rotationValues[2]
        val backRightPower = translationValues[3] * scaleFactor + rotationValues[3]
        fl?.power = frontLeftPower
        fr?.power = frontRightPower
        bl?.power = backLeftPower
        br?.power = backRightPower
    }

    /**
     * Set all motor speeds to zero
     */
    fun deinit() {
        setSpeedXYR(0.0, 0.0, 0.0)
        update()
    }

    /**
     * @param speedX relative east-west speed - positive: east
     * @param speedY relative north-south speed - positive: south
     * @param speedR rotation speed - positive: clockwise
     */
    fun setSpeedXYR(speedX: Double, speedY: Double, speedR: Double) {
        // X and Y have been swapped, and X has been inverted
        // This is to calibrate the controller movement to the robot
        this.speedX = clipMotorPower(speedY)
        this.speedY = clipMotorPower(-speedX)
        this.speedR = clipMotorPower(speedR)
    }

    /**
     * @param speed speed at which the motors will operate
     * @param directionDegrees direction at which the motors will move toward
     * @param speedR rotation speed - positive: anti-clockwise
     */
    fun setSpeedPolarR(speed: Double, directionDegrees: Double, speedR: Double) {
        val radians = Math.toRadians(directionDegrees)
        speedX = clipMotorPower(speed * cos(radians))
        speedY = clipMotorPower(speed * sin(radians))
        this.speedR = clipMotorPower(speedR)
    }

    private fun clipMotorPower(p: Double): Double {
        return Range.clip(p, -1.0, 1.0)
    }
}