package com.sequenceiq.cloudbreak.service.cluster;

public class Package {
    private String name;

    private PackageName pkg;

    private String command;

    private boolean prewarmed;

    private String grain;

    private String commandVersionPattern;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PackageName getPkg() {
        return pkg;
    }

    public void setPkg(PackageName pkg) {
        this.pkg = pkg;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public boolean isPrewarmed() {
        return prewarmed;
    }

    public void setPrewarmed(boolean prewarmed) {
        this.prewarmed = prewarmed;
    }

    public String getGrain() {
        return grain;
    }

    public void setGrain(String grain) {
        this.grain = grain;
    }

    public String getCommandVersionPattern() {
        return commandVersionPattern;
    }

    public void setCommandVersionPattern(String commandVersionPattern) {
        this.commandVersionPattern = commandVersionPattern;
    }
}
