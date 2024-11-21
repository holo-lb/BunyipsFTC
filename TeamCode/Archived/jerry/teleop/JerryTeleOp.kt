package org.murraybridgebunyips.jerry.teleop

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.murraybridgebunyips.bunyipslib.BunyipsOpMode
import org.murraybridgebunyips.bunyipslib.Direction
import org.murraybridgebunyips.bunyipslib.NullSafety
import org.murraybridgebunyips.bunyipslib.Threads
import org.murraybridgebunyips.bunyipslib.UserSelection
import org.murraybridgebunyips.bunyipslib.drive.CartesianFieldCentricMecanumDrive
import org.murraybridgebunyips.bunyipslib.drive.CartesianMecanumDrive
import org.murraybridgebunyips.jerry.components.JerryConfig
import org.murraybridgebunyips.jerry.components.JerryLift

/**
 * Primary TeleOp for all of Jerry's functions.
 *
 * Uses gamepad1 for drive control and gamepad2 for lift control.
 * > gamepad1 left stick for driving
 * > gamepad1 right stick for turning
 * > gamepad2 left stick for lift movement
 * > gamepad2 A to open claw
 * > gamepad2 B to close claw
 *
 * @author Lucas Bubner, 2022
 */
@TeleOp(name = "TeleOp")
class JerryTeleOp : BunyipsOpMode() {
    private var config = JerryConfig()
    private var drive: CartesianMecanumDrive? = null
    private var lift: JerryLift? = null
    private val selector: UserSelection<String> =
        UserSelection({ initDrive() }, "POV", "FIELD-CENTRIC")

    override fun onInit() {
        // Configure drive and lift subsystems
        config.init()
        Threads.start(selector)
        drive?.setToBrake()
        if (NullSafety.assertNotNull(config.armComponents)) {
            lift = JerryLift(
                JerryLift.ControlMode.MANUAL,
                config.claw!!,
                config.arm1!!,
                config.arm2!!,
                config.limit!!
            )
        }
    }

    private fun initDrive() {
        if (NullSafety.assertNotNull(config.driveMotors)) {
            drive = if (selector.result == "FIELD-CENTRIC" || config.imu == null) {
                CartesianFieldCentricMecanumDrive(
                    config.fl!!,
                    config.fr!!,
                    config.bl!!,
                    config.br!!,
                    config.imu!!,
                    true,
                    Direction.FORWARD
                )
            } else {
                CartesianMecanumDrive(
                    config.fl!!,
                    config.fr!!,
                    config.bl!!,
                    config.br!!
                )
            }
        }
    }

    override fun onInitLoop(): Boolean {
        return !Threads.isRunning(selector)
    }

    override fun activeLoop() {
        // Set changing variables and gather raw data
        val x = gamepad1.left_stick_x.toDouble()
        val y = gamepad1.left_stick_y.toDouble()
        val r = gamepad1.right_stick_x.toDouble()
        val v = gamepad2.left_stick_y.toDouble()

//        addTelemetry(String.format(Locale.getDefault(),
//            "Controller: X: %.2f, Y: %.2f, R: %.2f", x, y, r))

        // Set speeds of motors and interpret any data
        drive?.setSpeedUsingController(x, y, r)
        lift?.delta(v)
        if (gamepad2.a) {
            lift?.open()
        } else if (gamepad2.b) {
            lift?.close()
        }

        if (gamepad2.left_bumper) {
            lift?.reset()
        }

        // Update live movements of all motors
        drive?.update()
        lift?.update()
    }
}
