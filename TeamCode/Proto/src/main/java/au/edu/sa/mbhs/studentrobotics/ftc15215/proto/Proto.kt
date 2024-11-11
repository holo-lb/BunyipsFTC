package au.edu.sa.mbhs.studentrobotics.ftc15215.proto

import au.edu.sa.mbhs.studentrobotics.bunyipslib.BunyipsOpMode
import au.edu.sa.mbhs.studentrobotics.bunyipslib.RobotConfig
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.control.TrapezoidProfile
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.control.ff.ElevatorFeedforward
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.control.pid.PController
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Unit.Companion.of
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Inches
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.InchesPerSecond
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Seconds
import au.edu.sa.mbhs.studentrobotics.bunyipslib.hardware.Motor
import au.edu.sa.mbhs.studentrobotics.bunyipslib.hardware.ProfiledServo
import au.edu.sa.mbhs.studentrobotics.bunyipslib.localization.TwoWheelLocalizer
import au.edu.sa.mbhs.studentrobotics.bunyipslib.roadrunner.parameters.DriveModel
import au.edu.sa.mbhs.studentrobotics.bunyipslib.roadrunner.parameters.MecanumGains
import au.edu.sa.mbhs.studentrobotics.bunyipslib.roadrunner.parameters.MotionProfile
import au.edu.sa.mbhs.studentrobotics.bunyipslib.subsystems.DualServos
import au.edu.sa.mbhs.studentrobotics.bunyipslib.subsystems.HoldableActuator
import au.edu.sa.mbhs.studentrobotics.bunyipslib.subsystems.Switch
import au.edu.sa.mbhs.studentrobotics.bunyipslib.subsystems.drive.MecanumDrive
import au.edu.sa.mbhs.studentrobotics.bunyipslib.vision.Vision
import au.edu.sa.mbhs.studentrobotics.bunyipslib.vision.processors.intothedeep.YellowSample
import com.acmerobotics.roadrunner.ftc.LazyImu
import com.acmerobotics.roadrunner.ftc.RawEncoder
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.hardware.TouchSensor
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName

/**
 * FTC 15215 INTO THE DEEP 2024-2025 robot configuration
 *
 * @author Lucas Bubner, 2024
 */
class Proto : RobotConfig() {
    /**
     * Non-subsystem hardware instances.
     */
    val hw = Hardware()

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

//    /**
//     * Linked ascension mechanism for the claw lift.
//     */
//    lateinit var ascent: LinkedLift

    /**
     * Forward camera.
     */
    lateinit var camera: Vision

    /**
     * Yellow sample detector
     */
    lateinit var ys: YellowSample

    override fun onRuntime() {
        // Base is from GLaDOS
        hw.imu = getLazyImu(
            orientationOnRobot = RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.LEFT
            )
        )
        hw.fl = getHardware("fl", DcMotorEx::class.java) {
            it.direction = DcMotorSimple.Direction.FORWARD
        }
        hw.bl = getHardware("bl", DcMotorEx::class.java) {
            it.direction = DcMotorSimple.Direction.FORWARD
        }
        hw.br = getHardware("br", DcMotorEx::class.java) {
            it.direction = DcMotorSimple.Direction.REVERSE
        }
        hw.fr = getHardware("fr", DcMotorEx::class.java) {
            it.direction = DcMotorSimple.Direction.FORWARD
        }

        // REV Through Bore Encoders attached on ports 0 and 3 with the motors
        hw.pe = getHardware("fr", RawEncoder::class.java) {
            it.direction = DcMotorSimple.Direction.FORWARD
        }
        hw.ppe = getHardware("br", RawEncoder::class.java) {
            it.direction = DcMotorSimple.Direction.REVERSE
        }

        // End effectors
        hw.leftClaw = getHardware("lc", Servo::class.java) {
            it.direction = Servo.Direction.REVERSE
            it.scaleRange(0.6, 1.0)
        }
        hw.rightClaw = getHardware("rc", Servo::class.java) {
            it.direction = Servo.Direction.FORWARD
            it.scaleRange(0.0, 0.4)
        }
        hw.clawRotator = getHardware("cr", ProfiledServo::class.java) {
            it.setConstraints(TrapezoidProfile.Constraints(Constants.cr_v, Constants.cr_a))
            it.direction = Servo.Direction.REVERSE
            it.scaleRange(0.6, 1.0)
        }

