package net.foulest.pixeladdons.util.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Foulest
 * @project PixelAddons
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    /**
     * The name of the command. If it is a sub command then its values would be separated by periods.
     * ie. a command that would be a sub command of test would be 'test.subcommandname'.
     */
    String name();

    /**
     * Gets the required permission of the command.
     */
    String permission() default "";

    /**
     * The message sent to the player when they do not have permission to execute it.
     */
    String noPerm() default "&cNo permission.";

    /**
     * A list of alternate names that the command is executed under.
     * See name() for details on how names work.
     */
    String[] aliases() default {};

    /**
     * The description that will appear in /help of the command.
     */
    String description();

    /**
     * The usage that will appear in /help (command).
     */
    String usage();

    /**
     * Whether or not the command is available to players only.
     */
    boolean inGameOnly() default false;
}
