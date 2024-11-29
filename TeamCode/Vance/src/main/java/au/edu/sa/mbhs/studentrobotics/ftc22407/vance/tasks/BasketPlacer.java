package au.edu.sa.mbhs.studentrobotics.ftc22407.vance.tasks;

import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Milliseconds;
import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Seconds;

import au.edu.sa.mbhs.studentrobotics.bunyipslib.subsystems.HoldableActuator;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.subsystems.Switch;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.subsystems.drive.MecanumDrive;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.RunForTask;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.WaitTask;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.groups.ParallelTaskGroup;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.groups.SequentialTaskGroup;

/**
 * Places sample in basket. Intended for use in Autonomous pathing
 *
 * @author Lachlan Paul, 2024
 */
public class BasketPlacer extends SequentialTaskGroup {
    /**
     * Sequence of events:<br>
     * 1. Move the vertical arm to the top basket<br>
     * 2. Drop the sample in<br>
     * 3. Move back a little bit just in case<br>
     * 4. Home the vertical arm<br>
     * @param verticalArm   the vertical arm that holds the basket
     * @param basketRotator the basket rotator hopefully still holding a sample
     * @param drive         the robot's drivebase
     */
    public BasketPlacer(HoldableActuator verticalArm, Switch basketRotator, MecanumDrive drive) {
        super(
//                verticalArm.tasks.home().timeout(Seconds.of(1)),  // try to reset encoders as best we can
                verticalArm.tasks.goTo(800),  // TODO: test
                basketRotator.tasks.open(),
                new WaitTask(Milliseconds.of(1000)),
                new RunForTask(Milliseconds.of(100), () ->
                        drive.setMotorPowers(-1, -1, -1, -1),
                        () -> drive.setMotorPowers(0, 0, 0, 0)),
                new ParallelTaskGroup(verticalArm.tasks.home().timeout(Seconds.of(1)), basketRotator.tasks.close())
        );
    }
}
