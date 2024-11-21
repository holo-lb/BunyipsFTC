package org.murraybridgebunyips.jerry.autonomous

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import org.murraybridgebunyips.bunyipslib.AutonomousBunyipsOpMode
import org.murraybridgebunyips.bunyipslib.Controls
import org.murraybridgebunyips.bunyipslib.Direction
import org.murraybridgebunyips.bunyipslib.NullSafety
import org.murraybridgebunyips.bunyipslib.Reference
import org.murraybridgebunyips.bunyipslib.drive.CartesianMecanumDrive
import org.murraybridgebunyips.bunyipslib.external.units.Units.Seconds
import org.murraybridgebunyips.bunyipslib.vision.Vision
import org.murraybridgebunyips.common.powerplay.GetSignalTask
import org.murraybridgebunyips.jerry.components.JerryConfig
import org.murraybridgebunyips.jerry.tasks.JerryTimeDriveTask

/**
 * Basic Signal read and park OpMode. Uses camera to read the signal and then drives to the correct square.
 * Uses TimeDrive (which is deprecated), but works.
 */
@Suppress("KDocMissingDocumentation")
@Disabled
@Autonomous(
    name = "PowerPlay Signal Read & Park w/ TIME DRIVE",
    group = "JERRY",
    preselectTeleOp = "TeleOp"
)
class JerrySignalAutonomousBasic : AutonomousBunyipsOpMode() {
    private var config = JerryConfig()
    private var cam: Vision? = null
    private var drive: CartesianMecanumDrive? = null
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

        // Initialisation of guaranteed task loading completed. We can now dedicate our
        // CPU cycles to the init-loop and find the Signal position.
        tagtask = cam?.let { GetSignalTask(it) }

        tagtask?.let { setInitTask(it) }
    }

    override fun onReady(selectedOpMode: Reference<*>?, selectedButton: Controls) {
        addTask(JerryTimeDriveTask(Seconds.of(1.5), drive, 1.0, 0.0, 0.0))
    }

    override fun onInitDone() {
        // Determine our final task based on the parking position from the camera
        // If on center or NONE, do nothing and just stay in the center
        val position = tagtask?.position
        telemetry.add("ParkingPosition set to: $position")

        // Add movement tasks based on the signal position
        if (position == Direction.LEFT) {
            // Drive forward if the position of the signal is LEFT
            addTaskFirst(JerryTimeDriveTask(Seconds.of(1.5), drive, 0.0, 1.0, 0.0))
        } else if (position == Direction.RIGHT) {
            // Drive backward if the position of the signal is RIGHT
            addTaskFirst(JerryTimeDriveTask(Seconds.of(1.5), drive, 0.0, -1.0, 0.0))
        }
    }
}