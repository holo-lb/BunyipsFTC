package org.murraybridgebunyips.jerry.debug

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.murraybridgebunyips.bunyipslib.BunyipsOpMode
import org.murraybridgebunyips.bunyipslib.vision.Vision
import org.murraybridgebunyips.bunyipslib.vision.processors.AprilTag
import org.murraybridgebunyips.jerry.components.JerryConfig

/**
 * Test opmode for new vision wrappers
 */
//@Disabled
@TeleOp(name = "Test Vision Wrapper")
class JerryNewVision : BunyipsOpMode() {
    private var config = JerryConfig()
    private var vision: Vision? = null
    private var aprilTag =
        AprilTag()

    override fun onInit() {
        config.init()
        vision = Vision(config.webcam!!)
        vision?.init(aprilTag)
        vision?.start(aprilTag)
    }

    override fun activeLoop() {
        // Vision is updated automatically by the SDK on another thread
        telemetry.add("FPS: ${vision?.fps}")
        telemetry.add("Status: ${vision?.status}")
        telemetry.add("AprilTag: ${aprilTag.data}")
    }
}