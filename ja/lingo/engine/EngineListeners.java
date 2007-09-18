package ja.lingo.engine;

import ja.centre.util.assertions.Arguments;
import ja.lingo.engine.beans.IInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Multicasting listener.
 */
class EngineListeners implements IEngineListener {
    private List<IEngineListener> listeners = new ArrayList<IEngineListener>();

    public void dictionaryAdded( IInfo info ) {
        for ( IEngineListener listener : listeners ) {
            listener.dictionaryAdded( info );
        }
    }
    public void dictionaryDeleted( IInfo info ) {
        for ( IEngineListener listener : listeners ) {
            listener.dictionaryDeleted( info );
        }
    }
    public void dictionariesSwaped( int index0, int index1 ) {
        for ( IEngineListener listener : listeners ) {
            listener.dictionariesSwaped( index0, index1 );
        }
    }
    public void uncompiled() {
        for ( IEngineListener listener : listeners ) {
            listener.uncompiled();
        }
    }
    public void compiled() {
        for ( IEngineListener listener : listeners ) {
            listener.compiled();
        }
    }

    public void add( IEngineListener listener ) {
        Arguments.assertNotNull( "listener", listener );
        listeners.add( listener );
    }
}
