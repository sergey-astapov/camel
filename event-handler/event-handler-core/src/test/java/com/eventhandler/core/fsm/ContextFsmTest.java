package com.eventhandler.core.fsm;

import com.eventhandler.common.protocol.InterContext;
import com.eventhandler.common.protocol.StartContext;
import com.eventhandler.common.protocol.StopContext;
import org.junit.Test;
import ru.yandex.qatools.fsm.StateMachineException;
import ru.yandex.qatools.fsm.Yatomata;
import ru.yandex.qatools.fsm.impl.FSMBuilder;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ContextFsmTest {

    public static final String UID = "UID";
    public static final String DATA = "DATA";

    @Test
    public void test() {
        Yatomata<ContextFsm> fsm = new FSMBuilder<>(ContextFsm.class).build();
        assertTrue(fsm.getCurrentState() instanceof ContextState.Idle);

        fsm.fire(StartContext.builder()
                .runId(UID)
                .data(DATA)
                .build());
        assertTrue(fsm.getCurrentState() instanceof ContextState.Running);

        fsm.fire(InterContext.builder()
                .runId(UID)
                .data(DATA)
                .build());
        assertTrue(fsm.getCurrentState() instanceof ContextState.Running);

        fsm.fire(StopContext.builder()
                .runId(UID)
                .data(DATA)
                .total(1L)
                .build());
        assertTrue(fsm.getCurrentState() instanceof ContextState.WaitingLast);
        assertTrue(fsm.isCompleted());
    }

    @Test
    public void test2() {
        Yatomata<ContextFsm> fsm = new FSMBuilder<>(ContextFsm.class).build();
        assertTrue(fsm.getCurrentState() instanceof ContextState.Idle);

        fsm.fire(InterContext.builder()
                .runId(UID)
                .data(DATA)
                .build());
        assertTrue(fsm.getCurrentState() instanceof ContextState.WaitingStart);

        assertThat(fsm.getFSM().getEvents().size(), is(1));

        fsm.fire(StartContext.builder()
                .runId(UID)
                .data(DATA)
                .build());
        assertTrue(fsm.getCurrentState() instanceof ContextState.Running);
        assertThat(fsm.getFSM().getEvents().size(), is(0));
        assertThat(fsm.getFSM().getCurrent(), is(1L));

        fsm.fire(StopContext.builder()
                .runId(UID)
                .data(DATA)
                .total(2L)
                .build());
        assertTrue(fsm.getCurrentState() instanceof ContextState.WaitingLast);

        fsm.fire(InterContext.builder()
                .runId(UID)
                .data(DATA)
                .build());
        assertTrue(fsm.getCurrentState() instanceof ContextState.WaitingLast);
        assertTrue(fsm.isCompleted());
    }

    @Test(expected = StateMachineException.class)
    public void testError() {
        Yatomata<ContextFsm> fsm = new FSMBuilder<>(ContextFsm.class).build();
        fsm.fire(StartContext.builder()
                .runId(UID)
                .data(DATA)
                .build());
        fsm.fire(InterContext.builder()
                .runId("UID-2")
                .build());
    }
}
