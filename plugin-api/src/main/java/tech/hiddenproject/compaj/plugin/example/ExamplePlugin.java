package tech.hiddenproject.compaj.plugin.example;

import java.util.ArrayList;
import java.util.List;

import tech.hiddenproject.compaj.plugin.api.event.CompaJEvent;
import tech.hiddenproject.compaj.plugin.api.event.CompaJEvent.GLOBAL;
import tech.hiddenproject.compaj.plugin.api.CompaJPlugin;
import tech.hiddenproject.compaj.plugin.api.event.EventPublisher;
import tech.hiddenproject.compaj.plugin.api.Exports;
import tech.hiddenproject.compaj.plugin.api.ExportsSelf;
import tech.hiddenproject.compaj.plugin.api.event.PublisherSubscription;
import tech.hiddenproject.compaj.plugin.example.ExamplePlugin.ExamplePluginClass;

@Exports(ExamplePluginClass.class)
@ExportsSelf
public class ExamplePlugin implements CompaJPlugin {

  public ExamplePlugin() {
    PublisherSubscription subscription = EventPublisher.INSTANCE
        .subscribeOn(GLOBAL.STARTUP, this::catchStartupEvent);
  }

  private void catchStartupEvent(CompaJEvent event) {
    System.out.println("STARTUP: " + event);
  }

  public static class ExamplePluginClass {
    public static void say(String s) {
      System.out.println(s);
    }
  }
}
