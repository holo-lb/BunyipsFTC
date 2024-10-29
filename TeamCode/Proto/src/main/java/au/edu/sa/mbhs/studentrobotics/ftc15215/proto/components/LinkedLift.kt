package au.edu.sa.mbhs.studentrobotics.ftc15215.proto.components

import au.edu.sa.mbhs.studentrobotics.bunyipslib.hardware.Motor
import au.edu.sa.mbhs.studentrobotics.bunyipslib.subsystems.HoldableActuator

/**
 * Dual version of `HoldableActuator` for the Proto suspension lift.
 * Links one system controller to control two actuators.
 *
 * @author Lucas Bubner, 2024
 */
class LinkedLift(private val motor1: Motor, private val motor2: Motor) : HoldableActuator(motor1) {
    init {
        motor2.runToPositionController = motor1.runToPositionController
        motor2.zeroPowerBehavior = motor1.zeroPowerBehavior
        motor2.encoder.reset()
    }

    override fun periodic() {
        super.periodic()
        motor2.power = motor1.power
    }
}