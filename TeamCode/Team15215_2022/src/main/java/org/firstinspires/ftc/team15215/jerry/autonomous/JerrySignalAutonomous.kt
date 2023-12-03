package org.firstinspires.ftc.team15215.jerry.autonomous

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import org.firstinspires.ftc.team15215.jerry.components.JerryConfig
import org.firstinspires.ftc.team15215.jerry.tasks.JerryPrecisionDriveTask
import org.murraybridgebunyips.ftc.bunyipslib.BunyipsOpMode
import org.murraybridgebunyips.ftc.bunyipslib.CartesianMecanumDrive
import org.murraybridgebunyips.ftc.bunyipslib.IMUOp
import org.murraybridgebunyips.ftc.bunyipslib.NullSafety
import org.murraybridgebunyips.ftc.bunyipslib.OpenCVCam
import org.murraybridgebunyips.ftc.bunyipslib.tasks.AutoTask
import org.murraybridgebunyips.ftc.bunyipslib.tasks.GetSignalTask
import java.util.ArrayDeque

/**
 * Basic Signal read and park OpMode. Uses camera to read the signal and then drives to the correct square.
 */
@Autonomous(
    name = "PowerPlay Auto Signal Read & Park",
    group = "JERRY",
    preselectTeleOp = "TeleOp"
)
class JerrySignalAutonomous : BunyipsOpMode() {
    private var config = JerryConfig()
    private var cam: OpenCVCam? = null
    private var drive: CartesianMecanumDrive? = null
    private var imu: IMUOp? = null

    //    private var x: Odometer? = null
//    private var y: Odometer? = null
    private var tagtask: GetSignalTask? = null
    private val tasks = ArrayDeque<AutoTask>()

    override fun onInit() {
        // Configuration of camera and drive components
        config.init(this)
        cam = OpenCVCam(this, config.webcam, config.monitorID)
        if (NullSafety.assertNotNull(config.driveMotors))
            drive = CartesianMecanumDrive(
                this,
                config.bl!!,
                config.br!!,
                config.fl!!,
                config.fr!!
            )

//        if (NullSafety.assertNotNull(config.fl))
//            x = Odometer(this, config.fl!!, config.xDiameter, config.xTicksPerRev)
//
//        if (NullSafety.assertNotNull(config.fr))
//            y = Odometer(this, config.fr!!, config.yDiameter, config.yTicksPerRev)

        if (NullSafety.assertNotNull(config.imu))
            imu = IMUOp(this, config.imu!!)

        // Initialisation of guaranteed task loading completed. We can now dedicate our
        // CPU cycles to the init-loop and find the Signal position.
        tagtask = cam?.let { GetSignalTask(this, it) }
    }

    override fun onInitLoop(): Boolean {
        // Using OpenCV and AprilTags in order to detect the Signal sleeve
        tagtask?.run()
        return tagtask?.isFinished() ?: true
    }

    override fun onInitDone() {
        // Determine our final task based on the parking position from the camera
        // If on center or NONE, do nothing and just stay in the center
        val position = tagtask?.position
        addTelemetry("ParkingPosition set to: $position")

        // Add movement tasks based on the signal position
        if (position == GetSignalTask.ParkingPosition.LEFT) {
            // Drive forward if the position of the signal is LEFT
            tasks.add(
                JerryPrecisionDriveTask(
                    this,
                    3.5,
                    drive,
                    imu,
//                    x,
//                    y,
//                    400.0,
                    JerryPrecisionDriveTask.Directions.FORWARD,
                    0.5
                )
            )
        } else if (position == GetSignalTask.ParkingPosition.RIGHT) {
            // Drive backward if the position of the signal is RIGHT
            tasks.add(
                JerryPrecisionDriveTask(
                    this,
                    3.0,
                    drive,
                    imu,
//                    x,
//                    y,
//                    400.0,
                    JerryPrecisionDriveTask.Directions.BACKWARD,
                    0.5
                )
            )
        }
        // Use PrecisionDrive to move rightwards for 1.5 seconds
        // PrecisionDrive will take into account what components we are using and what it can do to achieve this goal.
        tasks.add(
            JerryPrecisionDriveTask(
                this,
                4.0,
                drive,
                imu,
//                x,
//                y,
//                600.0,
                JerryPrecisionDriveTask.Directions.RIGHT,
                0.5
            )
        )
    }

    override fun activeLoop() {
        val currentTask = tasks.peekFirst()
        if (currentTask == null) {
            finish()
            return
        }
        currentTask.run()
        if (currentTask.isFinished()) {
            tasks.removeFirst()
        }
        if (tasks.isEmpty()) {
            drive?.stop()
        }
    }
}