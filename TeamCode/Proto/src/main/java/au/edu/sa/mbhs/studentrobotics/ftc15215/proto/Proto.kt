package au.edu.sa.mbhs.studentrobotics.ftc15215.proto

import au.edu.sa.mbhs.studentrobotics.bunyipslib.RobotConfig
import au.edu.sa.mbhs.studentrobotics.bunyipslib.hardware.Motor
import au.edu.sa.mbhs.studentrobotics.bunyipslib.localization.TwoWheelLocalizer
import au.edu.sa.mbhs.studentrobotics.bunyipslib.roadrunner.parameters.DriveModel
import au.edu.sa.mbhs.studentrobotics.bunyipslib.roadrunner.parameters.MecanumGains
import au.edu.sa.mbhs.studentrobotics.bunyipslib.roadrunner.parameters.MotionProfile
import au.edu.sa.mbhs.studentrobotics.bunyipslib.subsystems.DualServos
import au.edu.sa.mbhs.studentrobotics.bunyipslib.subsystems.HoldableActuator
import au.edu.sa.mbhs.studentrobotics.bunyipslib.subsystems.Switch
import au.edu.sa.mbhs.studentrobotics.bunyipslib.subsystems.drive.MecanumDrive
import au.edu.sa.mbhs.studentrobotics.ftc15215.proto.components.LinkedLift
import com.acmerobotics.roadrunner.ftc.LazyImu
import com.acmerobotics.roadrunner.ftc.RawEncoder
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Servo

/**
 * FTC 15215 INTO THE DEEP 2024-2025 robot configuration
 *
 * @author Lucas Bubner, 2024
 */
class Proto : RobotConfig() {
    /**
     * Non-subsystem hardware instances.
     */
    val hw: Hardware = Hardware()

    /**
     * RoadRunner Mecanum Drive with Two-Wheel Localization.
     */
    lateinit var drive: MecanumDrive

    /**
     * Dual servos for the claw mechanism.
     */
    lateinit var claws: DualServos

    /**
     * `claws` rotation mechanism.
     */
    lateinit var clawRotator: Switch

    /**
     * Vertical lift for the claw mechanism (`claws` and `clawRotator`).
     */
    lateinit var clawLift: HoldableActuator

    /**
     * Linked ascension mechanism for the claw lift.
     */
    lateinit var ascent: LinkedLift

    override fun onRuntime() {
        // Base is from GLaDOS
        hw.imu = getLazyImu(
            orientationOnRobot = RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.RIGHT
            )
        )
        hw.fl = getHardware("fl", DcMotorEx::class.java) {
            it.direction = DcMotorSimple.Direction.FORWARD
        }
        hw.bl = getHardware("bl", DcMotorEx::class.java) {
            it.direction = DcMotorSimple.Direction.REVERSE
        }
        hw.br = getHardware("br", DcMotorEx::class.java) {
            it.direction = DcMotorSimple.Direction.REVERSE
        }
        hw.fr = getHardware("fr", DcMotorEx::class.java) {
            it.direction = DcMotorSimple.Direction.REVERSE
        }

        // REV Through Bore Encoders
        hw.pe = getHardware("pe", RawEncoder::class.java) {
            it.direction = DcMotorSimple.Direction.REVERSE
        }
        hw.ppe = getHardware("ppe", RawEncoder::class.java) {
            it.direction = DcMotorSimple.Direction.FORWARD
        }

        // End effectors
        hw.leftClaw = getHardware("lc", Servo::class.java)
        hw.rightClaw = getHardware("rc", Servo::class.java)
        hw.clawRotator = getHardware("cr", Servo::class.java) {
            // TODO: clawRotator limits (scale range)
            it.scaleRange(0.0, 1.0)
        }

        // TODO: kG and PID
        hw.clawLift = getHardware("cl", Motor::class.java)

        hw.leftAscent = getHardware("la", Motor::class.java) {
            // TODO: configuration
        }
        hw.rightAscent = getHardware("ra", Motor::class.java) // child of leftAscent, controller configured in LinkedLift

        // RoadRunner drivebase configuration
        val dm = DriveModel.Builder()
            .setInPerTick(0.0)
            .setLateralInPerTick(0.0)
            .setTrackWidthTicks(0.0)
            .build()
        val mp = MotionProfile.Builder()
            // TODO: constraints and tuning
            .setKs(0.0)
            .setKv(0.0)
            .setKa(0.0)
            .build()
        val mg = MecanumGains.Builder()
            .setAxialGain(0.0)
            .setLateralGain(0.0)
            .setHeadingGain(0.0)
            .setAxialVelGain(0.0)
            .setLateralVelGain(0.0)
            .setHeadingVelGain(0.0)
            .build()
        val twl = TwoWheelLocalizer.Params.Builder()
            .setParYTicks(0.0)
            .setPerpXTicks(0.0)
            .build()

        drive = MecanumDrive(dm, mp, mg, hw.fr!!, hw.fl!!, hw.br!!, hw.bl!!, hw.imu!!, hardwareMap.voltageSensor)
            .withLocalizer(TwoWheelLocalizer(dm, twl, hw.pe!!, hw.ppe!!, hw.imu!!.get()))
            .withName("Drive")
        claws = DualServos(hw.leftClaw!!, hw.rightClaw!!)
            .withName("Claws")
        clawRotator = Switch(hw.clawRotator!!)
            .withName("Claw Rotator")
        clawLift = HoldableActuator(hw.clawLift!!)
            .withName("Claw Lift")
    }

    /**
     * Raw hardware instances.
     */
    class Hardware {
        /**
         * Expansion 0: Front Left "fl"
         */
        var fl: DcMotorEx? = null

        /**
         * Expansion 1: Front Right "fr"
         */
        var fr: DcMotorEx? = null

        /**
         * Expansion 2: Back Right "br"
         */
        var br: DcMotorEx? = null

        /**
         * Expansion 3: Back Left "bl"
         */
        var bl: DcMotorEx? = null

        /**
         * Control 3: Parallel Encoder "pe"
         */
        var pe: RawEncoder? = null

        /**
         * Control 2: Perpendicular Encoder "ppe"
         */
        var ppe: RawEncoder? = null

        /**
         * Internally mounted on I2C C0 "imu"
         */
        var imu: LazyImu? = null

        /**
         * ?: Left Claw "lc"
         */
        var leftClaw: Servo? = null

        /**
         * ?: Right Claw "rc"
         */
        var rightClaw: Servo? = null

        /**
         * ?: Claw Rotator "cr"
         */
        var clawRotator: Servo? = null

        /**
         * ?: Claw Lift "cl"
         */
        var clawLift: Motor? = null

        /**
         * ?: Left Ascent "la"
         */
        var leftAscent: Motor? = null

        /**
         * ?: Right Ascent "ra"
         */
        var rightAscent: Motor? = null
    }
}