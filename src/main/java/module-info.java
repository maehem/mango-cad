module com.maehem.mangocad {
    requires java.logging;
    requires javafx.controls;
    requires javafx.graphics;
    requires java.base;
    requires yamlbeans;
    requires jdk.xml.dom;

    exports com.maehem.mangocad;
    exports com.maehem.mangocad.view;
    exports com.maehem.mangocad.view.controlpanel;
    exports com.maehem.mangocad.view.controlpanel.listitem;
}
