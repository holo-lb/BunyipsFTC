package au.edu.sa.mbhs.studentrobotics.ftc22407.vance.tasks;

import au.edu.sa.mbhs.studentrobotics.bunyipslib.subsystems.HoldableActuator;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.subsystems.Switch;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.subsystems.drive.MecanumDrive;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.groups.SequentialTaskGroup;

/**
 * Places sample in basket. Intended for Auto
 *
 * @author Lachlan Paul, 2024
 */
public class BasketPlacer extends SequentialTaskGroup {
    public BasketPlacer(HoldableActuator verticalArm, Switch basketRotator, MecanumDrive drive) {
        super(
                verticalArm.tasks.goTo(150),  // TODO: test
                basketRotator.tasks.open(),

                verticalArm.tasks.home()
        );
    }
}
