package org.murraybridgebunyips.jerry.debug

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.murraybridgebunyips.bunyipslib.BunyipsOpMode
import org.murraybridgebunyips.bunyipslib.vision.Vision
import org.murraybridgebunyips.common.powerplay.GetSignalTask
import org.murraybridgebunyips.jerry.components.JerryConfig

/**
 * Debug OpMode to continually output what AprilTag position the robot is currently seeing.
 */
@TeleOp(name = "PowerPlay Signal Debug")
class JerrySignalAnalyse : BunyipsOpMode() {
    private var config = JerryConfig()
    private var cam: Vision? = null
    private var task: GetSignalTask? = null

    override fun onInit() {
        config.init()
        cam = Vision(config.webcam)
        task = cam?.let { GetSignalTask(it) }
    }

    override fun activeLoop() {
        task?.run()
        telemetry.add("Currently seeing position: ${task?.position ?: "NONE"}")
    }
}