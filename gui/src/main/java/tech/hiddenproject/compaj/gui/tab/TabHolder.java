package tech.hiddenproject.compaj.gui.tab;

public enum TabHolder {

  INSTANCE;

  private final TerminalTab terminalTab;
  private final WorkSpaceTab workSpaceTab;

  TabHolder() {
    this.terminalTab = new TerminalTab();
    this.workSpaceTab = new WorkSpaceTab();
  }

  public TerminalTab getTerminalTab() {
    return terminalTab;
  }

  public WorkSpaceTab getWorkSpaceTab() {
    return workSpaceTab;
  }

}
