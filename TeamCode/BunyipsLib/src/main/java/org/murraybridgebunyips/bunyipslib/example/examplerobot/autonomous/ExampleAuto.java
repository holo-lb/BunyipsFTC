package org.murraybridgebunyips.bunyipslib.example.examplerobot.autonomous;

import androidx.annotation.Nullable;

import org.murraybridgebunyips.bunyipslib.AutonomousBunyipsOpMode;
import org.murraybridgebunyips.bunyipslib.OpModeSelection;
import org.murraybridgebunyips.bunyipslib.example.examplerobot.components.ExampleConfig;
import org.murraybridgebunyips.bunyipslib.example.examplerobot.components.ExampleDrive;
import org.murraybridgebunyips.bunyipslib.example.examplerobot.tasks.ExampleTimeDriveTask;
import org.murraybridgebunyips.bunyipslib.tasks.AutoTask;

import java.util.List;

public class ExampleAuto extends AutonomousBunyipsOpMode {
    private final ExampleConfig config = new ExampleConfig();
    private ExampleDrive drive;

    @Override
    protected void onInitialisation() {
        config.init(this);
        drive = new ExampleDrive(this, config.leftMotor, config.rightMotor);
    }

    @Override
    protected List<OpModeSelection> setOpModes() {
        return null;
    }

    @Override
    protected AutoTask setInitTask() {
        return null;
    }

    @Override
    protected void onQueueReady(@Nullable OpModeSelection selectedOpMode) {
        addTask(new ExampleTimeDriveTask(this, 5.0, drive));
    }
}
