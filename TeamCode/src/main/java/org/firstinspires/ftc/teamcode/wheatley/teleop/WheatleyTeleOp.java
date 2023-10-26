package org.firstinspires.ftc.teamcode.wheatley.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.common.BunyipsOpMode;
import org.firstinspires.ftc.teamcode.common.RobotConfig;
import org.firstinspires.ftc.teamcode.common.Suspender;
import org.firstinspires.ftc.teamcode.wheatley.components.WheatleyClaw;
import org.firstinspires.ftc.teamcode.wheatley.components.WheatleyConfig;
import org.firstinspires.ftc.teamcode.wheatley.components.WheatleyLift;
import org.firstinspires.ftc.teamcode.wheatley.components.WheatleyMecanumDrive;
import org.firstinspires.ftc.teamcode.wheatley.components.WheatleyRigArm;

/**
 * Primary TeleOp for all of Wheatley's functions.
 * Uses gamepad1 for drive control (presumably) and gamepad2 for both arms/claws
 * > gamepad1 left stick for driving
 * > gamepad1 right stick for turning
 * > gamepad2 left stick for Pixel Claw arm
 * > gamepad2 right stick for rigging arm rotation (this probably doesn't make sense without context)
 * > gamepad2 x operates the left pixel claw
 * > gamepad2 y operates the right pixel claw
 * > gamepad2 a extends rigging arm up
 * > gamepad2 b extends rigging arm down (i don't think this makes sense as a sentence)
 *
 * @author Lachlan Paul, 2023
 */

@TeleOp(name = "WHEATLEY: TeleOp", group = "WHEATLEY")
public class WheatleyTeleOp extends BunyipsOpMode {

    private WheatleyConfig config = new WheatleyConfig();
    private WheatleyClaw claw;
    private WheatleyLift lift;
    private WheatleyMecanumDrive drive;
    private WheatleyRigArm rigArm;

    @Override
    protected void onInit() {

        config = (WheatleyConfig) RobotConfig.newConfig(this, config, hardwareMap);

        drive = new WheatleyMecanumDrive(this, config.frontLeft, config.backLeft, config.frontRight, config.backRight);

    }

    @Override
    protected void activeLoop() {

        drive.setSpeedUsingController(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x);

        lift.armLift(gamepad2.left_stick_y);

        // Because of the way the rigging arm will actually use the stick,
        // this might need to be done differently.
        // This is because unlike the claw arm, the rigging arm will do a kind of rotation.
        // Basically like Genos rocketing himself up from the ground in One Punch Man.
        // (https://tenor.com/view/genos-one-punch-man-standing-gif-14470948)
        rigArm.armLift(gamepad2.right_stick_y);

        // TODO: Set safety measures that stops certain things from happening when the arm and
        // rotation are in certain states
        // Just need to find out why it doesn't get rotationStatus from Suspender properly
//        if (gamepad2.a || rotationStatus == Suspender.RotationStates.NOTSTOWED) {
//
//        }

        if (gamepad2.x) {
            claw.leftClaw();
        } else if (gamepad2.y) {
            claw.rightClaw();
        }

        // Adds a message on the driver hub stating the status of different controller inputs
        addTelemetry("Left Stick Y: " + gamepad1.left_stick_y);
        addTelemetry("Left Stick X: " + gamepad1.left_stick_x);

        // Gives a different message based on whether or not the camera is connected
        if (config.assertDevices(config.webCam)) {
            addTelemetry("Camera is connected");
        } else {
            addTelemetry("Camera is NOT connected");
        }

        /*
         * TODO: Pick out some good Wheatley voice lines for telemetry
         * Different lines will be displayed depending on different values
         * They should overwrite each other and NOT stack
         */
    }
}