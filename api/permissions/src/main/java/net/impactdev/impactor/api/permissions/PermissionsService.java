package net.impactdev.impactor.api.permissions;

import net.impactdev.impactor.api.core.ServiceProvider;
import net.kyori.adventure.util.TriState;

import java.util.function.Predicate;

public interface PermissionsService {

    /**
     * Validates if the command source has the specified permission. In most cases, {@link TriState#NOT_SET} will
     * link back to {@link TriState#FALSE}, depending on the implementation of a given permissions service.
     *
     * @param subject The subject being queried
     * @param permission The permission to query
     * @return A {@link TriState} representing the permission standing on the command source
     */
    TriState hasPermission(Subject subject, String permission);

    /**
     * Creates a {@link Predicate} responsible for verifying whether the source of an executed command
     * has a particular permission.
     * <p>
     * This type of predicate is meant to be chained in a manner such that further requirements for any particular
     * command can simply be appended to it via {@link Predicate#and(Predicate)}. As such, expected usage for
     * this predicate usage would be something along the lines of:
     * <ul><li>PermissionsService.validate(x).and(some other predicate)</li></ul>
     *
     * @param permission The permission to validate against the command source
     * @return A predicate which is responsible for the permissions validation
     */
    static Predicate<Subject> validate(String permission) {
        return stack -> ServiceProvider.instance().provide(PermissionsService.class).hasPermission(stack, permission).toBooleanOrElse(false);
    }

}
