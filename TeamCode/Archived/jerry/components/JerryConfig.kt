package org.murraybridgebunyips.jerry.components

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction
import com.qualcomm.robotcore.hardware.HardwareDevice
import com.qualcomm.robotcore.hardware.IMU
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.hardware.TouchSensor
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.murraybridgebunyips.bunyipslib.RobotConfig
import kotlin.properties.Delegates

/**
 * Jerry robot configuration and hardware declarations.
 */
class JerryConfig : RobotConfig() {
    /**
     * The monitor ID for the camera.
     */
    var monitorID by Delegates.notNull<Int>()

    /**
     * Control Hub "webcam" usb
     */
    var webcam: WebcamName? = null

    /**
     * Control Hub "bl" m0
     */
    var bl: DcMotorEx? = null

    /**
     * Control Hub "br" m1
     */
    var br: DcMotorEx? = null

    /**
     * Control Hub "fl" m2
     */
    var fl: DcMotorEx? = null

    /**
     * Control Hub "fr" m3
     */
    var fr: DcMotorEx? = null

    /**
     * Expansion Hub "claw" s0
     */
    var claw: Servo? = null

    /**
     * Expansion Hub "arm1" m0
     */
    var arm1: DcMotorEx? = null

    /**
     * Expansion Hub "arm2" m1
     */
    var arm2: DcMotorEx? = null

    /**
     * Expansion Hub "imu" i2c internal
     */
    var imu: IMU? = null

    /**
     * Expansion Hub "limit" i0
     */
    var limit: TouchSensor? = null

//    // Encoder configuration
//    val xDiameter = 50.0
//    val xTicksPerRev = 2400.0
//
//    // The Y encoder is the same as the X encoder
//    val yDiameter = xDiameter
//    val yTicksPerRev = xTicksPerRev

    /**
     * List of drive motors.
     */
    val driveMotors: List<DcMotorEx?>
        get() = listOf(bl, br, fl, fr)

    /**
     * List of arm components.
     */
    val armComponents: List<HardwareDevice?>
        get() = listOf(arm1, arm2, claw, limit)

    override fun onRuntime() {
        bl = getHardware("Back Left", DcMotorEx::class.java)
        br = getHardware("Back Right", DcMotorEx::class.java)
        fl = getHardware("Front Left", DcMotorEx::class.java)
        fr = getHardware("Front Right", DcMotorEx::class.java)
        arm1 = getHardware("Arm Motor 1", DcMotorEx::class.java)
        arm2 = getHardware("Arm Motor 2", DcMotorEx::class.java)
        claw = getHardware("Claw Servo", Servo::class.java)
        limit = getHardware("Arm Stop", TouchSensor::class.java)
        imu = getHardware("ch_imu", IMU::class.java)
        webcam = getHardware("Webcam", WebcamName::class.java)

        monitorID = hardwareMap.appContext.resources.getIdentifier(
            "cameraMonitorViewId", "id", hardwareMap.appContext.packageName
        )

        // Dead wheels were moved to other robots for future seasons in pairment with RoadRunner
        // Jerry is now a simple mecanum drive robot, and still relies on the older but reliable
        // CartesianMecanumDrive systems and manual handwritten movement generation tasks.
        // As such, dead wheel code has been commented out throughout the Jerry codebase.

        // Motor direction configuration
        fl?.direction = Direction.REVERSE
        fr?.direction = Direction.FORWARD
        bl?.direction = Direction.FORWARD
        br?.direction = Direction.REVERSE
        claw?.direction = Servo.Direction.FORWARD
        arm1?.direction = Direction.FORWARD
        arm2?.direction = Direction.REVERSE

        // Control Hub IMU configuration
        val parameters = IMU.Parameters(
            RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.LEFT
            )
        )
        imu?.initialize(parameters)
    }
}