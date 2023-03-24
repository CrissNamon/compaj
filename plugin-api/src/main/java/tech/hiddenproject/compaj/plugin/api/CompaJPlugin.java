package tech.hiddenproject.compaj.plugin.api;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents plugin for CompaJ.
 * All plugins must implement this interface.
 */
public interface CompaJPlugin {

  /**
   * @return List of classes that should be imported on system startup
   */
  default List<Class<?>> getClasses() {
    List<Class<?>> classes = Optional.ofNullable(this.getClass().getAnnotation(Exports.class))
        .stream()
        .flatMap(exports -> Stream.of(exports.value()))
        .collect(Collectors.toList());
    Optional.ofNullable(this.getClass().getAnnotation(ExportsSelf.class))
        .ifPresent(exportsSelf -> classes.add(this.getClass()));
    return classes;
  }

}
