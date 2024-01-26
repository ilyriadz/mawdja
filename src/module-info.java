
module ilyriadz.games.mawdja {
    requires java.logging;
    
    requires javafx.graphics;
    requires javafx.media;
    
    requires transitive org.jbox2d;
    
    opens ilyriadz.games.mawdja.core;
    
    exports ilyriadz.games.mawdja.menu;
    exports ilyriadz.games.mawdja.contact;
    exports ilyriadz.games.mawdja.core;
    exports ilyriadz.games.mawdja.core.media;
    exports ilyriadz.games.mawdja.fsm;
    exports ilyriadz.games.mawdja.fsm.states;
    exports ilyriadz.games.mawdja.message;
    exports ilyriadz.games.mawdja.gutil;
}
