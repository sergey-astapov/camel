package com.eventhandler.common.context;

import com.eventhandler.common.protocol.InterContext;
import com.eventhandler.common.protocol.StartContext;
import com.eventhandler.common.protocol.StopContext;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.qatools.fsm.FSMException;
import ru.yandex.qatools.fsm.StateMachineException;
import ru.yandex.qatools.fsm.impl.YatomataImpl;
import static com.eventhandler.common.context.ContextState.*;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ContextFsmTest {
    public static final String RUN_ID = "RUN_ID";
    public static final String DATA = "DATA";

    private YatomataImpl<ContextFsm> engine;

    @Before
    public void before() throws FSMException {
        Context state = Context.builder()
                .runId(RUN_ID)
                .current(0L)
                .state(new Idle())
                .build();
        engine = new YatomataImpl<>(ContextFsm.class, new ContextFsm(state));
        assertThat(engine.getCurrentState(), is(new Idle()));
        assertThat(engine.getFSM().getContext(), is(state));
    }

    @Test
    public void testIdleToRunning() throws FSMException {
        Running run = (Running)engine.fire(StartContext.builder().runId(RUN_ID).data(DATA).build());
        Context context = engine.getFSM().getContext();
        assertThat(context.getState(), is(run));
        assertThat(context.getRunId(), is(RUN_ID));
        assertThat(context.isRunning(), is(true));
        assertThat(context.isStopped(), is(false));

        run = (Running)engine.fire(InterContext.builder().runId(RUN_ID).data(DATA).build());
        context = engine.getFSM().getContext();
        assertThat(context.getState(), is(run));
        assertThat(context.getRunId(), is(RUN_ID));
        assertThat(context.getCurrent(), is(1L));
        assertThat(context.isRunning(), is(true));
        assertThat(context.isStopped(), is(false));

        Stopping stop = (Stopping)engine.fire(StopContext.builder().runId(RUN_ID).total(1L).data(DATA).build());
        assertThat(engine.isCompleted(), is(true));
        context = engine.getFSM().getContext();
        assertThat(context.getState(), is(stop));
        assertThat(context.getRunId(), is(RUN_ID));
        assertThat(context.getCurrent(), is(1L));
        assertThat(context.getTotal(), is(1L));
        assertThat(context.isRunning(), is(false));
        assertThat(context.isStopped(), is(true));
    }

    @Test
    public void testIdleToStarting() throws FSMException {
        Starting start = (Starting)engine.fire(InterContext.builder().runId(RUN_ID).data(DATA).build());
        Context context = engine.getFSM().getContext();
        assertThat(context.getState(), is(start));
        assertThat(context.getRunId(), is(RUN_ID));
        assertThat(context.getCurrent(), is(0L));
        assertThat(context.getWaiting(), is(1L));
        assertThat(context.isRunning(), is(false));
        assertThat(context.isStopped(), is(false));

        start = (Starting)engine.fire(StopContext.builder().runId(RUN_ID).total(1L).data(DATA).build());
        context = engine.getFSM().getContext();
        assertThat(context.getState(), is(start));
        assertThat(context.getRunId(), is(RUN_ID));
        assertThat(context.getWaiting(), is(1L));
        assertThat(context.getTotal(), is(1L));
        assertThat(context.isRunning(), is(false));
        assertThat(context.isStopped(), is(false));

        Running run = (Running)engine.fire(StartContext.builder().runId(RUN_ID).data(DATA).build());
        assertThat(engine.isCompleted(), is(true));
        context = engine.getFSM().getContext();
        assertThat(context.getState(), is(run));
        assertThat(context.getRunId(), is(RUN_ID));
        assertThat(context.getCurrent(), is(1L));
        assertThat(context.getWaiting(), is(0L));
        assertThat(context.getTotal(), is(1L));
        assertThat(context.isRunning(), is(false));
        assertThat(context.isStopped(), is(true));
    }

    @Test
    public void testIdleToStarting2() throws FSMException {
        Starting start = (Starting)engine.fire(StopContext.builder().runId(RUN_ID).total(1L).data(DATA).build());
        Context context = engine.getFSM().getContext();
        assertThat(context.getState(), is(start));
        assertThat(context.getRunId(), is(RUN_ID));
        assertThat(context.getCurrent(), is(0L));
        assertThat(context.getWaiting(), is(0L));
        assertThat(context.getTotal(), is(1L));
        assertThat(context.isRunning(), is(false));
        assertThat(context.isStopped(), is(false));

        start = (Starting)engine.fire(InterContext.builder().runId(RUN_ID).data(DATA).build());
        context = engine.getFSM().getContext();
        assertThat(context.getState(), is(start));
        assertThat(context.getRunId(), is(RUN_ID));
        assertThat(context.getWaiting(), is(1L));
        assertThat(context.getTotal(), is(1L));
        assertThat(context.isRunning(), is(false));
        assertThat(context.isStopped(), is(false));

        Running run = (Running)engine.fire(StartContext.builder().runId(RUN_ID).data(DATA).build());
        assertThat(engine.isCompleted(), is(true));
        context = engine.getFSM().getContext();
        assertThat(context.getState(), is(run));
        assertThat(context.getRunId(), is(RUN_ID));
        assertThat(context.getCurrent(), is(1L));
        assertThat(context.getWaiting(), is(0L));
        assertThat(context.getTotal(), is(1L));
        assertThat(context.isRunning(), is(false));
        assertThat(context.isStopped(), is(true));
    }

    @Test(expected = StateMachineException.class)
    public void validateError() throws FSMException {
        engine.fire(StartContext.builder().runId("test").data(DATA).build());
    }
}
