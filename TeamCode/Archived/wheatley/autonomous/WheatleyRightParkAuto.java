package org.firstinspires.ftc.teamcode.wheatley.autonomous;

import androidx.annotation.Nullable;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.firstinspires.ftc.teamcode.common.AutonomousBunyipsOpMode;
import org.firstinspires.ftc.teamcode.common.OpModeSelection;
import org.firstinspires.ftc.teamcode.common.tasks.AutoTask;
import org.firstinspires.ftc.teamcode.wheatley.components.WheatleyConfig;
import org.firstinspires.ftc.teamcode.wheatley.components.WheatleyMecanumDrive;
import org.firstinspires.ftc.teamcode.wheatley.tasks.WheatleyTimeDriveTask;

import java.util.Arrays;
import java.util.List;

/**
 * Parking autonomous for Wheatley
 * This Auto is for when you plan to park on the RIGHT of the backdrop
 * Make sure to coordinate with your alliance before selecting an Autonomous
 * <p></p>
 * A for Short Red
 * B for Short Blue
 * X for Long Red
 * Y for Long Blue
 *
 * @author Lachlan Paul, 2023
 */
@Autonomous(name = "RIGHT Park")
@Disabled
public class WheatleyRightParkAuto extends AutonomousBunyipsOpMode {
    private WheatleyConfig config = new WheatleyConfig();
    private WheatleyMecanumDrive drive;

    @Override
    protected void onInitialisation() {
        config = new WheatleyConfig();
        drive = new WheatleyMecanumDrive(this, config.fl, config.bl, config.fr, config.bl);
    }

    @Override
    protected List<OpModeSelection> setOpModes() {
        return Arrays.asList(
                new OpModeSelection("SHORT_BLUE"),
                new OpModeSelection("LONG_BLUE"),
                new OpModeSelection("SHORT_RED"),
                new OpModeSelection("LONG_RED")
        );
    }

    @Override
    protected AutoTask setInitTask() {
        return null;
    }

    @Override
    protected void onQueueReady(@Nullable Reference<?> selectedOpMode, Controls selectedButton) {
        // This is gonna end up looking pretty similar to GlaDOS's ParkAuto
        if (selectedOpMode == null) {
            // In case something messes up and we end up without an autonomous to do
            return;
        }

        switch (selectedOpMode.getName()) {
            case "SHORT_BLUE":
                break;
            case "LONG_BLUE":
                break;
            case "SHORT_RED":
                addTask(new WheatleyTimeDriveTask(this, 2, drive, 0.75, 0, 0));
                break;
            case "LONG_RED":
                break;
        }
    }
}
