package org.firstinspires.ftc.teamcode.jerry.components

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction
import com.qualcomm.robotcore.hardware.HardwareDevice
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.IMU
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.hardware.TouchSensor
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.teamcode.common.RobotConfig
import kotlin.properties.Delegates

/**
 * Jerry robot configuration and hardware declarations.
 */
class JerryConfig(override var hardwareMap: HardwareMap) : RobotConfig() {
    var monitorID by Delegates.notNull<Int>()
    var webcam: WebcamName? = null
    var bl: DcMotorEx? = null
    var br: DcMotorEx? = null
    var fl: DcMotorEx? = null
    var fr: DcMotorEx? = null
    var claw: Servo? = null
    var arm1: DcMotorEx? = null
    var arm2: DcMotorEx? = null
    var imu: IMU? = null
    var limit: TouchSensor? = null

    val driveMotors: List<DcMotorEx?>
        get() = listOf(bl, br, fl, fr)

    val armComponents: List<HardwareDevice?>
        get() = listOf(arm1, arm2, claw, limit)

    override fun init() {
        bl = getHardware("Back Left", DcMotorEx::class.java) as? DcMotorEx
        br = getHardware("Back Right", DcMotorEx::class.java) as? DcMotorEx
        fl = getHardware("Front Left", DcMotorEx::class.java) as? DcMotorEx
        fr = getHardware("Front Right", DcMotorEx::class.java) as? DcMotorEx
        arm1 = getHardware("Arm Motor 1", DcMotorEx::class.java) as? DcMotorEx
        arm2 = getHardware("Arm Motor 2", DcMotorEx::class.java) as? DcMotorEx
        claw = getHardware("Claw Servo", Servo::class.java) as? Servo
        limit = getHardware("Arm Stop", TouchSensor::class.java) as? TouchSensor
        imu = getHardware("ch_imu", IMU::class.java) as? IMU

        monitorID = hardwareMap.appContext.resources.getIdentifier(
            "cameraMonitorViewId", "id", hardwareMap.appContext.packageName
        )

        /*
           Deadwheels attached as an encoder on the front two motors
           X encoder = port 3, Front Left
           Y encoder = port 4, Front Right
         */

        // Motor direction configuration
        fl?.direction = Direction.REVERSE
        fr?.direction = Direction.FORWARD
        bl?.direction = Direction.FORWARD
        br?.direction = Direction.REVERSE

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