
module ilyriadz.games.mawdja {
    requires java.logging;
    
    requires javafx.graphics;
    requires javafx.media;
    
    requires org.jbox2d;
    
    requires com.bulletphysic;
    requires static vecmath;
    
    opens ilyriadz.games.mawdja;
    opens ilyriadz.games.mawdja.core;
    
    exports ilyriadz.games.mawdja.core.xyz;
    exports ilyriadz.games.mawdja.core.xyz.contact;
    
    exports ilyriadz.games.mawdja.menu;
    exports ilyriadz.games.mawdja.contact;
    exports ilyriadz.games.mawdja.core;
    exports ilyriadz.games.mawdja.core.media;
    exports ilyriadz.games.mawdja.fsm;
    exports ilyriadz.games.mawdja.fsm.states;
    exports ilyriadz.games.mawdja.message;
    exports ilyriadz.games.mawdja.gutil;
}
