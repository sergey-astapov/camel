package com.eventhandler.common.context;

import lombok.EqualsAndHashCode;

import java.io.Serializable;

public abstract class ContextState implements Serializable {
    @EqualsAndHashCode(callSuper = false)
    public static class Idle extends ContextState {}

    @EqualsAndHashCode(callSuper = false)
    public static class Starting extends ContextState {}

    @EqualsAndHashCode(callSuper = false)
    public static class Running extends ContextState {}

    @EqualsAndHashCode(callSuper = false)
    public static class Stopping extends ContextState {}
}
