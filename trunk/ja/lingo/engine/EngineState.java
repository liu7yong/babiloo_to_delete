package ja.lingo.engine;

import ja.centre.util.assertions.States;

class EngineState {
    public static final EngineState COMPILED = new EngineState( "compiled" ) {
        @Override
        public void fireEvents( EngineListeners listeners ) {
            listeners.compiled();
        }
        public void assertCompiled() {
            // do nothing
        }
    };
    public static final EngineState UNCOMPILED = new EngineState( "uncompiled" ) {
        @Override
        public void fireEvents( EngineListeners listeners ) {
            listeners.uncompiled();
        }
    };
    public static final EngineState CLOSED = new EngineState( "closed" ) {
        @Override
        public void assertOpen() {
            States.doThrow( "Expected to be not closed" );
        }
    };

    private String name;

    private EngineState( String name ) {
        this.name = name;
    }

    protected void fireEvents( EngineListeners listeners ) {
        // by default, a state posts no events
    }

    public void assertOpen() {
        // by default, a state is operative
    }
    public void assertCompiled() {
        // by default, a state is uncompiled
        States.doThrow( "Expected to be compiled" );
    }


    public String toString() {
        return name;
    }
}