        hw.clawLift = getHardware("cl", Motor::class.java) {
            it.direction = DcMotorSimple.Direction.REVERSE
            val p = PController(Constants.cl_kP)
            val ff = ElevatorFeedforward(0.0, Constants.cl_kG, 0.0, 0.0, { 0.0 }, { 0.0 })
            val c = p.compose(ff) { a, b -> a + b }
            it.runToPositionController = c
            BunyipsOpMode.ifRunning { o -> o.onActiveLoop({ c.setCoefficients(Constants.cl_kP, 0.0, 0.0, 0.0, 0.0, Constants.cl_kG, 0.0, 0.0) }) }
        }
        hw.bottom = getHardware("bottom", TouchSensor::class.java)

//        hw.leftAscent = getHardware("la", DcMotorEx::class.java) {
//            it.direction = DcMotorSimple.Direction.REVERSE
//            it.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
//        }
//        hw.rightAscent = getHardware("ra", DcMotorEx::class.java) {
//            it.direction = DcMotorSimple.Direction.REVERSE
//            it.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
//        }

        hw.camera = getHardware("webcam", WebcamName::class.java)

        // RoadRunner drivebase configuration
        val dm = DriveModel.Builder()
            .setDistPerTick(123.5 of Inches, 134005.0)
            .setLateralInPerTick(0.0007201181372108113)
            .setTrackWidthTicks(15480.209096658593)
            .build()
        val mp = MotionProfile.Builder()
            .setMaxWheelVel(40.0 of InchesPerSecond)
            .setKs(0.93)
            .setKv(0.00022)
            .setKa(0.00001)
            .build()
        val mg = MecanumGains.Builder()
            .setAxialGain(2.5)
            .setLateralGain(2.5)
            .setHeadingGain(4.0)
            .build()
        val twl = TwoWheelLocalizer.Params.Builder()
            .setParYTicks(-2109.055102501924)
            .setPerpXTicks(-5279.813561122253)
            .build()

//        val at = AprilTag {
//            AprilTag.setCameraPose(it)
//                .forward(Inches.of(8.0))
//                .right(Inches.one())
//                .apply()
//        }
        ys = YellowSample()
        camera = Vision(hw.camera)
            .init(ys)
            .start(ys)
            .flip()
        camera.startPreview()
        drive = MecanumDrive(dm, mp, mg, hw.fl, hw.bl, hw.br, hw.fr, hw.imu, hardwareMap.voltageSensor)
            .withLocalizer(TwoWheelLocalizer(dm, twl, hw.pe, hw.ppe, hw.imu?.get()))
            .withName("Drive")
        claws = DualServos(hw.leftClaw, hw.rightClaw)
            .withName("Claws")
        clawRotator = Switch(hw.clawRotator)
            .withName("Claw Rotator")
        clawLift = HoldableActuator(hw.clawLift)
            .withBottomSwitch(hw.bottom)
            .enableUserSetpointControl { dt -> dt * Constants.cl_TPS }
            .withMaxSteadyStateTime(10 of Seconds)
            .withUpperLimit(900)
            .withName("Claw Lift")
//        ascent = LinkedLift(hw.leftAscent!!, hw.rightAscent!!)
//            .withName("Ascender")
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
         * Expansion 3: Parallel Encoder "fr" (on motor)
         */
        var pe: RawEncoder? = null

        /**
         * Expansion 0: Perpendicular Encoder "br" (on motor)
         */
        var ppe: RawEncoder? = null

        /**
         * Internally mounted on I2C C0 "imu"
         */
        var imu: LazyImu? = null

        /**
         * Control S1: Left Claw "lc"
         */
        var leftClaw: Servo? = null

        /**
         * Control S0: Right Claw "rc"
         */
        var rightClaw: Servo? = null

        /**
         * Control S2: Claw Rotator "cr"
         */
        var clawRotator: Servo? = null

        /**
         * Control 1: Claw Lift "cl"
         */
        var clawLift: Motor? = null

        /**
         * Control Digital 1: "bottom" limit for claw lift
         */
        var bottom: TouchSensor? = null

//        /**
//         * Control 3: Left Ascent "la"
//         */
//        var leftAscent: DcMotorEx? = null
//
//        /**
//         * Control 0: Right Ascent "ra"
//         */
//        var rightAscent: DcMotorEx? = null

        /**
         * Control USB 3.0: Webcam
         */
        var camera: WebcamName? = null
    }
}