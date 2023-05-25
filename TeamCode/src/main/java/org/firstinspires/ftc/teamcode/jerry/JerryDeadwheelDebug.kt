package org.firstinspires.ftc.teamcode.jerry

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.common.BunyipsOpMode
import org.firstinspires.ftc.teamcode.common.Deadwheels
import org.firstinspires.ftc.teamcode.common.XYEncoder
import org.firstinspires.ftc.teamcode.jerry.components.JerryConfig

/**
 * Debug opmode for deadwheel readouts.
 */
@TeleOp(name = "<JERRY> Deadwheel Debug Data")
class JerryDeadwheelDebug : BunyipsOpMode() {
    private var config: JerryConfig? = null
    private var pos: Deadwheels? = null

    override fun onInit() {
        config = JerryConfig.newConfig(hardwareMap, telemetry)
        pos = Deadwheels(this, config?.fl!!, config?.fr!!)
        pos?.enableTracking(XYEncoder.Axis.BOTH)
    }

    override fun activeLoop() {
        telemetry.addLine("X Encoder: ${pos?.encoderReading(XYEncoder.Axis.X)}")
        telemetry.addLine("Y Encoder: ${pos?.encoderReading(XYEncoder.Axis.Y)}")
        telemetry.addLine("X MM: ${pos?.travelledMM(XYEncoder.Axis.X)}")
        telemetry.addLine("Y MM: ${pos?.travelledMM(XYEncoder.Axis.Y)}")
    }
}