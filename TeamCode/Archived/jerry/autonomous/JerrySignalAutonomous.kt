package org.murraybridgebunyips.jerry.autonomous

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.murraybridgebunyips.bunyipslib.AutonomousBunyipsOpMode
import org.murraybridgebunyips.bunyipslib.Controls
import org.murraybridgebunyips.bunyipslib.Direction
import org.murraybridgebunyips.bunyipslib.IMUEx
import org.murraybridgebunyips.bunyipslib.NullSafety
import org.murraybridgebunyips.bunyipslib.Reference
import org.murraybridgebunyips.bunyipslib.drive.CartesianMecanumDrive
import org.murraybridgebunyips.bunyipslib.external.units.Units.Seconds
import org.murraybridgebunyips.bunyipslib.vision.Vision
import org.murraybridgebunyips.common.powerplay.GetSignalTask
import org.murraybridgebunyips.jerry.components.JerryConfig
import org.murraybridgebunyips.jerry.tasks.JerryPrecisionDriveTask

/**
 * Basic Signal read and park OpMode. Uses camera to read the signal and then drives to the correct square.
 */
@Suppress("KDocMissingDocumentation")
@Autonomous(
    name = "PowerPlay Auto Signal Read & Park",
    group = "JERRY",
    preselectTeleOp = "TeleOp"
)
class JerrySignalAutonomous : AutonomousBunyipsOpMode() {
    private var config = JerryConfig()
    private var cam: Vision? = null
    private var drive: CartesianMecanumDrive? = null
    private var imu: IMUEx? = null

    //    private var x: Odometer? = null
//    private var y: Odometer? = null
    private var tagtask: GetSignalTask? = null

    override fun onInitialise() {
        // Configuration of camera and drive components
        config.init()
        cam =
            Vision(config.webcam)
        if (NullSafety.assertNotNull(config.driveMotors))
            drive = CartesianMecanumDrive(
                config.fl!!,
                config.fr!!,
                config.bl!!,
                config.br!!
            )

//        if (NullSafety.assertNotNull(config.fl))
//            x = Odometer(this, config.fl!!, config.xDiameter, config.xTicksPerRev)
//
//        if (NullSafety.assertNotNull(config.fr))
//            y = Odometer(this, config.fr!!, config.yDiameter, config.yTicksPerRev)

        if (NullSafety.assertNotNull(config.imu))
            imu = IMUEx(config.imu!!)

        // Initialisation of guaranteed task loading completed. We can now dedicate our
        // CPU cycles to the init-loop and find the Signal position.
        tagtask = cam?.let { GetSignalTask(it) }

        tagtask?.let { setInitTask(it) }
    }

    override fun onReady(selectedOpMode: Reference<*>?, selectedButton: Controls) {
        // Use PrecisionDrive to move rightwards for 1.5 seconds
        // PrecisionDrive will take into account what components we are using and what it can do to achieve this goal.
        addTask(
            JerryPrecisionDriveTask(
                Seconds.of(4.0),
                drive,
                imu,
//                x,
//                y,
//                600.0,
                Direction.RIGHT,
                0.5
            )
        )
    }

    override fun onInitDone() {
        // Determine our final task based on the parking position from the camera
        // If on center or NONE, do nothing and just stay in the center
        val position = tagtask?.position
        telemetry.add("ParkingPosition set to: $position")

        // Add movement tasks based on the signal position
        if (position == Direction.LEFT) {
            // Drive forward if the position of the signal is LEFT
            addTaskFirst(
                JerryPrecisionDriveTask(
                    Seconds.of(3.5),
                    drive,
                    imu,
//                    x,
//                    y,
//                    400.0,
                    Direction.FORWARD,
                    0.5
                )
            )
        } else if (position == Direction.RIGHT) {
            // Drive backward if the position of the signal is RIGHT
            addTaskFirst(
                JerryPrecisionDriveTask(
                    Seconds.of(3.0),
                    drive,
                    imu,
//                    x,
//                    y,
//                    400.0,
                    Direction.BACKWARD,
                    0.5
                )
            )
        }
    }
}