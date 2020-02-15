package org.terasology.gestalt.example.asteroids.common.engine.gamelogic;

import org.terasology.gestalt.entitysystem.component.Component;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for methods that process all entities with a set of required components every tick
 *
 * The methods should have the signature composed of
 * <ul>
 *     <li>EntityRef - the current entity</li>
 *     <li>components - required components, which will be populated from the current entity</li>
 * </ul>
 * e.g.
 * <pre>
 * {@literal @TickEntities}
 * public void moveCats(EntityRef entity, Location location, CatInfo catInfo, ComponentStore&lt;Location&gt; location) {...}
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface TickEntities {
}
