package au.edu.sa.mbhs.studentrobotics.cellphone.debug;

import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import au.edu.sa.mbhs.studentrobotics.bunyipslib.BunyipsOpMode;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.ContinuousTask;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.groups.DeadlineTaskGroup;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.Controls;
import au.edu.sa.mbhs.studentrobotics.cellphone.components.CellphoneConfig;

/**
 * Second edition of the fake mecanum drive (meccanum droive)
 */
@TeleOp
public class CellphoneMeccanumDroive2 extends BunyipsOpMode {
    private final CellphoneConfig config = new CellphoneConfig();

    @Override
    protected void onInit() {
        config.init();
    }

    @Override
    protected void activeLoop() {
        config.dummyDrive.setPower(Controls.vel(gamepad1.lsx, gamepad1.lsy, gamepad1.rsx));
        config.dummyDrive.periodic();
        if (gamepad1.a) {
            Actions.runBlocking(config.dummyDrive.makeTrajectory().lineToX(60).build());
        }
    }
}
