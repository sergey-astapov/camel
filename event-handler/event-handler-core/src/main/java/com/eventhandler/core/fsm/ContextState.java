package com.eventhandler.core.fsm;

public abstract class ContextState {
    public static class Idle extends ContextState {}
    public static class Running extends ContextState {}
    public static class WaitingStart extends ContextState {}
    public static class WaitingLast extends ContextState {}
}
